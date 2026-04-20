package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Documento;
import br.com.lata.velha.ordem_servico.domain.valueObjects.NumeroCelular;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ProprietarioRepository proprietarioRepository;

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

        when(request.proprietarioId()).thenReturn(1L);
        when(request.toDomain()).thenReturn(domain);
        when(proprietarioRepository.getActiveById(1L)).thenReturn(proprietario);
        when(veiculoRepository.save(domain)).thenReturn(saved);

        VeiculoResponse result = useCase.execute(request);

        assertNotNull(result);
        verify(proprietarioRepository).getActiveById(1L);
        verify(veiculoRepository).save(domain);
        verify(request).toDomain();
    }
}