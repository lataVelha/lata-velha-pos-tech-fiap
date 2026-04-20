package br.com.lata.velha.shared.api.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatusAprovacaoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusAprovacaoValido {
    String message() default "Status deve ser APROVADO ou RECUSADO";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
