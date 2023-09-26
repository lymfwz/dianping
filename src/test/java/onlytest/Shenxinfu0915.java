package onlytest;


import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-15-16:06
 */
class Shenxinfu0915 {
    public static void main(String[] args) {
        System.out.println(-1%2);
    }
}

/*class Main {
    static int min = Integer.MAX_VALUE;
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int t = in.nextInt();
        for (int i = 0; i < t; i++) {
            min = Integer.MAX_VALUE;
            int n = in.nextInt();
            int k = in.nextInt();
            int[] taskTimes = new int[n];
            for (int j = 0; j < n; j++) {
                taskTimes[j] = in.nextInt();
            }
            Integer[] index = new Integer[n];
            for (int l = 0; l < n; l++) {
                index[l] = l;
            }
            Arrays.sort(index, (a,b)->taskTimes[a] - taskTimes[b]);
            List<Integer> list = new ArrayList<>();
            Set<Integer> set = new HashSet<>();
            for (int l = 0; l < k; l++) {
                set.add(index[l]);
            }
            for (int l = 0; l < n; l++) {
                if (set.contains(l)) {
                    list.add(taskTimes[l]);
                }
            }
            min = Math.min(min, cal(list));
            System.out.println(min);
        }
    }

    private static int cal(List<Integer> list) {
        int n = list.size();
        int[] pre = new int[n + 1];
        int[] suffix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            pre[i + 1] = pre[i] + list.get(i);
        }
        for (int i = n-1; i >= 0; i--) {
            suffix[i] = suffix[i+1] + list.get(i);
        }
        int ans = Integer.MAX_VALUE;
        for (int i = 0; i <= n; i++) {
            int left = pre[i];
            int right = suffix[i];
            ans = Math.min(Math.max(left, right), ans);
        }
        return ans;
    }
}*/

/*
class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        StringBuilder sb = new StringBuilder(n+"");
        for (int i = 0; i < n; i++) {
            StringBuilder s = new StringBuilder(sb);
            s.insert(0, s.charAt(s.length() - 1));
            s.deleteCharAt(s.length() - 1);
            cal(sb, s);
        }
        System.out.println(sb.toString());
    }

    private static void cal(StringBuilder sb, StringBuilder s) {
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) - s.charAt(i) < 0) {
                sb = s;
                return;
            }
        }
    }
}
*/
//5
//6 3
//8 1 10 1 1 1
//5 3
//1 13 5 12 3
//10 6
//10 8 18 13 3 8 6 4 14 12
//10 5
//9 9 2 11 14 33 4 9 14 12
//1 1
//1
class Solution2 {
    public static void main(String[] args) {
        subarraysDivByK(new int[]{7, -5, 5, -8, -6, 6, -4, 7, -8, -7}, 7);
    }
    public static int subarraysDivByK(int[] nums, int k) {
        int n = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1); // 频次
        int[] pre = new int[n+1];
        for(int i=0;i<n;i++){
            pre[i+1] = pre[i] + nums[i];
        }
        int count = 0;
        for(int i=1;i<=n;i++){
            int re = pre[i] % k;
            if(map.containsKey(re)){
                count += map.get(re);
            }
            if(map.containsKey(re-k)){
                count += map.get(re-k);
            }
            map.put(re, map.getOrDefault(re, 0)+1);
        }
        return count;
    }
    // 0 -1 1
}

