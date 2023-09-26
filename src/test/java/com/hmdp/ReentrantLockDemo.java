package com.hmdp;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-05-23:42
 */
public class ReentrantLockDemo {
    private static int count = 0;
    private static ReentrantLock lock = new ReentrantLock(true);

    public static void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                increment();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Count: " + count);
    }
}
