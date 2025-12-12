package com.docker.practice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/advanced";
    }
}

