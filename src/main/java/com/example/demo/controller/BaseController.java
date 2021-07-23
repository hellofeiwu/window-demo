package com.example.demo.controller;

import com.example.demo.utils.Queue;
import com.example.demo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {
    @Autowired
    private RedisUtils redisUtils;

    public static final long DATA_EXPIRE_TIME = 6l;

    public void cleanQueue() {
        if (!Queue.userQueue.isEmpty()) {
            String token = Queue.userQueue.get(0);
            if (!redisUtils.exists(token)) { // 这里的校验 就意味着第一位只能保留30秒
                Queue.userQueue.remove(token);
            }
        }
    }
}
