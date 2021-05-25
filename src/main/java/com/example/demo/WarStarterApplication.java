package com.example.demo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// 把项目打包成war包一共4步：第4步，增加war包启动类
public class WarStarterApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 指向原本的启动类 WindowDemoApplication
        return builder.sources(WindowDemoApplication.class);
    }
}
