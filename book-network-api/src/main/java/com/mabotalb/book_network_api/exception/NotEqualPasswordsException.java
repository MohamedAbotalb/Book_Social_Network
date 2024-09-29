package com.mabotalb.book_network_api.exception;

public class NotEqualPasswordsException extends RuntimeException {
    public NotEqualPasswordsException(String message) {
        super(message);
    }
}
