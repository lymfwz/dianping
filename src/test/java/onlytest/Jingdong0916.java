package onlytest;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-16-11:01
 */
class Jingdong0916 {
}
/*class Main {
    public static String findDifferentChars(String s, String t) {
        StringBuilder result = new StringBuilder();

        // 创建一个字符数组来统计字符出现的次数
        int[] charCount = new int[26];

        // 遍历字符串s，增加字符计数
        for (char c : s.toCharArray()) {
            charCount[c - 'a']++;
        }

        // 遍历字符串t，减少字符计数
        for (char c : t.toCharArray()) {
            charCount[c - 'a']--;
        }

        // 遍历字符计数数组，找出不同的字符
        for (int i = 0; i < 26; i++) {
            if (charCount[i] != 0) {
                result.append((char) ('a' + i));
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.next();
        String t = in.next();
        String differentChars = findDifferentChars(s, t);
        System.out.println(differentChars); // 输出 "e"
    }
}*/

/*class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.next();
        System.out.println(dp(s, 0, s.length()-1));
    }

    private static boolean dp(String s, int i, int j) {
        if (i >= j) {
            return true;
        }
        char ch1 = s.charAt(i);
        char ch2 = s.charAt(j);
        if (ch1 == ch2) {
            return dp(s, i + 1, j - 1);
        } else {
            return false;
        }
    }
}*/
/*class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        BigInteger res = new BigInteger("0");
        BigInteger ans = new BigInteger("1");
        for (int i = 1; i <= n; i++) {
            BigInteger cur = new BigInteger((i+""));
            ans = ans.multiply(cur);
            res = res.add(ans);
        }
        System.out.println(res);
    }
}*/
/*class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int t = in.nextInt();
        for (int i = 0; i < t; i++) {
            int n = in.nextInt();
            int delx = in.nextInt();
            List<int[]> edges = new ArrayList<>();
            int[] outDegree = new int[n+1];
            //boolean[] vis = new boolean[n+1];
            for (int j = 0; j < n - 1; j++) {
                int u = in.nextInt();
                outDegree[u]++;
                int v = in.nextInt();
                edges.add(new int[]{u, v});
            }
            Deque<Integer> queue = new ArrayDeque<>();
            for (int j = 1; j < outDegree.length; j++) {
                if (outDegree[j] == 0) {
                    queue.offer(j);
                    vis[j] = true;
                }
            }
            boolean odd = true;
            boolean con = true;
            while (!queue.isEmpty() && con) {
                int size = queue.size();
                for (int k = 0; k < size; k++) {
                    int cur = queue.poll();
                    if(cur == delx){
                        if (odd) {
                            System.out.println("win");
                            con = false;
                            break;
                        } else {
                            System.out.println("lose");
                            con = false;
                            break;
                        }
                    }
                    for (int[] edge : edges) {
                        if(edge[1] == cur){
                            outDegree[edge[0]]--;
                            if (outDegree[edge[0]] == 0) {
                                queue.offer(edge[0]);
                            }
                        }
                    }
                }
                odd = !odd;
            }
        }
    }
}*/
/*class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int x = in.nextInt();
        int y = in.nextInt();
        int count = 0;
        List<int[]> op = new ArrayList<>();
        boolean res = true;
        for (int i = 30; i >= 0; i--) {
            int cury = (y>>i) & 1;
            int curx = (x>>i) & 1;
            if(curx == cury) continue;
            else if (cury == 1) {
                if(i == 0) {
                    res = false;
                    break;
                }
                int idx = -1;
                for (int j = i - 1; j >= 0; j--) {
                    if (((x >> j) & 1) == 1) {
                        idx = j;
                        break;
                    }
                }
                if (idx == -1) {
                    res = false;
                    break;
                } else {
                    int tmp = (x>>idx)<<idx;
                    int pre = (x>>idx)<<idx;
                    while (idx < i) {
                        op.add(new int[]{1, pre});
                        idx++;
                        count++;
                        pre <<= 1;
                    }
                    x -= tmp;
                    x += pre;
                }
            } else {
                op.add(new int[]{-1, curx << i});
                x -= -(curx << i);
                count++;
            }
        }
        if(res) {
            System.out.println(count);
            for (int i = 0; i < op.size(); i++) {
                int[] tmp = op.get(i);
                int num = tmp[0] * tmp[1];
                if (num < 0) {
                    System.out.println("- "+Math.abs(num));
                } else {
                    System.out.println("+ "+num);
                }
            }
        } else {
            System.out.println(-1);
        }
    }
}*/

/*class Main {
    static final int maxn = 100010;

    static List<Integer>[] E = new ArrayList[maxn];
    static int[][] vis = new int[maxn][maxn];

    static int[][] d = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };

    static class Node implements Comparable<Node> {
        int x, y, cnt;

        Node() {}

        Node(int a, int b, int c) {
            x = a;
            y = b;
            cnt = c;
        }

        public int compareTo(Node tmp) {
            return Integer.compare(tmp.cnt, cnt);
        }
    }

    static boolean check(int n, int m, int x, int y, int base) {
        if (x < 0 || x >= n || y < 0 || y >= m) return false;
        if (base + E[x].get(y) >= vis[x][y]) return false;
        return true;
    }

    static int bfs(int n, int m) {
        vis[0][0] = E[0].get(0);
        PriorityQueue<Node> q = new PriorityQueue<>();

        q.add(new Node(0, 0, vis[0][0]));
        while (!q.isEmpty()) {
            Node now = q.poll();
            if (now.x == n - 1 && now.y == m - 1) return now.cnt;
            for (int i = 0; i < 4; i++) {
                int xx = now.x + d[i][0];
                int yy = now.y + d[i][1];
                if (check(n, m, xx, yy, now.cnt)) {
                    vis[xx][yy] = now.cnt + E[xx].get(yy);
                    q.add(new Node(xx, yy, vis[xx][yy]));
                }
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        for (int i = 0; i < maxn; i++) {
            E[i] = new ArrayList<>();
            Arrays.fill(vis[i], maxn);
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int x = scanner.nextInt();
                E[i].add(x);
            }
        }

        System.out.println(bfs(n, m));
    }
}*/


// 数组升序最少交换次数
/*class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = in.nextInt();
        }
        int i = 0;
        int count = 0;
        Set<Integer> set = new HashSet<>();
        while (i < n) {
            int cur = nums[i];
            int next = i;
            int j = i;
            if (nums[j] == j + 1) {
                set.add(j);
            } else {
                while (!set.contains(j) && nums[j] != j + 1) {
                    set.add(j);
                    next = nums[j]-1;
                    if (next != i) {
                        count++;
                    }
                    if (nums[next] == next + 1) {
                        System.out.println(-1);
                        return;
                    }
                    j = next;
                }
            }
            i++;
        }
        System.out.println(count);
    }
}*/

// 染色字符
/*
class Main {
    static int[] memo;
    static int max = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.next();
        int n = s.length();
        memo = new int[n];
        Arrays.fill(memo, -1);
        int res = dp(s, 0);
        System.out.println(res);
    }

    private static int dp(String s, int i) {
        if (i == s.length()) {
            return 0;
        }
        if (memo[i] != -1) {
            return memo[i];
        }
        int cur = s.charAt(i) - '0';
        if (i < s.length() - 1 && (((s.charAt(i + 1) - '0') ^ cur)) == 1) {
            memo[i] = Math.max(dp(s, i + 1), dp(s, i + 2) + 1);
        } else {
            memo[i] = Math.max(dp(s, i + 1), dp(s, i + 1) + 1);
        }
        max = Math.max(max, memo[i]);
        return memo[i];
    }
}*/

// 视频愉悦度
/*
class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt(); // 视频的总数量
        int q = scanner.nextInt(); // 刷视频的次数

        long[][] video = new long[n][2]; // 每个视频的内存占用 每个视频第一次观看时给小红带来的愉悦值
        int[] xi = new int[q]; // 每次刷视频时，系统推荐的视频占用内存的上限

        for (int i = 0; i < n; i++) {
            video[i][0] = scanner.nextInt();
        }

        for (int i = 0; i < n; i++) {
            video[i][1] = scanner.nextInt();
        }

        for (int i = 0; i < q; i++) {
            xi[i] = scanner.nextInt();
        }

        Arrays.sort(video, new Comparator<long[]>() {
            @Override
            public int compare(long[] o1, long[] o2) {
                return (int) (o1[0] == o2[0] ? o2[1] - o1[1] : o2[0] - o1[0]);
            }
        });

        BigInteger happy = new BigInteger("0");
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < q; i++) {
            int maxN = xi[i];
            int j = getIndex(video, maxN);
            if (map.containsKey(j)) {
                happy = happy.add(count(map, j, video));
                map.put(j, map.get(j) + 1);
            } else {
                happy = happy.add(new BigInteger(""+video[j][1]));
                map.put(j, 1);
            }
        }

        System.out.println(happy);
    }

    private static int getIndex(long[][] video, int maxN) {
        int l = 0, r = video.length - 1;
        int res = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (video[mid][0] <= maxN) {
                res = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return res;
    }

    private static BigInteger count(Map<Integer, Integer> map, int j, long[][] video) {
        int cur = map.get(j); // 次数
        long h = video[j][1];
        h /= Math.pow(2, cur);
        return new BigInteger("" + h);
    }
}*/

/*class Main {
    static Map<String, Double> map = new HashMap<>();
    static double[][] dp;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int blood = in.nextInt();
        int g = 0;
        int full = 0;
        List<Integer> p = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int t = in.nextInt();
            if (t == 1) {
                int cur = in.nextInt();
                g += cur;
                full += cur;
            } else {
                int cur = in.nextInt();
                p.add(cur);
                full += 6;
            }
        }
        if (full < blood) {
            System.out.println(0);
            return;
        }
        int re = blood - g;
        probability(p.size(), p.size() * 6);
        double ans = dp(p, 0, re);
        System.out.println(ans);
    }

    private static double dp(List<Integer> p, int i, int re) {
        if (re <= 0) {
            return 1.0;
        }
        if (i == p.size()) {
            return 0;
        }
        String key = i + "_" + re;
        if (map.containsKey(key)) {
            return map.get(key);
        }
        double res = 0;
        int full = p.size() * 6 - i * 6;
        for (int j = p.size() - i; j <= full; j++) {
            res += dp[p.size() - i][j] * dp(p, i + 1, re - p.get(i) - j);
        }
        map.put(key, res);
        return res;
    }

    public static double probability(int k, int m) {
        if (k < 1 || m < k || m > 6 * k) {
            return 0.0;
        }

        // 创建一个二维数组dp，dp[i][j]表示扔i个骰子，总点数为j的概率
        dp = new double[k + 1][m + 1];

        // 初始化扔1个骰子的情况
        for (int j = 1; j <= 6; j++) {
            dp[1][j] = 1.0 / 6;
        }

        // 动态规划计算扔i个骰子的概率
        for (int i = 2; i <= k; i++) {
            for (int j = i; j <= 6 * i; j++) {
                for (int face = 1; face <= 6; face++) {
                    if (j - face > 0) {
                        dp[i][j] += dp[i - 1][j - face] * (1.0 / 6);
                    }
                }
            }
        }

        return dp[k][m];
    }

}*/

// 子数组极值差之和
/*class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] a = new int[n];
        List<Integer> b = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
//            for (int j = 0; j < a[i]; j++) {
//                b.add(i+1);
//            }
            b.add(a[i]);
        }
        int ji = 0;
        int mod = (int) (1e9 + 7);
        for (int i = n; i > 0; i--) { // 窗口
            // 降序
            Deque<Integer> q1 = new ArrayDeque<>();
            Deque<Integer> q2 = new ArrayDeque<>();
            int size = b.size();
            for (int j = 0; j < size; j++) {
                while (!q1.isEmpty() && b.get(q1.peekLast()) <= b.get(j)) {
                    q1.pollLast();
                }
                while (!q1.isEmpty() && j - q1.peekFirst()>= i) {
                    q1.poll();
                }
                while (!q2.isEmpty() && b.get(q2.peekLast()) >= b.get(j)) {
                    q2.pollLast();
                }
                while (!q2.isEmpty() && j - q2.peekFirst()>= i) {
                    q2.poll();
                }
                q1.offer(j);
                q2.offer(j);
                if (j >= i - 1) {
                    ji += b.get(q1.peekFirst()) - b.get(q2.peekFirst());
                    ji %= mod;
                }
            }
        }
        System.out.println(ji);
    }
}*/














//    List<String> list = Arrays.asList("A", "B", "C");
//    List<String> resultList = new ArrayList<>();
//        list.stream()
//                .filter(item ->{
//                System.out.print(item);
//                return true;
//                })
//                .forEach(item->{
//                if (item.equals("B")) {
//                resultList.add(item);
//                return;
//                }
//                resultList.add(item.toUpperCase());
//                });
//                System.out.println("\n" +resultList);