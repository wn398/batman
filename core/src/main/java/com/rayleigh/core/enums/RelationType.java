package com.rayleigh.core.enums;

/**
 * Created by wangn20 on 2017/6/14.
 * 映射关系
 */
public enum RelationType {
    OneToOne("OneToOne"),
    OneToMany("OneToMany"),
    ManyToOne("ManyToOne"),
    ManyToMany("ManyToMany");

    RelationType(String name){
    }
}
