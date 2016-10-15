package com.sin.cainiao;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sin.cainiao.Activity.EditFoodActivity;
import com.sin.cainiao.Activity.EditInfoActivity;
import com.sin.cainiao.Activity.Login_RegisterActivity;
import com.sin.cainiao.Activity.SearchFoodActivity;
import com.sin.cainiao.Activity.SearchMaterialActivity;
import com.sin.cainiao.Activity.ShopActivity;
import com.sin.cainiao.Activity.ShowProcessFoodDetailActivity;
import com.sin.cainiao.Adapter.MainFragmentAdapter;
import com.sin.cainiao.Fragment.FoodMainFragment;
import com.sin.cainiao.Fragment.MaterialMainFragment;
import com.sin.cainiao.Fragment.UserMainFragment;
import com.sin.cainiao.JavaBean.CaiNiaoUser;
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.Utils.CustomApplication;
import com.sin.cainiao.Utils.Key;
import com.sin.cainiao.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

import static com.sin.cainiao.Utils.Utils.Json2Object;
import static com.sin.cainiao.Utils.Utils.loadTxt;

public class MainActivity extends AppCompatActivity implements FoodMainFragment.FoodFragmentCallBack
        ,MaterialMainFragment.MaterialMainFragmentCallBack,UserMainFragment.UserMainFragmentCallBack{
    private final static String TAG = "MainActivity";

    private CustomApplication app;

    private CaiNiaoUser user;
    private MainFragmentAdapter mainFragmentAdapter;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private BottomNavigation bottomNavigation;
    private ImageView mSearchImg;

    private FoodMainFragment foodMainFragment;
    private MaterialMainFragment materialMainFragment;
    private UserMainFragment userMainFragment;
    private AppBarLayout appBarLayout;

    private long firstTime;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBmob();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupViewPager();
        setupBottomNavigation();
        setupImageView();

        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);

        LinearLayout ll = (LinearLayout)findViewById(R.id.ll_hottest_indicator);
    }

    private void initBmob(){
        Bmob.initialize(this, Key.BMOB_KEY);
        Json2Object(loadTxt(getApplicationContext()));

//        BmobUser.logOut();
        user = BmobUser.getCurrentUser(CaiNiaoUser.class);
        app = (CustomApplication)getApplication();
        app.setUser(user);

    }

    private void setupViewPager(){
        foodMainFragment = FoodMainFragment.newInstance();
        foodMainFragment.setCallBack(MainActivity.this);

        materialMainFragment = MaterialMainFragment.newInstance();
        materialMainFragment.setCallBack(MainActivity.this);

        fragmentList.add(foodMainFragment);
        fragmentList.add(materialMainFragment);

        if (user != null){
            userMainFragment = UserMainFragment.newInstance();
            fragmentList.add(userMainFragment);
        }

        mainFragmentAdapter = new MainFragmentAdapter(fragmentList,getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mainFragmentAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.setSelectedIndex(position,true);
                if (position == 0){
                    mSearchImg.setVisibility(View.VISIBLE);
                }else if (position == 1){
                    mSearchImg.setVisibility(View.VISIBLE);
                }else if(position == 2){
                    mSearchImg.setVisibility(View.INVISIBLE);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void setupBottomNavigation(){
        bottomNavigation = (BottomNavigation)findViewById(R.id.BottomNavigation);
        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes int i, int i1) {
                if (i1 == 2) {
                    if (user == null) {
                        Intent intent = new Intent(MainActivity.this, Login_RegisterActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }

                mViewPager.setCurrentItem(i1);
            }

            @Override
            public void onMenuItemReselect(@IdRes int i, int i1) { }
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
    public void onFoodCallBack(ProcessedFood food) {
        Intent intent = new Intent(MainActivity.this,ShowProcessFoodDetailActivity.class);
        intent.putExtra("food",food);
        startActivity(intent);
    }

    @Override
    public void onMaterialCallBack(Material material) {
        Intent intent = new Intent(MainActivity.this,SearchMaterialActivity.class);
        intent.putExtra("material",material);
        startActivity(intent);
    }

    @Override
    public void onUserCallBack() {
        Intent intent = new Intent(MainActivity.this, ShopActivity.class);
        startActivity(intent);
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
        if (user != null) {
            if (id == R.id.action_add) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EditFoodActivity.class);
                startActivity(intent);
            } else if (id == R.id.action_logout) {
                BmobUser.logOut();
                user = BmobUser.getCurrentUser(CaiNiaoUser.class);
                app = (CustomApplication) getApplication();
                app.setUser(user);
                mViewPager.setCurrentItem(0);
                bottomNavigation.setSelectedIndex(0, true);
            } else if (id == R.id.action_info){
                Intent intent = new Intent(MainActivity.this, EditInfoActivity.class);
                startActivityForResult(intent,101);
            }
        }else {
            Utils.toastShow(getApplicationContext(),"请先登录！");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1){
            userMainFragment = UserMainFragment.newInstance();
            fragmentList.add(userMainFragment);
            mainFragmentAdapter.swapData(fragmentList);
            user = app.getUser();
            mViewPager.setCurrentItem(2);
            bottomNavigation.setSelectedIndex(2,true);
        }else if (resultCode == 0){
            mViewPager.setCurrentItem(0);
            bottomNavigation.setSelectedIndex(0,true);
        }
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
            Utils.toastShow(getApplicationContext(),"再次点击将退出菜鸟");
            firstTime = secondTime;//更新firstTime
        } else {                                                    //两次按键小于2秒时，退出应用
            super.onBackPressed();
        }
    }
}
