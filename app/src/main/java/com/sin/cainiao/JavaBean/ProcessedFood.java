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
    private List<String> steps;
    private List<String> steps_img;
    private Integer number;
    private Integer clickTime;
    private BmobRelation likes;
    private String type;
    private CaiNiaoUser author;

    public ProcessedFood(){

    }

    /**
     *
     * @param name
     * @param desc
     * @param cover_img
     * @param ings_names
     * @param ings_units
     * @param steps
     * @param steps_img
     * @param number
     * @param type
     */
    public ProcessedFood(String name
            ,CaiNiaoUser author
            ,String desc
            , String cover_img
            , List<String> ings_names
            , List<String> ings_units
            , List<String> steps
            , List<String> steps_img
            , Integer number
            , String type
            ,Integer clickTime) {
        this.name = name;
        this.author = author;
        this.desc = desc;
        this.cover_img = cover_img;
        this.ings_units = ings_units;
        this.steps = steps;
        this.ings_names = ings_names;
        this.steps_img = steps_img;
        this.number = number;
        this.type = type;
        this.clickTime = clickTime;
    }

    public CaiNiaoUser getAuthor() {
        return author;
    }

    public void setAuthor(CaiNiaoUser author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<String> getSteps_img() {
        return steps_img;
    }

    public void setSteps_img(List<String> steps_img) {
        this.steps_img = steps_img;
    }
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
