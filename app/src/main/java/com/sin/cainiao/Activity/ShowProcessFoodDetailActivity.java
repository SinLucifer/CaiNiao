package com.sin.cainiao.Activity;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.sin.cainiao.Adapter.ClAdapter;
import com.sin.cainiao.Adapter.ProcessClAdapter;
import com.sin.cainiao.DataHelper.FoodDataHelper;
import com.sin.cainiao.DataHelper.MaterialDataHelper;
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ShowProcessFoodDetailActivity extends AppCompatActivity {
    private final static String TAG = "ShowProcessFoodDetailActivity";
    private ImageView titleImage;
    private ProcessedFood mFood;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RecyclerView mRecyclerView;
    private ProcessClAdapter mClAdapter;
    private Bitmap mTitleBitmap = null;

    private final static int SUCCESS = 1;
    private final static int ERROR = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS){
                titleImage.setImageBitmap(mTitleBitmap);// TODO: 2016/9/17

                mCollapsingToolbarLayout.setTitle(mFood.getName());
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


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("FOOD_ITEM_ID");



        titleImage = (ImageView)findViewById(R.id.title_img);
        titleImage.setImageResource(R.drawable.test_img);

        mRecyclerView = (RecyclerView)findViewById(R.id.cl_rec);

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
