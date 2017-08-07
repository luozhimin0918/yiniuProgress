package com.jyh.kxt.trading.json;

/**
 * Created by Mr'Dai on 2017/8/1.
 */

/**
 * article_num : 31
 * auth_type : 1
 * id : 1689
 * name : 老猫解盘
 * num_fans : 16
 * picture : http://img.kxt.com/Member/69191/avatar/597adb122c824.png
 * point_num : 0
 */
public class ViewPointHotBean {

    /**
     * article_num : 2
     * auth_type : 1
     * id : 1288
     * name : 宋仪论金
     * num_fans : 38
     * picture : http://img.kxt.com/Member/58902/avatar/59182f8451d52.jpg
     */

    private int article_num;
    private int auth_type;
    private String id;
    private String name;
    private int num_fans;
    private String picture;

    public int getArticle_num() {
        return article_num;
    }

    public void setArticle_num(int article_num) {
        this.article_num = article_num;
    }

    public int getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(int auth_type) {
        this.auth_type = auth_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum_fans() {
        return num_fans;
    }

    public void setNum_fans(int num_fans) {
        this.num_fans = num_fans;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
