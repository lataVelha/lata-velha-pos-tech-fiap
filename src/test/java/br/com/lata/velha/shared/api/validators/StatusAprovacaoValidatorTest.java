package br.com.lata.velha.shared.api.validators;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusAprovacaoValidatorTest {

    private StatusAprovacaoValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new StatusAprovacaoValidator();
    }

    @Test
    @DisplayName("deve retornar true quando servico é null")
    void deveRetornarTrueQuandoServicoEhNull() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    @DisplayName("deve retornar true quando status é null")
    void deveRetornarTrueQuandoStatusEhNull() {
        var servico = new AprovarOrdemServicoRequest.Servico(1L, null);
        assertThat(validator.isValid(servico, context)).isTrue();
    }

    @Test
    @DisplayName("deve retornar true quando status é APROVADO")
    void deveRetornarTrueQuandoStatusEhAprovado() {
        var servico = new AprovarOrdemServicoRequest.Servico(1L, StatusExecucaoServico.APROVADO);
        assertThat(validator.isValid(servico, context)).isTrue();
    }

    @Test
    @DisplayName("deve retornar true quando status é RECUSADO")
    void deveRetornarTrueQuandoStatusEhRecusado() {
        var servico = new AprovarOrdemServicoRequest.Servico(1L, StatusExecucaoServico.RECUSADO);
        assertThat(validator.isValid(servico, context)).isTrue();
    }

    @Test
    @DisplayName("deve retornar false quando status é PENDENTE")
    void deveRetornarFalseQuandoStatusEhPendente() {
        var servico = new AprovarOrdemServicoRequest.Servico(5L, StatusExecucaoServico.PENDENTE);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        assertThat(validator.isValid(servico, context)).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(contains("5"));
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    @DisplayName("deve retornar false quando status é EM_EXECUCAO")
    void deveRetornarFalseQuandoStatusEhEmExecucao() {
        var servico = new AprovarOrdemServicoRequest.Servico(10L, StatusExecucaoServico.EM_EXECUCAO);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        assertThat(validator.isValid(servico, context)).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("deve retornar false quando status é FINALIZADO")
    void deveRetornarFalseQuandoStatusEhFinalizado() {
        var servico = new AprovarOrdemServicoRequest.Servico(2L, StatusExecucaoServico.FINALIZADO);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        assertThat(validator.isValid(servico, context)).isFalse();
    }

    @Test
    @DisplayName("deve retornar false quando status é AGUARDANDO_PECA")
    void deveRetornarFalseQuandoStatusEhAguardandoPeca() {
        var servico = new AprovarOrdemServicoRequest.Servico(3L, StatusExecucaoServico.AGUARDANDO_PECA);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        assertThat(validator.isValid(servico, context)).isFalse();
    }

    @Test
    @DisplayName("deve incluir servicoOsId na mensagem de violação")
    void deveIncluirServicoOsIdNaMensagemDeViolacao() {
        var servico = new AprovarOrdemServicoRequest.Servico(99L, StatusExecucaoServico.PENDENTE);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        validator.isValid(servico, context);

        verify(context).buildConstraintViolationWithTemplate(
                argThat(msg -> msg.contains("99") && msg.contains("APROVADO") && msg.contains("RECUSADO"))
        );
    }
}
