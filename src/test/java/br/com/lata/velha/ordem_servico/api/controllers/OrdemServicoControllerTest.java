package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.CriarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.AddServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.ServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.*;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoController.class)
@Import(SecurityConfig.class)
class OrdemServicoControllerTest {

    private static final String TEST_USER_ID = "00000000-0000-0000-0000-000000000001";

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
        private BuscarTempoMedioExecucaoServicosFinalizadosUseCase buscarTempoMedioExecucaoServicosFinalizadosUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private OrdemServicoResponse buildOrdemResponse() {
        return new OrdemServicoResponse(
                1L, "RECEBIDA", "Barulho ao frear",
                new FuncionarioResumoResponse(2L, "Maria Atendente"),
                null,
                new ProprietarioResumoResponse(4L, "João Proprietário"),
                new VeiculoResumoResponse(3L, "Fiat Uno 2020"),
                LocalDateTime.now(), null, null, LocalDateTime.now(),
                List.of(), null
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /ordens-servico deve retornar 201 com a ordem criada")
    void shouldReturn201OnCreate() throws Exception {
        var request = new CriarOrdemServicoRequest(3L, 4L, "Barulho ao frear");

        when(criarOrdemServicoUseCase.execute(any())).thenReturn(buildOrdemResponse());

        mockMvc.perform(post("/ordens-servico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /ordens-servico com body inválido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new CriarOrdemServicoRequest(null, null, "x".repeat(501));

        mockMvc.perform(post("/ordens-servico")
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /ordens-servico/metricas/tempo-medio-execucao deve retornar 200 para ADMIN")
    void shouldReturn200OnGetAverageExecutionTimeForAdmin() throws Exception {
        var response = new TempoMedioExecucaoResponse(
                List.of(new TempoMedioExecucaoServicoItemResponse(5L, "Troca freio", new BigDecimal("32.50"))),
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-31"),
                "America/Sao_Paulo"
        );

        when(buscarTempoMedioExecucaoServicosFinalizadosUseCase.execute(any(), any())).thenReturn(response);

        mockMvc.perform(get("/ordens-servico/metricas/tempo-medio-execucao")
                        .param("dataInicio", "01/01/2026")
                        .param("dataFim", "31/01/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicos[0].servicoId").value(5))
                .andExpect(jsonPath("$.servicos[0].servicoNome").value("Troca freio"))
                .andExpect(jsonPath("$.servicos[0].tempoMedioMinutos").value(32.5));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /ordens-servico/metricas/tempo-medio-execucao deve retornar 403 para USER")
    void shouldReturn403OnGetAverageExecutionTimeForUser() throws Exception {
        mockMvc.perform(get("/ordens-servico/metricas/tempo-medio-execucao"))
                .andExpect(status().isForbidden());
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
                1L, "EM_DIAGNOSTICO", "Barulho",
                new FuncionarioResumoResponse(2L, "Maria"),
                new FuncionarioResumoResponse(5L, "Carlos Mecânico"),
                new ProprietarioResumoResponse(4L, "João"),
                new VeiculoResumoResponse(3L, "Fiat Uno"),
                LocalDateTime.now(), null, null, LocalDateTime.now(),
                List.of(), null
        );
        mockMvc.perform(patch("/ordens-servico/1/iniciar-diagnostico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_MECANICO"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @DisplayName("PATCH /ordens-servico/{idOs}/adicionar-servico deve retornar 200")
    void shouldReturn200OnAdicionarServico() throws Exception {
        var servicoRequest = new ServicoRequest(10L, List.of(), new BigDecimal("150.00"));
        var request = new AddServicoRequest(List.of(servicoRequest));

        doNothing().when(adicionarServicoUseCase).execute(any());

        mockMvc.perform(patch("/ordens-servico/1/adicionar-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @DisplayName("PATCH /ordens-servico/{idOs}/adicionar-servico com lista vazia deve retornar 400")
    void shouldReturn400OnAdicionarServicoWithInvalidRequest() throws Exception {
        var invalid = new AddServicoRequest(List.of());

        mockMvc.perform(patch("/ordens-servico/1/adicionar-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @DisplayName("PATCH /ordens-servico/{idOs}/{idFunc}/finalizar-diagnostico deve retornar 200")
    void shouldReturn200OnFinalizarDiagnostico() throws Exception {
        doNothing().when(finalizarDiagnosticoUseCase).execute(any());

        mockMvc.perform(patch("/ordens-servico/1/finalizar-diagnostico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_MECANICO"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/{idOs}/aprovar deve retornar 200")
    void shouldReturn200OnAprovar() throws Exception {
        var servico = new AprovarOrdemServicoRequest.Servico(10L, "APROVADO");
        var request = new AprovarOrdemServicoRequest(List.of(servico));
        var output = new AprovarOrdemServicoUseCase.Output(1L, "EM_EXECUCAO",
                List.of(new AprovarOrdemServicoUseCase.Output.Servico(10L, "APROVADO")), null);

        when(aprovarOrdemServicoUseCase.execute(any())).thenReturn(output);

        mockMvc.perform(patch("/ordens-servico/1/aprovar")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOs").value(1L))
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/{idOs}/aprovar com lista vazia deve retornar 400")
    void shouldReturn400OnAprovarWithInvalidRequest() throws Exception {
        var invalid = new AprovarOrdemServicoRequest(List.of());

        mockMvc.perform(patch("/ordens-servico/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/{idOs}/{idFunc}/reprovar deve retornar 200")
    void shouldReturn200OnReprovar() throws Exception {
        doNothing().when(reprovarOrdemServicoUseCase).execute(any());

        mockMvc.perform(patch("/ordens-servico/1/reprovar")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /ordens-servico/{idOs}/{idMecanico}/iniciar com role USER deve retornar 403")
    void shouldReturn403OnIniciarDiagnosticoForUserRole() throws Exception {
        mockMvc.perform(patch("/ordens-servico/1/iniciar-diagnostico"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /ordens-servico/create sem autenticação deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        var request = new CriarOrdemServicoRequest(3L, 4L, "Test");

        mockMvc.perform(post("/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
