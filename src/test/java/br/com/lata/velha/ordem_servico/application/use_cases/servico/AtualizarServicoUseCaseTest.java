package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarServicoUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private AtualizarServicoUseCase useCase;

    @Test
    @DisplayName("Deve atualizar serviço com sucesso")
    void deveAtualizarServicoComSucesso() {
        var request = new AtualizarServicoRequest("Alinhamento 3D", "Alinhamento eletrônico completo");
        var servico = new Servico(1L, "Alinhamento", "Alinhamento comum", true);

        when(repository.findActiveById(1L)).thenReturn(servico);
        when(repository.save(servico)).thenReturn(servico);

        var result = useCase.execute(1L, request);

        assertEquals("Alinhamento 3D", servico.getNome());
        assertEquals("Alinhamento eletrônico completo", servico.getDescricao());
        assertEquals("Alinhamento 3D", result.nome());
        verify(repository).save(servico);
    }

    @Test
    @DisplayName("Deve falhar quando serviço não existir")
    void deveFalharQuandoServicoNaoExistir() {
        var request = new AtualizarServicoRequest("Nome", "Descrição");
        when(repository.findActiveById(99L)).thenThrow(new IllegalArgumentException("Serviço não encontrado"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L, request));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
