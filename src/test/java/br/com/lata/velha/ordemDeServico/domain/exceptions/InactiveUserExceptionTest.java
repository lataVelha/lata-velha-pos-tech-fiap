package br.com.lata.velha.ordemDeServico.domain.exceptions;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.ordemDeServico.domain.entities.Cargo;
import br.com.lata.velha.ordemDeServico.domain.entities.Funcionario;
import br.com.lata.velha.shared.domain.exceptions.DomainException;
import br.com.lata.velha.shared.domain.valueObjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InactiveUserExceptionTest {

    @Test
    @DisplayName("fromFuncionario deve criar exceção com mensagem não nula")
    void shouldCreateExceptionFromFuncionario() {
        Funcionario funcionario = new Funcionario(1L, "Fulano", new Cargo(1L, "MECANICO", null), UserId.random());

        InactiveUserException ex = InactiveUserException.fromFuncionario(funcionario);

        assertNotNull(ex);
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isBlank());
    }

    @Test
    @DisplayName("mensagem deve referenciar o tipo do funcionário")
    void messageShouldReferenceClass() {
        Funcionario funcionario = new Funcionario(1L, "Fulano", new Cargo(1L, "MECANICO", null), UserId.random());

        InactiveUserException ex = InactiveUserException.fromFuncionario(funcionario);

        assertTrue(ex.getMessage().contains("Funcionario"));
    }

    @Test
    @DisplayName("deve ser subclasse de DomainException")
    void shouldBeDomainException() {
        Funcionario funcionario = new Funcionario(1L, "Fulano", new Cargo(1L, "MECANICO", null), UserId.random());

        InactiveUserException ex = InactiveUserException.fromFuncionario(funcionario);

        assertInstanceOf(DomainException.class, ex);
    }
}
