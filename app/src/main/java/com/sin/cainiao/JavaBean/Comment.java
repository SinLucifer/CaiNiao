package com.sin.cainiao.javaBean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {
    private String content;
    private ProcessedFood food;
    private CaiNiaoUser user;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ProcessedFood getFood() {
        return food;
    }

    public void setFood(ProcessedFood food) {
        this.food = food;
    }

    public CaiNiaoUser getUser() {
        return user;
    }

    public void setUser(CaiNiaoUser user) {
        this.user = user;
    }
}
