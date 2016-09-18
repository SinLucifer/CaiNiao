package com.sin.cainiao.Adapter;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sin.cainiao.JavaBean.ProcessedFood;

import java.util.ArrayList;
import java.util.List;


public class HeaderAdapter extends PagerAdapter {
    private final static String TAG = "HeaderAdapter";

    private List<ProcessedFood> foods;
    private List<Bitmap> bitmaps;
    private List<ImageView> imageList = new ArrayList<>();

    public HeaderAdapter(List<ImageView> imageList){
        this.imageList = imageList;
    }

    public void swapData(List<ProcessedFood> foods, List<Bitmap> bitmaps){
        this.bitmaps = bitmaps;
        this.foods = foods;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem: " + position);
        if (bitmaps != null){
            imageList.get(position).setImageBitmap(bitmaps.get(position));
        }
        container.addView(imageList.get(position));
        return imageList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
