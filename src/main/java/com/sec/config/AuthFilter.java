package com.sec.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.sec.dao.UserRepository;
import com.sec.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class AuthFilter implements Filter{
	
	private static final List<String> EXCLUDED_ENDPOINTS = Arrays.asList("/registration/**", "/login/**");
	
	private final AntPathMatcher pathMatcher = new AntPathMatcher();
	
	@Autowired
	UserRepository userRepo;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		boolean isExcludedEndpoint = EXCLUDED_ENDPOINTS.stream()
				.anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
		if (isExcludedEndpoint) {
			chain.doFilter(request, response);
			return;
		} else {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (securityContext.getAuthentication() != null) {
				UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
				User users = userRepo.findByEmail(user.getUsername());
				if (users.isActive()) {
					chain.doFilter(request, response);
				} else {
					HttpServletResponse httpResponse = (HttpServletResponse) response;
	                httpResponse.sendRedirect("/login");
				}
			}else {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.sendRedirect("/login");
			}
		}
	}

}
