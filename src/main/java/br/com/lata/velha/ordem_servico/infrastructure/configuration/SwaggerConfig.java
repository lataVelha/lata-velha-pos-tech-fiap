package br.com.lata.velha.ordem_servico.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lata Velha — API de Oficina")
                        .description("""
                                API de gerenciamento de ordens de serviço para oficina mecânica.

                                **Fluxo de uma Ordem de Serviço:**
                                1. `POST /ordens-servico` — Atendente abre a OS
                                2. `PATCH /{idOs}/{idMecanico}/iniciar-diagnostico` — Mecânico inicia diagnóstico
                                3. `PATCH /{idOs}/{idFunc}/finalizar-diagnostico` — Mecânico finaliza diagnóstico
                                4. `PATCH /adiciona-servico` — Adição dos serviços a executar
                                5. `PATCH /aprovar` — Atendente aprova ou reprova serviços
                                6. `PATCH /{idOs}/{idFunc}/iniciar-servico` — Mecânico inicia execução
                                7. `PATCH /{idOs}/{idFunc}/finalizar-servico` — Mecânico finaliza execução
                                8. `PATCH /{idOs}/{idFunc}/retirar-veiculo` — Atendente registra entrega do veículo

                                Todos os endpoints requerem autenticação via **Bearer Token JWT**.
                                Use `POST /auth/login` para obter o token.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Lata Velha")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtido via POST /auth/login")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
