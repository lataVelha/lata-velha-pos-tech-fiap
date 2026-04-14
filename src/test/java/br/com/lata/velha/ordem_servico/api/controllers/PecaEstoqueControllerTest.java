package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordem_servico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.AjustarPecaEstoqueUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.BuscarPecaEstoqueUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.EntradaPecaEstoqueUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.SaidaPecaEstoqueUseCase;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PecaEstoqueController.class)
@Import(SecurityConfig.class)
class PecaEstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BuscarPecaEstoqueUseCase buscarPecaEstoqueUseCase;

    @MockBean
    private EntradaPecaEstoqueUseCase entradaPecaEstoqueUseCase;

    @MockBean
    private SaidaPecaEstoqueUseCase saidaPecaEstoqueUseCase;

    @MockBean
    private AjustarPecaEstoqueUseCase ajustarPecaEstoqueUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private PecaEstoqueResponse buildResponse(int quantidade) {
        return new PecaEstoqueResponse(1L, quantidade);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /pecas/{pecaId}/estoque deve retornar 200 com o estoque")
    void shouldReturn200OnBuscarEstoque() throws Exception {
        when(buscarPecaEstoqueUseCase.execute(1L)).thenReturn(buildResponse(50));

        mockMvc.perform(get("/pecas/1/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pecaId").value(1L))
                .andExpect(jsonPath("$.quantidadeArmazenada").value(50));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /pecas/{pecaId}/estoque/entrada deve retornar 200 com estoque atualizado")
    void shouldReturn200OnEntrada() throws Exception {
        var request = new MovimentarPecaEstoqueRequest(10);

        when(entradaPecaEstoqueUseCase.execute(eq(1L), any())).thenReturn(buildResponse(60));

        mockMvc.perform(post("/pecas/1/estoque/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeArmazenada").value(60));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /pecas/{pecaId}/estoque/entrada com quantidade inválida deve retornar 400")
    void shouldReturn400OnEntradaWithInvalidQuantidade() throws Exception {
        var invalid = new MovimentarPecaEstoqueRequest(0);

        mockMvc.perform(post("/pecas/1/estoque/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /pecas/{pecaId}/estoque/saida deve retornar 200 com estoque atualizado")
    void shouldReturn200OnSaida() throws Exception {
        var request = new MovimentarPecaEstoqueRequest(5);

        when(saidaPecaEstoqueUseCase.execute(eq(1L), any())).thenReturn(buildResponse(45));

        mockMvc.perform(post("/pecas/1/estoque/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeArmazenada").value(45));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /pecas/{pecaId}/estoque/saida com quantidade inválida deve retornar 400")
    void shouldReturn400OnSaidaWithInvalidQuantidade() throws Exception {
        var invalid = new MovimentarPecaEstoqueRequest(-1);

        mockMvc.perform(post("/pecas/1/estoque/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /pecas/{pecaId}/estoque/ajuste deve retornar 200 com estoque ajustado")
    void shouldReturn200OnAjuste() throws Exception {
        var request = new AjustarPecaEstoqueRequest(100);

        when(ajustarPecaEstoqueUseCase.execute(eq(1L), any())).thenReturn(buildResponse(100));

        mockMvc.perform(patch("/pecas/1/estoque/ajuste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeArmazenada").value(100));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /pecas/{pecaId}/estoque/ajuste com quantidade negativa deve retornar 400")
    void shouldReturn400OnAjusteWithNegativeQuantidade() throws Exception {
        var invalid = new AjustarPecaEstoqueRequest(-5);

        mockMvc.perform(patch("/pecas/1/estoque/ajuste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /pecas/{pecaId}/estoque sem autenticação deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/pecas/1/estoque"))
                .andExpect(status().isUnauthorized());
    }
}
