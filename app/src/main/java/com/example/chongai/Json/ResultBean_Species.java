package com.example.chongai.Json;

import java.util.List;

public class ResultBean_Species {


    public String name;

    public double score;

    public BaikeInfoBean baike_info;

    public class BaikeInfoBean {

        public String baike_url;
        public String image_url;
        public String description;

        public String getBaike_url() {
            return baike_url;
        }

        public void setBaike_url(String baike_url) {
            this.baike_url = baike_url;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
