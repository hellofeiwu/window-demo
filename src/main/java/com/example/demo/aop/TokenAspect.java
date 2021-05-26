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

@Aspect
@Component
public class TokenAspect {

    @Autowired
    private RedisUtils redisUtils;

    @Around("execution(* com.example.demo.controller.DataController.setData(..))")
    public Object checkLogin(ProceedingJoinPoint pjp) throws Throwable {
        String token = RequestContextHolder.currentRequestAttributes().getSessionId();
        // 1. 看缓存中有没有这个token，如果没有的话，删除队列中这个token，返回请登录
        if (!redisUtils.exists(token)) {
            Queue.userQueue.remove(token);
            return IMOOCJSONResult.errorMsg("请登录");
        }

        // 2. 如果有的话，看在队列中是第几个，如果不是就返回在队列中的index
        Integer index = Queue.userQueue.indexOf(token);
        if (index != 0) {
            return IMOOCJSONResult.errorMsg("你现在排在第 " + index++ + " 位");
        }

        // 3. 如果是第一个的话，看看还剩下多长时间，如果还剩下30秒，就再存一次，续一下时间
        System.out.printf("this is the first one in the queue " + token);
        return pjp.proceed();
    }
}
