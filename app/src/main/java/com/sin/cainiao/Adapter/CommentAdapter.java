package com.sin.cainiao.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sin.cainiao.JavaBean.Comment;
import com.sin.cainiao.JavaBean.Item;
import com.sin.cainiao.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sin on 2016/10/12.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private Bitmap mBitmap;

    private LruCache<String,BitmapDrawable> mMemoryCache;
    private int mLastAnimatedItemPosition = -1;

    private List<Comment> commentList;

    public CommentAdapter(Context context){
        this.mContext = context;

        if(commentList == null){
            commentList = new ArrayList<>();
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

    public void swapData(List<Comment> commentList){
        this.commentList = commentList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tv_user_name;
        public final TextView tv_content;
        public final TextView tv_date;
        public final View mTextContainer;
        public final CircleImageView img_user_cover;
        public final ImageView img_thumb;

        public ViewHolder(View view){
            super(view);
            tv_user_name = (TextView)view.findViewById(R.id.tv_user_name);
            tv_content = (TextView)view.findViewById(R.id.tv_content);
            tv_date = (TextView)view.findViewById(R.id.tv_date);
            mTextContainer = view.findViewById(R.id.text_container);
            img_user_cover = (CircleImageView)view.findViewById(R.id.img_user_cover);
            img_thumb = (ImageView)view.findViewById(R.id.img_thumb);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment mComment = commentList.get(position);

        if (mComment.getUser().getNick() == null){
            holder.tv_user_name.setText(mComment.getUser().getUsername());
        }else {
            holder.tv_user_name.setText(mComment.getUser().getNick());
        }

        holder.tv_content.setText(mComment.getContent());
        holder.tv_date.setText(mComment.getCreatedAt());
        if (mComment.getUser() != null){
            String imgUrl = mComment.getUser().getUser_cover();
            if (imgUrl != null){
                BitmapDrawable drawable = getBitmapDrawableFromMemoryCache(imgUrl);
                if (drawable != null){
                    holder.img_user_cover.setImageDrawable(drawable);
                }else if (cancelPotentialTask(imgUrl,holder.img_user_cover)){
                    DownLoadTask task = new DownLoadTask(holder.img_user_cover,mComment);
                    AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(),mBitmap,task);
                    holder.img_user_cover.setImageDrawable(asyncDrawable);
                    task.execute(imgUrl);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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
            downLoadTaskWeakReference = new WeakReference<DownLoadTask>(doLoadTask);
        }

        private DownLoadTask getDownLoadTaskFromAsyncDrawable(){
            return downLoadTaskWeakReference.get();
        }
    }

    class DownLoadTask extends AsyncTask<String,Void,BitmapDrawable> {
        String url;
        private WeakReference<ImageView> imageWeakReference;
        private Comment mComment;

        public DownLoadTask(ImageView imageView, Comment mComment){
            imageWeakReference = new WeakReference<ImageView>(imageView);
            this.mComment = mComment;
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
