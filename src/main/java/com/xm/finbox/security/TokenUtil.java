package com.xm.finbox.security;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;

@Component
public class TokenUtil {

	@Value("${jwt.header}")
	private String header;

	@Value("${jwt.prefix}")
	private String prefix;

	@Value("${jwt.expiration}")
	private Long expiration;

	@Value("${jwt.secret}")
	private String secret;

	private Clock clock = DefaultClock.INSTANCE;

	public String getUsernameFromToken(String token) {
		return getClaimsFromToken(token).getSubject();
	}

	public String getIdFromToken(String token) {
		return getClaimsFromToken(token).getId();
	}

	private Claims getClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(secret.getBytes())
				.parseClaimsJws(token)
				.getBody();
	}

	public String generateToken(String username) {
		String id = UUID.randomUUID().toString();
		Date issueDate = clock.now();

		return Jwts.builder()
				.setId(id)
				.setSubject(username)
				.setIssuedAt(issueDate)
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
	}
}
