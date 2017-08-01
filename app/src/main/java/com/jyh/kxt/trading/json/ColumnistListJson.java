package com.jyh.kxt.trading.json;

/**
 * 项目名:Kxt
 * 类描述:名家专栏列表json
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/31.
 */

public class ColumnistListJson {


    /**
     * id : 1120
     * auth_type : 1
     * name : 第一黄金网
     * picture : http://img.kxt.com/Uploads/Picture/2017-04-28/59030dbcdf1f4.png
     * num_fans : 310
     * article_num : 559
     * point_num : 1
     */

    private String id;
    private String auth_type;
    private String name;
    private String picture;
    private String num_fans;
    private String article_num;
    private String point_num;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNum_fans() {
        return num_fans;
    }

    public void setNum_fans(String num_fans) {
        this.num_fans = num_fans;
    }

    public String getArticle_num() {
        return article_num;
    }

    public void setArticle_num(String article_num) {
        this.article_num = article_num;
    }

    public String getPoint_num() {
        return point_num;
    }

    public void setPoint_num(String point_num) {
        this.point_num = point_num;
    }
}
