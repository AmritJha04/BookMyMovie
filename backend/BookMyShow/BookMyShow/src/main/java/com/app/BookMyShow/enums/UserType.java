package com.app.BookMyShow.enums;

public enum UserType {
     CUSTOMER ,
     ADMIN;

    public static boolean contains(String test) {
        for (UserType user : UserType.values()) {
            if (user.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
