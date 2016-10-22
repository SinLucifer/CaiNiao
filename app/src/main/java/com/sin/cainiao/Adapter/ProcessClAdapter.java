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

public class ProcessClAdapter extends RecyclerView.Adapter<ProcessClAdapter.ViewHolder> {
    private final static String TAG = "ClAdapter";
    private List<String> name = new ArrayList<>();
    private List<String> unit = new ArrayList<>();
    private onClItemClickListener listener;


    public interface onClItemClickListener{
        void onClItemClick(String cl);
    }

    public void setOnClItemClickListener(onClItemClickListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView name;
        public final View mTextLayout;
        public final TextView unit;

        public ViewHolder(View view){
            super(view);
            name = (TextView)view.findViewById(R.id.tv_cl_show);
            unit = (TextView)view.findViewById(R.id.tv_cl_unit);
            mTextLayout = view.findViewById(R.id.cl_container);
        }
    }

    public ProcessClAdapter(Context context){
        Context mContext = context;
    }

    public void swapData(List<String> name,List<String> unit){
        this.name = name;
        this.unit = unit;
        Log.i(TAG, "swapData: " + name+ " " + unit);
        notifyDataSetChanged();
    }

    @Override
    public ProcessClAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_process_detail_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProcessClAdapter.ViewHolder holder, int position) {
        if (name.size() != 0){
            holder.name.setText(name.get(position));
            holder.unit.setText(unit.get(position));

            if (listener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClItemClick(name.get(holder.getAdapterPosition()));
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return name.size();
    }
}
