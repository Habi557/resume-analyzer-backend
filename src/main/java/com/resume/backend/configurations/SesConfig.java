package com.resume.backend.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
@Configuration
@Profile("prod")
public class SesConfig {

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.AP_SOUTH_1)
                // NO credentialsProvider()
                .build();
    }
}

