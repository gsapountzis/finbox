package com.xm.finbox.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.xm.finbox.security.TokenStore;
import com.xm.finbox.security.TokenUtil;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private TokenStore tokenStore;

	@Value("${jwt.header}")
	private String tokenHeader;

	@Value("${jwt.prefix}")
	private String tokenPrefix;

	/**
	 * This endpoint is used to register a new user.
	 *
	 * @param user the details of the new user
	 *
	 * @return
	 * <ul>
	 * <li>Returns 201 if user created successfully</li>
	 * <li>Returns 400 if validation error</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> register(@Valid @RequestBody UserRegistrationForm user) {
		try {
			userService.register(user);
			URI location = linkTo(methodOn(UserController.class).login(null)).toUri();
			return ResponseEntity.created(location).build();
		} catch (UserExistsException ex) {
			// TODO set error details document
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * This endpoint is used to authenticate a registered user.
	 *
	 * @param user the details of the user to be authenticated
	 *
	 * @return
	 * <ul>
	 * <li>Returns 204 if authorized</li>
	 * <li>Returns 401 if username or password is wrong</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@PostMapping(path = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> login(@Valid @RequestBody UserLoginForm user) {
		// Convert external credentials to internal token
		UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), Collections.emptyList());

		try {
			// Try authenticate
			authenticationManager.authenticate(auth);

			// Update security context
			SecurityContextHolder.getContext().setAuthentication(auth);

			// Generate external token
			String token = tokenUtil.generateToken(user.getEmail());

			// Import token
			tokenStore.login(token);

			return ResponseEntity.noContent().header(tokenHeader, tokenPrefix + " " + token).build();
		} catch (AuthenticationException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	/**
	 * This endpoint is used to logout a registered user.
	 *
	 * @return
	 * <ul>
	 * <li>Returns 204 if logged out successfully</li>
	 * <li>Returns 401 if user not authenticated</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@PostMapping(path = "/logout")
	public ResponseEntity<Void> logout(Authentication auth) {
		logger.debug("Authenticated principal name: {}", auth.getName());

		// Extract token from authentication details
		String token = (String) auth.getDetails();

		// Discard token
		tokenStore.logout(token);

		return ResponseEntity.noContent().build();
	}

	/**
	 * This endpoint is used to get the details of the logged in user.
	 *
	 * @return
	 * <ul>
	 * <li>Returns 200 if user logged in and information retrieved</li>
	 * <li>Returns 401 if user not logged in</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> account(Authentication auth) {
		logger.debug("Authenticated principal name: {}", auth.getName());

		String email = auth.getName();
		Account account = userService.getAccoutDetails(email);
		return ResponseEntity.ok(account);
	}

}
