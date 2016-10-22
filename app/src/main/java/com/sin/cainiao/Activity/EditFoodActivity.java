package com.sin.cainiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sin.cainiao.adapter.ProcessClAdapter;
import com.sin.cainiao.fragment.SelectFragment;
import com.sin.cainiao.javaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.utils.CustomApplication;
import com.sin.cainiao.utils.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class EditFoodActivity extends AppCompatActivity implements SelectFragment.onClickListener{
    private static final String TAG = "EditFoodActivity";
    private List<ViewHolder> ls_item;
    private List<View> ls_childView;
    private List<String> stepImgList;
    private List<String> urlList;
    private List<Integer> hasImgList;
    private View childView;
    private LayoutInflater inflater;
    private LinearLayout ll_step_container;
    private EditText et_title;
    private EditText et_desc;

    private String cover_url = null;

    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> unitList = new ArrayList<>();

    private int mark = 0;

    private ProcessClAdapter processClAdapter;

    private ImageView img_cover;

    private List<String> stepList;
    private String type = null;

    private static final int SUCCESS = 1;

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS){
                processClAdapter.swapData(nameList,unitList);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupView();
    }

    private void setupView(){
        Button bn_add = (Button) findViewById(R.id.bn_add);
        Button bn_save = (Button) findViewById(R.id.bn_save);
        Button bn_remove = (Button) findViewById(R.id.bn_remove);


        img_cover = (ImageView)findViewById(R.id.img_cover);

        img_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        et_title = (EditText) findViewById(R.id.edit_title);
        et_desc = (EditText)findViewById(R.id.et_desc);

        ll_step_container = (LinearLayout)findViewById(R.id.step_container);

        TextView tv_edit_material = (TextView)findViewById(R.id.tv_edit_material);

        nameList = new ArrayList<>();
        unitList = new ArrayList<>();
        stepImgList = new ArrayList<>();
        urlList = new ArrayList<>();
        hasImgList = new ArrayList<>();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rec_edit_material);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        processClAdapter = new ProcessClAdapter(getApplicationContext());
        mRecyclerView.setAdapter(processClAdapter);

        ls_item = new ArrayList<>();  //step
        ls_childView = new ArrayList<>();
        stepList = new ArrayList<>();

        AppCompatSpinner sp_type = (AppCompatSpinner) findViewById(R.id.sp_type);
        final String[] mItems = getResources().getStringArray(R.array.food_type);

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this
                , android.R.layout.simple_spinner_item, mItems);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_type.setAdapter(arrayAdapter);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        type = "jxc";
                        break;
                    case 1:
                        type = "ksc";
                        break;
                    case 2:
                        type = "xfc";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inflater = LayoutInflater.from(getApplicationContext());
        childView = inflater.inflate(R.layout.edit_item,null);
        childView.setId(mark);
        ll_step_container.addView(childView,mark);
        getViewInstance(childView);

        tv_edit_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditFoodActivity.this,EditMaterialItemActivity.class);
                intent.putStringArrayListExtra("name",nameList);
                intent.putStringArrayListExtra("unit",unitList);
                startActivityForResult(intent,101);
            }
        });

        bn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark++;
                childView = inflater.inflate(R.layout.edit_item,null);
                childView.setId(mark);
                ll_step_container.addView(childView,mark);
                getViewInstance(childView);
            }
        });



        bn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);

                if (img_cover.getTag() instanceof String){
                    stepImgList.add((String)img_cover.getTag());
                }else {
                    Utils.toastShow(getApplicationContext(),"请选择一张封面！");
                    v.setClickable(true);
                    return;
                }

                if (et_title.getText().toString().equals("")){
                    Utils.toastShow(getApplicationContext(),"请给您的作品起一个名字！");
                    v.setClickable(true);
                    return;
                }

                if (nameList.size() == 0){
                    Utils.toastShow(getApplicationContext(),"请输入材料！");
                    v.setClickable(true);
                    return;
                }

                for (int i = 0; i < ll_step_container.getChildCount(); i++) {
                    ViewHolder vh = ls_item.get(i);
                    String path;
                    stepList.add(vh.et_step_content.getText().toString());
                    if (vh.img_step_img.getTag() instanceof String){
                        path = (String)vh.img_step_img.getTag();
                        stepImgList.add(path);
                        hasImgList.add(vh.id);
                    }
//                    Log.i(TAG, "onClick: " + vh.id + " path :" + path);
                }


                Log.i(TAG, "onClick: " + stepList + hasImgList);

                final String[] filePaths = new String[stepImgList.size()];
                stepImgList.toArray(filePaths);

                BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> list, List<String> list1) {
                        if(list1.size()==filePaths.length){//如果数量相等，则代表文件全部上传完成
                            List<String> content = new ArrayList<>();
                            cover_url = list1.get(0);
                            for (int i = 1; i < list1.size(); i++) {
                                content.add(list1.get(i));
                            }

                            String[] result = new String[stepList.size()];

                            for (int i = 0; i < hasImgList.size(); i++) {
                                result[hasImgList.get(i)] = content.get(i);
                            }

                            urlList = Utils.Array2List(result);

                            Log.i(TAG, "onSuccess: " + urlList);

                            upload();

                        }
                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });

        bn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_step_container.getChildCount() > 1){
                    mark--;
                    ll_step_container.removeView(ls_childView.get(ls_childView.size()-1));
                    ls_item.remove(ls_item.size()-1);
                    ls_childView.remove(ls_childView.size()-1);
                }
            }
        });
    }

    private void upload(){
        BmobQuery<ProcessedFood> query = new BmobQuery<>();
        query.count(ProcessedFood.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null){
                    ProcessedFood food = new ProcessedFood(
                            et_title.getText().toString(),
                            ((CustomApplication)getApplication()).getUser(),
                            et_desc.getText().toString(),
                            cover_url,nameList,unitList,
                            stepList,urlList,integer+1,type,0);
                    food.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Utils.toastShow(getApplicationContext(),"创建菜谱成功！");
                                finish();
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }else {
                    Log.i(TAG, "done: " + e);
                }
            }
        });
    }

    public class ViewHolder{
        private int id;
        private TextView tv_step_number;
        private EditText et_step_content;
        private ImageView img_step_img;
    }

    private void getViewInstance(final View childView){
        ViewHolder vh = new ViewHolder();
        vh.id = childView.getId();
        vh.et_step_content = (EditText) childView.findViewById(R.id.tv_step_content);
        vh.tv_step_number = (TextView) childView.findViewById(R.id.tv_num);
        vh.img_step_img = (ImageView) childView.findViewById(R.id.img_step);

        vh.tv_step_number.setText(mark+1+".");
        vh.img_step_img.setTag(childView.getId());
        vh.img_step_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, childView.getId());
            }
        });

        Log.i(TAG, "getViewInstance: " + vh.img_step_img);

        ls_item.add(vh);
        ls_childView.add(childView);
    }



    @Override
    public void onClick(String type) {
        this.type = type;
        Log.i(TAG, "onClick: "  + type);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            return;
        }
        Log.i(TAG, "onActivityResult: " + requestCode);
        Bitmap bitmap;
        String path = null;
        if (requestCode >= 0 && requestCode < 100 && resultCode == RESULT_OK){
            bitmap = data.getParcelableExtra("data");
            if (bitmap == null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    Uri originalUri = data.getData();
                    path = Utils.getUri(originalUri,getContentResolver());
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

            ls_item.get(requestCode).img_step_img.setImageBitmap(bitmap);
            ls_item.get(requestCode).img_step_img.setTag(path);
        }else if (requestCode == 100 && resultCode == RESULT_OK ){
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
        }else if (requestCode == 101 && resultCode == 1){
            nameList = data.getStringArrayListExtra("name");
            unitList = data.getStringArrayListExtra("unit");

            Log.i(TAG, "onActivityResult: " + nameList);
            Log.i(TAG, "onActivityResult: " + unitList);
            Message msg = new Message();
            msg.what = SUCCESS;
            mHandler.sendMessage(msg);
        }
    }
}
