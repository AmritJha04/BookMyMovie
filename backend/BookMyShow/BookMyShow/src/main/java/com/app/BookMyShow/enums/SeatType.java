package com.app.BookMyShow.enums;

public enum SeatType {
    PREMIUM ,
    CLASSIC,
    RECLINER;

    public static boolean contains(String test) {
        for (SeatType seat : SeatType.values()) {
            if (seat.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
