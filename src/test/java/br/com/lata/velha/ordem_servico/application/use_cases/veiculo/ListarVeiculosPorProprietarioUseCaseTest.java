package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarVeiculosPorProprietarioUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @Mock
    private VeiculoAssembler assembler;

    @InjectMocks
    private ListarVeiculosPorProprietarioUseCase useCase;

    @Test
    @DisplayName("deve listar veículos por proprietário")
    void shouldListByProprietario() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        VeiculoResponse response = mock(VeiculoResponse.class);

        when(repository.findActiveByProprietarioId(1L)).thenReturn(List.of(veiculo));
        when(assembler.toResponse(veiculo)).thenReturn(response);

        List<VeiculoResponse> result = useCase.execute(1L);

        assertEquals(1, result.size());
        verify(repository).findActiveByProprietarioId(1L);
        verify(assembler).toResponse(veiculo);
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não tem veículos")
    void shouldReturnEmptyList() {
        when(repository.findActiveByProprietarioId(99L)).thenReturn(List.of());

        List<VeiculoResponse> result = useCase.execute(99L);

        assertTrue(result.isEmpty());
        verify(repository).findActiveByProprietarioId(99L);
    }
}