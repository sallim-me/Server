package me.sallim.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Configuration
public class FirebaseConfig {
    @Value("${spring.firebase.admin-sdk:#{null}}")
    private String serviceAccountKeyPath;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.firebase.enabled", havingValue = "true", matchIfMissing = false)
    public FirebaseApp firebaseApp() throws IOException {
        // Check if serviceAccountKeyPath is null or empty
        if (serviceAccountKeyPath == null || serviceAccountKeyPath.trim().isEmpty()) {
            throw new IllegalStateException("Firebase Admin SDK path is not configured");
        }
        
        // Check if the default FirebaseApp already exists
        List<FirebaseApp> apps = FirebaseApp.getApps();
        for (FirebaseApp app : apps) {
            if (FirebaseApp.DEFAULT_APP_NAME.equals(app.getName())) {
                return app;
            }
        }

        // Firebase Admin SDK 초기화
        FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.firebase.enabled", havingValue = "true", matchIfMissing = false)
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
