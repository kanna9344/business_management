package app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import app.security.JwtFilter;
import app.utils.Response;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtFilter filter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf((csrf) -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/request/completeRequest").permitAll()
						.requestMatchers(HttpMethod.GET, "/business/**", "/shop/**", "/request/**")
						.hasAnyAuthority("ADMIN", "BUSINESS").requestMatchers("/business/**", "/shop/**", "/request/**")
						.hasAuthority("BUSINESS").anyRequest().authenticated())
				
				.exceptionHandling((excep) -> excep.accessDeniedHandler((request, response, accessex) -> {

					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType("application/json");

					response.getWriter()
							.write("{\"error\": \"ACCESS_DENIED\", \"message\":" + accessex.getMessage() + "}");

				}).authenticationEntryPoint((request, response, authex) -> {

					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

					response.getWriter()
							.write("{\"error\": \"Unauthorized\", \"message\": \"" + authex.getMessage() + "\"}");
				})).sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
