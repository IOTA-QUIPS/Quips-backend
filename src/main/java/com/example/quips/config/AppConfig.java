package com.example.quips.config;

import com.example.quips.model.BovedaCero;
import com.example.quips.model.DAG;
import com.example.quips.model.ERole;
import com.example.quips.model.Role;
import com.example.quips.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
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
