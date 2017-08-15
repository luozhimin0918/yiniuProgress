package com.jyh.kxt.trading.json;

import com.jyh.kxt.av.json.CommentBean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class CommentDetailBean extends CommentBean {

    /**
     * 后台返回的
     */
    private int sub_comment_count;
    private int root_id;
    private List<CommentDetailBean> sub_comment;
    private String parent_member_name;

    public int getSub_comment_count() {
        return sub_comment_count;
    }

    public void setSub_comment_count(int sub_comment_count) {
        this.sub_comment_count = sub_comment_count;
    }

    public int getRoot_id() {
        return root_id;
    }

    public void setRoot_id(int root_id) {
        this.root_id = root_id;
    }

    public List<CommentDetailBean> getSub_comment() {
        return sub_comment;
    }

    public void setSub_comment(List<CommentDetailBean> sub_comment) {
        this.sub_comment = sub_comment;
    }

    public String getParent_member_name() {
        return parent_member_name;
    }

    public void setParent_member_name(String parent_member_name) {
        this.parent_member_name = parent_member_name;
    }
}
