package com.sin.cainiao.DataHelper;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import com.sin.cainiao.Adapter.MaterialSuggestion;
import com.sin.cainiao.JavaBean.Material;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Sin on 2016/9/1.
 */
public class MaterialDataHelper {
    private static final String FOODS_FILE_NAME = "food.txt";
    private static final String TAG = "MaterialDateHelper";

    private static List<MaterialSuggestion> sMaterialSuggestions = new ArrayList<>();

    public interface onFindMaterialsListener {
        void onResults(List<Material> results, boolean status);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<MaterialSuggestion> results);
    }

    public interface onFindMaterialsStringListener {
        void onResults(List<String> results);
    }

    public static List<MaterialSuggestion> getHistory(Context context, int count) {
        //需要改的
        List<MaterialSuggestion> suggestionList = new ArrayList<>();
        MaterialSuggestion materialSuggestion;
        for (int i = 0; i < sMaterialSuggestions.size(); i++) {
            materialSuggestion = sMaterialSuggestions.get(i);
            materialSuggestion.setIsHistory(true);
            suggestionList.add(materialSuggestion);
            if (suggestionList.size() == count){
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (MaterialSuggestion materialSuggestion : sMaterialSuggestions) {
            materialSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener){
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                try{
                    Thread.sleep(simulatedDelay);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                MaterialDataHelper.resetSuggestionsHistory();
                List<MaterialSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)){
                    for (MaterialSuggestion suggestion : sMaterialSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())){
                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit){
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<MaterialSuggestion>() {
                    @Override
                    public int compare(MaterialSuggestion o1, MaterialSuggestion o2) {
                        return o1.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (listener != null){
                    listener.onResults((List<MaterialSuggestion>) results.values);
                }
            }
        }.filter(query);
    }

    public static void matchSuggestionsAndFindMaterials(Context context,String query
            ,final onFindMaterialsStringListener listener){
        List<String> suggestionList = new ArrayList<>();
        if (!(query == null || query.length() == 0)){
            for (MaterialSuggestion suggestion : sMaterialSuggestions) {
                if (query.toUpperCase().contains(suggestion.getBody().toUpperCase())){
                    suggestionList.add(suggestion.getBody());
                }
            }
        }

        Log.i(TAG, "matchSuggestionsAndFindMaterials: " + suggestionList);

        if (listener != null){
            listener.onResults(suggestionList);
        }
    }

    public static void findMaterials(Context context,String query,final onFindMaterialsListener listener){
        BmobQuery<Material> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("food_Name",query);
        bmobQuery.findObjects(new FindListener<Material>() {
            @Override
            public void done(List<Material> list, BmobException e) {
                if (e == null){
                    Log.i(TAG, "查询成功: " + list.get(0));
                    if (listener != null){
                        listener.onResults(list,true);
                    }
                }else {
                    Log.i(TAG, "查询失败"+e.getMessage()+e.getErrorCode());
                    if (listener != null){
                        listener.onResults(list,false);
                    }
                }
            }
        });
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

    public static String Json2Object(String jsonString){
        String result = "";

        try{
            JSONArray json = new JSONArray(jsonString);
            for (int i = 0; i < json.length(); i++) {
                sMaterialSuggestions.add(new MaterialSuggestion(json.getString(i)));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return result;
    }
}
