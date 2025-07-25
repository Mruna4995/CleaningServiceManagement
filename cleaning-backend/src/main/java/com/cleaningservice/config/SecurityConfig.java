package com.cleaningservice.config;

import com.cleaningservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // Let the CorsFilter bean handle this
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(
            	        "/admin/login",
            	        "/admin/send-otp",
            	        "/admin/verify-otp",
            	        "/api/auth/login",
            	        "/api/auth/register"
            	    ).permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/requests").permitAll()
            	    .requestMatchers(HttpMethod.POST, "/api/requests").permitAll() // ✅ allow POST to submit cleaning request
            	    .requestMatchers(HttpMethod.PUT, "/api/requests/*/assign").permitAll()

            	    .requestMatchers(HttpMethod.GET, "/api/requests/pending").permitAll()

            	    .requestMatchers("/api/requests/*/approve", "/api/requests/*/reject").permitAll() 
            	    .requestMatchers("/api/requests/update-status/**").hasRole("ADMIN")
            	    .requestMatchers("/api/requests/**").hasAnyRole("ADMIN", "STAFF")
            	    .requestMatchers("/api/user/**").hasRole("USER")
            	    .requestMatchers("/api/staff/**").hasRole("STAFF")
            	    .requestMatchers("/api/admin/**").hasRole("ADMIN")
            	    .anyRequest().authenticated()
            	)

            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:4200")); // ✅ Angular app
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
