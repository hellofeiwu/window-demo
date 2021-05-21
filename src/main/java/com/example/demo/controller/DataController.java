package com.example.demo.controller;

import com.example.demo.utils.IMOOCJSONResult;
import com.example.demo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class DataController {
    @Autowired
    private RedisUtils redisUtils;

    @PostMapping(value = "/setData")
    public IMOOCJSONResult setData(@RequestParam String key, @RequestParam String value) {
        redisUtils.set(key,value,60L, TimeUnit.SECONDS);
        return IMOOCJSONResult.ok();
    }

    @GetMapping(value = "/getData")
    public IMOOCJSONResult getData(@RequestParam String key) {
        if(redisUtils.exists(key)) {
            return IMOOCJSONResult.ok(redisUtils.get(key).toString());
        }else {
            return IMOOCJSONResult.errorMsg("No such key");
        }
    }
}
