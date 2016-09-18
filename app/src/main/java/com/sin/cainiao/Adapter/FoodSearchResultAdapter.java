package com.sin.cainiao.Adapter;

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
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.JavaBean.Food;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FoodSearchResultAdapter extends RecyclerView.Adapter<FoodSearchResultAdapter.ViewHolder> {
    private Context mContext;
    private Bitmap mBitmap;
    private onOldFoodItemClickListener onOldFoodItemClickListener;
    private onNewFoodItemClickListener onNewFoodItemClickListener;
    private boolean mode = true; //true == oldFood

    private List<Food.ShowapiResBodyBean.CbListBean> mFoodList = new ArrayList<>();
    private List<ProcessedFood> mProcessedFoodList = new ArrayList<>();

    private int mLastAnimatedItemPosition = -1;

    private LruCache<String,BitmapDrawable> mMemoryCache;

    public interface onOldFoodItemClickListener{
        void onClick(Food.ShowapiResBodyBean.CbListBean food);
    }

    public interface onNewFoodItemClickListener{
        void onClick(ProcessedFood food);
    }

    public void setOnOldFoodItemClickListener(onOldFoodItemClickListener listener){
        this.onOldFoodItemClickListener = listener;
    }

    public void setOnNewFoodItemClickListener(onNewFoodItemClickListener listener){
        this.onNewFoodItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mFoodName;
        public final TextView mFoodCl;
        public final TextView mFoodZf;
        public final View mTextContainer;
        public final ImageView mFoodImg;

        public ViewHolder(View view){
            super(view);
            mFoodName = (TextView)view.findViewById(R.id.food_name);
            mFoodCl = (TextView)view.findViewById(R.id.food_cl);
            mFoodZf = (TextView)view.findViewById(R.id.food_zf);
            mTextContainer = view.findViewById(R.id.container);
            mFoodImg = (ImageView)view.findViewById(R.id.food_img);
        }
    }

    public FoodSearchResultAdapter(Context context){
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

    public void swapOldData(List<Food.ShowapiResBodyBean.CbListBean> mNewList){
        mFoodList = mNewList;
        this.mode = true;
        notifyDataSetChanged();
    }

    public void swapProcessedData(List<ProcessedFood> mNewList){
        mProcessedFoodList = mNewList;
        this.mode = false;
        notifyDataSetChanged();
    }

    @Override
    public FoodSearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodSearchResultAdapter.ViewHolder holder, final int position) {
        if (mode){
            final Food.ShowapiResBodyBean.CbListBean mFood = mFoodList.get(position);
            holder.mFoodName.setText(mFood.getName());
            //holder.mFoodZf.setText(mFood.getZf());
            holder.mFoodCl.setText(mFood.getCl());
            if (mFood.getImgList() != null){
                if (mFood.getImgList().size() != 0){
                    String imgUrl = mFood.getImgList().get(0).getImgUrl();
                    BitmapDrawable drawable = getBitmapDrawableFromMemoryCache(imgUrl);
                    if (drawable != null){
                        holder.mFoodImg.setImageDrawable(drawable);
                    }else if (cancelPotentialTask(imgUrl,holder.mFoodImg)){
                        DownLoadTask task = new DownLoadTask(holder.mFoodImg,mFood);
                        AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(),mBitmap,task);
                        holder.mFoodImg.setImageDrawable(asyncDrawable);
                        task.execute(imgUrl);
                    }
                }
            }

            if(mLastAnimatedItemPosition < position){
                animateItem(holder.itemView);
                mLastAnimatedItemPosition = position;
            }

            if (onOldFoodItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onOldFoodItemClickListener.onClick(mFoodList.get(position));
                    }
                });
            }
        }else {
            final ProcessedFood mFood = mProcessedFoodList.get(position);
            holder.mFoodName.setText(mFood.getName());
            String temp = "";
            for (String cl : mFood.getIngs_names()){
                temp = temp + cl + "、";
            }
            holder.mFoodCl.setText(temp);

            if (mFood.getCover_img() != null){
                String imgUrl = mFood.getCover_img();
                BitmapDrawable drawable = getBitmapDrawableFromMemoryCache(imgUrl);
                if (drawable != null){
                    holder.mFoodImg.setImageDrawable(drawable);
                }else if (cancelPotentialTask(imgUrl,holder.mFoodImg)){
                    DownLoadTask task = new DownLoadTask(holder.mFoodImg,mFood);
                    AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(),mBitmap,task);
                    holder.mFoodImg.setImageDrawable(asyncDrawable);
                    task.execute(imgUrl);
                }

            }

            if(mLastAnimatedItemPosition < position){
                animateItem(holder.itemView);
                mLastAnimatedItemPosition = position;
            }

            if (onNewFoodItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onNewFoodItemClickListener.onClick(mProcessedFoodList.get(position));
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mode){
            return mFoodList.size();
        }else {
            return mProcessedFoodList.size();
        }
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

        public AsyncDrawable(Resources resources,Bitmap bitmap,DownLoadTask doLoadTask){
            super(resources,bitmap);
            downLoadTaskWeakReference = new WeakReference<DownLoadTask>(doLoadTask);
        }

        private DownLoadTask getDownLoadTaskFromAsyncDrawable(){
            return downLoadTaskWeakReference.get();
        }
    }

    class DownLoadTask extends AsyncTask<String,Void,BitmapDrawable>{
        String url;
        private WeakReference<ImageView> imageWeakReference;
        private Food.ShowapiResBodyBean.CbListBean mFood;
        private ProcessedFood mBmobFood;

        public DownLoadTask(ImageView imageView, Food.ShowapiResBodyBean.CbListBean food){
            imageWeakReference = new WeakReference<ImageView>(imageView);
            this.mFood = food;
        }

        public DownLoadTask(ImageView imageView, ProcessedFood food){
            imageWeakReference = new WeakReference<ImageView>(imageView);
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
