package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
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
class CadastrarServicoUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private CadastrarServicoUseCase useCase;

    @Test
    @DisplayName("Deve cadastrar serviço com sucesso")
    void deveCadastrarServicoComSucesso() {
        var request = new CadastrarServicoRequest("Alinhamento", "Alinhamento completo");
        var savedDomain = new Servico(10L, "Alinhamento", "Alinhamento completo", true);

        when(repository.save(any())).thenReturn(savedDomain);

        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nome()).isEqualTo("Alinhamento");
        assertThat(result.descricao()).isEqualTo("Alinhamento completo");
        verify(repository).save(any());
    }
}
