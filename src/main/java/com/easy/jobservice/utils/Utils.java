package com.easy.jobservice.utils;

import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.Cookie;

public class Utils {

    private static final int TOKEN_LENGTH = 30;

    public static String genToken(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String genToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

    public static Cookie genRootPathCookie(String name, String value, int timeToLive) {
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(timeToLive);
        return cookie;
    }

    public static boolean isValidLocationArray(Double lat, Double lon) {
        return lat != null && (lat >=-180.0 && lat <=180.0)
                && lon != null && (lon >=-90.0 && lon <=90.0);
    }

    public static String toLocationString(Double[] location) {
        if (location != null) {
            return location[0] + "," + location[1];
        }
        return null;
    }

    public static Double[] parseLocation(String location) {
        if (location != null) {
            String[] ar = location.split(",");
            if (ar.length == 2) {
                Double longitude = toDouble(ar[0].trim());
                Double latitude = toDouble(ar[1].trim());
                if (longitude != null && latitude != null) {
                    return new Double[] {longitude, latitude};
                }
            }
        }
        return null;
    }

    public static Double toDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ign) {}
        return null;
    }

}
