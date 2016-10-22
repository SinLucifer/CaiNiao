package com.sin.cainiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sin.cainiao.adapter.ProcessClAdapter;
import com.sin.cainiao.dataHelper.FoodDataHelper;
import com.sin.cainiao.dataHelper.MaterialDataHelper;
import com.sin.cainiao.javaBean.CaiNiaoUser;
import com.sin.cainiao.javaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.utils.CustomApplication;
import com.sin.cainiao.utils.Utils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ShowProcessFoodDetailActivity extends AppCompatActivity {
    private final static String TAG = "ShowProcessFoodDetailActivity";

    private CaiNiaoUser user;

    private LinearLayout ll_step_container;

    private ImageView titleImage;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView_Desc;

    private ProcessedFood mFood;
    private ProcessClAdapter mClAdapter;
    private Bitmap mTitleBitmap = null;

    private LayoutInflater inflater;

    private List<ViewHolder> ls_item;
    private List<View> ls_childView;

    private final static int SUCCESS = 1;
    private final static int STEP_IMG_SUCCESS = 2;
    private final static int COVER_SUCCESS = 3;
    private final static int ERROR = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS){
                mCollapsingToolbarLayout.setTitle(mFood.getName());
                mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

                String result = mFood.getDesc().replaceAll("。","\n");
                mTextView_Desc.setText(result);

                mClAdapter.swapData(mFood.getIngs_names(),mFood.getIngs_units());

                ls_childView = new ArrayList<>();
                ls_item = new ArrayList<>();

                for (int i = 0;i <mFood.getSteps().size();i++){
                    View childView = inflater.inflate(R.layout.show_item, null);
                    childView.setId(i);
                    getViewInstance(childView);
                    ll_step_container.addView(childView);
                }

                getCover();
                loadStepImg();
            }else if (msg.what == STEP_IMG_SUCCESS){
                List<Bitmap> bitmapList = (List<Bitmap>)msg.obj;
                for (int i = 0; i < ll_step_container.getChildCount(); i++) {
                    Bitmap bitmap = bitmapList.get(i);
                    if (bitmap != null){
                        ls_item.get(i).img_step_img.setImageBitmap(bitmap);
                    }else {
                        ls_item.get(i).img_step_img.setVisibility(View.GONE);
                    }

                }
            }else if (msg.what == COVER_SUCCESS){
                titleImage.setImageBitmap(mTitleBitmap);
            }else if (msg.what == ERROR){

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

        Intent intent = getIntent();

        Message msg = new Message();
        if (intent.getSerializableExtra("food") instanceof ProcessedFood){
            mFood = (ProcessedFood) intent.getSerializableExtra("food");
            msg.what = SUCCESS;
        }else {
            msg.what = ERROR;
        }

        mHandler.sendMessage(msg);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);

        CustomApplication app = (CustomApplication) getApplication();
        user = app.getUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null){
                    CaiNiaoUser bmobUser = BmobUser.getCurrentUser(CaiNiaoUser.class);
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
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    Utils.toastShow(getApplicationContext(),"请先登录哦~");
                }
            }
        });

        titleImage = (ImageView)findViewById(R.id.title_img);
        titleImage.setImageResource(R.mipmap.ic_launcher);

        mRecyclerView = (RecyclerView)findViewById(R.id.rec_process_food_material);
        mTextView_Desc = (TextView)findViewById(R.id.tv_process_food_desc);

        ll_step_container = (LinearLayout)findViewById(R.id.step_container);

        inflater = LayoutInflater.from(getApplicationContext());

        setupItemList();

        TextView tv_comment = (TextView)findViewById(R.id.tv_comment);
        tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowProcessFoodDetailActivity.this,CommentActivity.class);
                intent.putExtra("food",mFood);
                startActivity(intent);
            }
        });

        refreshClickTime();
    }

    private void refreshClickTime(){
        mFood.setClickTime(mFood.getClickTime()+1);
        mFood.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null){
                    Log.i(TAG, "done: clickTime update success");
                }else {
                    Log.i(TAG, "done: " + e.getMessage());
                }
            }
        });
    }

    private void getViewInstance(final View childView){
        ViewHolder vh = new ViewHolder();
        vh.id = childView.getId();
        vh.tv_step_content = (TextView) childView.findViewById(R.id.tv_step_content);
        vh.tv_step_number = (TextView) childView.findViewById(R.id.tv_num);
        vh.img_step_img = (ImageView) childView.findViewById(R.id.img_step);

        vh.tv_step_number.setText(vh.id+1+".");
        vh.tv_step_content.setText(mFood.getSteps().get(childView.getId()));

        ls_childView.add(childView);
        ls_item.add(vh);
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

    private void getCover(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                mTitleBitmap = Utils.downLoadImg(mFood.getCover_img());
                if (mTitleBitmap != null){
                    msg.what = COVER_SUCCESS;
                }else {
                    msg.what = ERROR;
                }
                mHandler.sendMessage(msg);
            }
        }.start();
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

    public class ViewHolder{
        private int id;
        private TextView tv_step_number;
        private TextView tv_step_content;
        private ImageView img_step_img;
    }

    private void loadStepImg(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                List<Bitmap> bitmaps = Utils.downLoadImgList(mFood.getSteps_img());
                if (bitmaps != null){
                    if (bitmaps.size() != 0){
                        msg.what = STEP_IMG_SUCCESS;
                        msg.obj = bitmaps;
                    }else {
                        msg.what = ERROR;
                    }
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }



}
