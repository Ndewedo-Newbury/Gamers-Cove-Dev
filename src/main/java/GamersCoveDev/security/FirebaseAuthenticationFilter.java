package GamersCoveDev.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static java.util.Collections.emptyList;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        log.debug("Processing authentication for path: {}", request.getRequestURI());
        
        String token = extractToken(request);
        
        if (StringUtils.hasText(token)) {
            log.debug("Found JWT token in Authorization header");
            
            try {
                log.debug("Verifying Firebase ID token...");
                FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token, true);
                log.debug("Successfully verified token for user: {}", firebaseToken.getUid());
                
                setSecurityContext(firebaseToken);
                log.debug("Successfully set security context for user: {}", firebaseToken.getUid());
                
            } catch (FirebaseAuthException e) {
                log.error("Firebase Authentication failed: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Authentication failed: " + e.getMessage() + "\"}");
                response.setContentType("application/json");
                return;
            } catch (Exception e) {
                log.error("Unexpected error during authentication", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Internal server error during authentication\"}");
                response.setContentType("application/json");
                return;
            }
        } else {
            log.debug("No JWT token found in Authorization header");
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        log.debug("No valid Authorization header found");
        return null;
    }

    private void setSecurityContext(FirebaseToken firebaseToken) {
        String uid = firebaseToken.getUid();
        String email = firebaseToken.getEmail();
        String name = firebaseToken.getName();
        boolean emailVerified = Boolean.TRUE.equals(firebaseToken.isEmailVerified());

        log.debug("Creating UserDetails for user: {}", uid);
        FirebaseUserDetails userDetails = new FirebaseUserDetails(
            uid,
            email,
            name,
            emailVerified
        );

        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                userDetails, 
                null, 
                emptyList()
            );
        
        authentication.setDetails(firebaseToken.getClaims());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        log.info("Successfully authenticated user: {} (email: {})", uid, email);
    }
}