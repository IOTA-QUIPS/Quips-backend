package com.example.quips.config;

import com.example.quips.model.BovedaCero;
import com.example.quips.model.DAG;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BovedaCero bovedaCero(SistemaConfig sistemaConfig) {
        return new BovedaCero(sistemaConfig.getTokensIniciales());
    }

    @Bean
    public DAG dag() {
        return new DAG();
    }
}
