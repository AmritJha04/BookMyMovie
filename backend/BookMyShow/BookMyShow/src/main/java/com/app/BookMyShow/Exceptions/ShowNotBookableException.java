package com.app.BookMyShow.Exceptions;

public class ShowNotBookableException extends RuntimeException {
    public ShowNotBookableException(String message) {
        super(message);
    }
}