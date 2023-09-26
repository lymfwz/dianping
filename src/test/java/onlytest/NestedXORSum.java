package onlytest;

import org.aspectj.weaver.ast.Var;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-09-22:14
 */
class NestedXORSum {
    public static int nestedXORSum(int[] nums) {
        int n = nums.length;
        int[][] xorSum = new int[n][n]; // 用于存储以nums[i]结尾的子数组的异或和

        int result = 0;
        for (int i = 0; i < n-1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (j == i + 1) {
                    xorSum[i][j] = nums[i] ^ nums[j];
                } else{
                    xorSum[i][j] = nums[j] ^ xorSum[i][j-1];
                }
            }
        }
        int mod = (int) (1e9+7);
        System.out.print("   ");
        for (int i = 0; i < n; i++) {
            System.out.print(i+1+" ");
        }
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.print(i+1+"*"+" ");
            for (int j = 0; j < n; j++) {
                System.out.print(xorSum[i][j] + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < n-1; i++) {
            for (int j = i+1; j < n; j++) {
                result += xorSum[i][j];
                result %= mod;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        int result = nestedXORSum(nums);
        System.out.println(result);  // 输出结果为 6
        Random random = new Random();
        random.nextInt();
    }
}
class Solution1 {
    public int findKthLargest(int[] nums, int k) {
        // 逆序快排
        return quickSort(nums, 0, nums.length-1, k-1);
    }
    public int quickSort(int[] nums, int l, int r, int k){
        if(l == r) return nums[l];
        int idx = (int)Math.random()*(r - l + 1) + l;
        int target = nums[idx];
        swap(nums, idx, r);
        int left = l-1, right = r+1, i = l;
        while(i < right){
            int cur = nums[i];
            if(cur > target){
                swap(nums, ++left, i++);
            } else if(cur == target) {
                i++;
            } else {
                swap(nums, --right, i);
            }
        }
        if(left < k && right > k) {
            return target;
        } else if(k <= left) {
            return quickSort(nums, l, left, k);
        } else return quickSort(nums, right, r, k);
    }
    public void swap(int[] nums, int i, int j){
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        Solution1 s = new Solution1();
        s.findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2);
    }
}

class ExtractNumbers {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        int n = s.length();
        int i =0, j = 0, k = 0;
        List<Integer> res = new ArrayList<>();
        while (k < n) {
            if (Character.isLetter(s.charAt(k))) {
                String cur = s.substring(i, j).replaceAll(" ", "");
                if (!cur.equals("")) {
                    res.add(Integer.parseInt(cur));
                }
                i = k+1;
                j = i;
            } else {
                j++;
            }
            k++;
        }
        String cur = s.substring(i, j).replaceAll(" ", "");
        if (!cur.equals("")) {
            res.add(Integer.parseInt(cur));
        }
        System.out.println(res.toString());
    }
}


