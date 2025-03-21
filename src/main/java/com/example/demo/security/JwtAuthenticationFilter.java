package com.example.demo.security;

import com.example.demo.util.JwtUtil;
import com.example.demo.login.domain.User;
import com.example.demo.login.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            logger.info("필터 토큰 확인: {}", token);

            try {
                Long userId = jwtUtil.extractUserId(token);
                logger.info("필터 아이디 추출: {}", userId);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userService.findById(userId);
                    if (user != null && jwtUtil.validateToken(token, user.getId())) {
                        String role = "USER";
                        if (role == null || role.trim().isEmpty()) {
                            role = "USER";
                        }

                        logger.info("Role: {}", role);

                        // UserDetails 대신 User 객체를 Principal로 사용
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null,
                                        Collections.singletonList(new SimpleGrantedAuthority(role)));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("필터에서 인증넣기");
                    } else {
                        logger.warn("Invalid token or expired token");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid or expired token");
                        return;
                    }
                }
            } catch (Exception e) {
                logger.error("Error extracting or validating token", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Authentication failed: " + e.getMessage());
                return;
            }
        }
        logger.info("필터 token:"+token);
        filterChain.doFilter(request, response);
    }
}
