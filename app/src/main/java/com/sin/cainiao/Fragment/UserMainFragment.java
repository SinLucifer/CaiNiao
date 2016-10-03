package com.sin.cainiao.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sin.cainiao.Adapter.ClAdapter;
import com.sin.cainiao.Adapter.FoodSearchResultAdapter;
import com.sin.cainiao.DataHelper.FoodDataHelper;
import com.sin.cainiao.JavaBean.CaiNiaoUser;
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.CustomApplication;
import com.sin.cainiao.Utils.WrapContentHeightViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.id.list;


public class UserMainFragment extends Fragment{
    private static final String TAG = "UserMainFragment";
    private static CustomApplication app;

    private NestedScrollView mNestedScrollView;

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
        View v = inflater.inflate(R.layout.user_main_fragment,container,false);

        setupView(v);
        return v;
    }

    private void setupView(View view){
        WrapContentHeightViewPager mViewPager = (WrapContentHeightViewPager) view.findViewById(R.id.fav_container);

        TextView tv_user_name = (TextView)view.findViewById(R.id.tv_user_name);
        tv_user_name.setText(app.getUser().getUsername());
        CircleImageView img_ico = (CircleImageView)view.findViewById(R.id.user_ico);

        List<Fragment> fragmentList = new ArrayList<>();

        FavFragment fav_food = FavFragment.newInstance("food");
        FavFragment fav_material = FavFragment.newInstance("material");

        fragmentList.add(fav_food);
        fragmentList.add(fav_material);

        ItemAdapter itemAdapter = new ItemAdapter(getActivity().getSupportFragmentManager(),fragmentList);
        mViewPager.setAdapter(itemAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mNestedScrollView = (NestedScrollView)view.findViewById(R.id.sv_user_main);
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
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
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
                FoodDataHelper.getUserFavFood(app.getUser(), new FoodDataHelper.onFindFavFoodListener() {
                    @Override
                    public void onFind(List<ProcessedFood> foodList, boolean status) {
                        Message msg = new Message();
                        msg.what = FOOD_SUCCESS;
                        msg.obj = foodList;
                        mHandler.sendMessage(msg);
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
    }

    
}
