package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarPecaUseCaseTest {

    @Mock
    private PecaRepository repository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private PecaAssembler assembler;

    @InjectMocks
    private CadastrarPecaUseCase useCase;

    @Test
    @DisplayName("Deve cadastrar peça com sucesso")
    void deveCadastrarPecaComSucesso() {
        var request = new CadastrarPecaRequest("Pastilha", "Pastilha dianteira", new BigDecimal("150.00"));
        var domain = new Peca(null, "Pastilha", "Pastilha dianteira", new BigDecimal("150.00"), true);
        var savedDomain = new Peca(10L, "Pastilha", "Pastilha dianteira", new BigDecimal("150.00"), true);
        var response = new PecaResponse(10L, "Pastilha", "Pastilha dianteira", new BigDecimal("150.00"), true);

        when(assembler.toDomain(request)).thenReturn(domain);
        when(repository.save(domain)).thenReturn(savedDomain);
        when(assembler.toResponse(savedDomain)).thenReturn(response);

        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nome()).isEqualTo("Pastilha");
        assertThat(result.ativo()).isTrue();
        verify(repository).save(domain);
        verify(pecaEstoqueRepository).save(org.mockito.ArgumentMatchers.argThat(
            estoque -> estoque.getPecaId().equals(10L) && estoque.getQuantidadeArmazenada().equals(0)
        ));
    }
}
