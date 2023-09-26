package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.SimpleRedisLock;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Autowired
    private ISeckillVoucherService seckillVoucherService; // 查询秒杀券库存

    @Autowired
    private RedisIdWorker redisIdWorker; // 全局id生成器

/*    @Autowired
    private IVoucherOrderService voucherOrderService;*/

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT = new DefaultRedisScript<>();
    private static final String consumer;

    static {
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
        consumer = UUID.randomUUID().toString();
    }

    // private BlockingQueue<VoucherOrder> orderBlockingQueue = new ArrayBlockingQueue<>(1024 * 1024);

    private static final ExecutorService BLOCK_ES = Executors.newSingleThreadExecutor();

    private IVoucherOrderService proxy1;

    @PostConstruct
    public void init() {
        BLOCK_ES.submit(new VoucherOrderHandler());
    }

    // 为了任务开启项目就可以执行
    private class VoucherOrderHandler implements Runnable {
        String queueName = "stream.orders";

        @Override
        public void run() {
            while (true) {
                // 获取
                try {
                    // VoucherOrder voucherOrder = orderBlockingQueue.take();
                    // 1. 获取消息队列
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", consumer),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    // 1.1 获取成功：
                    if (list == null || list.isEmpty()) {
                        // 1.2 获取失败：继续获取
                        continue;
                    }
                    MapRecord<String, Object, Object> entry = list.get(0);
                    Map<Object, Object> value = entry.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 如果获取成功，可以下单 ==> 写入到数据库
                    // 创建订单（数据库）
                    handlerVoucherOrder(voucherOrder);
                    // 3. ACK确认
                    stringRedisTemplate.opsForStream().acknowledge(
                            queueName,
                            "g1", entry.getId()

                    );

                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    handlePendingList();
                }
            }
        }

        private void handlePendingList() {
            while (true) {
                // 获取
                try {
                    // VoucherOrder voucherOrder = orderBlockingQueue.take();
                    // 1. 获取消息队列
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", consumer),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );
                    // 1.1 获取成功：
                    if (list == null || list.isEmpty()) {
                        // 1.2 获取失败：pending-list 没有异常消息，跳出
                        break;
                    }
                    MapRecord<String, Object, Object> entry = list.get(0);
                    Map<Object, Object> value = entry.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 如果获取成功，可以下单 ==> 写入到数据库
                    // 创建订单（数据库）
                    handlerVoucherOrder(voucherOrder);
                    // 3. ACK确认
                    stringRedisTemplate.opsForStream().acknowledge(
                            queueName,
                            "g1", entry.getId()

                    );


                } catch (Exception e) {
                    log.error("处理 pending-List 订单异常", e);
                }
            }
        }
    }

    @Override
    public Result seckillVoucher(Long voucherId) {

        //用戶
        Long userId = UserHolder.getUser().getId();
        if (userId == null) {
//            log.info("用户id为空？");
            return Result.fail("用户ID为空？");
        }
        Long orderId = redisIdWorker.nextId("order");
        // 1. 执行Lua脚本 (成功 ： 用户有资格、记录下单、放入消息队列)
        Long r = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), orderId.toString()
        );
        if (r == null) {
            return Result.fail("lua脚本失败");
        }
        int res = r.intValue();
        if (res != 0) { // 没有秒杀成功
            return Result.fail(res == 1 ? "库存不足" : "禁止重复下单");
        }

        // 3. 获取代理对象 ==> 子线程无法获取父线程ThreadLocal的变量
        proxy1 = (IVoucherOrderService) AopContext.currentProxy();

        return Result.ok(orderId);
    }

/*    @Override // 改进下单需要操作数据库 （先操作redis， 然后消息队列再操作数据库）
    public Result seckillVoucher(Long voucherId) {
        //用戶
        Long userId = UserHolder.getUser().getId();
        // 1. 执行Lua脚本
        Long r = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString()
        );
        int res = r.intValue();
        if (res != 0) { // 没有秒杀成功
            return Result.fail(res == 1 ? "库存不足" : "禁止重复下单");
        }
        // 2. 保存到阻塞队列
        long orderId = redisIdWorker.nextId("order");
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setUserId(userId);
        voucherOrder.setId(orderId);
        orderBlockingQueue.add(voucherOrder);


        // 3. 获取代理对象 ==> 子线程无法获取父线程ThreadLocal的变量
        proxy1 = (IVoucherOrderService) AopContext.currentProxy();

        return Result.ok(orderId);
    }*/

    /* @Override
     // @Transactional // 加事务可以失败回滚
     public Result seckillVoucher(Long voucherId) {
         // 查询秒杀券
         SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
         if (voucher == null) {
             return Result.fail("抢购失败");
         }
         // 1. 未开始或者已经结束
         LocalDateTime begin = voucher.getBeginTime();
         LocalDateTime end = voucher.getEndTime();
         if (begin.isAfter(LocalDateTime.now())) {
             return Result.fail("秒杀未开始");
         }
         if (LocalDateTime.now().isAfter(end)) {
             return Result.fail("秒杀已结束");
         }

         // 2. 查询库存是否充足
         Integer stock = voucher.getStock();
         if (stock < 1) {
             return Result.fail("库存不足");
         }


         Long userId = UserHolder.getUser().getId();
         // 分布式锁 ==> 改进之前的 synchronized 锁只能锁一个jvm里面的请求


         // redisson 分布式锁 方法：
         RLock lock = redissonClient.getLock("lock:order:" + userId);
         boolean isLock = lock.tryLock();
         if (!isLock) {
             return Result.fail("不允许重复下单");
         }
         try {
             IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
             return proxy.createVoucherOrder(voucherId);
         } finally {
             lock.unlock();
         }


         // 自定义分布式锁方法：
        *//* // 只用锁同一个用户即可，不同用户获取对应自己id的不同锁，防止同一个用户秒杀多单的情况
        SimpleRedisLock lock = new SimpleRedisLock("order:"+userId, stringRedisTemplate);

        // 获取锁
        boolean isLock = lock.tryLock(1200, TimeUnit.SECONDS);
        if (!isLock) {
            // 获取锁失败， 返回错误或重试
            return Result.fail("不允许重复下单");
        }
        // try ... finally 也行？
        try {
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        } finally {
            lock.unLock();
        }
*//*




        // 开始解决方法：
//        synchronized (userId.toString().intern()) { // 找常量池里的字符串的引用 ==> 确保同一个对象，同一把锁
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            // 这里使用事务需要 ： 1.获取当前代理对象 2.接口中定义方法 3. 引入依赖 4. 调用
//            return proxy.createVoucherOrder(voucherId);
//        }
    }
*/

    private void handlerVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        // 不用获取锁？
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            log.error("不允许重复下单");
            return;
        }
        try {
            if (proxy1 != null) {
                proxy1.createVoucherOrder2(voucherOrder);
            }
        } finally {
            lock.unlock();
        }
    }

    // 消息队列创建订单的方法
    @Transactional
    public void createVoucherOrder2(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();


        // 实现一人一单功能
        Integer count = query().eq("user_id", userId).eq("voucher_id", voucherId)
                .count();
        if (count > 0) {
            log.error("已经购买过了！");
            return;
        }


        // 3. 扣减库存
        boolean res = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherId).gt("stock", 0) // 乐观锁！ CAS ==> Compare and set
                .update();
        if (!res) {
            log.error("禁止重复下单！");
            return;
        }


        save(voucherOrder);
    }


    @Transactional // 事务 - 原子性，可回滚
    public Result createVoucherOrder(Long voucherId) {
        Long userId = UserHolder.getUser().getId();


        // 实现一人一单功能
        Integer count = query().eq("user_id", userId).eq("voucher_id", voucherId)
                .count();
        if (count > 0) {
            return Result.fail("用户已经购买过一次");
        }


        // 3. 扣减库存
        boolean res = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherId).gt("stock", 0) // 乐观锁！ CAS ==> Compare and set
                .update();
        if (!res) {
            return Result.fail("库存不足");
        }

        // 4. 新增一个订单信息
        VoucherOrder voucherOrder = new VoucherOrder();
        // 订单号使用自己创建的
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        // 优惠券id
        voucherOrder.setVoucherId(voucherId);
        // 用户id ==> 从ThreadLocal获取
        // Long userId = UserHolder.getUser().getId();
        voucherOrder.setUserId(userId);
        // voucherOrderService.save(voucherOrder);
        save(voucherOrder);

        return Result.ok(orderId);

    }
}
