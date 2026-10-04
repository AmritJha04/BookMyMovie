package com.app.BookMyShow.enums;

public enum HoldStatus {
    ACTIVE,
    EXPIRED,
    CONFIRMED,
    RELEASED;

    public static boolean contains(String test) {
        for (HoldStatus status : HoldStatus.values()) {
            if (status.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
