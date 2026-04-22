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
                        .title("Lata Velha — API de Oficina Mecânica")
                        .description("""
                                API de gerenciamento de Ordens de Serviço para oficina mecânica.

                                **Perfis:** ATENDENTE (abre OS, aprova serviços, entrega veículo) · MECANICO (diagnóstico e execução) · ADMIN (acesso total ao sistema)

                                ---

                                **Fluxo principal da OS:**

                                1. `POST /ordens-servico` — ATENDENTE abre a OS → status `RECEBIDA`
                                2. `PATCH /ordens-servico/{idOs}/iniciar-diagnostico` — MECANICO assume a OS → `EM_DIAGNOSTICO`
                                3. `PATCH /ordens-servico/adiciona-servico` — MECANICO adiciona serviços e peças (pode chamar múltiplas vezes)
                                4. `PATCH /ordens-servico/{idOs}/finalizar-diagnostico` — MECANICO encerra diagnóstico → `AGUARDANDO_APROVACAO` _(e-mail de orçamento ao cliente)_
                                5. `PATCH /ordens-servico/aprovar` — ATENDENTE aprova ou recusa cada serviço → `APROVADA` _(e-mail de confirmação ao cliente; e-mail ao ADMIN se houver peças a encomendar)_
                                6. `PATCH /ordens-servico/{idOs}/iniciar-servico/{servicoId}` — MECANICO inicia um serviço por vez → serviço `EM_EXECUCAO`
                                7. `PATCH /ordens-servico/{idOs}/finalizar-servico/{servicoId}` — MECANICO conclui o serviço → serviço `FINALIZADO` _(ao finalizar o último: OS → `FINALIZADA` e e-mail ao cliente)_
                                8. `PATCH /ordens-servico/{idOs}/retirar-veiculo` — ATENDENTE registra entrega → `ENTREGUE`

                                ---

                                **Fluxo de recusa:**

                                - `PATCH /ordens-servico/aprovar` com serviços marcados como `RECUSADO` — recusa parcial (mantém ao menos um `APROVADO`)
                                - `PATCH /ordens-servico/{idOs}/reprovar` — ATENDENTE recusa a OS inteira → `REPROVADA` (terminal, nenhum serviço é executado)

                                ---

                                **Notificações automáticas por e-mail:**
                                - Cliente ao finalizar diagnóstico (orçamento estimado)
                                - ADMIN ao aprovar serviço com peça sem estoque suficiente (encomenda)
                                - Cliente ao finalizar todos os serviços (veículo pronto para retirada)

                                ---

                                **Autenticação:** todos os endpoints exigem `Bearer <token>` — obtenha via `POST /auth/login`.
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
