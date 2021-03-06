package com.sin.cainiao.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sin.cainiao.dataHelper.MaterialDataHelper;
import com.sin.cainiao.javaBean.Material;
import com.sin.cainiao.R;
import com.sin.cainiao.utils.Utils;

import java.util.Calendar;
import java.util.Random;


public class MaterialMainFragment extends Fragment {
    private static final String TAG = "MaterialMainFragment";

    private LinearLayout header;
    private ImageView today_img;
    private TextView today_name;
    private TextView today_desc;

    private Bitmap bitmap;

    private static final int LOAD_TODAY_SUCCESS = 1;
    private static final int ERROR = 0;
    private static final int LOAD_LIST_SUCCESS = 2;

    private MaterialMainFragmentCallBack callBack;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: " + msg.what);
            if (msg.what == LOAD_TODAY_SUCCESS){
                final Material material = (Material) msg.obj;
                today_img.setImageBitmap(bitmap);
                today_name.setText(getResources().getText(R.string.today_material)
                        + "----" + material.getName());
                today_desc.setText(material.getDesc());


                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callBack != null){
                            callBack.onMaterialCallBack(material);
                        }
                    }
                });
            }else if (msg.what == ERROR){

            }
            super.handleMessage(msg);
        }
    };

    public interface MaterialMainFragmentCallBack{
        void onMaterialCallBack(Material material);
    }

    public void setCallBack(MaterialMainFragmentCallBack callBack){
        this.callBack = callBack;
    }



    public static MaterialMainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MaterialMainFragment fragment = new MaterialMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.material_main_fragment,container,false);

        setupHeader(v);
        loadData();

        return v;
    }

    private void setupHeader(View v){
        header = (LinearLayout)v.findViewById(R.id.header_container);
        today_name = (TextView)v.findViewById(R.id.tv_today_name);
        today_img = (ImageView)v.findViewById(R.id.img_today_material);
        today_desc = (TextView)v.findViewById(R.id.tv_today_desc);
    }

    private void loadData(){
        Calendar now = Calendar.getInstance();
        Log.i(TAG, "loadData: " + now.get(Calendar.YEAR)+(now.get(Calendar.MONTH)+1)
                + now.get(Calendar.DAY_OF_MONTH));
        Random random = new Random(now.get(Calendar.YEAR)+(now.get(Calendar.MONTH)+1)
                + now.get(Calendar.DAY_OF_MONTH)+2);
        Integer number = random.nextInt(105);
        Log.i(TAG, "onClick: " + number);
        MaterialDataHelper.findMaterialByID(number, new MaterialDataHelper.onFindMaterialsListener() {
            @Override
            public void onResults(final Material result, boolean status) {
                if (status){
                    Log.i(TAG, "onSingleResult: " + result.getName());
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            Message msg = new Message();
                            msg.what = LOAD_TODAY_SUCCESS;
                            bitmap = Utils.downLoadImg(result.getPicUrl());
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    }.start();
                }else {
                    Message msg = new Message();
                    msg.what = ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }
}
