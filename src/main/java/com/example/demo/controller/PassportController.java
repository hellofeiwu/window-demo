package com.example.demo.controller;

import com.example.demo.utils.IMOOCJSONResult;
import com.example.demo.utils.Queue;
import com.example.demo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.TimeUnit;

@RestController
public class PassportController {
    @Autowired
    private RedisUtils redisUtils;

    @PostMapping(value = "/login")
    public IMOOCJSONResult login() {
//        1. 生成token
        String token = RequestContextHolder.currentRequestAttributes().getSessionId();
//        2. 把token存入缓存，设置30秒过期
        int index = Queue.userQueue.indexOf(token);
        if (index != -1) {
            return IMOOCJSONResult.build(501,"请勿重复登录", ++index);
        }
        redisUtils.set(token,"",30L, TimeUnit.SECONDS);
//        3. 把token加入队列
        Queue.userQueue.add(token);
//        4. 返回再队列中的位置给前端
        index = Queue.userQueue.indexOf(token);

        return IMOOCJSONResult.ok(++index);
    }

    @GetMapping(value = "/logout")
    public IMOOCJSONResult logout(@RequestParam String token) {
//        1. 从缓存中删除token
        redisUtils.remove(token);
//        2. 从缓存中删除所有操作数据
        redisUtils.removePattern("action*");
//        3. 从队列中删除token
        Queue.userQueue.remove(token);

        return IMOOCJSONResult.ok(Queue.userQueue.toString());
    }
}
