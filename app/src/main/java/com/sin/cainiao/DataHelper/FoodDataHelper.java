package com.sin.cainiao.DataHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.show.api.ShowApiRequest;
import com.sin.cainiao.JavaBean.Food;
import com.sin.cainiao.JavaBean.FoodItem;
import com.sin.cainiao.Utils.Key;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2016/9/3.
 */
public class FoodDataHelper {
    private static final String TAG = "FoodDataHelper";

    public interface onFindFoodsListener{
        void onGroupResult(List<Food.ShowapiResBodyBean.CbListBean> foodList, boolean status);
        void onSingleResult(FoodItem.ShowapiResBodyBean item, boolean status);
    }

    public static void findFoods(Context context,final String query,final onFindFoodsListener listener){
        new Thread(){
            @Override
            public void run() {
                final String res = new ShowApiRequest("http://route.showapi.com/95-106"
                        , Key.YIYUAN_APPID,Key.YIYUAN_SECRET)
                        .addTextPara("name",query)
                        .post();
                Gson gson = new Gson();
                Food food = gson.fromJson(res,Food.class);
                List<Food.ShowapiResBodyBean.CbListBean> foodList =
                        food.getShowapi_res_body().getCbList();
                if (foodList != null){
                    listener.onGroupResult(foodList,true);
                    Log.i(TAG, "query Success: " + foodList);
                }else{
                    listener.onGroupResult(null,false);
                    Log.i(TAG, "query Error: " + foodList);
                }
            }
        }.start();

    }

    public static void findFoodByID(final String id , final onFindFoodsListener listener){
        new Thread(){
            @Override
            public void run() {
                final String res = new ShowApiRequest("http://route.showapi.com/95-35"
                        , Key.YIYUAN_APPID,Key.YIYUAN_SECRET)
                        .addTextPara("cbId",id)
                        .post();
                Gson gson = new Gson();
                FoodItem item = gson.fromJson(res,FoodItem.class);
                if (item.getShowapi_res_body() != null){
                    Log.i(TAG, "query Success: " + item.getShowapi_res_body().getCl());
                    item.getShowapi_res_body().setBitmaps(downLoadImgList(item.getShowapi_res_body().getImgList()));
                    listener.onSingleResult(item.getShowapi_res_body(),true);

                }else{
                    listener.onSingleResult(null,false);
                    Log.i(TAG, "query Error: " + item.getShowapi_res_body());
                }
            }
        }.start();
    }

    public static List<Bitmap> downLoadImgList(List<FoodItem.ShowapiResBodyBean.ImgListBean> urlList){
        List<Bitmap> bitmaps = new ArrayList<>();
        try {
            for (int i = 0; i < urlList.size(); i++) {
                String url = urlList.get(i).getImgUrl();
                URL imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.connect();
                Log.i(TAG, "downLoadBitmap: " + url);
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null)
                    bitmaps.add(bitmap);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return bitmaps;
    }

}
