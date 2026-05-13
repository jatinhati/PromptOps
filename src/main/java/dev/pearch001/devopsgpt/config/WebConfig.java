package dev.pearch001.devopsgpt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This configuration class enables Cross-Origin Resource Sharing (CORS)
 * for the entire API, allowing the React frontend (on localhost:3000)
 * to communicate with the backend (on localhost:8080).
 */
@Configuration // <-- This tells Spring: "This is a setup file, load it."
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply this rule to all endpoints under /api/

                // Allow requests from our React development server.
                .allowedOriginPatterns("*")

                // Allow standard HTTP methods. 'OPTIONS' is crucial for preflight requests.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // Allow all headers in the request (like Content-Type).
                .allowedHeaders("*")

                // Allow cookies and credentials to be sent.
                .allowCredentials(true);
    }
}