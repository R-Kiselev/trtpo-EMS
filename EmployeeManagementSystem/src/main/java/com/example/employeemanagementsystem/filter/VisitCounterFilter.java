package com.example.employeemanagementsystem.filter;

import com.example.employeemanagementsystem.service.VisitCounter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitCounterFilter implements Filter {

    private final VisitCounter visitCounter;

    @Autowired
    public VisitCounterFilter(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();

        if (requestUri.startsWith("/api")) {
            visitCounter.incrementVisit(requestUri);
        }

        chain.doFilter(request, response);
    }
}