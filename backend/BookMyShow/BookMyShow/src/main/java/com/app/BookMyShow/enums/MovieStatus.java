package com.app.BookMyShow.enums;

public enum MovieStatus {
    COMING_SOON ,
    NOW_SHOWING;

    public static boolean contains(String test) {
        for (MovieStatus status : MovieStatus.values()) {
            if (status.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
