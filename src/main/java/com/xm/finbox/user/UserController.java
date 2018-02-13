package com.xm.finbox.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

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
	ResponseEntity<Void> register(@Valid @RequestBody UserRegisterForm user) {
		try {
			userService.register(user);
			// XXX set location header to 'login' or 'my-account' url ?
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
	ResponseEntity<Void> login(@Valid @RequestBody UserLoginForm user) {
		throw new UnsupportedOperationException("Not implemented");
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
	ResponseEntity<Void> logout() {
		throw new UnsupportedOperationException("Not implemented");
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
	ResponseEntity<?> account() {
		throw new UnsupportedOperationException("Not implemented");
	}

}
