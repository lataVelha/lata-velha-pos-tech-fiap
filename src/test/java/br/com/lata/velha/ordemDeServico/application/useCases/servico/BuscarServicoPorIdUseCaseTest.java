package br.com.lata.velha.ordemDeServico.application.useCases.servico;

import br.com.lata.velha.ordemDeServico.application.assemblers.ServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Servico;
import br.com.lata.velha.ordemDeServico.domain.repositories.ServicoRepository;
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

    @Mock
    private ServicoAssembler assembler;

    @InjectMocks
    private BuscarServicoPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar serviço ativo por ID")
    void deveBuscarServicoAtivoPorId() {
        var servico = new Servico(1L, "Alinhamento", "Alinhamento completo", true);
        var response = new ServicoResponse(1L, "Alinhamento", "Alinhamento completo");

        when(repository.findActiveById(1L)).thenReturn(servico);
        when(assembler.toResponse(servico)).thenReturn(response);

        var result = useCase.execute(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("Alinhamento");
        verify(repository).findActiveById(1L);
    }
}
