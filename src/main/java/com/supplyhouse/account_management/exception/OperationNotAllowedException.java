package com.supplyhouse.account_management.exception;

public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public OperationNotAllowedException(String errorMessage) {
        super(errorMessage);
    }
}
