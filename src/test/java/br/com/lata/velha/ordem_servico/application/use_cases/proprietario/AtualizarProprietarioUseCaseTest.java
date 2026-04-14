package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.assemblers.ProprietarioAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
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
class AtualizarProprietarioUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @Mock
    private ProprietarioAssembler assembler;

    @InjectMocks
    private AtualizarProprietarioUseCase useCase;

    @Test
    @DisplayName("deve atualizar proprietário com sucesso")
    void shouldUpdateProprietario() {
        ProprietarioRequest request = mock(ProprietarioRequest.class);
        Proprietario existing = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        Proprietario saved = new Proprietario(1L, "Maria", "maria@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        ProprietarioResponse response = mock(ProprietarioResponse.class);

        when(repository.findActiveById(1L)).thenReturn(existing);
        doNothing().when(assembler).updateDomain(existing, request);
        when(repository.save(existing)).thenReturn(saved);
        when(assembler.toResponse(saved)).thenReturn(response);

        ProprietarioResponse result = useCase.execute(1L, request);

        assertNotNull(result);
        verify(repository).findActiveById(1L);
        verify(assembler).updateDomain(existing, request);
        verify(repository).save(existing);
        verify(assembler).toResponse(saved);
    }
}