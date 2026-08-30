package br.com.lata.velha.shared.infrasctructure.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:correlation-id-filter-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
class CorrelationIdFilterIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("deve retornar um X-Correlation-Id gerado quando nenhum for enviado")
    void shouldReturnGeneratedCorrelationIdWhenNoneSent() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(header().exists(CorrelationIdFilter.HEADER_NAME));
    }

    @Test
    @DisplayName("deve ecoar o X-Correlation-Id recebido na requisicao")
    void shouldEchoIncomingCorrelationId() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness")
                        .header(CorrelationIdFilter.HEADER_NAME, "it-test-correlation-id"))
                .andExpect(status().isOk())
                .andExpect(header().string(CorrelationIdFilter.HEADER_NAME, "it-test-correlation-id"));
    }
}
