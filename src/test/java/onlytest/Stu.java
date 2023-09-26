package onlytest;

import java.util.Objects;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-15-15:14
 */
public class Stu {
    int id;
    String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stu stu = (Stu) o;
        return id == stu.id && Objects.equals(name, stu.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public static void main(String[] args) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
            }
        };
        thread.start();

    }
}
