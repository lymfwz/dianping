package com.hmdp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-08-22-14:46
 */

class test {
    static volatile int x = 0;
    static volatile int y = 0;
    static volatile int a = 0;
    static volatile int b = 0;
    public static void main(String[] args) {
        // 10010 r - >  ~r - > 01101
        // r & (!r + 1);
        // 11000 -> 00111 -> 01000
        System.out.println(500 & (~500 + 1));
//        Map<Integer, Integer> map = new HashMap<>();
//        int i = 0;
//        while (true) {
//            i++;
//            x = y = a = b = 0;
//            MyThread m1 = new MyThread();
//            MyThread2 m2 = new MyThread2();
//            m1.start();
//            m2.start();
//            System.out.println("第"+i+"次 : "+"x = "+x+" , y = "+y);
//            if (i == 200000) {
//                break;
//            }
//        }
    }
    static class MyThread extends Thread {
        public void run() {
            // 线程执行的代码
            for (int i = 0; i < 8000; i++);
            a = 1;
            x = b;
        }
    }
    static class MyThread2 extends Thread {
        public void run() {
            b = 1;
            y = a;
        }
    }

}