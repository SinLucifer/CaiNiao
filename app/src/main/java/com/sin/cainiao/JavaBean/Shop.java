package com.sin.cainiao.javaBean;

import cn.bmob.v3.BmobObject;

public class Shop extends BmobObject{
    private String poiId;
    private String name;
    private String add;
    private CaiNiaoUser owner;
    private String desc;
    private String shop_cover;

    public String getShop_cover() {
        return shop_cover;
    }

    public void setShop_cover(String shop_cover) {
        this.shop_cover = shop_cover;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public CaiNiaoUser getOwner() {
        return owner;
    }

    public void setOwner(CaiNiaoUser owner) {
        this.owner = owner;
    }


}
