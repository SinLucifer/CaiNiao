package com.sin.cainiao.utils;

import android.app.Application;

import com.sin.cainiao.javaBean.CaiNiaoUser;

public class CustomApplication extends Application {
    private static final String TAG = "CustomApplication";
    private CaiNiaoUser user;

    public CaiNiaoUser getUser() {
        return user;
    }

    public void setUser(CaiNiaoUser user) {
        this.user = user;
    }
}
