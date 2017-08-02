package com.jyh.kxt.trading.json;

/**
 * Created by Mr'Dai on 2017/8/1.
 */

public class ViewPointTradeBean {

    /**
     * 自定义的  格式发生改变  时不动
     */

    private int itemViewType = 1;

    public int getItemViewType() {
        return itemViewType;
    }

    public void setItemViewType(int itemViewType) {
        this.itemViewType = itemViewType;
    }

    /**
     * comment_num : 0
     * content : 阿素福非凡方法asfsa阿司法司法额发生的还是
     * create_time : 1501487885
     * id : 45
     * num_good : 1
     * picture :
     * ref_id : 49
     */

    private int comment_num;
    private String content;
    private int create_time;
    private int id;
    private int num_good;
    private String picture;
    private int ref_id;

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum_good() {
        return num_good;
    }

    public void setNum_good(int num_good) {
        this.num_good = num_good;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getRef_id() {
        return ref_id;
    }

    public void setRef_id(int ref_id) {
        this.ref_id = ref_id;
    }


}
