package com.example.beautyspot.annotations;

import com.example.beautyspot.validations.PasswordMatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {

	String message() default "Password do not match";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};

}
