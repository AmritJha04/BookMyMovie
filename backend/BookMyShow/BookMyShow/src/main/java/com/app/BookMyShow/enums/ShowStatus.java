package com.app.BookMyShow.enums;

public enum ShowStatus {
    SCHEDULED ,
    CANCELLED;

    public static boolean contains(String test) {
        for (ShowStatus status : ShowStatus.values()) {
            if (status.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
