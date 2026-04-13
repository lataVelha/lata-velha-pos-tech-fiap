package br.com.lata.velha.ordemDeServico.application.useCases.veiculo;

import br.com.lata.velha.ordemDeServico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Proprietario;
import br.com.lata.velha.ordemDeServico.domain.entities.Veiculo;
import br.com.lata.velha.ordemDeServico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Documento;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.NumeroCelular;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ProprietarioRepository proprietarioRepository;

    @Mock
    private VeiculoAssembler assembler;

    @InjectMocks
    private CriarVeiculoUseCase useCase;

    @Test
    @DisplayName("deve criar veículo com sucesso")
    void shouldCreateVeiculo() {
        VeiculoRequest request = mock(VeiculoRequest.class);
        Proprietario proprietario = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        Veiculo domain = new Veiculo(null, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        Veiculo saved = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        VeiculoResponse response = mock(VeiculoResponse.class);

        when(request.proprietarioId()).thenReturn(1L);
        when(proprietarioRepository.findActiveById(1L)).thenReturn(proprietario);
        when(assembler.toDomain(request)).thenReturn(domain);
        when(veiculoRepository.save(domain)).thenReturn(saved);
        when(assembler.toResponse(saved)).thenReturn(response);

        VeiculoResponse result = useCase.execute(request);

        assertNotNull(result);
        verify(proprietarioRepository).findActiveById(1L);
        verify(veiculoRepository).save(domain);
        verify(assembler).toResponse(saved);
    }
}