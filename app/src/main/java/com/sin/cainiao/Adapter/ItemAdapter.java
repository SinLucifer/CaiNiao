package com.sin.cainiao.adapter;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.sin.cainiao.javaBean.Item;
import com.sin.cainiao.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context mContext;
    private Bitmap mBitmap;

    private LruCache<String,BitmapDrawable> mMemoryCache;
    private int mLastAnimatedItemPosition = -1;

    private List<Item> itemList;

    public ItemAdapter(Context context){
        this.mContext = context;

        if(itemList == null){
            itemList = new ArrayList<>();
        }

        //缓存图片
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/6;
        mMemoryCache = new LruCache<String,BitmapDrawable>(cacheSize){
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                if (value.getBitmap() != null){
                    return value.getBitmap().getByteCount();
                }else {
                    return 0;
                }

            }
        };
    }

    public void swapData(List<Item> itemList){
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tv_item_name;
        public final TextView tv_item_price;
        public final TextView tv_item_desc;
        public final TextView tv_item_count;
        public final View mTextContainer;
        public final ImageView img_item_cover;

        public ViewHolder(View view){
            super(view);
            tv_item_name = (TextView)view.findViewById(R.id.tv_item_name);
            tv_item_desc = (TextView)view.findViewById(R.id.tv_item_desc);
            tv_item_price = (TextView)view.findViewById(R.id.tv_item_price);
            tv_item_count = (TextView) view.findViewById(R.id.tv_item_count);
            mTextContainer = view.findViewById(R.id.text_container);
            img_item_cover = (ImageView)view.findViewById(R.id.img_item_cover);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item mItem = itemList.get(position);
        holder.tv_item_name.setText(mItem.getName());
        holder.tv_item_desc.setText(mItem.getDesc());
        holder.tv_item_price.setText(mItem.getPrice().toString());
        holder.tv_item_count.setText(mItem.getCount().toString());
        if (mItem.getCover_url() != null){
            String imgUrl = mItem.getCover_url();
            BitmapDrawable drawable = getBitmapDrawableFromMemoryCache(imgUrl);
            if (drawable != null){
                holder.img_item_cover.setImageDrawable(drawable);
            }else if (cancelPotentialTask(imgUrl,holder.img_item_cover)){
                DownLoadTask task = new DownLoadTask(holder.img_item_cover,mItem);
                AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(),mBitmap,task);
                holder.img_item_cover.setImageDrawable(asyncDrawable);
                task.execute(imgUrl);
            }

        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
        private Item mItem;

        public DownLoadTask(ImageView imageView, Item mItem){
            imageWeakReference = new WeakReference<>(imageView);
            this.mItem = mItem;
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
