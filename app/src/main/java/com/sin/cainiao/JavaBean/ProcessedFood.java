package com.sin.cainiao.JavaBean;

import java.util.List;

import cn.bmob.v3.BmobObject;


public class ProcessedFood extends BmobObject {
    private String name;
    private String desc;
    private String cover_img;
    private List<String> ings_units;
    private List<String> ings_names;
    private String step;
    private int number;
    private int clickTime;


    public int getClickTime() {
        return clickTime;
    }

    public void setClickTime(int clickTime) {
        this.clickTime = clickTime;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
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
