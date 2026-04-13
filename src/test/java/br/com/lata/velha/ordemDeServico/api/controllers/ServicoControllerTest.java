package br.com.lata.velha.ordemDeServico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordemDeServico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.servico.*;
import br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions.ServicoNotFoundException;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServicoController.class)
@Import(SecurityConfig.class)
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CadastrarServicoUseCase cadastrarUseCase;

    @MockBean
    private BuscarServicosUseCase buscarTodosUseCase;

    @MockBean
    private BuscarServicoPorIdUseCase buscarPorIdUseCase;

    @MockBean
    private AtualizarServicoUseCase atualizarUseCase;

    @MockBean
    private DesativarServicoUseCase desativarUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private ServicoResponse buildResponse() {
        return new ServicoResponse(1L, "Troca de Óleo", "Troca de óleo e filtro");
    }

    private CadastrarServicoRequest buildRequest() {
        return new CadastrarServicoRequest("Troca de Óleo", "Troca de óleo e filtro");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /servicos deve retornar 201 e o serviço criado")
    void shouldReturn201OnCreate() throws Exception {
        when(cadastrarUseCase.execute(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Troca de Óleo"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /servicos com body inválido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new CadastrarServicoRequest("", "");

        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /servicos deve retornar 200 com lista paginada")
    void shouldReturn200OnList() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildResponse()), 0, 10, 1L, 1);

        when(buscarTodosUseCase.execute(0, 10)).thenReturn(paginated);

        mockMvc.perform(get("/servicos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /servicos/{id} deve retornar 200 com o serviço")
    void shouldReturn200OnFindById() throws Exception {
        when(buscarPorIdUseCase.execute(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Troca de Óleo"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /servicos/{id} deve retornar 404 quando não encontrado")
    void shouldReturn404WhenServicoNotFound() throws Exception {
        when(buscarPorIdUseCase.execute(99L))
                .thenThrow(ServicoNotFoundException.fromId(99L));

        mockMvc.perform(get("/servicos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /servicos/{id} deve retornar 200 com o serviço atualizado")
    void shouldReturn200OnUpdate() throws Exception {
        var request = new AtualizarServicoRequest("Troca de Óleo Completa", "Inclui filtro e verificação");
        var updated = new ServicoResponse(1L, "Troca de Óleo Completa", "Inclui filtro e verificação");

        when(atualizarUseCase.execute(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/servicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Troca de Óleo Completa"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /servicos/{id} com body inválido deve retornar 400")
    void shouldReturn400OnInvalidUpdateRequest() throws Exception {
        var invalid = new AtualizarServicoRequest("", "");

        mockMvc.perform(put("/servicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /servicos/{id}/desativar deve retornar 204")
    void shouldReturn204OnDesativar() throws Exception {
        doNothing().when(desativarUseCase).execute(1L);

        mockMvc.perform(patch("/servicos/1/desativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /servicos/{id}/desativar deve retornar 404 quando não encontrado")
    void shouldReturn404OnDesativarWhenNotFound() throws Exception {
        doThrow(ServicoNotFoundException.fromId(99L)).when(desativarUseCase).execute(99L);

        mockMvc.perform(patch("/servicos/99/desativar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /servicos sem autenticação deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/servicos"))
                .andExpect(status().isUnauthorized());
    }
}
