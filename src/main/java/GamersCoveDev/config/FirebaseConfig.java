package GamersCoveDev.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id}")
    private String projectId;
    
    @Value("${FIREBASE_CREDENTIALS_JSON:#{null}}")
    private String firebaseCredentialsJson;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        log.info("Initializing Google Credentials...");
        
        try {
            // Load directly from classpath resource
            ClassPathResource resource = new ClassPathResource("firebase-credentials.json");
            if (!resource.exists()) {
                throw new FileNotFoundException("firebase-credentials.json not found in classpath");
            }
            
            log.info("Loading Firebase credentials from classpath resource");
            try (InputStream inputStream = resource.getInputStream()) {
                return GoogleCredentials.fromStream(inputStream);
            }
            
        } catch (IOException e) {
            log.error("Failed to load Firebase credentials: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials credentials) {
        log.info("Initializing Firebase App...");
        try {
            // Check if Firebase app is already initialized
            FirebaseApp app = null;
            try {
                app = FirebaseApp.getInstance();
                log.info("Firebase App already initialized: {}", app.getName());
                return app;
            } catch (IllegalStateException e) {
                // Firebase app is not initialized, proceed with initialization
                log.info("No Firebase App found, initializing new one...");
            }
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();

            return FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            log.error("Error initializing Firebase App: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase App", e);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        log.info("Initializing Firebase Auth...");
        return FirebaseAuth.getInstance(firebaseApp);
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        log.info("Initializing Firestore...");
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
