package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarFuncionarioUseCaseTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private FuncionarioAssembler assembler;

    @InjectMocks
    private AtualizarFuncionarioUseCase useCase;

    @Test
    @DisplayName("Deve atualizar funcionario com sucesso")
    void deveAtualizarFuncionarioComSucesso() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", "novo.username", 2L);
        var cargoNovo = new Cargo(2L, "ATENDENTE", null);
        var funcionario = new Funcionario(1L, "Nome Antigo", "antigo", null, new Cargo(1L, "MECANICO", null), true);
        var response = new FuncionarioResponse(1L, "Novo Nome", "novo.username", true, "ATENDENTE");

        when(funcionarioRepository.findActiveById(1L)).thenReturn(funcionario);
        when(cargoRepository.findById(2L)).thenReturn(Optional.of(cargoNovo));
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(assembler.toResponse(funcionario)).thenReturn(response);

        var result = useCase.execute(1L, request);

        assertEquals("Novo Nome", funcionario.getNome());
        assertEquals("novo.username", funcionario.getUsername());
        assertEquals("ATENDENTE", funcionario.getCargo().getNome());
        assertEquals("Novo Nome", result.nome());
        verify(funcionarioRepository).save(funcionario);
    }

    @Test
    @DisplayName("Deve falhar quando cargo nao existir")
    void deveFalharQuandoCargoNaoExistir() {
        var request = new AtualizarFuncionarioRequest("Nome", "username", 99L);
        var funcionario = new Funcionario(1L, "Nome", "username", null, new Cargo(1L, "MECANICO", null), true);

        when(funcionarioRepository.findActiveById(1L)).thenReturn(funcionario);
        when(cargoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, request));
        verify(funcionarioRepository, never()).save(funcionario);
    }
}
