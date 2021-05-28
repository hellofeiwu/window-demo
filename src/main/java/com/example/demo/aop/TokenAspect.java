package com.example.demo.aop;

import com.example.demo.utils.IMOOCJSONResult;
import com.example.demo.utils.Queue;
import com.example.demo.utils.RedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class TokenAspect {

    @Autowired
    private RedisUtils redisUtils;

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
            redisUtils.set(token,"",30L, TimeUnit.SECONDS);
        }else {
            // 3. 不是的话，清理当前队列
            token = Queue.userQueue.get(0);
            if (!redisUtils.exists(token)) { // 这里的校验 就意味着第一位只能保留30秒
                Queue.userQueue.remove(token);
            }
            return IMOOCJSONResult.errorMsg("你现在排在第 " + index + " 位");
        }

        return pjp.proceed();
    }
}
