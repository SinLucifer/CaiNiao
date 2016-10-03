package com.sin.cainiao.DataHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.show.api.ShowApiRequest;
import com.sin.cainiao.JavaBean.CaiNiaoUser;
import com.sin.cainiao.JavaBean.Food;
import com.sin.cainiao.JavaBean.FoodItem;
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.Utils.Key;
import com.sin.cainiao.Utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class FoodDataHelper {
    private static final String TAG = "FoodDataHelper";

    public interface onFindFoodsListener{
        void onGroupResult(List<Food.ShowapiResBodyBean.CbListBean> foodList, boolean status);
        void onSingleResult(FoodItem.ShowapiResBodyBean item, boolean status);
    }

    public interface onFindProcessFoodListener{
        void onSingleResult(ProcessedFood item,boolean status);
        void onGroupResult(List<ProcessedFood> foodList, boolean status);
    }

    public interface onFindProcessFoodWithPicListener{
        void onFind(List<ProcessedFood> fooList,List<Bitmap> bitmaps,boolean status);
    }

    public interface onFindFavFoodListener{
        void onFind(List<ProcessedFood> foodList,boolean status);
    }

    public static void findFoods(Context context,final String query,final onFindFoodsListener listener){
        new Thread(){
            @Override
            public void run() {
                final String res = new ShowApiRequest("http://route.showapi.com/95-106"
                        , Key.YIYUAN_APPID,Key.YIYUAN_SECRET)
                        .addTextPara("name",query)
                        .post();
                Log.i(TAG, "run: " + res);
                try {
                    Gson gson = new Gson();
                    Food food = gson.fromJson(res,Food.class);
                    List<Food.ShowapiResBodyBean.CbListBean> foodList =
                            food.getShowapi_res_body().getCbList();
                    if (foodList != null){
                        listener.onGroupResult(foodList,true);
                        Log.i(TAG, "query Success: " + foodList);
                    }else{
                        listener.onGroupResult(null,false);
                        Log.i(TAG, "query Error: " );
                    }
                }catch (Exception e){
                    e.printStackTrace();
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

    public static void findProcessFoodByID(final String id,final onFindProcessFoodListener listener){
        BmobQuery<ProcessedFood> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("number",Integer.parseInt(id));
        bmobQuery.findObjects(new FindListener<ProcessedFood>() {
            @Override
            public void done(List<ProcessedFood> list, BmobException e) {
                if(e==null){
                    if (listener != null){
                        listener.onSingleResult(list.get(0),true);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    if (listener != null){
                        listener.onSingleResult(null,false);
                    }
                }
            }
        });
    }

    public static void findTenProcessFood(final onFindProcessFoodListener listener){

        BmobQuery<ProcessedFood> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereGreaterThanOrEqualTo("number",0);
        bmobQuery.setLimit(10);
        bmobQuery.findObjects(new FindListener<ProcessedFood>() {
            @Override
            public void done(final List<ProcessedFood> list, BmobException e) {
                if(e==null){
                    if (listener != null){
                        Log.i(TAG, "findTenProcessFood: " + list.get(0).getName());
                        listener.onGroupResult(list,true);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                    if (listener != null){
                        listener.onGroupResult(null,false);
                    }
                }
            }
        });
    }

    public static void findProcessFoodByMaterial(final String material,final onFindProcessFoodListener listener){
        BmobQuery<ProcessedFood> query = new BmobQuery<>();
        query.addWhereStartsWith("ings_names", material);
        query.findObjects(new FindListener<ProcessedFood>() {
            @Override
            public void done(List<ProcessedFood> list, BmobException e) {
                if(e==null){
                    if (listener != null){
                        listener.onGroupResult(list,true);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                    if (listener != null){
                        listener.onGroupResult(null,false);
                    }
                }
            }
        });
    }

    public static void findProcessFoodByHot(int hot,final onFindProcessFoodWithPicListener listener){
        BmobQuery<ProcessedFood> query = new BmobQuery<>();
        query.addWhereGreaterThanOrEqualTo("clickTime", hot);
        query.setLimit(3);
        query.findObjects(new FindListener<ProcessedFood>() {
            @Override
            public void done(final List<ProcessedFood> list, BmobException e) {
                if(e==null){
                    if (listener != null){
                        new Thread(){
                            @Override
                            public void run() {
                                List<String> urls = new ArrayList<>();
                                for (ProcessedFood food:list) {
                                    urls.add(food.getCover_img());
                                }
                                List<Bitmap> bitmaps =  Utils.downLoadImgList(urls);
                                if (listener != null){
                                    Log.i(TAG, "onFind: " + bitmaps.size() +  " " + list.size());
                                    listener.onFind(list,bitmaps,true);
                                }
                            }
                        }.start();

                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                    if (listener != null){
                        listener.onFind(null,null,false);
                    }
                }
            }
        });
    }

    public static void getUserFavFood(CaiNiaoUser user, final onFindFavFoodListener listener){
        BmobQuery<ProcessedFood> query = new BmobQuery<>();
        query.addWhereRelatedTo("foodLikes",new BmobPointer(user));
        query.findObjects(new FindListener<ProcessedFood>() {
            @Override
            public void done(List<ProcessedFood> list, BmobException e) {
                if (e == null){
                    if (listener != null){
                        listener.onFind(list,true);
                    }
                    Log.i("bmob","查询个数："+list.size());
                }else {
                    if (listener != null){
                        listener.onFind(null,false);
                    }
                }
            }
        });
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
