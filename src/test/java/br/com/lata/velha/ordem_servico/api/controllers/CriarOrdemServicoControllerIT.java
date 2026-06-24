package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.CriarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResumoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResumoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResumoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:criar-os-ctrl-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
class CriarOrdemServicoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CriarOrdemServicoUseCase criarOrdemServicoUseCase;

    @MockBean
    private IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;

    @MockBean
    private BuscarOrdemServicoUseCase buscarOrdemServicoUseCase;

    @MockBean
    private AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;

    @MockBean
    private ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;

    @MockBean
    private AdicionarServicoUseCase adicionarServicoUseCase;

    @MockBean
    private FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;

    @MockBean
    private IniciarServicoUseCase iniciarServicoUseCase;

    @MockBean
    private FinalizarServicoUseCase finalizarServicoUseCase;

    @MockBean
    private RetirarVeiculoUseCase retirarVeiculoUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /ordens-servico deve retornar 201 com a OrdemServico criada")
    void shouldReturn201WhenCreatingOrdemServico() throws Exception {
        var request = new CriarOrdemServicoRequest(3L, 4L, "Barulho ao frear", 3L , 4, 3l, new BigDecimal(3));
        var osResponse = new OrdemServicoResponse(
                1L, "RECEBIDA", "Barulho ao frear",
                new FuncionarioResumoResponse(2L, "Maria Atendente"),
                null,
                new ProprietarioResumoResponse(4L, "João Proprietário"),
                new VeiculoResumoResponse(3L, "Fiat Uno 2020"),
                LocalDateTime.now(), null, null, LocalDateTime.now(),
                List.of(), null
        );
        when(criarOrdemServicoUseCase.execute(any())).thenReturn(osResponse);

        mockMvc.perform(post("/ordens-servico")
                        .with(jwt().jwt(b -> b.subject(UUID.randomUUID().toString())).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /ordens-servico com campos obrigatórios nulos deve retornar 400")
    void shouldReturn400WhenRequiredFieldsAreNull() throws Exception {
        var invalid = new CriarOrdemServicoRequest(null, null, "Motivo",3L , 4, 3l, new BigDecimal(3));

        mockMvc.perform(post("/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /ordens-servico com reclamação acima de 500 caracteres deve retornar 400")
    void shouldReturn400WhenReclamacaoExceedsMaxLength() throws Exception {
        var invalid = new CriarOrdemServicoRequest(3L, 4L, "x".repeat(501), 3L , 4, 3l, new BigDecimal(3));

        mockMvc.perform(post("/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /ordens-servico sem autenticação deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        var request = new CriarOrdemServicoRequest(3L, 4L, "Barulho ao frear", 3L , 4, 3l, new BigDecimal(3));

        mockMvc.perform(post("/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
