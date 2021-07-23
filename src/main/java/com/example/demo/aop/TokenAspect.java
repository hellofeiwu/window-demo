package com.example.demo.aop;

import com.example.demo.controller.BaseController;
import com.example.demo.utils.IMOOCJSONResult;
import com.example.demo.utils.Queue;
import com.example.demo.utils.RedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class TokenAspect extends BaseController {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${token-expire-time}")
    private String tokenExpireTime;

    @Around("execution(* com.example.demo.controller.DataController.setData(..))")
    public Object checkLogin(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println(Queue.userQueue.toString());

        // 1. 不在队列中的话，返回请登录
        String token = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (!Queue.userQueue.contains(token)) {
            return IMOOCJSONResult.errorMsg("请登录");
        }

        // 2. 比对是不是队列中的第一位，是的话就续期
        Integer index = Queue.userQueue.indexOf(token);
        if (index == 0) {
            redisUtils.set(token,"",Long.valueOf(tokenExpireTime), TimeUnit.SECONDS);
        }else {
            // 3. 不是的话，清理当前队列
            cleanQueue();
            index ++;
            return IMOOCJSONResult.build(502,"你现在排在第 " + index + " 位", index);
        }

        return pjp.proceed();
    }
}
