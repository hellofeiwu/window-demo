package com.example.demo.controller;

import com.example.demo.utils.IMOOCJSONResult;
import com.example.demo.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class DataController extends BaseController {
    public static final Logger log = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private RedisUtils redisUtils;

    @PostMapping(value = "/setData")
    public IMOOCJSONResult setData(@RequestParam String key, @RequestParam String value) {
        redisUtils.set(key,value,DATA_EXPIRE_TIME, TimeUnit.SECONDS);
        log.info("****** setData api is called, key is " + key + ", value is " + value + " ******");
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
