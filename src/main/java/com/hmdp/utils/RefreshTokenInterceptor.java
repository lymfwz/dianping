package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hmdp.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-08-21-15:42
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 刷新拦截器，只做刷新
        // 1. 获取请求头中的token ==> 这里应该是结合前端的拦截器
        String token = request.getHeader("authorization");
        // 如果token 空
        if (StringUtils.isBlank(token)) {
            return true;
        }

        // 2. 获取 Redis 中的用户哈希map
        String tokenKey = LOGIN_USER_KEY + token;
        Map<Object, Object> userDtoMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        log.warn(String.format("===token : %s === map : %s", token, userDtoMap.toString()));
        // 3. 判断用户是否存在
        if (userDtoMap == null) {

            // 4. 不存在, 放行
            return true;
        }
        // 5. 存在，保存用户信息到ThreadLocal , 不要存敏感、不必要信息
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userDtoMap, new UserDTO(), false);
        // 6. 保存用户信息到 ThreadLocal 当中
        UserHolder.saveUser(userDTO);
        // 7. 刷新缓存
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户 ， 防止内存泄露
        UserHolder.removeUser();
    }
}
