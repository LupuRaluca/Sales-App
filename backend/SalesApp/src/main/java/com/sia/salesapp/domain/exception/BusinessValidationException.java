package com.sia.salesapp.domain.exception;

public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) { super(message); }
}
