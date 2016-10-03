package com.sin.cainiao.JavaBean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

public class CaiNiaoUser extends BmobUser {
    private Boolean sex;
    private String nick;
    private Integer age;
    private BmobRelation foodLikes;
    private BmobRelation materialLikes;

    public BmobRelation getMaterialLikes() {
        return materialLikes;
    }

    public void setMaterialLikes(BmobRelation materialLikes) {
        this.materialLikes = materialLikes;
    }

    public BmobRelation getFoodLikes() {
        return foodLikes;
    }

    public void setFoodLikes(BmobRelation foodLikes) {
        this.foodLikes = foodLikes;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
