package br.edu.oficina.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SegurancaConfig {
    @Bean
    UserDetailsService usuarios(@Value("${app.senha.atendente}") String senhaAtendente,
                                @Value("${app.senha.consulta}") String senhaConsulta) {
        if (senhaAtendente.isBlank() || senhaConsulta.isBlank()) {
            throw new IllegalStateException("Defina APP_SENHA_ATENDENTE e APP_SENHA_CONSULTA ou use o perfil local.");
        }

        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new InMemoryUserDetailsManager(
            User.withUsername("atendente").password(encoder.encode(senhaAtendente)).roles("ATENDENTE").build(),
            User.withUsername("consulta").password(encoder.encode(senhaConsulta)).roles("CONSULTA").build()
        );
    }

    @Bean
    SecurityFilterChain acesso(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(regra -> regra
                .requestMatchers("/login", "/estilo.css", "/saude").permitAll()
                .requestMatchers("/clientes/novo", "/clientes/*/editar", "/ordens/nova", "/ordens/*/editar").hasRole("ATENDENTE")
                .requestMatchers(HttpMethod.POST, "/clientes/**", "/ordens/**").hasRole("ATENDENTE")
                .anyRequest().authenticated())
            .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/ordens"))
            .build();
    }
}
