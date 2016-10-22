package com.sin.cainiao.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sin.cainiao.activity.ShowProcessFoodDetailActivity;
import com.sin.cainiao.adapter.ClAdapter;
import com.sin.cainiao.adapter.FoodSearchResultAdapter;
import com.sin.cainiao.dataHelper.FoodDataHelper;
import com.sin.cainiao.javaBean.CaiNiaoUser;
import com.sin.cainiao.javaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.utils.CustomApplication;
import com.sin.cainiao.utils.Utils;
import com.sin.cainiao.utils.View.WrapContentHeightViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserMainFragment extends Fragment{
    private static final String TAG = "UserMainFragment";
    private static final int SUCCESS = 1;
    private static final int ERROR = 0;

    private static CustomApplication app;

    private CaiNiaoUser user;

    private NestedScrollView mNestedScrollView;

    private UserMainFragmentCallBack mCallBack;

    private Bitmap bitmap;
    private CircleImageView img_ico;
    private TextView tv_user_desc;
    private TextView tv_nick_name;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: ");
            switch (msg.what){
                case SUCCESS:
                    img_ico.setImageBitmap(bitmap);
                    break;
                case ERROR:
                    break;
            }
        }
    };

    public interface UserMainFragmentCallBack{
        void onUserCallBack();
    }

    public static UserMainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        UserMainFragment fragment = new UserMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (CustomApplication)getActivity().getApplication();
        user = BmobUser.getCurrentUser(CaiNiaoUser.class);
        Log.i(TAG, "onCreate: " + user.getShopkeeper());

        if (getActivity() instanceof UserMainFragmentCallBack){
            mCallBack = (UserMainFragmentCallBack)getActivity();
        }else{
            throw new RuntimeException(getActivity().toString()
                    + " must implement UserMainFragmentCallBack");
        }
    }

    private void loadUserImg(final String url){
        new Thread(){
            @Override
            public void run() {
                super.run();
                bitmap = Utils.downLoadImg(url);
                Log.i(TAG, "run: " + bitmap);
                Message msg = new Message();
                msg.what = SUCCESS;
                mHandler.sendMessage(msg);
            }
        }.start();
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            Log.i(TAG, "setUserVisibleHint: Visiable");
            mNestedScrollView.smoothScrollTo(0,20);
        }
    }
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;

        Log.i(TAG, "onCreateView: ");

        if (!user.getShopkeeper()){
            v = inflater.inflate(R.layout.fragment_user_main,container,false);
        }else{
            v = inflater.inflate(R.layout.fragment_shop_user_main,container,false);
            Button bn_my_shop = (Button) v.findViewById(R.id.bn_my_shop);
            bn_my_shop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onUserCallBack();
                }
            });
        }


        setupView(v);
        return v;
    }

    private void update(){
        if (app.getUser().getDesc() == null){
            tv_user_desc.setText("这家伙很懒，什么都没写");
        }else {
            tv_user_desc.setText(app.getUser().getDesc());
        }

        if (app.getUser().getNick() == null){
            tv_nick_name.setText(app.getUser().getUsername());
        }else {
            tv_nick_name.setText(app.getUser().getNick());
        }
    }

    private void setupView(View view){
        WrapContentHeightViewPager mViewPager = (WrapContentHeightViewPager) view.findViewById(R.id.fav_container);

        mNestedScrollView = (NestedScrollView)view.findViewById(R.id.sv_user_main);

        tv_user_desc = (TextView)view.findViewById(R.id.tv_user_desc);


        tv_nick_name = (TextView)view.findViewById(R.id.tv_nick_name);


        img_ico = (CircleImageView)view.findViewById(R.id.user_ico);

        if (user != null){
            if (user.getUser_cover() != null){
                Log.i(TAG, "setupView: download" + user.getUser_cover());
                loadUserImg(user.getUser_cover());
            }
            update();
        }



        List<Fragment> fragmentList = new ArrayList<>();

        FavFragment fav_food = FavFragment.newInstance("food");
        FavFragment fav_material = FavFragment.newInstance("material");

        fragmentList.add(fav_food);
        fragmentList.add(fav_material);

        ItemAdapter itemAdapter = new ItemAdapter(getActivity().getSupportFragmentManager(),fragmentList);
        mViewPager.setAdapter(itemAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mNestedScrollView.scrollTo(0,20);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(mViewPager);


    }

    private class ItemAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList;

        public ItemAdapter(FragmentManager fm,List<Fragment> fragments) {
            super(fm);
            this.fragmentList = fragments;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "我关注的菜谱";
                case 1:
                    return "我关注的百科";
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

    public static class FavFragment extends Fragment {

        private static final int ERROR = 0;
        private static final int FOOD_SUCCESS = 1;

        private FoodSearchResultAdapter foodAdapter;
        private ClAdapter clAdapter;

        private Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case FOOD_SUCCESS:
                        foodAdapter.swapProcessedData((List<ProcessedFood>)msg.obj);
                        Log.i(TAG, "handleMessage: SWAP");
                        break;
                    case ERROR:
                        Log.i(TAG, "handleMessage: ERROR");
                }
            }
        };

        private boolean mode = true;
        public FavFragment() {
        }

        public static FavFragment newInstance(String mode) {
            FavFragment fragment = new FavFragment();
            Bundle args = new Bundle();
            args.putString("mode",mode);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser){
                if (mode){
                    searchFavFood();
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fav, container, false);
            if (getArguments() != null){
                switch (getArguments().getString("mode")){
                    case "food":
                        mode = true;
                        break;
                    case "material":
                        mode = false;
                        break;
                    default:
                        break;
                }
            }

            RecyclerView mRecyclerView = (RecyclerView)rootView.findViewById(R.id.rec_fav);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            if (mode){
                foodAdapter = new FoodSearchResultAdapter(getContext());
                mRecyclerView.setAdapter(foodAdapter);
//                List<ProcessedFood> foodList = new ArrayList<>();
//                adapter.swapProcessedData(foodList);
                searchFavFood();
                foodAdapter.setOnNewFoodItemClickListener(new FoodSearchResultAdapter.onNewFoodItemClickListener() {
                    @Override
                    public void onClick(ProcessedFood food) {
                        Intent intent = new Intent(getActivity(), ShowProcessFoodDetailActivity.class);
                        intent.putExtra("food",food);
                        startActivity(intent);
                    }
                });
            }else {
                clAdapter = new ClAdapter(getContext());
                mRecyclerView.setAdapter(clAdapter);
//                List<Material> materialList = (List<Material>)BmobUser.getObjectByKey("materialListID");
//                adapter.swapData(materialList);
            }

            return rootView;
        }

        private void searchFavFood(){
            FoodDataHelper.getUserFavFood(app.getUser(), new FoodDataHelper.onFindFavFoodListener() {
                @Override
                public void onFind(List<ProcessedFood> foodList, boolean status) {
                    Message msg = new Message();
                    msg.what = FOOD_SUCCESS;
                    msg.obj = foodList;
                    mHandler.sendMessage(msg);
                }
            });
        }
    }

    
}
