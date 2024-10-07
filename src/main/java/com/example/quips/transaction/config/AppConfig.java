package com.example.quips.transaction.config;

import com.example.quips.shared.config.SistemaConfig;
import com.example.quips.transaction.domain.model.BovedaCero;
import com.example.quips.transaction.domain.model.DAG;
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
