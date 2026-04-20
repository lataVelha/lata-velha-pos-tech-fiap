package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarServicoPorIdUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private BuscarServicoPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar serviço ativo por ID")
    void deveBuscarServicoAtivoPorId() {
        var servico = new Servico(1L, "Alinhamento", "Alinhamento completo", true);

        when(repository.findActiveById(1L)).thenReturn(servico);

        var result = useCase.execute(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("Alinhamento");
        verify(repository).findActiveById(1L);
    }
}
