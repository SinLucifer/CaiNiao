package com.sin.cainiao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sin.cainiao.adapter.CommentAdapter;
import com.sin.cainiao.javaBean.CaiNiaoUser;
import com.sin.cainiao.javaBean.Comment;
import com.sin.cainiao.javaBean.ProcessedFood;
import com.sin.cainiao.R;
import com.sin.cainiao.utils.Utils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private static final int SUCCESS = 1;
    private static final int ERROR = 0;

    private CommentAdapter adapter;

    private EditText et_content;

    private ProcessedFood mFood;
    private List<Comment> commentList;

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    adapter.swapData(commentList);
                    break;
                case ERROR:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent.getSerializableExtra("food") instanceof ProcessedFood){
            mFood = (ProcessedFood)intent.getSerializableExtra("food");
        }

        initView();
        loadComment();
    }

    private void initView(){
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rec_comment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new CommentAdapter(getApplicationContext());
        mRecyclerView.setAdapter(adapter);

        et_content = (EditText)findViewById(R.id.et_content);
        Button bn_send = (Button) findViewById(R.id.bn_send);

        bn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BmobUser.getCurrentUser(CaiNiaoUser.class) == null){
                    Utils.toastShow(getApplicationContext(),"请先登录！");
                    return;
                }

                if (et_content.getText().toString().equals("")){
                    Utils.toastShow(getApplicationContext(),"请输入评论！");
                    return;
                }

                Comment comment = new Comment();
                comment.setContent(et_content.getText().toString());
                comment.setUser(BmobUser.getCurrentUser(CaiNiaoUser.class));
                comment.setFood(mFood);
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Utils.toastShow(getApplicationContext(),"评论发表成功");
                            Log.i("bmob","评论发表成功");
                            loadComment();
                        }else{
                            Utils.toastShow(getApplicationContext(),"评论发表失败");
                            Log.i("bmob","失败："+e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private void loadComment(){
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("food",mFood);
        query.include("user,food.author");
        query.order("-createdAt");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                Message msg = new Message();
                if (e == null){
                    commentList = list;
                    msg.what = SUCCESS;
                }else{
                    msg.what = ERROR;
                    Log.i(TAG, "done: " + e.getMessage());
                }
                mHandler.sendMessage(msg);
            }
        });
    }

}
