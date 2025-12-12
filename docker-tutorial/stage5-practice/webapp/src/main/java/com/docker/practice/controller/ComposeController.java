package com.docker.practice.controller;

import com.docker.practice.service.ComposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Docker Compose 演示控制器
 */
@Controller
@RequiredArgsConstructor
public class ComposeController {

    private final ComposeService composeService;

    /**
     * 首页 - 显示 Compose 信息
     */
    @GetMapping({"/", "/compose"})
    public String index(Model model) {
        model.addAttribute("composeInfo", composeService.getComposeInfo());
        model.addAttribute("services", composeService.getServicesStatus());
        model.addAttribute("environment", composeService.getEnvironmentInfo());
        return "compose";
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

