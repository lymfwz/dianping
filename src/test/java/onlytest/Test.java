package onlytest;

import java.util.*;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-10-10:22
 */

class Solution {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String[] m1 = in.next().split(",");
        String[] n1 = in.next().split(",");
        int[][] m = new int[m1.length][2];
        // int[] n = new int[n1.length];
        for (int i = 0; i < m1.length; i++) {
            m[i][0] = Integer.parseInt(m1[i]);
            m[i][1] = Integer.parseInt(n1[i]);
        }
        int k = in.nextInt();
//        int ans = findMax(m, k);
//        System.out.println(ans);
    }
}

//class Main {
//    static int[] memo;
//    public static void main(String[] args) {
//        Scanner in = new Scanner(System.in);
//        int n = in.nextInt();
//        memo = new int[n+1];
//        Arrays.fill(memo, -1);
//        int ans = dp(n);
//        System.out.println(ans);
//    }
//
//    private static int dp(int n) {
//        if(n == 0) return 0;// 边长
//        if (memo[n] != -1) {
//            return memo[n];
//        }
//        int res = Integer.MAX_VALUE;
//        int min = -1;
//        for (int j = (int) Math.sqrt(n); j >= 1; j--) {
//            int temp = res;
//            res = Math.min(res, dp(n - j*j) + 4 * j);
//            if (temp != res) {
//                min = j;
//            }
//        }
//        System.out.println("选择了："+min);
//        memo[n] = res == Integer.MAX_VALUE ? -1 : res;
//        System.out.println("memo["+n+"] : "+memo[n]);
//        return memo[n];
//    }
//}

public class Test {
    public static void main(String[] args) {
        try {
            test();
            System.out.println("A");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("B");
            return;
        } catch (Exception e) {
            System.out.println("C");
        } finally {
            System.out.println("D");
        }
    }

    private static void test() {
        int[] a = {1, 2, 4};
        System.out.println(a[3]);
    }
}

class Example {
    String str = new String("good");
    char[] ch = {'a', 'b', 'c'};

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            int num = sc.nextInt();
            System.out.println("A");
        } catch (NullPointerException e) {
            System.out.println("B");
        }finally {
            System.out.println("C");
        }
    }

    public void change(String str, char[] ch) {
        str = "test ok";
        ch[0] = 'g';
    }

    static class Solution {
        public static void main(String[] args) {
            coinChange(new int[]{186,419,83,408}, 6249);
        }
        static int[] memo;
        public static int coinChange(int[] coins, int amount) {
            memo = new int[amount+1];
            Arrays.fill(memo, -1);
            Arrays.sort(coins);
            return dp(coins, amount);
        }
        public static int dp(int[] coins, int amount){
            if(amount < 0) return -1;
            if(amount == 0) return 0;
            if(memo[amount] != -1) return memo[amount];
            int ans = Integer.MAX_VALUE;
            for(int i = coins.length-1;i>=0;i--){
                int temp = dp(coins, amount - coins[i]);
                if(temp != -1){
                    ans = Math.min(ans, temp + 1);
                }
            }
            if(ans != Integer.MAX_VALUE) {
                memo[amount] = ans;
            }
            return memo[amount];
        }
    }
}
