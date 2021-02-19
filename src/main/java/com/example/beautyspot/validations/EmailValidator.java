package com.example.beautyspot.validations;

import com.example.beautyspot.annotations.ValidEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

	public static final String EMAIL_PATTERN = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

	@Override
	public void initialize(ValidEmail constraintAnnotation) {

	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
		return validateEmail(email);
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
