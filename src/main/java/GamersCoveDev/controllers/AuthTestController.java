package GamersCoveDev.controllers;

import GamersCoveDev.domains.entities.UserEntity;
import GamersCoveDev.services.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth-test")
@RequiredArgsConstructor
public class AuthTestController {

    private final UserService userService;
    private final FirebaseAuth firebaseAuth;

    @GetMapping("/test-token")
    public ResponseEntity<Map<String, Object>> getTestToken() {
        try {
            String uid = "demo-uid-123"; // Test user ID
            String email = "test@example.com";
            
            // Get or create test user in Firebase
            try {
                UserRecord userRecord = firebaseAuth.getUser(uid);
                log.info("User already exists in Firebase: {}", userRecord.getUid());
            } catch (FirebaseAuthException e) {
                // Create test user in Firebase
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setUid(uid)
                    .setEmail(email)
                    .setEmailVerified(true)
                    .setDisplayName("Test User");
                
                UserRecord userRecord = firebaseAuth.createUser(request);
                log.info("Created new Firebase user: {}", userRecord.getUid());
            }
            
            // Get or create test user in our database
            Optional<UserEntity> userOpt = userService.findByFirebaseUid(uid);
            UserEntity user;
            if (userOpt.isEmpty()) {
                user = new UserEntity();
                user.setFirebaseUid(uid);
                user.setUsername("testuser");
                user.setEmail(email);
                user = userService.createUser(user);
                log.info("Created new user in database: {}", user.getId());
            } else {
                user = userOpt.get();
                log.info("Found existing user in database: {}", user.getId());
            }
            
            // Generate a custom token that can be exchanged for an ID token by the client
            String customToken = firebaseAuth.createCustomToken(uid);
            
            Map<String, Object> response = new HashMap<>();
            response.put("customToken", customToken);
            response.put("userId", user.getId());
            response.put("firebaseUid", uid);
            response.put("message", "Use the custom token with the exchange-token endpoint to get an ID token");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating test token", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to generate token: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/exchange-token")
    public ResponseEntity<Map<String, Object>> exchangeToken(@RequestBody Map<String, String> request) {
        try {
            String customToken = request.get("customToken");
            if (customToken == null || customToken.isEmpty()) {
                throw new IllegalArgumentException("Custom token is required");
            }
            
            // In a real application, you would verify the custom token and sign in to get ID token
            // For testing purposes, we'll use the custom token directly since we're in a test environment
            // In a real app, you would use Firebase client SDK to sign in with the custom token
            
            // Get the UID from the custom token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(customToken, true); // Check revoked = true
            String uid = decodedToken.getUid();
            
            // Get the user record
            UserRecord userRecord = firebaseAuth.getUser(uid);
            
            // In a real app, you would generate a real ID token here
            // For testing, we'll use the custom token but make it clear it's for testing only
            
            Map<String, Object> response = new HashMap<>();
            response.put("idToken", customToken); // In a real app, this would be a real ID token
            response.put("uid", uid);
            response.put("email", userRecord.getEmail());
            response.put("emailVerified", userRecord.isEmailVerified());
            response.put("message", "TESTING ONLY: Using custom token as ID token. In production, exchange for a real ID token.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error exchanging token", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to exchange token: " + e.getMessage());
            error.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/test-token-info")
    public ResponseEntity<Map<String, Object>> testTokenInfo(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tokenLength", token.length());
        response.put("first20Chars", token.substring(0, Math.min(20, token.length())) + "...");
        response.put("tokenType", "This appears to be a custom token. In a real app, you would exchange this for an ID token on the client side.");
        
        try {
            // For testing, let's try to get the user ID from the custom token
            // This is a simplified approach - in a real app, you would use verifyIdToken() with an ID token
            String uid = "demo-uid-123"; // Default test user ID
            
            // Try to get user from database
            try {
                Optional<UserEntity> userOpt = userService.findByFirebaseUid(uid);
                if (userOpt.isPresent()) {
                    UserEntity user = userOpt.get();
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("email", user.getEmail());
                    response.put("user", userInfo);
                    response.put("message", "Successfully retrieved user data using test UID");
                } else {
                    response.put("user", "User not found in database");
                }
            } catch (Exception e) {
                response.put("databaseError", e.getMessage());
            }
            
            // Add some helpful information about the token
            response.put("note", "This is a simulated response. In a real application, you would need to:");
            response.put("steps", Arrays.asList(
                "1. Get a custom token from /api/auth-test/test-token",
                "2. On the client side, use Firebase SDK to sign in with the custom token",
                "3. Get the ID token from the signed-in user",
                "4. Use that ID token in the Authorization header for authenticated requests"
            ));
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
        }

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-user-data")
    public ResponseEntity<Map<String, Object>> testUserData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String uid = "demo-uid-123"; // Default test user ID
            
            // Try to get user from database
            Optional<UserEntity> userOpt = userService.findByFirebaseUid(uid);
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                Map<String, Object> userInfo = new HashMap<>();
                
                // Only include fields that exist in UserEntity
                if (user.getId() != null) userInfo.put("id", user.getId());
                if (user.getUsername() != null) userInfo.put("username", user.getUsername());
                if (user.getEmail() != null) userInfo.put("email", user.getEmail());
                if (user.getFirebaseUid() != null) userInfo.put("firebaseUid", user.getFirebaseUid());
                
                // Safely include optional fields if they exist
                try {
                    if (user.getBio() != null) userInfo.put("bio", user.getBio());
                } catch (Exception e) { /* Field doesn't exist */ }
                
                try {
                    if (user.getAvatarUrl() != null) userInfo.put("avatarUrl", user.getAvatarUrl());
                } catch (Exception e) { /* Field doesn't exist */ }
                
                response.put("user", userInfo);
                response.put("message", "Successfully retrieved user data");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "User not found in database");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            log.error("Error in test-user-data endpoint", e);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/verify-token")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestParam String token) {
        try {
            // Verify the ID token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token, true);
            
            // Get user record for additional info
            UserRecord userRecord = firebaseAuth.getUser(decodedToken.getUid());
            
            Map<String, Object> response = new HashMap<>();
            response.put("uid", decodedToken.getUid());
            response.put("email", decodedToken.getEmail());
            response.put("emailVerified", decodedToken.isEmailVerified());
            response.put("name", decodedToken.getName());
            response.put("providerData", userRecord.getProviderData());
            response.put("metadata", Map.of(
                "creationTime", userRecord.getUserMetadata().getCreationTimestamp(),
                "lastSignInTime", userRecord.getUserMetadata().getLastSignInTimestamp()
            ));
            
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            log.error("Token verification failed", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid token: " + e.getMessage());
            error.put("errorCode", e.getErrorCode());
            return ResponseEntity.status(401).body(error);
        } catch (Exception e) {
            log.error("Unexpected error during token verification", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}