package com.pruebanelson.dto;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private List<Error> errors;

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
    
    public void clean() {
        errors = null;
    }

    public void addError(Error error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }
    
    
}
