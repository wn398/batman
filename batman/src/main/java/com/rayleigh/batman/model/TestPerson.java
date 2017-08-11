package com.rayleigh.batman.model;

/**
 * Created by wangn20 on 2017/6/14.
 */
public class TestPerson {
    private String name;
    private int age;

    public TestPerson(String name,int age){
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
