package com.example.demo.controller;

import com.example.demo.pojo.vo.LoginVO;
import com.example.demo.utils.IMOOCJSONResult;
import com.example.demo.utils.Queue;
import com.example.demo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.TimeUnit;

@RestController
public class PassportController extends BaseController {
    @Autowired
    private RedisUtils redisUtils;

    @Value("${token-expire-time}")
    private String tokenExpireTime;

    @PostMapping(value = "/login")
    public IMOOCJSONResult login() {
        // 1. 清理队列
        cleanQueue();
//        2. 生成token
        String token = RequestContextHolder.currentRequestAttributes().getSessionId();
//        3. 把token存入缓存，设置30秒过期
        int index = Queue.userQueue.indexOf(token);
        if (index != -1) {
            return IMOOCJSONResult.build(501,"请勿重复登录", ++index);
        }
        redisUtils.set(token,"",Long.valueOf(tokenExpireTime), TimeUnit.SECONDS);
//        4. 把token加入队列
        Queue.userQueue.add(token);
//        5. 返回再队列中的位置给前端
        index = Queue.userQueue.indexOf(token);

        LoginVO loginVO = new LoginVO();
        loginVO.setIndex(++index);
        loginVO.setToken(token);

        return IMOOCJSONResult.ok(loginVO);
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
