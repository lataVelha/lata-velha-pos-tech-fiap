package br.com.lata.velha.application.usecase.servico;

import br.com.lata.velha.application.assembler.ServicoAssembler;
import br.com.lata.velha.application.dto.request.AtualizarServicoRequest;
import br.com.lata.velha.application.dto.response.ServicoResponse;
import br.com.lata.velha.domain.model.Servico;
import br.com.lata.velha.domain.repository.ServicoRepository;
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

    @Mock
    private ServicoAssembler assembler;

    @InjectMocks
    private AtualizarServicoUseCase useCase;

    @Test
    @DisplayName("Deve atualizar serviço com sucesso")
    void deveAtualizarServicoComSucesso() {
        var request = new AtualizarServicoRequest("Alinhamento 3D", "Alinhamento eletrônico completo");
        var servico = new Servico(1L, "Alinhamento", "Alinhamento comum", true);
        var response = new ServicoResponse(1L, "Alinhamento 3D", "Alinhamento eletrônico completo");

        when(repository.findActiveById(1L)).thenReturn(servico);
        when(repository.save(servico)).thenReturn(servico);
        when(assembler.toResponse(servico)).thenReturn(response);

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
