package com.example.beautyspot.annotations;


import com.example.beautyspot.validations.EmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
	String message() default "Invalid mail";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}
