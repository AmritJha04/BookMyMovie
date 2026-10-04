package com.app.BookMyShow.Exceptions;

public class ShowOverlapException extends RuntimeException {
    public ShowOverlapException(String message) {
        super(message);
    }
}