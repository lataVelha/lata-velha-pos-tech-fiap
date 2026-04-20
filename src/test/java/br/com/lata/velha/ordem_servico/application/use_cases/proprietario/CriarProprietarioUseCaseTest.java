package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
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
        ProprietarioRequest request = new ProprietarioRequest(
                "João", "joao@email.com", "52998224725", "11999990001", null);
        Proprietario saved = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(repository.save(any())).thenReturn(saved);

        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("João");
        verify(repository).save(any());
        verify(notificarUseCase).execute(saved);
    }
}
