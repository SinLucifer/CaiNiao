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
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.Utils.Key;
import com.sin.cainiao.DataHelper.MaterialDataHelper;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private BottomNavigation bottomNavigation;
    private ImageView mImageView;

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

        mImageView = (ImageView)findViewById(R.id.mSearchBar);
        mImageView.setOnClickListener(new View.OnClickListener() {
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

    private void initBmob(){
        Bmob.initialize(this, Key.BMOB_KEY);
        MaterialDataHelper.Json2Object(MaterialDataHelper.loadTxt(getApplicationContext()));
//
//        Material mMaterial = new Material();
//        mMaterial.setPrice(20);
//        mMaterial.setSkill("看肉膘。\n" +
//                "\n" +
//                "肉膘光亮，说明是当天宰杀的猪，猪肉新鲜，制作菜品味道更为美。如果肉膘发白，没怎么有光亮，" +
//                "说明是放置过夜的猪肉，天冷活有较好的冷藏条件还好，" +
//                "如果天热，可能会变质。选购这种猪肉时最好到有冷柜的超市、定点冷鲜肉店或者屠宰点去购买。\n");
//        mMaterial.setDesc("猪肉又名豚肉，是主要家畜之一、猪科动物家猪的肉。其性味甘咸平，含有丰富的蛋白质及脂肪、碳水化合物、钙、铁、磷等成分。" );
//        mMaterial.setFood_Name("猪肉");
//        mMaterial.setAdvantage(
//                " 猪肉是日常生活的主要副食品，具有补虚强身，滋阴润燥、丰肌泽肤的作用。凡病后体弱、产后血虚、面黄赢瘦者，皆可用之作营养滋补之品。");
//        mMaterial.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                Log.i(TAG, "done: ");
//            }
//        });
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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.setSelectedIndex(position,true);
                if (position == 0){
                    toolbar.setBackgroundColor(Color.parseColor("#f4511e"));
                    mImageView.setVisibility(View.VISIBLE);
                }else if (position == 1){
                    toolbar.setBackgroundColor(Color.parseColor("#00acc1"));
                    mImageView.setVisibility(View.VISIBLE);
                }else if(position == 2){
                    toolbar.setBackgroundColor(Color.parseColor("#7eb499"));
                    mImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
