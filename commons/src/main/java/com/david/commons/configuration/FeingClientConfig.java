package com.david.commons.configuration;

import com.david.commons.clients.FeingErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeingClientConfig {
    @Bean
    ErrorDecoder errorDecoder(){
        return new FeingErrorDecoder();
    }
}
