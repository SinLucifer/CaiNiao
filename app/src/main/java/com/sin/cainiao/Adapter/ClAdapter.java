package com.sin.cainiao.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sin.cainiao.R;

import java.util.ArrayList;
import java.util.List;


public class ClAdapter extends RecyclerView.Adapter<ClAdapter.ViewHolder> {
    private final static String TAG = "ClAdapter";
    private List<String> mClList = new ArrayList<>();
    private onClItemClickListener listener;


    public interface onClItemClickListener{
        void onClItemClick(String cl);
    }

    public void setOnClItemClickListener(onClItemClickListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTextView;
        public final View mTextLayout;

        public ViewHolder(View view){
            super(view);
            mTextView = (TextView)view.findViewById(R.id.tv_cl_show);
            mTextLayout = view.findViewById(R.id.cl_container);
        }
    }

    public ClAdapter(Context context){
        Context mContext = context;
    }

    public void swapData(List<String> list){
        this.mClList = list;
        Log.i(TAG, "swapData: " + list);
        notifyDataSetChanged();
    }

    @Override
    public ClAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_detail_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ClAdapter.ViewHolder holder, int position) {
        if (mClList.size() != 0){
            holder.mTextView.setText(mClList.get(position));

            if (listener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClItemClick(mClList.get(holder.getAdapterPosition()));
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mClList.size();
    }
}
