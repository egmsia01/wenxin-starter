package com.gearwenxin.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 校验两个被标记相同value数字的属性是否至少有一个为null
 *
 * @author Ge Mingjia
 */
@Documented
@Constraint(validatedBy = OnlyValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Only {
    String message() default "被标记相同 value 的属性至少有一个为 null !";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double value();
}
