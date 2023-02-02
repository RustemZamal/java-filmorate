package ru.yandex.practicum.filmorate.validation.annotation;

import ru.yandex.practicum.filmorate.validation.validator.AfterOrEqualDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterOrEqualDateValidator.class)
public @interface AfterOrEqualDate {
    String message() default "{AfterOrEqualDateValidator: invalid date}";

    String value();

    String pattern() default "yyyy-MM-dd";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
