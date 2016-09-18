package com.sin.cainiao;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.sin.cainiao.Activity.SearchFoodActivity;
import com.sin.cainiao.Activity.SearchMaterialActivity;
import com.sin.cainiao.Activity.ShowDetailActivity;
import com.sin.cainiao.Adapter.MainFragmentAdapter;
import com.sin.cainiao.Fragment.FoodMainFragment;
import com.sin.cainiao.Fragment.MaterialMainFragment;
import com.sin.cainiao.Fragment.UserMainFragment;
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.Utils.Key;
import com.sin.cainiao.DataHelper.MaterialDataHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private MainFragmentAdapter mainFragmentAdapter;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private BottomNavigation bottomNavigation;
    private ImageView mSearchImg;

    private FoodMainFragment foodMainFragment;
    private MaterialMainFragment materialMainFragment;
    private UserMainFragment userMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.parseColor("#f4511e"));
        setSupportActionBar(toolbar);

        initBmob();
        setupViewPager();
        setupBottomNavigation();
        setupImageView();
    }

    private void initBmob(){
        Bmob.initialize(this, Key.BMOB_KEY);
        MaterialDataHelper.Json2Object(MaterialDataHelper.loadTxt(getApplicationContext()));
    }

    private void setupBottomNavigation(){
        bottomNavigation = (BottomNavigation)findViewById(R.id.BottomNavigation);
        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes int i, int i1) {
                mViewPager.setCurrentItem(i1);
                if (i1 == 0){
                    toolbar.setBackgroundColor(Color.parseColor("#f4511e"));
                }else if (i1 == 1){
                    toolbar.setBackgroundColor(Color.parseColor("#00acc1"));
                }else {
                    toolbar.setBackgroundColor(Color.parseColor("#7eb499"));
                }
            }

            @Override
            public void onMenuItemReselect(@IdRes int i, int i1) { }
        });
    }

    private void setupViewPager(){

        List<Fragment> fragmentList = new ArrayList<>();
        foodMainFragment = FoodMainFragment.newInstance();
        materialMainFragment = MaterialMainFragment.newInstance();
        userMainFragment = UserMainFragment.newInstance();

        fragmentList.add(foodMainFragment);
        fragmentList.add(materialMainFragment);
        fragmentList.add(userMainFragment);

        mainFragmentAdapter = new MainFragmentAdapter(fragmentList,getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mainFragmentAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.setSelectedIndex(position,true);
                if (position == 0){
                    toolbar.setBackgroundColor(Color.parseColor("#f4511e"));
                    mSearchImg.setVisibility(View.VISIBLE);
                }else if (position == 1){
                    toolbar.setBackgroundColor(Color.parseColor("#00acc1"));
                    mSearchImg.setVisibility(View.VISIBLE);
                }else if(position == 2){
                    toolbar.setBackgroundColor(Color.parseColor("#7eb499"));
                    mSearchImg.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void setupImageView(){
        mSearchImg = (ImageView)findViewById(R.id.mSearchBar);
        mSearchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (mViewPager.getCurrentItem() == 0){
                    intent.setClass(MainActivity.this,SearchFoodActivity.class);
                }else if(mViewPager.getCurrentItem() == 1){
                    intent.setClass(MainActivity.this,SearchMaterialActivity.class);
                }
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
