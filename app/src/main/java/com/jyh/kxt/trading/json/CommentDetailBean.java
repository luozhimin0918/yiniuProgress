package com.jyh.kxt.trading.json;

import com.jyh.kxt.av.json.CommentBean;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class CommentDetailBean extends CommentBean {
    private String sub_comment_count;

    public String getSub_comment_count() {
        return sub_comment_count;
    }

    public void setSub_comment_count(String sub_comment_count) {
        this.sub_comment_count = sub_comment_count;
    }
}
