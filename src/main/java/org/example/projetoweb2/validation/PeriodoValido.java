package org.example.projetoweb2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de validação customizada (Bean Validation) aplicada em nível de classe.
 *
 * Verifica se a data final de um período não é anterior à data inicial.
 * Como a regra envolve DOIS campos ao mesmo tempo, ela não pode ser feita
 * com anotações de campo (@NotNull, @Size...), por isso é aplicada na classe.
 *
 * A lógica da validação fica em {@link PeriodoValidoValidator}, ligada
 * a esta anotação através de @Constraint(validatedBy = ...).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PeriodoValidoValidator.class)
public @interface PeriodoValido {

    /** Mensagem exibida quando o período é inválido. */
    String message() default "A data final não pode ser anterior à data inicial.";

    /** Grupos de validação (obrigatório pela especificação Bean Validation). */
    Class<?>[] groups() default {};

    /** Metadados extras (obrigatório pela especificação Bean Validation). */
    Class<? extends Payload>[] payload() default {};
}
