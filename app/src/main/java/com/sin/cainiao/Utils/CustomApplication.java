package com.sin.cainiao.Utils;

import android.app.Application;
import android.util.Log;

import com.sin.cainiao.JavaBean.CaiNiaoUser;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

import static com.sin.cainiao.Utils.Utils.Json2Object;
import static com.sin.cainiao.Utils.Utils.loadTxt;

/**
 * Created by Sin on 2016/10/2.
 */

public class CustomApplication extends Application {
    private static final String TAG = "CustomApplication";
    private CaiNiaoUser user;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public CaiNiaoUser getUser() {
        return user;
    }

    public void setUser(CaiNiaoUser user) {
        this.user = user;
    }
}
