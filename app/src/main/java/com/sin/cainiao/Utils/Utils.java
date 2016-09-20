package com.sin.cainiao.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Utils {
    private static final String TAG = "Utils";

    public static List<String> Array2List(String[] array){
        List<String> list = new ArrayList<>();
        for (String temp:array) {
            list.add(temp);
        }

        return list;
    }

    public static void toastShow(Context context,String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    public static Bitmap downLoadImg(String url){
        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.connect();
            Log.i(TAG, "downLoadBitmap: " + url);
            InputStream inputStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

        }catch (IOException e){
            e.printStackTrace();
        }

        return bitmap;
    }


    public static List<Bitmap> downLoadImgList(List<String> urls){
        List<Bitmap> bitmaps = new ArrayList<>();
        try {
            for (String url:urls) {
                URL imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.connect();
                Log.i(TAG, "downLoadBitmap: " + url);
                InputStream inputStream = conn.getInputStream();
                bitmaps.add(BitmapFactory.decodeStream(inputStream));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return bitmaps;
    }

    public static String modifyImg(String read){
        Document doc = Jsoup.parse(read);
        Elements png = doc.select("img[src]");

        for (Element e:png){
            e.removeAttr("alt");
            e.removeAttr("height");
            e.removeAttr("width");
            e.attr("style","max-width: 100%;max-height:auto; display:block;margin-left:auto;margin-right:auto;");
        }

        return doc.toString();
    }
}
