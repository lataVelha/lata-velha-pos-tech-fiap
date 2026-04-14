package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesativarServicoUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private DesativarServicoUseCase useCase;

    @Test
    @DisplayName("Deve desativar serviço ativo com sucesso")
    void deveDesativarServicoAtivoComSucesso() {
        var servico = new Servico(1L, "Balanceamento", "Balanceamento das rodas", true);
        when(repository.findActiveById(1L)).thenReturn(servico);

        useCase.execute(1L);

        assertFalse(servico.isAtivo());
        verify(repository).save(servico);
    }

    @Test
    @DisplayName("Deve falhar quando serviço já estiver desativado")
    void deveFalharQuandoServicoJaEstiverDesativado() {
        var servico = new Servico(1L, "Balanceamento", "Balanceamento das rodas", false);
        when(repository.findActiveById(1L)).thenReturn(servico);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L));
        verify(repository, never()).save(servico);
    }
}
