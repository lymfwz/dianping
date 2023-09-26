package com.hmdp;

import java.util.concurrent.locks.ReentrantLock;

public class MyClass {
    static int x = 0;

    private static synchronized void inc() {
        x++;
    }

    public static void main(String[] args) {
        new Thread(
                () -> {
                    for (int i = 0; i < 1000000; i++) {
                        inc();
                    }
                }
        ).start();
        new Thread(
                () -> {
                    for (int i = 0; i < 1000000; i++) {
                        inc();
                    }
                }
        ).start();
        System.out.println(x);
    }
}
