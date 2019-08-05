package com.pruebanelson.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.springframework.stereotype.Component;

import com.pruebanelson.dto.ErrorResponse;
import com.pruebanelson.dto.Error;

@Component("errorResponse")
public class ErrorResponseHandler {

    @Handler
    public ErrorResponse handler(Exchange exchange) {
        Object exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        ErrorResponse errorResponse = new ErrorResponse();

        if (exception instanceof BeanValidationException) {
            BeanValidationException beanValidationException = (BeanValidationException) exception;
            Set<ConstraintViolation<Object>> listErrors = beanValidationException.getConstraintViolations();
            List<Error> errors = new ArrayList<>();

            Iterator<ConstraintViolation<Object>> listError = listErrors.iterator();
            while (listError.hasNext()) {
                ConstraintViolation<Object> constraint = listError.next();
                Error error = new Error();
                // Iteramos Sobre el conjunto de constrains violations y tomamos
                // los valores
                String message = constraint.getPropertyPath() + " - " + constraint.getMessage();
                // Construimos el error
                error.setError("Validation Error");
                error.setDescription(message);
                // Agregamos el error y su descripcion en la lista de estos
                errors.add(error);
            }
            errorResponse.setErrors(errors);
        } else if (exception instanceof NullPointerException) {
            List<Error> errors = new ArrayList<>();
            NullPointerException nullPointerException = (NullPointerException) exception;
            String Error = nullPointerException.getMessage();
            Throwable DescError = nullPointerException.getCause();
            Error error = new Error();
            error.setError(Error);
            error.setDescription(DescError.toString());
            errors.add(error);
            errorResponse.setErrors(errors);
        } else {
            Exception exc = (Exception) exception;
            Error error = new Error();
            error.setError(exception.getClass().getName());
            error.setDescription(exc.getMessage());
            errorResponse.addError(error);
        }

        return errorResponse;
    }
}
