package com.sin.cainiao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;
import com.sin.cainiao.javaBean.ProcessedFood;
import com.sin.cainiao.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
    private final static String TAG = "FoodListAdapter";
    private final Context mContext;
    private Bitmap mBitmap;

    private onFoodItemClickListener listener;

    private int mLastAnimatedItemPosition = -1;

    private LruCache<String,BitmapDrawable> mMemoryCache;

    private List<ProcessedFood> mProcessedFoodList = new ArrayList<>();

    public interface onFoodItemClickListener{
        void onClick(ProcessedFood food);
    }

    public void setOnFoodItemClickListener(onFoodItemClickListener listener){
        this.listener = listener;
    }

    public FoodListAdapter(Context context){
        this.mContext = context;

        //缓存图片
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/6;
        mMemoryCache = new LruCache<String,BitmapDrawable>(cacheSize){
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }
        };
    }

    public void swapData(List<ProcessedFood> mNewList){
        mProcessedFoodList = mNewList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public final ImageView mFoodImg;
        public final View mTextLayout;
        public final TextView name;

        public ViewHolder(View view){
            super(view);
            mFoodImg = (ImageView) view.findViewById(R.id.item_food_img);
            name = (TextView)view.findViewById(R.id.item_food_name);
            mTextLayout = view.findViewById(R.id.item_food_container);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_main_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: " + mProcessedFoodList.size());
        if (mProcessedFoodList.size() != 0) {
            final ProcessedFood mFood = mProcessedFoodList.get(holder.getAdapterPosition());
            holder.name.setText(mFood.getName());

            if (mFood.getCover_img() != null) {
                String imgUrl = mFood.getCover_img();
                BitmapDrawable drawable = getBitmapDrawableFromMemoryCache(imgUrl);
                if (drawable != null) {
                    holder.mFoodImg.setImageDrawable(drawable);
                } else if (cancelPotentialTask(imgUrl, holder.mFoodImg)) {
                    DownLoadTask task = new DownLoadTask(holder.mFoodImg, mFood);
                    AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), mBitmap, task);
                    holder.mFoodImg.setImageDrawable(asyncDrawable);
                    task.execute(imgUrl);
                }
            }
        }

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        if (listener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(mProcessedFoodList.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mProcessedFoodList.size();
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    private boolean cancelPotentialTask(String imageUrl, ImageView imageView){
        DownLoadTask task = getDownTask(imageView);
        if(task != null){
            String url = task.url;
            if (url == null || url.equals(imageUrl)){
                task.cancel(true);
            }else {
                return false;
            }
        }
        return true;
    }

    private BitmapDrawable getBitmapDrawableFromMemoryCache(String imageUrl){
        return mMemoryCache.get(imageUrl);
    }

    private void addBitmapDrawableToMemoryCache(String imageUrl,BitmapDrawable drawable){
        if (getBitmapDrawableFromMemoryCache(imageUrl) == null){
            mMemoryCache.put(imageUrl,drawable);
        }
    }

    private DownLoadTask getDownTask(ImageView imageView){
        if (imageView != null){
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable)drawable).getDownLoadTaskFromAsyncDrawable();
            }
        }
        return null;
    }

    class AsyncDrawable extends BitmapDrawable{
        private WeakReference<DownLoadTask> downLoadTaskWeakReference;

        public AsyncDrawable(Resources resources, Bitmap bitmap, DownLoadTask doLoadTask){
            super(resources,bitmap);
            downLoadTaskWeakReference = new WeakReference<>(doLoadTask);
        }

        private DownLoadTask getDownLoadTaskFromAsyncDrawable(){
            return downLoadTaskWeakReference.get();
        }
    }

    class DownLoadTask extends AsyncTask<String,Void,BitmapDrawable> {
        String url;
        private WeakReference<ImageView> imageWeakReference;
        private ProcessedFood mBmobFood;


        public DownLoadTask(ImageView imageView, ProcessedFood food){
            imageWeakReference = new WeakReference<>(imageView);
            this.mBmobFood = food;
        }

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            url = params[0];
            Bitmap bitmap = downLoadBitmap(url);
            BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),bitmap);
            addBitmapDrawableToMemoryCache(url,drawable);

            return drawable;
        }

        private ImageView getAttachedImageView(){
            ImageView imageView = imageWeakReference.get();
            if (imageView != null){
                DownLoadTask task = getDownTask(imageView);
                if (this == task){
                    return imageView;
                }
            }
            return null;
        }

        private Bitmap downLoadBitmap(String url){
            Bitmap bitmap = null;
            try{
                URL imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.connect();
                Log.i("FoodSearchResultAdapter", "downLoadBitmap: " + url);
                InputStream inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }catch (IOException e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
            super.onPostExecute(bitmapDrawable);
            ImageView imageView = getAttachedImageView();
            if (imageView != null && bitmapDrawable != null){
                imageView.setImageDrawable(bitmapDrawable);
            }
        }
    }
}
