package com.sin.cainiao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.sin.cainiao.Adapter.ClAdapter;
import com.sin.cainiao.DataHelper.FoodDataHelper;
import com.sin.cainiao.DataHelper.MaterialDataHelper;
import com.sin.cainiao.JavaBean.Food;
import com.sin.cainiao.JavaBean.FoodItem;
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.Utils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class ShowDetailActivity extends AppCompatActivity {
    private final static String TAG = "ShowDetailActivity";
    private ImageView mImageView;
    private FoodItem.ShowapiResBodyBean mFood;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private List<String> materialList;
    private RecyclerView mRecyclerView;
    private ClAdapter mClAdapter;


    private final int DOWNLOAD_SUCCESS = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DOWNLOAD_SUCCESS){
                if (mFood.getBitmaps().size() != 0) {
                    mImageView.setImageBitmap(mFood.getBitmaps().get(0));
                }

                mCollapsingToolbarLayout.setTitle(mFood.getName());
                materialList = getMaterialByFoodItem(mFood);
                mClAdapter.swapData(materialList);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
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
        mImageView = (ImageView)findViewById(R.id.title_img);
        mImageView.setImageResource(R.drawable.test_img);

        mRecyclerView = (RecyclerView) findViewById(R.id.cl_rec);

        setupItemList();
        getMaterial(id);
    }

    private void setupItemList(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));   //Fuck this code
        mClAdapter = new ClAdapter(this);
        mRecyclerView.setAdapter(mClAdapter);
        mClAdapter.setOnClItemClickListener(new ClAdapter.onClItemClickListener() {
            @Override
            public void onClItemClick(String cl) {
                MaterialDataHelper.matchSuggestionsAndFindMaterials(getApplicationContext(), cl,
                        new MaterialDataHelper.onFindMaterialsStringListener() {
                    @Override
                    public void onResults(List<String> results) {
                        if (results.size() != 0 ){
                            Intent intent = new Intent(ShowDetailActivity.this,SearchMaterialActivity.class);
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
        FoodDataHelper.findFoodByID(id, new FoodDataHelper.onFindFoodsListener() {
            @Override
            public void onGroupResult(List<Food.ShowapiResBodyBean.CbListBean> foodList, boolean status) {}

            @Override
            public void onSingleResult(FoodItem.ShowapiResBodyBean item, boolean status) {
                if (status){
                    Message msg = new Message();
                    msg.what = DOWNLOAD_SUCCESS;
                    mFood = item;
                    mHandler.sendMessage(msg);
                    Log.i(TAG, "onSingleResult: " + item.getCl());
                }else {
                    Log.i(TAG, "onSingleResult: Error");
                }
            }
        });
    }

    private List<String> getMaterialByFoodItem(FoodItem.ShowapiResBodyBean foodItem){
        String materials = foodItem.getCl();
        String[] results = materials.split("[，。、【 ]");
        List<String> list = Utils.Array2List(results);

        //强行删除空格
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            if (iterator.next().equals(""))
                iterator.remove();
        }
        return list;
    }

}
