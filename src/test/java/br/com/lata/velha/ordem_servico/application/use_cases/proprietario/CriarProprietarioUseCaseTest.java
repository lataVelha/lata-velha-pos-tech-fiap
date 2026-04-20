package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
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
class CriarProprietarioUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @Mock
    private NotificarCadastroProprietarioUseCase notificarUseCase;

    @InjectMocks
    private CriarProprietarioUseCase useCase;

    @Test
    @DisplayName("deve criar proprietário com sucesso")
    void shouldCreateProprietario() {
        ProprietarioRequest request = mock(ProprietarioRequest.class);
        Proprietario domain = new Proprietario(null, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        Proprietario saved = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(request.toDomain()).thenReturn(domain);
        when(repository.save(domain)).thenReturn(saved);

        ProprietarioResponse result = useCase.execute(request);

        assertNotNull(result);
        verify(request).toDomain();
        verify(repository).save(domain);
        verify(notificarUseCase).execute(saved);
    }
}