package com.hmdp;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-09-17:32
 */
public abstract class AITest {
    private int id;
    protected int name;
    public int s;

    public AITest(int id, int name, int s) {
        this.id = id;
        this.name = name;
        this.s = s;
    }

    public AITest() {

    }

    private int getName() {
        return this.name;
    }

    abstract int getId();

    public static void main(String[] args) {
        AITest aiTest = new AITest() {
            @Override
            int getId() {
                return 0;
            }
        };
        int name = aiTest.getName();
        System.out.println(name);
    }
}
