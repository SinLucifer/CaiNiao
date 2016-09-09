package com.sin.cainiao.Utils;

import java.util.ArrayList;
import java.util.List;


public class Utils {
    public static List<String> Array2List(String[] array){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }

        return list;
    }
}
