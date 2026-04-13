package br.com.lata.velha.ordemDeServico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordemDeServico.application.dtos.request.*;
import br.com.lata.velha.ordemDeServico.application.dtos.response.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordemDeServico.application.dtos.response.AprovarServicoOsResponse;
import br.com.lata.velha.ordemDeServico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.ordemservico.*;
import br.com.lata.velha.ordemDeServico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordemDeServico.domain.enums.StatusServico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoController.class)
@Import(SecurityConfig.class)
class OrdemServicoControllerTest {

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
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private OrdemServicoResponse buildOrdemResponse() {
        return new OrdemServicoResponse(
                1L, 2L, "Maria Atendente", 3L, "Fiat Uno 2020",
                4L, "João Proprietário", null, null,
                "RECEBIDA", "Barulho ao frear",
                LocalDateTime.now(), null, null, LocalDateTime.now(), List.of()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /ordens-servico/create deve retornar 201 com a ordem criada")
    void shouldReturn201OnCreate() throws Exception {
        var request = new OrdemServicoRequest(3L, 4L, 2L, "Barulho ao frear");

        when(criarOrdemServicoUseCase.execute(any())).thenReturn(buildOrdemResponse());

        mockMvc.perform(post("/ordens-servico/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /ordens-servico/create com body inválido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new OrdemServicoRequest(null, null, null, "x".repeat(501));

        mockMvc.perform(post("/ordens-servico/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /ordens-servico deve retornar 200 com lista paginada")
    void shouldReturn200OnList() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildOrdemResponse()), 0, 10, 1L, 1);

        when(buscarOrdemServicoUseCase.execute(null, null, null, null, 0, 10)).thenReturn(paginated);

        mockMvc.perform(get("/ordens-servico")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /ordens-servico com filtros deve retornar 200")
    void shouldReturn200OnListWithFilters() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildOrdemResponse()), 0, 10, 1L, 1);

        when(buscarOrdemServicoUseCase.execute(eq(1L), eq(StatusOrdemServico.RECEBIDA), eq(4L), eq(null), eq(0), eq(10)))
                .thenReturn(paginated);

        mockMvc.perform(get("/ordens-servico")
                        .param("id", "1")
                        .param("status", "RECEBIDA")
                        .param("proprietarioId", "4")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @DisplayName("PATCH /ordens-servico/{idOs}/{idMecanico}/iniciar deve retornar 200")
    void shouldReturn200OnIniciarDiagnostico() throws Exception {
        var response = new OrdemServicoResponse(
                1L, 2L, "Maria", 3L, "Fiat Uno", 4L, "João",
                5L, "Carlos Mecânico", "EM_DIAGNOSTICO",
                "Barulho", LocalDateTime.now(), null, null, LocalDateTime.now(), List.of()
        );

        when(iniciarDiagnosticoUseCase.execute(1L, 5L)).thenReturn(response);

        mockMvc.perform(patch("/ordens-servico/1/5/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"));
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @DisplayName("PATCH /ordens-servico/adiciona-servico deve retornar 200")
    void shouldReturn200OnAdicionarServico() throws Exception {
        var servicoRequest = new ServicoOSRequest(10L, List.of(), new BigDecimal("150.00"));
        var request = new AddServicoOsRequest(1L, List.of(servicoRequest));

        when(adicionarServicoUseCase.execute(any())).thenReturn(buildOrdemResponse());

        mockMvc.perform(patch("/ordens-servico/adiciona-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @DisplayName("PATCH /ordens-servico/adiciona-servico com body inválido deve retornar 400")
    void shouldReturn400OnAdicionarServicoWithInvalidRequest() throws Exception {
        var invalid = new AddServicoOsRequest(null, List.of());

        mockMvc.perform(patch("/ordens-servico/adiciona-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @DisplayName("PATCH /ordens-servico/{idOs}/{idFunc}/finalizar-diagnostico deve retornar 200")
    void shouldReturn200OnFinalizarDiagnostico() throws Exception {
        var response = new OrdemServicoResponse(
                1L, 2L, "Maria", 3L, "Fiat Uno", 4L, "João",
                5L, "Carlos", "AGUARDANDO_APROVACAO",
                "Barulho", LocalDateTime.now(), LocalDateTime.now(), null, LocalDateTime.now(), List.of()
        );

        when(finalizarDiagnosticoUseCase.execute(1L, 5L)).thenReturn(response);

        mockMvc.perform(patch("/ordens-servico/1/5/finalizar-diagnostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/aprovar deve retornar 200")
    void shouldReturn200OnAprovar() throws Exception {
        var aprovacaoRequest = new AprovarServicoOsRequest(10L, StatusServico.APROVADO);
        var request = new AprovarOrdemSevicoRequest(1L, 2L, List.of(aprovacaoRequest));
        var response = new AprovarOrdemServicoResponse(1L, "EM_EXECUCAO",
                List.of(new AprovarServicoOsResponse(10L, "APROVADO")));

        when(aprovarOrdemServicoUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(patch("/ordens-servico/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOs").value(1L))
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/aprovar com body inválido deve retornar 400")
    void shouldReturn400OnAprovarWithInvalidRequest() throws Exception {
        var invalid = new AprovarOrdemSevicoRequest(null, null, List.of());

        mockMvc.perform(patch("/ordens-servico/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/{idOs}/{idFunc}/reprovar deve retornar 200")
    void shouldReturn200OnReprovar() throws Exception {
        var response = new OrdemServicoResponse(
                1L, 2L, "Maria", 3L, "Fiat Uno", 4L, "João",
                null, null, "REPROVADA",
                "Barulho", LocalDateTime.now(), null, null, LocalDateTime.now(), List.of()
        );

        when(reprovarOrdemServicoUseCase.execute(1L, 2L)).thenReturn(response);

        mockMvc.perform(patch("/ordens-servico/1/2/reprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPROVADA"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/{idOs}/{idMecanico}/iniciar com role USER deve retornar 403")
    void shouldReturn403OnIniciarDiagnosticoForUserRole() throws Exception {
        mockMvc.perform(patch("/ordens-servico/1/5/iniciar"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /ordens-servico/create sem autenticação deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        var request = new OrdemServicoRequest(3L, 4L, 2L, "Test");

        mockMvc.perform(post("/ordens-servico/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
