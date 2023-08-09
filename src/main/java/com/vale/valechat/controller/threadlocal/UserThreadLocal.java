package com.vale.valechat.controller.threadlocal;

import com.vale.valechat.model.entity.User;

public class UserThreadLocal {

    private UserThreadLocal(){}

    /**
     * 保存用户对象的ThreadLocal
     */
    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();

    /**
     * 将当前用户存放进ThreadLocal里
     */
    public static void set(User simpleUser){
        System.out.println("Put user in ThreadLocal");
        LOCAL.set(simpleUser);
    }

    public static User get(){
        System.out.println("Get user from ThreadLocal");
        return LOCAL.get();
    }

    public static void remove(){
        System.out.println("Remove user from ThreadLocal");
        LOCAL.remove();
    }

}
