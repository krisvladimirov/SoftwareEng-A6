package com.group1.artatawe;

import java.util.HashMap;

public class Testing {
    public static void main(String[] args) {

        HashMap<String, String> map = new HashMap<>();
        System.out.println(map.size());
        map.put("Kris", "liki");
        System.out.println(map.get("Kris"));
        map.remove("Kris");
        System.out.println(map.size());
        System.out.println(map.containsKey("Kris"));
    }
}
