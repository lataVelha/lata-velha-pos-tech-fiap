package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

        when(repository.getActiveById(1L)).thenReturn(servico);
        when(repository.save(servico)).thenReturn(servico);

        var result = useCase.execute(1L, request);

        assertThat(servico.getNome()).isEqualTo("Alinhamento 3D");
        assertThat(servico.getDescricao()).isEqualTo("Alinhamento eletrônico completo");
        assertThat(result.nome()).isEqualTo("Alinhamento 3D");
        verify(repository).save(servico);
    }

    @Test
    @DisplayName("Deve falhar quando serviço não existir")
    void deveFalharQuandoServicoNaoExistir() {
        var request = new AtualizarServicoRequest("Nome", "Descrição");
        when(repository.getActiveById(99L)).thenThrow(new IllegalArgumentException("Serviço não encontrado"));

        assertThatThrownBy(() -> useCase.execute(99L, request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());
    }
}
