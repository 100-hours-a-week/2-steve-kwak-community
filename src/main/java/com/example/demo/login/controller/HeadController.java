package com.example.demo.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HeadController {

    @GetMapping("/header1")
    public String getHeader1() {
        return "header1"; // header1.html 템플릿을 반환
    }

    @GetMapping("/header")
    public String getHeader() {
        return "header"; // header1.html 템플릿을 반환
    }
}

