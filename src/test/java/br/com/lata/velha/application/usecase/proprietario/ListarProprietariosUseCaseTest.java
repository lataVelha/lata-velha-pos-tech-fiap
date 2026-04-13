package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.domain.valueObject.Documento;
import br.com.lata.velha.domain.valueObject.NumeroCelular;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarProprietariosUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @Mock
    private ProprietarioAssembler proprietarioAssembler;

    @Mock
    private PaginatedAssembler paginatedAssembler;

    @InjectMocks
    private ListarProprietariosUseCase useCase;

    @Test
    @DisplayName("deve listar proprietários paginado")
    void shouldListPaginated() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        PaginatedResult<Proprietario> paginatedResult = new PaginatedResult<>(
                List.of(domain), 0, 10, 1, 1);
        PaginatedResult<ProprietarioResponse> paginatedResponse =
                new PaginatedResult<>(
                        List.of(mock(ProprietarioResponse.class)),
                        0,
                        10,
                        1,
                        1
                );

        when(repository.findAllActivePaginated(0, 10)).thenReturn(paginatedResult);
        when(paginatedAssembler.toResponse(eq(paginatedResult), any(Function.class)))
                .thenReturn(paginatedResponse);

        PaginatedResult<ProprietarioResponse> result = useCase.execute(0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(repository).findAllActivePaginated(0, 10);
    }
}