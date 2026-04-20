package br.com.lata.velha.shared.api.validators;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusAprovacaoValidator implements ConstraintValidator<StatusAprovacaoValido, AprovarOrdemServicoRequest.Servico> {
    @Override
    public boolean isValid(AprovarOrdemServicoRequest.Servico servico, ConstraintValidatorContext context) {
        if (servico == null || servico.status() == null) return true;
        if (servico.status() == StatusExecucaoServico.APROVADO || servico.status() == StatusExecucaoServico.RECUSADO) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Status inválido para o serviço id " + servico.servicoOsId() + ": deve ser APROVADO ou RECUSADO"
        ).addConstraintViolation();
        return false;
    }
}
