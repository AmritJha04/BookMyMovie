package com.app.BookMyShow.enums;

public enum MovieCertificate {
    U ,
    A ,
    UA ;

    public static boolean contains(String test) {
        for (MovieCertificate certificate : MovieCertificate.values()) {
            if (certificate.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
