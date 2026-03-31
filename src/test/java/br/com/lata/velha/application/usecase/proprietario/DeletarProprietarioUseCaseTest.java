package br.com.lata.velha.application.usecase.proprietario;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletarProprietarioUseCaseTest {

    @Mock
    private ProprietarioRepository repository;

    @InjectMocks
    private DeletarProprietarioUseCase useCase;

    @Test
    @DisplayName("deve desativar proprietário (soft delete)")
    void shouldDeactivateProprietario() {
        Proprietario proprietario = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(repository.findActiveById(1L)).thenReturn(proprietario);
        when(repository.save(proprietario)).thenReturn(proprietario);

        useCase.execute(1L);

        assertFalse(proprietario.isAtivo());
        verify(repository).findActiveById(1L);
        verify(repository).save(proprietario);
    }
}