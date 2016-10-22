package com.sin.cainiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sin.cainiao.adapter.ItemAdapter;
import com.sin.cainiao.javaBean.CaiNiaoUser;
import com.sin.cainiao.javaBean.Item;
import com.sin.cainiao.javaBean.Shop;
import com.sin.cainiao.R;
import com.sin.cainiao.utils.Utils;
import com.sin.cainiao.utils.View.AppBarStateChangeListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ShopActivity extends AppCompatActivity {
    private static final String TAG = "ShopActivity";
    private CaiNiaoUser user;
    private Shop shop =  null;

    private static final int GET_SHOP_SUCCESS = 1;
    private static final int GET_ITEM_SUCCESS = 2;
    private static final int GET_SHOP_COVER = 3;
    private static final int NONE = 0;
    private static final int ERROR = -1;

    private TextView tv_title;
    private List<Item> itemList;

    private List<Item> meatList;
    private List<Item> seafoodList;
    private List<Item> vegList;
    private List<Item> fruitList;
    private List<Item> riceList;
    private List<Item> seasonList;

    private TextView tv_shop_name;
    private TextView tv_shop_add;
    private ImageView img_shop_cover;

    private Bitmap bitmap;
    private ItemAdapter itemAdapter;

    private boolean shopkeeper = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_SHOP_SUCCESS:
                    tv_shop_name.setText(shop.getName());
                    tv_shop_add.setText(shop.getAdd());
                    tv_title.setText(shop.getName());
                    getShopCover();
                    getItems();
                    break;
                case GET_SHOP_COVER:
                    img_shop_cover.setImageBitmap(bitmap);
                    break;
                case GET_ITEM_SUCCESS:
                    classify();
                    itemAdapter.swapData(meatList);
                    initTv();
                    break;
                case NONE:
                    Intent intent = new Intent(ShopActivity.this,ShopRegisterActivity.class);
                    startActivity(intent);
                    break;
                case ERROR:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        initList();

        user = BmobUser.getCurrentUser(CaiNiaoUser.class);
        if (user != null){
            if (user.getShopkeeper()){
                initEditableShop();
                shopkeeper = true;
            }else {
                initNormalShop();
                shopkeeper = false;
            }
        }else {
            initNormalShop();
            shopkeeper = false;
        }


        initView();
    }

    private void initNormalShop(){
        Intent intent = getIntent();
        shop = (Shop) intent.getSerializableExtra("shop");
        Message msg = new Message();
        msg.what = GET_SHOP_SUCCESS;
        mHandler.sendMessage(msg);
    }

    private void initEditableShop(){
        if (user != null){
            Log.i(TAG, "onCreate: " + user.getShop());
            BmobQuery<Shop> query = new BmobQuery<>();
            query.addWhereEqualTo("owner", user);
            query.findObjects(new FindListener<Shop>() {

                @Override
                public void done(List<Shop> object, BmobException e) {
                    Message msg = new Message();
                    if (e == null) {
                        shop = object.get(0);
                        msg.what = GET_SHOP_SUCCESS;
                    } else {
                        msg.what = NONE;
                    }

                    mHandler.sendMessage(msg);
                }
            });
        }
    }

    private void initView(){
        tv_title = (TextView)findViewById(R.id.tv_title);

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if( state == State.EXPANDED ) {
                    tv_title.setVisibility(View.INVISIBLE);
                }else if(state == State.COLLAPSED){
                    tv_title.setVisibility(View.VISIBLE);
                }
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.item_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemAdapter = new ItemAdapter(getApplicationContext());
        mRecyclerView.setAdapter(itemAdapter);

        tv_shop_name = (TextView)findViewById(R.id.tv_shop_name);
        tv_shop_add = (TextView)findViewById(R.id.tv_shop_add);
        img_shop_cover = (ImageView)findViewById(R.id.img_shop_cover);
    }

    private void initList(){
        meatList = new ArrayList<>();
        seafoodList = new ArrayList<>();
        vegList = new ArrayList<>();
        fruitList = new ArrayList<>();
        riceList = new ArrayList<>();
        seasonList = new ArrayList<>();
    }

    private void initTv(){
        TextView tv_meat = (TextView) findViewById(R.id.tv_meat);
        TextView tv_seafood = (TextView) findViewById(R.id.tv_seafood);
        TextView tv_veg = (TextView) findViewById(R.id.tv_veg);
        TextView tv_fruit = (TextView) findViewById(R.id.tv_fruit);
        TextView tv_rice = (TextView) findViewById(R.id.tv_rice);
        TextView tv_season = (TextView) findViewById(R.id.tv_seasoning);

        tv_meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdapter.swapData(meatList);
            }
        });

        tv_seafood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdapter.swapData(seafoodList);
            }
        });

        tv_fruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdapter.swapData(fruitList);
            }
        });

        tv_veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdapter.swapData(vegList);
            }
        });

        tv_season.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdapter.swapData(seasonList);
            }
        });

        tv_rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdapter.swapData(riceList);
            }
        });
    }

    private void getShopCover(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                bitmap = Utils.downLoadImg(shop.getShop_cover());
                if (bitmap != null){
                    msg.what = GET_SHOP_COVER;
                }else {
                    msg.what = ERROR;
                }

                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void classify(){
        for (Item item: itemList) {
            switch (item.getType()){
                case 0:
                    meatList.add(item);
                    break;
                case 1:
                    seafoodList.add(item);
                    break;
                case 2:
                    vegList.add(item);
                    break;
                case 3:
                    fruitList.add(item);
                    break;
                case 4:
                    riceList.add(item);
                    break;
                case 5:
                    seasonList.add(item);
                    break;
            }
        }
    }

    private void getItems(){
        BmobQuery<Item> query = new BmobQuery<>();
        query.addWhereEqualTo("shop",new BmobPointer(shop));
        query.findObjects(new FindListener<Item>() {
            @Override
            public void done(List<Item> list, BmobException e) {
                Message msg = new Message();
                if (e == null){
                    msg.what = GET_ITEM_SUCCESS;
                    itemList = list;
                }else {
                    msg.what = ERROR;
                    Log.i(TAG, "getItems: ERROR");
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_add){
            if (shopkeeper){
                Intent intent = new Intent(ShopActivity.this,AddShopItem.class);
                intent.putExtra("shop",shop);
                startActivity(intent);
            }else {
                Utils.toastShow(getApplicationContext(),"您不是商家！无权操作！");
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
