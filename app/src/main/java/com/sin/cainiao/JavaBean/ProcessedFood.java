package com.sin.cainiao.JavaBean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;


public class ProcessedFood extends BmobObject {
    private String name;
    private String desc;
    private String cover_img;
    private List<String> ings_units;
    private List<String> ings_names;
    private String step;
    private Integer number;
    private Integer clickTime;
    private BmobRelation likes;

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }


    public Integer getClickTime() {
        return clickTime;
    }

    public void setClickTime(Integer clickTime) {
        this.clickTime = clickTime;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngs_names() {
        return ings_names;
    }

    public void setIngs_names(List<String> ings_names) {
        this.ings_names = ings_names;
    }

    public List<String> getIngs_units() {
        return ings_units;
    }

    public void setIngs_units(List<String> ings_units) {
        this.ings_units = ings_units;
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
