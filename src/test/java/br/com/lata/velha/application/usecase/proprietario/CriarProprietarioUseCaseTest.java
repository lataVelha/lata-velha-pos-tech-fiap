package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.application.dto.request.ProprietarioRequest;
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
class CriarProprietarioUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @Mock
    private ProprietarioAssembler assembler;

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
        ProprietarioResponse response = mock(ProprietarioResponse.class);

        when(assembler.toDomain(request)).thenReturn(domain);
        when(repository.save(domain)).thenReturn(saved);
        when(assembler.toResponse(saved)).thenReturn(response);

        ProprietarioResponse result = useCase.execute(request);

        assertNotNull(result);
        verify(assembler).toDomain(request);
        verify(repository).save(domain);
        verify(assembler).toResponse(saved);
    }
}