package org.acs.idp.taskservice.security;

public final class CurrentUser {
    private static final ThreadLocal<String> EMAIL = new ThreadLocal<>();

    private CurrentUser() {}

    public static void set(String email) {
        EMAIL.set(email);
    }

    public static String getEmail() {
        return EMAIL.get();
    }

    public static void clear() {
        EMAIL.remove();
    }
}
