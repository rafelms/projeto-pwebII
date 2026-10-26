package org.example.projetoweb2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.projetoweb2.dto.FiltroConsulta;

/**
 * Implementação da regra da anotação {@link PeriodoValido}.
 *
 * O método isValid() é chamado automaticamente pelo Hibernate Validator
 * sempre que um objeto anotado com @PeriodoValido for validado (@Valid).
 */
public class PeriodoValidoValidator implements ConstraintValidator<PeriodoValido, FiltroConsulta> {

    /**
     * Retorna true quando o período é válido.
     * Se alguma das datas não foi informada, não há período para comparar,
     * então o filtro é considerado válido.
     *
     * Em caso de erro, a mensagem é associada ao campo "dataFim",
     * para que o Thymeleaf a exiba logo abaixo desse input.
     *
     * @param filtro  objeto de filtro a ser validado
     * @param context contexto da validação, usado para direcionar a mensagem ao campo
     * @return true se o período for válido, false caso contrário
     */
    @Override
    public boolean isValid(FiltroConsulta filtro, ConstraintValidatorContext context) {
        if (filtro == null || filtro.getDataInicio() == null || filtro.getDataFim() == null) {
            return true;
        }

        boolean valido = !filtro.getDataFim().isBefore(filtro.getDataInicio());

        if (!valido) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("dataFim")
                    .addConstraintViolation();
        }
        return valido;
    }
}
