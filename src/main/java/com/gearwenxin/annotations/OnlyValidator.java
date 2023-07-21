package com.gearwenxin.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Ge Mingjia
 */
public class OnlyValidator implements ConstraintValidator<Only, Number> {

    private double value;

    @Override
    public void initialize(Only constraintAnnotation) {
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number number, ConstraintValidatorContext context) {
        if (number == null) {
            return true;
        }

        double doubleValue = number.doubleValue();
        return doubleValue != value;
    }
}
