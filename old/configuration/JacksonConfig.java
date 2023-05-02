package com.jku.dke.bac.ubsm.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AirspaceUser.class, new AirspaceUserDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}
