package onlytest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-18-19:10
 */

/*
class Main {
    static Map<String, Integer> map = new HashMap<>();
    static int op = 0;
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int m = 0;
        while(m <= n){
            if(dp(m, 1, 0) >= n){
                System.out.println(m);
                break;
            }
            m++;
        }
    }
    public static int dp(int n, int num, int copy){
        if(n <=0) return num;
        String key = n + "_" + num + "_" + copy;
        if(map.containsKey(key)){
            return map.get(key);
        }
        int one = dp(n-1, num+copy, copy); // V
        int two = dp(n-1, num, num); // copy all
        int res = Math.max(one, two);
        map.put(key, res);
        return res;
    }
}*/

/*
class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        int comma = s.lastIndexOf(',');
        String split0 = s.substring(0, comma);
        String split1 = s.substring(comma + 1);

        String arrStr = split0.substring(split0.indexOf('[') + 1, split0.lastIndexOf(']'));
        String[] s1 = arrStr.split(",");
        int[] bana = new int[s1.length];
        int maxHeight = 0;
        for (int i = 0; i < s1.length; i++) {
            bana[i] = Integer.parseInt(s1[i]);
            maxHeight = Math.max(maxHeight, bana[i]);
        }
        int h = Integer.parseInt(split1.substring(split1.indexOf('=') + 1));
        if (bana.length > h) {
            System.out.println(-1);
            return;
        }
        int lo = 1, hi = maxHeight;
        int res = Integer.MAX_VALUE;
        while(lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cur = count(bana, mid);
            if(cur <= h) {
                res = mid;
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }
        System.out.println(res);
    }

    private static int count(int[] bana, int mid) {
        int c = 0;
        for (int i = 0; i < bana.length; i++) {
            c += bana[i] / mid;
            if(bana[i] % mid != 0) c++;
        }
        return c;
    }
}
*/

/*
class Main{
    static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }
    public int dp(TreeNode root){
        if(root == null) return 0;
        Deque<TreeNode> queue = new ArrayDeque<>(){{offer(root);}};
        int height = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (cur.left != null) {
                    queue.offer(cur.left);
                }
                if (cur.right != null) {
                    queue.offer(cur.right);
                }
            }
            height++;
        }
        return height;
    }
}*/

/*
class Main{
    public static void main(String[] args) {
        List<List<Integer>> nums = new ArrayList<>();
        nums.add(Arrays.asList(3, 4, 5, 6, 7, 8, 9));
        nums.add(Arrays.asList(1, 2, 3, 15, 17, 18, 19));
        nums.add(Arrays.asList(13, 14, 15, 16, 17, 18, 19));
        List<Integer> res = mergeSort(nums, 0, 2);
        System.out.println(res);

    }

    public static List<Integer> mergeSort(List<List<Integer>> numsList, int i, int j) {
        if(i == j) return numsList.get(i);
        int mid = i + (j-i)/2;
        List<Integer> l1 = mergeSort(numsList, i, mid);
        List<Integer> l2 = null;
        if (mid < j) {
            l2 = mergeSort(numsList, mid + 1, j);
        }
        return merge(l1, l2);
    }

    private static List<Integer> merge(List<Integer> l1, List<Integer> l2) {
        if (l2 == null) {
            return l1;
        }
        int i = 0, j = 0;
        List<Integer> res = new ArrayList<>();
        while (i < l1.size() && j < l2.size()) {
            if (l1.get(i) <= l2.get(j)) {
                res.add(l1.get(i++));
            } else {
                res.add(l2.get(j++));
            }
        }
        while (i < l1.size()) {
            res.add(l1.get(i++));
        }
        while (j < l2.size()) {
            res.add(l2.get(j++));
        }
        return res;
    }
}
*/

/*class StorageOptimization {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt(); // 会员数量
        int[] memberCosts = new int[n];

        for (int i = 0; i < n; i++) {
            memberCosts[i] = scanner.nextInt();
        }
        if (n == 1) {
            System.out.println(memberCosts[0] / 2);
            return;
        } else if (n == 2) {
            System.out.println((memberCosts[0] + memberCosts[1]) / 2);
            return;
        }
        Arrays.sort(memberCosts); // 对会员存储成本排序
        long totalCost = 0;

        for (int i = 0; i < n; i++) {
            int currentCost = memberCosts[i];
            totalCost += currentCost;
        }
        totalCost -= memberCosts[n - 2] / 2;
        totalCost -= memberCosts[n - 1] / 2;

        System.out.println(totalCost);
    }
}*/

/*class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = in.nextInt();
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(nums[i], map.getOrDefault(nums[i], 0) + 1);
        }
        Map<Integer, Integer> map3 = new HashMap<>();
        Map<Integer, Integer> map2 = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() >= 3) {
                map3.put(entry.getKey(), entry.getValue());
            }
            if (entry.getValue() >= 3) {
                map2.put(entry.getKey(), entry.getValue());
            }
        }
        int count = 0;
        int mod = (int) (1e9+7);
        for (Map.Entry<Integer, Integer> e1 : map3.entrySet()) {
            for (Map.Entry<Integer, Integer> e2 : map2.entrySet()) {
                if (e1.getKey() > e2.getKey()) {
                    int p1 = e1.getValue();
                    int l = p1 * (p1 -1) * (p1-2) / 6;
                    int p2 = e2.getValue();
                    int r = p2 * (p2-1) / 2;
                    count += l * r;
                    count %= mod;
                }
            }
        }
        System.out.println(count);
    }
}*/

public class CVTE0918 {
}



class Main22 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 输入班级队伍的行数m和列数n
        int m = scanner.nextInt();
        int n = scanner.nextInt();

        // 创建班级队伍的二维数组，并初始化
        int[][] grid = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = scanner.nextInt();
            }
        }

        // 初始化传球次数的二维数组，用来记录从每个起点到达最后一列的传球次数
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }
        dp[0][0] = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    int c1 = (i-1>=0 && grid[i-1][j]==1)? dp[i-1][j] : Integer.MAX_VALUE;
                    int c2 = (j - 1 >= 0 && grid[i][j - 1] == 1) ? dp[i][j - 1] : Integer.MAX_VALUE;
                    dp[i][j] = Math.min(dp[i][j], Math.min(c1, c2));
                }
            }
        }
        int res = dp[m-1][n-1] == Integer.MAX_VALUE ? -1 : dp[m-1][n-1];
        System.out.println(res);
    }
}





/*class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 输入班级队伍的行数m和列数n
        int m = scanner.nextInt();
        int n = scanner.nextInt();

        // 创建班级队伍的二维数组，并初始化
        int[][] grid = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = scanner.nextInt();
            }
        }

        int result = findMinPassCount(grid, m - 1, n - 1);
        System.out.println(result);
    }

    // 递归函数，计算从指定位置逆向传球到右下角的最小传球次数
    private static int findMinPassCount(int[][] grid, int row, int col) {
        int m = grid.length;
        int n = grid[0].length;

        // 如果当前位置是女生，无法传球到右下角，返回-1
        if (grid[row][col] == 0) {
            return -1;
        }

        // 如果已经到达右下角，传球次数为0
        if (row == m - 1 && col == n - 1) {
            return 0;
        }

        // 初始化最小传球次数为无穷大
        int minPassCount = Integer.MAX_VALUE;

        // 逆向传球到上下左右四个方向的位置
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            Arrays.fill(dp[i], 10000);
        }
        dp[m-1][n-1] = 0;
        for (int i = m-1; i >=0; i--) {
            for (int j = n-1; j >= 0; j--) {
                for (int k = 0; k < 4; k++) {
                    int newRow = row + dr[k];
                    int newCol = col + dc[k];

                    // 判断下一个位置是否越界
                    if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n && grid[newRow][newCol] == 1) {
                        dp[newRow][newCol] = Math.min(dp[i][j] + 1, dp[newRow][newCol]);
                    }
                }
            }
        }
        for (int i = 0; i < m; i++) {
            if (grid[i][0] == 1 && dp[i][0] != 10000) {
                minPassCount = Math.min(minPassCount, dp[i][0]);
            }
        }

        if(minPassCount == 0) return -1;
        return minPassCount == Integer.MAX_VALUE ? -1 : minPassCount;
    }
}*/



class Main3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 输入班级队伍的行数m和列数n
        int m = scanner.nextInt();
        int n = scanner.nextInt();

        // 创建班级队伍的二维数组，并初始化
        int[][] grid = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = scanner.nextInt();
            }
        }

        int result = findMinPassCount(grid);
        System.out.println(result);
    }

    // 使用广度优先搜索(BFS)计算最短传球次数
    private static int findMinPassCount(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        // 如果右下角是女生，则无法传球到右下角，返回-1
        if (grid[m - 1][n - 1] == 0) {
            return -1;
        }

        // 方向数组，表示上下左右四个方向
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        int[][] dp = new int[m][n]; // 用来保存传递次数
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j] = Integer.MAX_VALUE;
            }
        }

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{m - 1, n - 1}); // 添加右下角位置到队列
        dp[m - 1][n - 1] = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] currentPosition = queue.poll();
                int x = currentPosition[0];
                int y = currentPosition[1];

                // 从当前位置传球到上下左右四个方向的位置
                for (int k = 0; k < 4; k++) {
                    int nx = x + dx[k];
                    int ny = y + dy[k];

                    // 判断下一个位置是否越界，是否是男生
                    if (nx >= 0 && nx < m && ny >= 0 && ny < n && grid[nx][ny] == 1) {
                        if (dp[nx][ny] == Integer.MAX_VALUE) {
                            queue.offer(new int[]{nx, ny});
                        }
                        dp[nx][ny] = Math.min(dp[nx][ny], dp[x][y] + 1);
                    }
                }
            }
        }

        // 找出左侧第一列男生的最小传球次数
        int minPassCount = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            if (grid[i][0] == 1) {
                minPassCount = Math.min(minPassCount, dp[i][0]);
            }
        }
        if(minPassCount == 0) {
            return -1;
        }
        return minPassCount == Integer.MAX_VALUE ? -1 : minPassCount;
    }
}


