package com.sin.cainiao.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.sin.cainiao.JavaBean.CaiNiaoUser;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.bitmap;
import static com.sin.cainiao.R.id.et_nick_name;
import static com.sin.cainiao.R.id.img_cover;

public class EditInfoActivity extends AppCompatActivity {
    private static final String TAG = "EditInfoActivity";

    private CircleImageView img_user_cover;
    private TextView tv_phone_number;
    private EditText et_user_name;
    private EditText et_user_desc;
    private EditText et_user_job;
    private EditText et_user_home;
    private DatePicker datePicker;
    private AppCompatSpinner sp_sex;
    private Button bn_save;

    private String sex;
    private Bitmap bitmap;
    private CaiNiaoUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = BmobUser.getCurrentUser(CaiNiaoUser.class);

        initView();
    }

    private void initView(){
        img_user_cover = (CircleImageView)findViewById(R.id.img_user_cover);
        tv_phone_number = (TextView)findViewById(R.id.tv_phone_number);
        tv_phone_number.setText("注册手机号: " + user.getMobilePhoneNumber());
        et_user_name = (EditText)findViewById(R.id.et_nick_name);
        et_user_name.setText(user.getNick());

        et_user_desc = (EditText)findViewById(R.id.et_user_desc);
        et_user_desc.setText(user.getDesc());

        et_user_job = (EditText)findViewById(R.id.et_user_job);
        et_user_job.setText(user.getJob());

        et_user_home = (EditText)findViewById(R.id.et_user_home);
        et_user_home.setText(user.getHome());

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        sp_sex = (AppCompatSpinner)findViewById(R.id.sp_sex);
        final String[] mItems = getResources().getStringArray(R.array.sex);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this
                , android.R.layout.simple_spinner_item, mItems);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_sex.setAdapter(arrayAdapter);
        sp_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex = mItems[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bn_save = (Button)findViewById(R.id.bn_save);
        bn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);
                if (!(img_user_cover.getTag() instanceof String)){
                    Utils.toastShow(getApplicationContext(),"请设置头像！");
                    v.setClickable(true);
                    return;
                }

                upLoad();
            }
        });

        img_user_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });
    }

    private void upLoad(){

        final BmobFile bmobFile = new BmobFile(new File((String)img_user_cover.getTag()));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i(TAG, "done: " + bmobFile.getFileUrl());
                    updateUser(bmobFile.getFileUrl());
                }else{
                    Log.i(TAG, "done: " + e.getMessage());
                }
            }
        });
    }

    private void updateUser(String url) {
        CaiNiaoUser newUser = new CaiNiaoUser();
        newUser.setJob(et_user_job.getText().toString());
        newUser.setHome(et_user_home.getText().toString());
        newUser.setNick(et_user_name.getText().toString());
        newUser.setUser_cover(url);
        newUser.setDesc(et_user_desc.getText().toString());
        if (sex.equals("男")){
            newUser.setSex(true);
        }else {
            newUser.setSex(false);
        }
        newUser.update(user.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Utils.toastShow(getApplicationContext(),"更新用户信息成功");
                    finish();
                }else{
                    Utils.toastShow(getApplicationContext(),"更新用户信息失败" + e.getMessage());
                }
            }
        });

//        user.setBirthDay();
//        user.setAge(et_);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK){
            bitmap = data.getParcelableExtra("data");
            if (bitmap == null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

            img_user_cover.setImageBitmap(bitmap);
            img_user_cover.setTag(Utils.getUri(data.getData(),getContentResolver()));
        }
    }
}
