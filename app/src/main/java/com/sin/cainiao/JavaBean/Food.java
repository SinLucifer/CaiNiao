package com.sin.cainiao.javaBean;


import java.io.Serializable;
import java.util.List;

public class Food {

    private int showapi_res_code;
    private String showapi_res_error;


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
        private String ret_code;
        private boolean flag;
        private String remark;


        private List<CbListBean> cbList;

        public String getRet_code() {
            return ret_code;
        }

        public void setRet_code(String ret_code) {
            this.ret_code = ret_code;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public List<CbListBean> getCbList() {
            return cbList;
        }

        public void setCbList(List<CbListBean> cbList) {
            this.cbList = cbList;
        }

        public static class CbListBean implements Serializable{
            private String zf;
            private String name;
            private String cl;
            private String cbId;
            private String type;
            private String mainType;

            private List<ImgListBean> imgList;

            public String getZf() {
                return zf;
            }

            public void setZf(String zf) {
                this.zf = zf;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCl() {
                return cl;
            }

            public void setCl(String cl) {
                this.cl = cl;
            }

            public String getCbId() {
                return cbId;
            }

            public void setCbId(String cbId) {
                this.cbId = cbId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getMainType() {
                return mainType;
            }

            public void setMainType(String mainType) {
                this.mainType = mainType;
            }

            public List<ImgListBean> getImgList() {
                return imgList;
            }

            public void setImgList(List<ImgListBean> imgList) {
                this.imgList = imgList;
            }

            public static class ImgListBean implements Serializable{
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
}
