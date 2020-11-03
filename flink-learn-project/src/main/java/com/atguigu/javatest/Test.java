package com.atguigu.javatest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Kasa
 * @date : 2020/11/3 11:45
 * @descripthon :
 */
public class Test {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>(){
            {
                add("a");
                add("b");
            }
        };
        list.add("1");

        for (String str : list) {
            System.out.println(str);
        }
        System.out.println("---------------------");
        List<String> list2 = new ArrayList<>();
        list2.add("2");

        for (String str : list2) {
            System.out.println(str);
        }
    }
}
