package com.sin.cainiao.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.sin.cainiao.adapter.MaterialSuggestion;

import org.json.JSONArray;
import org.json.JSONException;
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
    private static final String FOODS_FILE_NAME = "dictionary.txt";
    public static List<MaterialSuggestion> sMaterialSuggestions = new ArrayList<>();

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
                if (url != null){
                    URL imgUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    Log.i(TAG, "downLoadBitmap: " + url);
                    InputStream inputStream = conn.getInputStream();
                    bitmaps.add(BitmapFactory.decodeStream(inputStream));
                }else {
                    bitmaps.add(null);
                }

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

    public static String loadTxt(Context context){
        String jsonString = "";

        try {
            InputStream is = context.getAssets().open(FOODS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "GBK");
        }catch (IOException e){
            e.printStackTrace();
        }
        return jsonString;
    }

    public static void Json2Object(String jsonString){
        try{
            JSONArray json = new JSONArray(jsonString);
            for (int i = 0; i < json.length(); i++) {
                sMaterialSuggestions.add(new MaterialSuggestion(json.getString(i)));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public static String getUri(Uri uri, ContentResolver resolver){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = resolver.query(uri,proj,null,null,null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}
