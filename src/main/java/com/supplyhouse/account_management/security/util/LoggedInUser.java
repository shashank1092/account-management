package com.supplyhouse.account_management.security.util;

import com.supplyhouse.account_management.dto.ResponseAccountDto;

/**
 * Class to store user account details in Thread local to access logged in user when required
 */
public class LoggedInUser {
    private static final ThreadLocal<ResponseAccountDto> currentUser = new ThreadLocal<>();

    public static void set(ResponseAccountDto userDetails) {
        currentUser.set(userDetails);
    }

    public static ResponseAccountDto get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}

