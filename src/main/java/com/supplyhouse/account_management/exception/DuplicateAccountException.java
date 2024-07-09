package com.supplyhouse.account_management.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public DuplicateAccountException(String errorMessage) {
        super(errorMessage);
    }
}
