package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.service.IUserService;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryBlogById(Long id) {
        // 1. 查询blog
        Blog blog = this.getById(id);
        if (blog == null) {
            return Result.fail("博文不存在");
        }
        // 2. 查询用户信息 - 并且封装
        queryBlogUser(blog);

        // 4.点赞结果
        // 获取点赞
        isBlogLiked(blog);
        return Result.ok(blog);
    }

    private void queryBlogUser(Blog blog) { // 设置当前blog的用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getNickName, User::getIcon)
                .eq(User::getId, blog.getUserId());
        User user = userService.getOne(queryWrapper);
        // 3. 封装结果
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    private void isBlogLiked(Blog blog) { // 设置当前用户是否点赞当前博客
        if (UserHolder.getUser() == null) {
            return;
        }
        String key = "blog:like:" + blog.getId();
        Long userId = UserHolder.getUser().getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        blog.setIsLike(score != null);
    }

    @Override
    public Result queryHotBlog(Integer current) {
        // 1. 分页查询
        Page<Blog> page = new Page<>(current, 10L);
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Blog::getLiked);
        this.page(page, queryWrapper);
        // 返回List
        List<Blog> records = page.getRecords();
        records.forEach(
                blog -> {
                    queryBlogUser(blog);
                    isBlogLiked(blog); // 这里哪里错了？
                }
        );
        // 2. 封装结果
        return Result.ok(records);
    }


    @Override
    public Result likeBlog(Long id) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        Long userId = user.getId();
        String key = "blog:like:" + id;
        // 1. 判断是否点赞
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (score == null) {
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
        } else{
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result queryBlogLikes(Long id) {
        String key = "blog:like:" + id;
        // 1. 查询top5 点赞用户 zrange key 0 4
        Set<String> userIds = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if(userIds == null || userIds.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        // 2. 查询用户信息
        List<Long> ids = userIds.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        List<UserDTO> userDTOS = userService.query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOS);
    }
}
