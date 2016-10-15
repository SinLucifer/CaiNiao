package com.sin.cainiao.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sin.cainiao.JavaBean.Item;
import com.sin.cainiao.JavaBean.Shop;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddShopItem extends AppCompatActivity {
    private static final String TAG = "AddShopItem";

    private EditText et_name;
    private EditText et_price;
    private EditText et_desc;
    private EditText et_count;
    private ImageView img_cover;
    private AppCompatSpinner sp_type;
    private Bitmap bitmap;

    private Integer type;
    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        shop = (Shop)intent.getSerializableExtra("shop");

        initView();
    }

    private void initView(){
        et_name = (EditText)findViewById(R.id.et_name);
        et_desc = (EditText)findViewById(R.id.et_desc);
        et_price = (EditText)findViewById(R.id.et_price);
        et_count = (EditText)findViewById(R.id.et_count);
        img_cover = (ImageView)findViewById(R.id.img_cover);
        sp_type = (AppCompatSpinner)findViewById(R.id.sp_type);
        final String[] mItems = getResources().getStringArray(R.array.material_type);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this
                , android.R.layout.simple_spinner_item, mItems);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        img_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        sp_type.setAdapter(arrayAdapter);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button bn_save = (Button)findViewById(R.id.bn_save);
        bn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);
                if (!(img_cover.getTag() instanceof String)){
                    Utils.toastShow(getApplicationContext(),"请选择一张封面！");
                    v.setClickable(true);
                    return;
                }

                if (et_name.getText().toString().equals("") || et_price.getText().toString().equals("")
                        ||et_count.getText().toString().equals("")){
                    Utils.toastShow(getApplicationContext(),"请检查你的输入");
                    v.setClickable(true);
                }else{
                    final BmobFile bmobFile = new BmobFile(new File((String)img_cover.getTag()));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Log.i(TAG, "done: " + bmobFile.getFileUrl());
                                saveItem(bmobFile.getFileUrl());
                            }else{
                                Log.i(TAG, "done: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void saveItem(String url){
        Item item = new Item();
        item.setName(et_name.getText().toString());
        item.setPrice(Double.parseDouble(et_price.getText().toString()));
        item.setType(type);
        item.setShop(shop);
        item.setCount(Integer.parseInt(et_count.getText().toString()));
        item.setDesc(et_desc.getText().toString());
        item.setCover_url(url);

        item.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Utils.toastShow(getApplicationContext(),"添加商品成功！");
                    finish();
                }else{
                    Utils.toastShow(getApplicationContext(),"添加商品失败！");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            bitmap = data.getParcelableExtra("data");
            if (bitmap == null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

            img_cover.setImageBitmap(bitmap);
            img_cover.setTag(Utils.getUri(data.getData(),getContentResolver()));
        }
    }
}
