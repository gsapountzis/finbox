package com.xm.finbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.xm.finbox.security.TokenAuthenticationFilter;
import com.xm.finbox.security.UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UnauthorizedEntryPoint unauthorizedHandler;

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
		return new TokenAuthenticationFilter();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()

			// disable default logout handler (clears and redirects to /login?logout)
			.logout().disable()

			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

			// do not create session
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

			.authorizeRequests()

			// allow static resources
			.antMatchers(HttpMethod.GET, "/static/**").permitAll()

			// allow H2 console
			.antMatchers("/h2-console/**/**").permitAll()

			// allow register and login pages
			.antMatchers(HttpMethod.POST, "/users", "/auth").permitAll()

			.anyRequest().authenticated();

		// custom token based security filter
		http
			.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		// disable page caching, required for H2 or else H2 console will be blank
		http
			.headers()
			.frameOptions().sameOrigin()
			.cacheControl();
	}

	@Override
 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.debug(false);
	}
}
