package com.sin.cainiao.javaBean;

import android.graphics.Bitmap;

import java.util.List;


public class FoodItem {

    /**
     * showapi_res_code : 0
     * showapi_res_error :
     * showapi_res_body : {"zf":"1、鸡蛋打成蛋液，调入适量的盐充分拌匀。 2、火腿肠切成丝。 3、奶酪片切成丝。 4、番茄洗净去皮后，切成小丁。 5、锅中热油，先倒入火腿丝和番茄丁翻炒，待炒软后盛出备用。 6、平底锅洗净烧热后，转中小火倒入蛋液，旋转锅子让鸡蛋摊转成蛋饼。 7、蛋饼的底部刚开始凝固，而表面仍有未凝固的蛋液时。 8、平铺上炒好的火腿番茄丝和奶酪丝，然后马上用铲子或者筷子将蛋饼卷成卷，再切块即可。","ret_code":0,"imgList":[{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272292.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272412.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272453.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272507.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272562.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272606.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272668.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272743.jpg"}],"flag":true,"cl":"鸡蛋，火腿肠或火腿片，番茄，奶酪片或马苏里拉奶酪丝","name":"火腿番茄奶酪鸡蛋卷","cbId":"539364","mainType":"猪肉 蛋类","type":"火腿 鸡蛋"}
     */

    private int showapi_res_code;
    private String showapi_res_error;
    /**
     * zf : 1、鸡蛋打成蛋液，调入适量的盐充分拌匀。 2、火腿肠切成丝。 3、奶酪片切成丝。 4、番茄洗净去皮后，切成小丁。 5、锅中热油，先倒入火腿丝和番茄丁翻炒，待炒软后盛出备用。 6、平底锅洗净烧热后，转中小火倒入蛋液，旋转锅子让鸡蛋摊转成蛋饼。 7、蛋饼的底部刚开始凝固，而表面仍有未凝固的蛋液时。 8、平铺上炒好的火腿番茄丝和奶酪丝，然后马上用铲子或者筷子将蛋饼卷成卷，再切块即可。
     * ret_code : 0
     * imgList : [{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272292.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272412.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272453.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272507.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272562.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272606.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272668.jpg"},{"imgUrl":"http://app2.showapi.com/img/cookBook/1438165272743.jpg"}]
     * flag : true
     * cl : 鸡蛋，火腿肠或火腿片，番茄，奶酪片或马苏里拉奶酪丝
     * name : 火腿番茄奶酪鸡蛋卷
     * cbId : 539364
     * mainType : 猪肉 蛋类
     * type : 火腿 鸡蛋
     */

    private ShowapiResBodyBean showapi_res_body;

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public ShowapiResBodyBean getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(ShowapiResBodyBean showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    public static class ShowapiResBodyBean {
        private String zf;
        private int ret_code;
        private boolean flag;
        private String cl;
        private String name;
        private String cbId;
        private String mainType;
        private String type;

        public List<Bitmap> getBitmaps() {
            return bitmaps;
        }

        public void setBitmaps(List<Bitmap> bitmaps) {
            this.bitmaps = bitmaps;
        }

        private List<Bitmap> bitmaps;
        /**
         * imgUrl : http://app2.showapi.com/img/cookBook/1438165272292.jpg
         */

        private List<ImgListBean> imgList;

        public String getZf() {
            return zf;
        }

        public void setZf(String zf) {
            this.zf = zf;
        }

        public int getRet_code() {
            return ret_code;
        }

        public void setRet_code(int ret_code) {
            this.ret_code = ret_code;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getCl() {
            return cl;
        }

        public void setCl(String cl) {
            this.cl = cl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCbId() {
            return cbId;
        }

        public void setCbId(String cbId) {
            this.cbId = cbId;
        }

        public String getMainType() {
            return mainType;
        }

        public void setMainType(String mainType) {
            this.mainType = mainType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<ImgListBean> getImgList() {
            return imgList;
        }

        public void setImgList(List<ImgListBean> imgList) {
            this.imgList = imgList;
        }

        public static class ImgListBean {
            private String imgUrl;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }
        }
    }
}
