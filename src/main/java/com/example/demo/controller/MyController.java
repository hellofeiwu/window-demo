package com.example.demo.controller;

import com.example.demo.utils.IMOOCJSONResult;
import com.example.demo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.TimeUnit;

@RestController
public class MyController {
    @Autowired
    private RedisUtils redisUtils;

    @GetMapping(value = "/test")
    public IMOOCJSONResult test() {
        //查询缓存中是否存在
        String id = "mykey";
        boolean hasKey = redisUtils.exists(id);
        String str = "test1";
        if(hasKey){
            //获取缓存
            Object object =  redisUtils.get(id);
            str = object.toString();
        }else{
            //数据插入缓存（set中的参数含义：key值，user对象，缓存存在时间10（long类型），时间单位）
            redisUtils.set(id,str,10L, TimeUnit.MINUTES);
        }
        return IMOOCJSONResult.ok(redisUtils.get(id).toString());
    }

    @GetMapping(value = "/value")
    public IMOOCJSONResult getValueByKey(@RequestParam String key) {
        if(redisUtils.exists(key)) {
            return IMOOCJSONResult.ok(redisUtils.get(key).toString());
        }else {
            return IMOOCJSONResult.errorMsg("No such key");
        }
    }
}
