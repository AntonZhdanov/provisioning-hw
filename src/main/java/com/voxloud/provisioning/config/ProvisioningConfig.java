package com.voxloud.provisioning.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.strategy.ProvisioningStrategyFactory;
import com.voxloud.provisioning.util.OverrideFragmentParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProvisioningConfig {
    @Bean
    public OverrideFragmentParser overrideFragmentParser(ObjectMapper objectMapper) {
        return new OverrideFragmentParser(objectMapper);
    }

    @Bean
    public ProvisioningStrategyFactory provisioningStrategyFactory(OverrideFragmentParser
                                                                           overrideFragmentParser) {
        return new ProvisioningStrategyFactory(overrideFragmentParser);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
