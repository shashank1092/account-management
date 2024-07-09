package com.supplyhouse.account_management.exception;

public class InvitationExpiredException extends RuntimeException {
    public InvitationExpiredException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public InvitationExpiredException(String errorMessage) {
        super(errorMessage);
    }
}
