package com.xm.finbox.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private TokenStore tokenStore;

	@Value("${jwt.header}")
	private String tokenHeader;

	@Value("${jwt.prefix}")
	private String tokenPrefix;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		// Convert external authentication token (JWT) to internal token (Spring) (maybe null)
		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

		// Update security context
		SecurityContextHolder.getContext().setAuthentication(authentication);

		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String header = request.getHeader(tokenHeader);

		if (header == null || !header.startsWith(tokenPrefix)) {
			return null;
		}

		// Extract (JWT) token
		String token = header.substring(tokenPrefix.length() + 1);

		// Check token validity
		if (!tokenStore.isValid(token)) {
			return null;
		}

		// Update token last access time
		tokenStore.refresh(token);

		// Extract username
		String username = tokenUtil.getUsernameFromToken(token);

		// Check username validity
		if (username == null) {
			return null;
		}

		// Create (Spring) token with principal, (empty) credentials and (empty) authorities
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

		// Keep token in authentication details
		authentication.setDetails(token);

		return authentication;
	}
}
