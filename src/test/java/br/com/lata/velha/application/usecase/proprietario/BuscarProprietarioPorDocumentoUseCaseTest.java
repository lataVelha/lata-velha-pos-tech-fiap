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
class BuscarProprietarioPorDocumentoUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @Mock
    private ProprietarioAssembler assembler;

    @InjectMocks
    private BuscarProprietarioPorDocumentoUseCase useCase;

    @Test
    @DisplayName("deve buscar proprietário por documento")
    void shouldFindByDocumento() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        ProprietarioResponse response = mock(ProprietarioResponse.class);

        when(repository.findActiveByDocumento("52998224725")).thenReturn(domain);
        when(assembler.toResponse(domain)).thenReturn(response);

        ProprietarioResponse result = useCase.execute("529.982.247-25");

        assertNotNull(result);
        verify(repository).findActiveByDocumento("52998224725");
        verify(assembler).toResponse(domain);
    }

    @Test
    @DisplayName("deve limpar formatação do documento antes de buscar")
    void shouldCleanDocumentoBeforeSearch() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        ProprietarioResponse response = mock(ProprietarioResponse.class);

        when(repository.findActiveByDocumento("52998224725")).thenReturn(domain);
        when(assembler.toResponse(domain)).thenReturn(response);

        useCase.execute("52998224725");

        verify(repository).findActiveByDocumento("52998224725");
    }
}