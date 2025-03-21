package com.example.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Utf8CharacterFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 요청과 응답에 대해 UTF-8 인코딩 설정
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");

        // 필터 체인 계속 실행
        chain.doFilter(req, res);
    }
}

