package com.sin.cainiao.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sin.cainiao.Adapter.ProcessClAdapter;
import com.sin.cainiao.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";
    private List<ViewHolder> ls_item;
    private List<View> ls_childView;
    private View childView;
    private LayoutInflater inflater;
    private LinearLayout ll_step_container;

    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> unitList = new ArrayList<>();

    int mark = 0;

    private RecyclerView mRecyclerView;
    private ProcessClAdapter processClAdapter;

    private ImageView img_cover;

    private List<String> stepList;

    private static final int SUCCESS = 1;

    private Handler mHandler = new Handler(){
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

        ll_step_container = (LinearLayout)findViewById(R.id.step_container);

        TextView tv_edit_material = (TextView)findViewById(R.id.tv_edit_material);
        nameList = new ArrayList<>();
        unitList = new ArrayList<>();

        mRecyclerView = (RecyclerView)findViewById(R.id.rec_edit_material);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        processClAdapter = new ProcessClAdapter(getApplicationContext());
        mRecyclerView.setAdapter(processClAdapter);


        ls_item = new ArrayList<>();  //step
        ls_childView = new ArrayList<>();
        stepList = new ArrayList<>();

        inflater = LayoutInflater.from(getApplicationContext());
        childView = inflater.inflate(R.layout.edit_item,null);
        childView.setId(mark);
        ll_step_container.addView(childView,mark);
        getViewInstance(childView);

        tv_edit_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this,EditMaterial.class);
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
                for (int i = 0; i < ll_step_container.getChildCount(); i++) {
                    ViewHolder vh = ls_item.get(i);
                    stepList.add(vh.et_step_content.getText().toString());
                    Log.i(TAG, "onClick: " + vh.id);
                }

                Log.i(TAG, "onClick: " + stepList);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            return;
        }
        Log.i(TAG, "onActivityResult: " + requestCode);
        Bitmap bitmap = null;
        if (requestCode >= 0 && requestCode < 100){
            bitmap = data.getParcelableExtra("data");
            if (bitmap == null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

            ls_item.get(requestCode).img_step_img.setImageBitmap(bitmap);
        }else if (requestCode == 100){
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
}
