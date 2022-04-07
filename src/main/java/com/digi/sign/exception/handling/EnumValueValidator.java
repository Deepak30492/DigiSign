package com.digi.sign.exception.handling;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, CharSequence> {

	private List<String> acceptedValues;

	@Override
	public void initialize(EnumValue annotation) {
		acceptedValues = Stream.of(annotation.type().getEnumConstants()).map(Enum::name)
				.collect(Collectors.toList());
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		return acceptedValues.contains(value.toString());
	}
}
