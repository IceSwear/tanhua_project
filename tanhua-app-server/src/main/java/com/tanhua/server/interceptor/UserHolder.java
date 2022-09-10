package com.tanhua.server.interceptor;

import com.tanhua.model.domain.User;

/**
 * @Description: threadlocal线程
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
public class UserHolder {

    private static ThreadLocal<User> tl = new ThreadLocal<>();


    public static void set(User user) {
        tl.set(user);
    }


    public static User get() {
        return tl.get();
    }


    public static Long getUserId() {
        return tl.get().getId();
    }


    public static String getMoblie() {
        return tl.get().getMobile();
    }


    public static void remove() {
        tl.remove();
    }
}
