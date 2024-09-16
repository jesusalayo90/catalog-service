package com.mservices.catalog.controller.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BindingResultUtil {

    public static String formatErrors(BindingResult result) {
        List<String> message = result.getFieldErrors().stream()
                .map(f -> String.format("Field '%s' %s.", f.getField(), formatErrorArguments(f.getDefaultMessage(), f))).collect(Collectors.toList());
        return message.toString();
    }

    private static String formatErrorArguments(String defaultMessage, FieldError fieldError) {
        List<String> args = null;

        if (fieldError != null && fieldError.getArguments() != null && fieldError.getArguments().length > 0) {
            for (int i = 0; i < fieldError.getArguments().length; i++) {
                if ("ResolvableAttribute".equals(fieldError.getArguments()[i].getClass().getSimpleName())) {
                    if (args == null) {
                        args = new ArrayList<>();
                    }
                    args.add(fieldError.getArguments()[i].toString());
                }
            }
        }
        return args != null? MessageFormat.format(defaultMessage, args.toArray(String[]::new)) : defaultMessage;
    }
}
