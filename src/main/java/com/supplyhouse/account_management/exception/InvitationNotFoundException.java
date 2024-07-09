package com.supplyhouse.account_management.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public InvitationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
