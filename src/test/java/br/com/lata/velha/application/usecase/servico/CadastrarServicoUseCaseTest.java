package br.com.lata.velha.application.usecase.servico;

import br.com.lata.velha.application.assembler.ServicoAssembler;
import br.com.lata.velha.application.dto.request.CadastrarServicoRequest;
import br.com.lata.velha.application.dto.response.ServicoResponse;
import br.com.lata.velha.domain.entities.Servico;
import br.com.lata.velha.domain.repository.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarServicoUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @Mock
    private ServicoAssembler assembler;

    @InjectMocks
    private CadastrarServicoUseCase useCase;

    @Test
    @DisplayName("Deve cadastrar serviço com sucesso")
    void deveCadastrarServicoComSucesso() {
        var request = new CadastrarServicoRequest("Alinhamento", "Alinhamento completo");
        var domain = new Servico(null, "Alinhamento", "Alinhamento completo", true);
        var savedDomain = new Servico(10L, "Alinhamento", "Alinhamento completo", true);
        var response = new ServicoResponse(10L, "Alinhamento", "Alinhamento completo");

        when(assembler.toDomain(request)).thenReturn(domain);
        when(repository.save(domain)).thenReturn(savedDomain);
        when(assembler.toResponse(savedDomain)).thenReturn(response);

        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nome()).isEqualTo("Alinhamento");
        assertThat(result.descricao()).isEqualTo("Alinhamento completo");
        verify(repository).save(domain);
    }
}
