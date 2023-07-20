package com.gearwenxin.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 校验两个数是否在规定值之间
 * includeMin为是否包含最小值
 *
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Documented
@Constraint(validatedBy = BetweenValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Between {
    String message() default "Value must be between {min} and {max}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    double min();
    double max();
    boolean includeMin() default false;
    boolean includeMax() default false;
}
