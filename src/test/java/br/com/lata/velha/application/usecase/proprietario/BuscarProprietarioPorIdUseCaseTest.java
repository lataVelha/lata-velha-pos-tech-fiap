package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.entities.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.domain.valueObject.Documento;
import br.com.lata.velha.domain.valueObject.NumeroCelular;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarProprietarioPorIdUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @Mock
    private ProprietarioAssembler assembler;

    @InjectMocks
    private BuscarProprietarioPorIdUseCase useCase;

    @Test
    @DisplayName("deve buscar proprietário por id")
    void shouldFindById() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        ProprietarioResponse response = mock(ProprietarioResponse.class);

        when(repository.findActiveById(1L)).thenReturn(domain);
        when(assembler.toResponse(domain)).thenReturn(response);

        ProprietarioResponse result = useCase.execute(1L);

        assertNotNull(result);
        verify(repository).findActiveById(1L);
        verify(assembler).toResponse(domain);
    }
}