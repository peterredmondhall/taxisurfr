package com.taxisurfr.server.util;

/**
 * Created by nbb on 25.07.2015.
 */
public class Pair<T, T1> {
    String first;
    String second;

    public static Pair<String, String> of(String date, String print) {
        Pair<String,String> pair = new Pair();
        pair.first = date;
        pair.second = print;
        return pair;
    }
}
