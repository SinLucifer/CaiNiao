package com.sin.cainiao.JavaBean;

import cn.bmob.v3.BmobObject;

public class Material extends BmobObject {
    private String name;
    private Double price;
    private String desc;
    private String skill;
    private String picUrl;
    private String worth;
    private Double hot_click;
    private Double id;

    public Double getHot_click() {
        return hot_click;
    }

    public void setHot_click(Double hot_click) {
        this.hot_click = hot_click;
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public String getWorth() {
        return worth;
    }

    public void setWorth(String worth) {
        this.worth = worth;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

}
