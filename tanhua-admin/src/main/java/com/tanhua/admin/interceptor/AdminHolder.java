package com.tanhua.admin.interceptor;


import com.tanhua.model.domain.Admin;


/**
 * @Description:  通过ThreadLocal的形式，存储用户的数据
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
public class   AdminHolder {

    private static ThreadLocal<Admin> admins = new ThreadLocal<>();


    /**
     *  向当前线程存储数据
     * @param admin
     */
    public static void set(Admin admin) {
        admins.set(admin);
    }


    /**
     * 从当前线程获取数据
     * @return
     */
    public static Admin getAdmin() {
        return admins.get();
    }

    public static void remove() {
        admins.remove();
    }

    /**
     * 获取当前用户的id
     * @return
     */
    public static Long getUserId() {
        return admins.get().getId();
    }
}
