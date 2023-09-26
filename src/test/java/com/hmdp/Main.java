package com.hmdp;

import java.util.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class MyClas {
    private String privateField = "Hello, Reflection!";

    public void printMessage() {
        System.out.println(privateField);
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        // 获取类信息
        Class<?> clazz = MyClas.class;
        String className = clazz.getName();
        System.out.println("Class Name: " + className);

        // 创建对象
        Object instance = clazz.newInstance();

        // 获取字段值
        Field privateField = clazz.getDeclaredField("privateField");
        privateField.setAccessible(true); // 绕过访问控制检查
        String fieldValue = (String) privateField.get(instance);
        System.out.println("Private Field Value: " + fieldValue);

        // 调用方法
        Method printMessageMethod = clazz.getDeclaredMethod("printMessage");
        printMessageMethod.setAccessible(true);
        printMessageMethod.invoke(instance);
    }
}
