package com.example.adwebsite.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 放在启动类同包或子包下（确保被扫描）
@RestController
public class IndexController {
    // 映射根路径 /
//    @GetMapping("/")
//    public String index() {
//        return "✅ 广告网站服务已正常启动！";
//    }

    @GetMapping("/")
    public String welcome() {
        return "forward:/admin.html";
    }

    // 可选：添加测试接口，验证业务可用
    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\",\"database\":\"connected\"}";
    }

    // 可选：映射/error，替换默认白标错误页
    @GetMapping("/error")
    public String customError() {
        return "{\"code\":404,\"msg\":\"访问的路径不存在\"}";
    }
}