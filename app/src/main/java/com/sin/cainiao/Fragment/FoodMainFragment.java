package com.sin.cainiao.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sin.cainiao.Activity.ShowProcessFoodDetailActivity;
import com.sin.cainiao.Adapter.HeaderAdapter;
import com.sin.cainiao.DataHelper.FoodDataHelper;
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class FoodMainFragment extends Fragment{
    private static final String TAG = "FoodMainFragment";
    private static final int LOAD_SUCCESS = 1;
    private static final int ERROR = 0;
    private static final int UPTATE_VIEWPAGER = 2;
    private int autoCurrIndex = 0; //now position

    private ImageView randomImg;
    private ViewPager viewPager;
    private HeaderAdapter mHeaderAdapter;

    private List<ImageView> imageViews;
    private List<ProcessedFood> foods;
    private List<Bitmap> food_imgs;

    private ImageView[] mBottomImgs;
    private LinearLayout mBottomLayout;
    private TextView tv_hot_name;

    private Timer timer = new Timer();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_SUCCESS){
                mHeaderAdapter.swapData(foods,food_imgs);
                tv_hot_name.setText("最热搜索：" + foods.get(0).getName());
            }else if (msg.what == UPTATE_VIEWPAGER){
                if (msg.arg1 != 0) {
                    viewPager.setCurrentItem(msg.arg1);
                } else {
                    //false 当从末页调到首页是，不显示翻页动画效果，
                    viewPager.setCurrentItem(msg.arg1);
                }
                tv_hot_name.setText("最热搜索：" + foods.get(msg.arg1).getName());
            }else {
                Log.i(TAG, "handleMessage: ERROR" );
            }
        }
    };

    public static FoodMainFragment newInstance() {
        
        Bundle args = new Bundle();
        FoodMainFragment fragment = new FoodMainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.food_main_fragment,container,false);

        randomImg = (ImageView)v.findViewById(R.id.img_random);
        randomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                Integer number = random.nextInt(200);
                Log.i(TAG, "onClick: " + number);
                Intent intent = new Intent(getActivity(), ShowProcessFoodDetailActivity.class);
                intent.putExtra("FOOD_ITEM_ID",number.toString());
                startActivity(intent);
            }
        });


        setupViewPager(v);
        loadHot();

        return v;
    }

    private void loadHot(){
        FoodDataHelper.findProcessFoodByHot(50, new FoodDataHelper.onFindProcessFoodWithPicListener() {
            @Override
            public void onFind(List<ProcessedFood> fooList, List<Bitmap> bitmaps, boolean status) {
                Message msg = new Message();
                if (status){
                    foods = fooList;
                    food_imgs = bitmaps;
                    msg.what = LOAD_SUCCESS;
                }else {
                    msg.what = ERROR;
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    private void setupViewPager(View v) {

        imageViews = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ImageView view = new ImageView(getActivity());
            view.setImageResource(R.mipmap.ic_launcher);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViews.add(view);
        }

        viewPager = (ViewPager) v.findViewById(R.id.vp_hottest);
        mHeaderAdapter = new HeaderAdapter(imageViews);
        viewPager.setAdapter(mHeaderAdapter);

        mBottomImgs = new ImageView[3];

        for (int i = 0; i < mBottomImgs.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
            params.setMargins(8, 0, 8, 0);
            imageView.setLayoutParams(params);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.dot_select);
            } else {
                imageView.setBackgroundResource(R.drawable.dot_not_select);
            }

            mBottomImgs[i] = imageView;

            mBottomLayout = (LinearLayout) v.findViewById(R.id.ll_hottest_indicator);

            mBottomLayout.addView(mBottomImgs[i]);
        }

        tv_hot_name = (TextView)v.findViewById(R.id.tv_hot_name);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int total = mBottomImgs.length;
                for (int j = 0; j < total; j++) {
                    if (j == position) {
                        mBottomImgs[j].setBackgroundResource(R.drawable.dot_select);
                    } else {
                        mBottomImgs[j].setBackgroundResource(R.drawable.dot_not_select);
                    }
                    tv_hot_name.setText("最热搜索：" + foods.get(position).getName());
                }

                autoCurrIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = UPTATE_VIEWPAGER;
                if (autoCurrIndex == foods.size() - 1) {
                    autoCurrIndex = -1;
                }
                msg.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(msg);
            }
        },5000,5000);
    }
}
