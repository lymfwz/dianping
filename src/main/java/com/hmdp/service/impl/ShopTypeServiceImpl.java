package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.SHOP_TYPE_KEY;
import static com.hmdp.utils.RedisConstants.SHOP_TYPE_TTL;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        String key = SHOP_TYPE_KEY;
        // 1. 查询缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(key);

        // 2. 非空
        if (StringUtils.isNotBlank(jsonStr)) {
            List<ShopType> shopTypes = JSONUtil.toList(jsonStr, ShopType.class);
            return Result.ok(shopTypes);
        }
        // 3. 空
        LambdaQueryWrapper<ShopType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ShopType::getSort);
        List<ShopType> shopTypes = baseMapper.selectList(queryWrapper);
        if (shopTypes == null) {
            return Result.fail("商铺信息错误！");
        }
        // 4. 非空，放入缓存
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopTypes), SHOP_TYPE_TTL, TimeUnit.MINUTES);

        return Result.ok(shopTypes);
    }
}
