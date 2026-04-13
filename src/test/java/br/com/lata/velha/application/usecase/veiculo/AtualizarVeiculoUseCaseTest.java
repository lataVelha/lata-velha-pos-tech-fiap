package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.request.VeiculoRequest;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.entities.Proprietario;
import br.com.lata.velha.domain.entities.Veiculo;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import br.com.lata.velha.domain.valueObject.Documento;
import br.com.lata.velha.domain.valueObject.NumeroCelular;
import br.com.lata.velha.domain.valueObject.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ProprietarioRepository proprietarioRepository;

    @Mock
    private VeiculoAssembler assembler;

    @InjectMocks
    private AtualizarVeiculoUseCase useCase;

    @Test
    @DisplayName("deve atualizar veículo com sucesso")
    void shouldUpdateVeiculo() {
        VeiculoRequest request = mock(VeiculoRequest.class);
        Veiculo existing = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        Veiculo saved = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Toyota", "Corolla", 2023, "Preto");
        Proprietario proprietario = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        VeiculoResponse response = mock(VeiculoResponse.class);

        when(request.proprietarioId()).thenReturn(1L);
        when(veiculoRepository.findActiveById(1L)).thenReturn(existing);
        when(proprietarioRepository.findActiveById(1L)).thenReturn(proprietario);
        doNothing().when(assembler).updateDomain(existing, request);
        when(veiculoRepository.save(existing)).thenReturn(saved);
        when(assembler.toResponse(saved)).thenReturn(response);

        VeiculoResponse result = useCase.execute(1L, request);

        assertNotNull(result);
        verify(veiculoRepository).findActiveById(1L);
        verify(proprietarioRepository).findActiveById(1L);
        verify(assembler).updateDomain(existing, request);
        verify(veiculoRepository).save(existing);
    }
}