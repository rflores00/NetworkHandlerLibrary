package cl.rf.networkhandlerexample;

import androidx.annotation.NonNull;

public class SimpleResponse {
    private String name;
    private int age;
    private int count;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getCount() {
        return count;
    }

    @NonNull
    @Override
    public String toString() {
        return "SimpleResponse{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", count=" + count +
                '}';
    }
}
