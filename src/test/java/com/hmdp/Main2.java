package com.hmdp;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-02-20:39
 */

import java.util.*;

public class Main2 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = in.nextInt();
        }
        int[] seq = generateSequence(nums.length);
        // System.out.println(Arrays.toString(seq));
        adjustSequence(seq, nums);
        for (int i : seq) {
            System.out.println(i + " ");
        }
    }

    public static int[] generateSequence(int n) {
        int[] seq = new int[2 * n];
        for (int i = 0; i < n; i++) {
            seq[i] = i + 1;
            seq[n + i] = i + 1;
        }
        Random rand = new Random();
        for (int i = 2 * n - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = seq[i];
            seq[i] = seq[j];
            seq[j] = temp;
        }
        return seq;
    }

    public static void adjustSequence(int[] seq, int[] nums) {
        Arrays.sort(nums);
        for (int i = 1; i < seq.length; i++) {
            if (seq[i] >= seq[i - 1]) {
                continue;
            }
            int j = i;
            while (j > 0 && seq[j] < seq[j - 1]) {
                int temp = seq[j];
                seq[j] = seq[j - 1];
                seq[j - 1] = temp;
                j--;
            }
        }
        int count = 0;
        for (int i = 1; i < seq.length; i++) {
            if (seq[i] < seq[i - 1]) {
                count++;
            }
            if (count == nums.length) {
                break;
            }
        }
        while (count > 0) {
            int i = seq.length - 1;
            while (i > 0 && seq[i] >= seq[i - 1]) {
                i--;
            }
            int j = i;
            while (j < seq.length - 1 && seq[j] < seq[j + 1]) {
                j++;
            }
            int temp = seq[i];
            seq[i] = seq[j];
            seq[j] = temp;
            count--;
        }
    }
}

