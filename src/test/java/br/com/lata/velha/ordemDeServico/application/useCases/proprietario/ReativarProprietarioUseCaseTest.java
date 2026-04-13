package br.com.lata.velha.ordemDeServico.application.useCases.proprietario;

import br.com.lata.velha.ordemDeServico.application.assemblers.ProprietarioAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Proprietario;
import br.com.lata.velha.ordemDeServico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Documento;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.NumeroCelular;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReativarProprietarioUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @Mock
    private ProprietarioAssembler assembler;

    @InjectMocks
    private ReativarProprietarioUseCase useCase;

    @Test
    @DisplayName("deve reativar proprietário inativo")
    void shouldReactivateProprietario() {
        Proprietario proprietario = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        proprietario.deactivate();
        ProprietarioResponse response = mock(ProprietarioResponse.class);

        when(repository.findInactiveById(1L)).thenReturn(proprietario);
        when(repository.save(proprietario)).thenReturn(proprietario);
        when(assembler.toResponse(proprietario)).thenReturn(response);

        ProprietarioResponse result = useCase.execute(1L);

        assertTrue(proprietario.isAtivo());
        assertNotNull(result);
        verify(repository).findInactiveById(1L);
        verify(repository).save(proprietario);
    }
}