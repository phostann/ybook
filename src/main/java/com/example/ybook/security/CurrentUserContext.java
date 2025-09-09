package com.example.ybook.security;

/**
 * 使用 ThreadLocal 保存当前请求的用户信息。
 */
public final class CurrentUserContext {
    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    private CurrentUserContext() {}

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        CurrentUser u = HOLDER.get();
        return u == null ? null : u.getId();
    }

    public static String getUsername() {
        CurrentUser u = HOLDER.get();
        return u == null ? null : u.getUsername();
    }

    public static Long requireUserId() {
        Long id = getUserId();
        if (id == null) {
            throw new IllegalStateException("No authenticated user in CurrentUserContext");
        }
        return id;
    }

    public static void clear() {
        HOLDER.remove();
    }
}

