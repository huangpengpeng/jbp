package com.jbp.common.yop.utils;

import lombok.Getter;
import lombok.Setter;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BeanValidator {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Getter
    @Setter
    public static class ValidationResult {
        private boolean hasError;
        private Map<String, String> errors;
    }

    public static <T> ValidationResult validateObject(T obj) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && set.size() != 0) {
            result.setHasError(true);
            Map<String, String> errorMsg = new HashMap<String, String>();
            for (ConstraintViolation<T> cv : set) {
                errorMsg.put(cv.getPropertyPath().toString(), cv.getMessage());
            }
            result.setErrors(errorMsg);
        }
        return result;
    }


}
