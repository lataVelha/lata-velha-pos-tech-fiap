package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        VeiculoRequest request = new VeiculoRequest(1L, "ABC1234", "Fiat", "Uno", 2020, "Prata");
        Proprietario proprietario = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        Veiculo saved = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");

        when(proprietarioRepository.getActiveById(1L)).thenReturn(proprietario);
        when(veiculoRepository.save(any())).thenReturn(saved);

        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.placa()).isEqualTo("ABC-1234");
        verify(proprietarioRepository).getActiveById(1L);
        verify(veiculoRepository).save(any());
    }
}
