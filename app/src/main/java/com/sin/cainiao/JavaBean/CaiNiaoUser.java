package com.sin.cainiao.JavaBean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

public class CaiNiaoUser extends BmobUser {
    private Boolean sex;
    private String nick;
    private Integer age;
    private BmobRelation foodLikes;
    private BmobRelation materialLikes;
    private Boolean shopkeeper;
    private Shop shop;
    private String job;
    private String home;
    private BmobDate birthDay;
    private String user_cover;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public BmobDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(BmobDate birthDay) {
        this.birthDay = birthDay;
    }

    public String getUser_cover() {
        return user_cover;
    }

    public void setUser_cover(String user_cover) {
        this.user_cover = user_cover;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Boolean getShopkeeper() {
        return shopkeeper;
    }

    public void setShopkeeper(Boolean shopkeeper) {
        this.shopkeeper = shopkeeper;
    }

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
