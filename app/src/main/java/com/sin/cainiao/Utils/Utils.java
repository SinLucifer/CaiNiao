package com.sin.cainiao.Utils;

import android.content.Context;
import android.widget.Toast;

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

    public static void toastShow(Context context,String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
