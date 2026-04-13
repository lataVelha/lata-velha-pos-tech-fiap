package br.com.lata.velha.ordemDeServico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordemDeServico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.AtualizarFuncionarioUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.BuscarFuncionarioPorIdUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.CadastrarFuncionarioUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.DesativarFuncionarioUseCase;
import br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions.FuncionarioNotFoundException;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
@Import(SecurityConfig.class)
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CadastrarFuncionarioUseCase cadastrarUseCase;

    @MockBean
    private BuscarFuncionarioPorIdUseCase buscarPorIdUseCase;

    @MockBean
    private AtualizarFuncionarioUseCase atualizarUseCase;

    @MockBean
    private DesativarFuncionarioUseCase desativarUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private FuncionarioResponse buildResponse() {
        return new FuncionarioResponse(1L, "Carlos Técnico", "MECANICO");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /funcionarios deve retornar 201 e o funcionário criado")
    void shouldReturn201OnCreate() throws Exception {
        var request = new CadastrarFuncionarioRequest("Carlos Técnico", "carlos@example.com", "Senha1@!", 1L);

        when(cadastrarUseCase.execute(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Carlos Técnico"))
                .andExpect(jsonPath("$.cargo").value("MECANICO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /funcionarios com body inválido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new CadastrarFuncionarioRequest("", "", "", null);

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /funcionarios com username duplicado deve retornar 409")
    void shouldReturn409OnDuplicateUsername() throws Exception {
        var request = new CadastrarFuncionarioRequest("Carlos", "duplicado@example.com", "Senha1@!", 1L);

        when(cadastrarUseCase.execute(any()))
                .thenThrow(new ResourceAlreadyExistsException("Username já cadastrado"));

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /funcionarios/{id} deve retornar 200 com o funcionário")
    void shouldReturn200OnFindById() throws Exception {
        when(buscarPorIdUseCase.execute(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/funcionarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Carlos Técnico"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /funcionarios/{id} deve retornar 400 quando não encontrado")
    void shouldReturn400WhenFuncionarioNotFound() throws Exception {
        when(buscarPorIdUseCase.execute(99L))
                .thenThrow(FuncionarioNotFoundException.fromId(99L));

        mockMvc.perform(get("/funcionarios/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /funcionarios/{id} deve retornar 200 com o funcionário atualizado")
    void shouldReturn200OnUpdate() throws Exception {
        var request = new AtualizarFuncionarioRequest("Carlos Atualizado", 2L);
        var updated = new FuncionarioResponse(1L, "Carlos Atualizado", "ADMIN");

        when(atualizarUseCase.execute(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/funcionarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /funcionarios/{id} com body inválido deve retornar 400")
    void shouldReturn400OnInvalidUpdateRequest() throws Exception {
        var invalid = new AtualizarFuncionarioRequest("", null);

        mockMvc.perform(put("/funcionarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /funcionarios/{id}/desativar deve retornar 204")
    void shouldReturn204OnDesativar() throws Exception {
        doNothing().when(desativarUseCase).execute(1L);

        mockMvc.perform(patch("/funcionarios/1/desativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /funcionarios/{id}/desativar deve retornar 400 quando não encontrado")
    void shouldReturn400OnDesativarWhenNotFound() throws Exception {
        doThrow(FuncionarioNotFoundException.fromId(99L)).when(desativarUseCase).execute(99L);

        mockMvc.perform(patch("/funcionarios/99/desativar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /funcionarios com role USER deve retornar 403")
    void shouldReturn403ForUserRole() throws Exception {
        var request = new CadastrarFuncionarioRequest("Carlos", "carlos@example.com", "Senha1@!", 1L);

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /funcionarios sem autenticação deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/funcionarios/1"))
                .andExpect(status().isUnauthorized());
    }
}
