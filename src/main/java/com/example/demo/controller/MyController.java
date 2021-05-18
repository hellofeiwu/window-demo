package com.example.demo.controller;

import com.example.demo.utils.IMOOCJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class MyController {
    @GetMapping(value = "/test")
    public IMOOCJSONResult test() {
        return IMOOCJSONResult.ok("works");
    }
}
