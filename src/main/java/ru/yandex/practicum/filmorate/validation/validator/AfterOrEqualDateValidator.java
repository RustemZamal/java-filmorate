package ru.yandex.practicum.filmorate.validation.validator;

import ru.yandex.practicum.filmorate.validation.annotation.AfterOrEqualDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AfterOrEqualDateValidator implements ConstraintValidator<AfterOrEqualDate, LocalDate> {

    private LocalDate date;
    @Override
    public void initialize(AfterOrEqualDate constraint) {
        date = LocalDate.parse(constraint.value(), DateTimeFormatter.ofPattern(constraint.pattern()));
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate != null && localDate.isAfter(date);
    }
}
