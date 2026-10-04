package com.app.BookMyShow.enums;

public enum ShowSeatStatus {
    AVAILABLE,
    HELD,
    BOOKED,
    BLOCKED;

    public static boolean contains(String test) {
        for (ShowSeatStatus status : ShowSeatStatus.values()) {
            if (status.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
