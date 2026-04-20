package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Documento;
import br.com.lata.velha.ordem_servico.domain.valueObjects.NumeroCelular;
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

    @InjectMocks
    private BuscarProprietarioPorDocumentoUseCase useCase;

    @Test
    @DisplayName("deve buscar proprietário por documento")
    void shouldFindByDocumento() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(repository.findActiveByDocumento("52998224725")).thenReturn(domain);

        ProprietarioResponse result = useCase.execute("529.982.247-25");

        assertNotNull(result);
        verify(repository).findActiveByDocumento("52998224725");
    }

    @Test
    @DisplayName("deve limpar formatação do documento antes de buscar")
    void shouldCleanDocumentoBeforeSearch() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(repository.findActiveByDocumento("52998224725")).thenReturn(domain);

        useCase.execute("52998224725");

        verify(repository).findActiveByDocumento("52998224725");
    }
}