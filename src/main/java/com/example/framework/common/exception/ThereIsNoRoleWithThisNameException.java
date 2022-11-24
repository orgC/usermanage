package com.example.framework.common.exception;

public class ThereIsNoRoleWithThisNameException extends RuntimeException {
    public ThereIsNoRoleWithThisNameException(String message) {
        super(message);
    }

    public ThereIsNoRoleWithThisNameException(String message, Exception e) {
        super(message, e);
    }
}
