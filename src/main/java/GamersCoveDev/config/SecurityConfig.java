package GamersCoveDev.config;

import GamersCoveDev.security.FirebaseAuthenticationFilter;
import GamersCoveDev.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    
    private static final String[] PUBLIC_ENDPOINTS = {
        "/",
        "/index.html",
        "/profile.html",      // Profile page
        "/ai-chat.html",      // Chat page
        "/game/**",
        "/game-detail.html",
        "/css/**",
        "/js/**",
        "/images/**",
        "/actuator/health",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/error",
        "/login",             // Login endpoint
        "/api/auth-test/**",  // Allow all endpoints under /api/auth-test for testing
        "/api/games/**",      // Make games endpoint public
        "/api/chat/**"        // Make chat endpoint and all subpaths public
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/profile.html",
                    "/ai-chat.html",
                    "/game/**",
                    "/game-detail.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/actuator/health",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/error",
                    "/api/auth-test/**",
                    "/api/games/**",
                    "/api/chat/**"
                ).permitAll()
                // Allow preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Disable form login and basic auth
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            // Configure exception handling
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(unauthorizedHandler)
            )
            // Add Firebase authentication filter
            .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}