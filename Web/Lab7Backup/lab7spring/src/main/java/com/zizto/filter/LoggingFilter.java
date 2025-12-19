package com.zizto.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Order(1)
public class LoggingFilter implements Filter {

    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        
        log.info("[{}] {} {} - запрос получен", timestamp, method, uri);
        
        try {
            chain.doFilter(request, response);
        } finally {
            log.info("[{}] {} {} - запрос завершен", timestamp, method, uri);
        }
    }
}
