package com.sin.cainiao.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sin.cainiao.Activity.SearchFoodActivity;
import com.sin.cainiao.Activity.ShowProcessFoodDetailActivity;
import com.sin.cainiao.Adapter.FoodListAdapter;
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
    private static final int LOAD_LIST_SUCCESS = 3;
    private int autoCurrIndex = 0; //now position

    private ImageView randomImg;
    private ViewPager viewPager;

    private HeaderAdapter mHeaderAdapter;
    private FoodListAdapter listAdapter;

    private List<ImageView> imageViews;
    private List<ProcessedFood> foods;
    private List<Bitmap> food_imgs;
    private List<ProcessedFood> ten_foodList;

    private ImageView[] mBottomImgs;
    private LinearLayout mBottomLayout;
    private TextView tv_hot_name;
    private RecyclerView mRecycler_food_list;

    private NestedScrollView mNestedScrollView;

    private Timer timer = new Timer();

    private FoodFragmentCallBack mCallBack;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_SUCCESS){
                mHeaderAdapter.swapData(food_imgs);
                tv_hot_name.setText("最热搜索：" + foods.get(0).getName());
            }else if (msg.what == UPTATE_VIEWPAGER){
                if (msg.arg1 != 0) {
                    viewPager.setCurrentItem(msg.arg1);
                } else {
                    //false 当从末页调到首页是，不显示翻页动画效果，
                    viewPager.setCurrentItem(msg.arg1);
                }
                tv_hot_name.setText("最热搜索：" + foods.get(msg.arg1).getName());
            }else if (msg.what == LOAD_LIST_SUCCESS) {
                listAdapter.swapData(ten_foodList);
            }else{
                Log.i(TAG, "handleMessage: ERROR" );
            }
        }
    };

    public interface FoodFragmentCallBack{
        void onFoodCallBack(String id);
    }

    public static FoodMainFragment newInstance() {
        Bundle args = new Bundle();
        FoodMainFragment fragment = new FoodMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCallBack(FoodFragmentCallBack callBack){
        this.mCallBack = callBack;
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



        setupView(v);
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

    private void setupView(View v){
        tv_hot_name = (TextView)v.findViewById(R.id.tv_hot_name);

        mRecycler_food_list = (RecyclerView)v.findViewById(R.id.rec_food_list);
        mRecycler_food_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        listAdapter = new FoodListAdapter(getContext());
        listAdapter.setOnFoodItemClickListener(new FoodListAdapter.onFoodItemClickListener() {
            @Override
            public void onClick(ProcessedFood food) {
                if (mCallBack != null){
                    Integer number = food.getNumber();
                    mCallBack.onFoodCallBack(number.toString());
                }else {
                    Log.i(TAG, "handleMessage: ERROR");
                }
            }
        });

        mRecycler_food_list.setAdapter(listAdapter);

        FoodDataHelper.findTenProcessFood(new FoodDataHelper.onFindProcessFoodListener() {
            @Override
            public void onSingleResult(ProcessedFood item, boolean status) {}

            @Override
            public void onGroupResult(List<ProcessedFood> foodList, boolean status) {
                Message msg = new Message();
                if (status){
                    msg.what = LOAD_LIST_SUCCESS;
                    ten_foodList = foodList;
                }else{
                    msg.what = ERROR;
                }

                mHandler.sendMessage(msg);
            }
        });

        mNestedScrollView = (NestedScrollView)v.findViewById(R.id.sv_food_main);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {   //解决起始滑动界面
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mNestedScrollView != null){
            mNestedScrollView.smoothScrollTo(0,20);
        }
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

        mHeaderAdapter.setOnItemClickListener(new HeaderAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),ShowProcessFoodDetailActivity.class);
                Integer id = foods.get(position).getNumber();
                intent.putExtra("FOOD_ITEM_ID",id.toString());
                startActivity(intent);
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = UPTATE_VIEWPAGER;
                if (foods != null){
                    if (autoCurrIndex == foods.size() - 1) {
                        autoCurrIndex = -1;
                    }
                    msg.arg1 = autoCurrIndex + 1;
                    mHandler.sendMessage(msg);
                }
            }
        },5000,5000);
    }
}
