package com.sin.cainiao.Activity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.util.Util;
import com.sin.cainiao.Adapter.ClAdapter;
import com.sin.cainiao.Adapter.ProcessClAdapter;
import com.sin.cainiao.DataHelper.FoodDataHelper;
import com.sin.cainiao.DataHelper.MaterialDataHelper;
import com.sin.cainiao.JavaBean.CaiNiaoUser;
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.CustomApplication;
import com.sin.cainiao.Utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ShowProcessFoodDetailActivity extends AppCompatActivity {
    private final static String TAG = "ShowProcessFoodDetailActivity";

    private CustomApplication app;
    private CaiNiaoUser user;

    private ImageView titleImage;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView_Desc;
    private WebView wv_step;


    private ProcessedFood mFood;
    private ProcessClAdapter mClAdapter;
    private Bitmap mTitleBitmap = null;

    private final static int SUCCESS = 1;
    private final static int ERROR = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS){
                titleImage.setImageBitmap(mTitleBitmap);

                mCollapsingToolbarLayout.setTitle(mFood.getName());
                mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

                String result = mFood.getDesc().replaceAll("。","\n");
                mTextView_Desc.setText(result);

                String test = mFood.getStep().replaceAll("\\[","").replaceAll("]","").replaceAll(",","");
                test = Utils.modifyImg(test);
                wv_step.loadData(test,"text/html; charset=UTF-8", null);


                mClAdapter.swapData(mFood.getIngs_names(),mFood.getIngs_units());
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_process_food_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);

        app = (CustomApplication)getApplication();
        user = app.getUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null){
                    CaiNiaoUser bmobUser = app.getUser();
                    BmobRelation relation = new BmobRelation();
                    relation.add(mFood);
                    bmobUser.setFoodLikes(relation);
                    bmobUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Utils.toastShow(getApplicationContext(),"收藏成功");
                            }else{
                                Utils.toastShow(getApplicationContext(),"收藏失败");
                            }
                        }
                    });

//                    mFood.setLikes(relation);
//                    mFood.update(new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if(e==null){
//                                Utils.toastShow(getApplicationContext(),"收藏成功");
//                            }else{
//                                Utils.toastShow(getApplicationContext(),"收藏失败");
//                            }
//                        }
//                    });


                }
            }
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("FOOD_ITEM_ID");



        titleImage = (ImageView)findViewById(R.id.title_img);
        titleImage.setImageResource(R.drawable.test_img);

        mRecyclerView = (RecyclerView)findViewById(R.id.rec_process_food_material);
        mTextView_Desc = (TextView)findViewById(R.id.tv_process_food_desc);

        wv_step = (WebView)findViewById(R.id.wv_process_food_step);

        setupItemList();
        getMaterial(id);
    }

    private void setupItemList(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mClAdapter = new ProcessClAdapter(this);
        mRecyclerView.setAdapter(mClAdapter);
        mClAdapter.setOnClItemClickListener(new ProcessClAdapter.onClItemClickListener() {
            @Override
            public void onClItemClick(String cl) {
                MaterialDataHelper.matchSuggestionsAndFindMaterials(getApplicationContext(), cl,
                        new MaterialDataHelper.onFindMaterialsStringListener() {
                            @Override
                            public void onResults(List<String> results) {
                                if (results.size() != 0 ){
                                    Intent intent = new Intent(ShowProcessFoodDetailActivity.this,SearchMaterialActivity.class);
                                    intent.putExtra("cl",(Serializable) results);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(getApplicationContext()
                                            ,"百科中尚未收录该种食材~",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void getMaterial(String id){
        FoodDataHelper.findProcessFoodByID(id, new FoodDataHelper.onFindProcessFoodListener() {
            @Override
            public void onSingleResult(ProcessedFood item,boolean status) {
                final Message msg = new Message();
                if(status){
                    mFood = item;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            mTitleBitmap = Utils.downLoadImg(mFood.getCover_img());
                            msg.what = SUCCESS;
                            mHandler.sendMessage(msg);
                        }
                    }.start();

                }else{
                    mFood = null;
                    msg.what = ERROR;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onGroupResult(List<ProcessedFood> foodList, boolean status) {
            }
        });

    }



}
