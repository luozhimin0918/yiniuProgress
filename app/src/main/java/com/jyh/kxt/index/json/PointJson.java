package com.jyh.kxt.index.json;

import com.jyh.kxt.av.json.CommentBean;

/**
 * Created by Mr'Dai on 2017/8/11.
 */

public class PointJson extends CommentBean {

    /**
     * content : 哦哦哦哦
     * id : 220
     * member_nickname : 工作QQ
     * member_picture : http://img.kxt.com/Member/28688/picture/5965ceaadd6a8.png
     * num_good : 0
     * num_reply : 0
     * o_action : detail
     * o_class : viewpoint
     * o_id : 126
     * parent_content : 舅舅家
     * parent_member_name : 0
     * point_name : 大师说财经
     * point_picture : http://img.kxt.com/Member/55215/avatar/59632fcb10399.png
     * root_id : 219
     * time : 1502333246
     * title : 黄金目前从4小时线上看，大方向一直都在走阶梯式上涨的结构，只要此结构一直没有破坏，行情望继续上涨，昨日4小时在上方小幅的震荡下行，最低到了1262
     * 美元附近止跌，同时此位也是接近上次震荡的顶部，所以此位是重要的上涨行情支撑线。从昨日行情走势来看，由于临近大数据前夕，市场观望情绪比较严重，虽然黄金目前依旧强势运行，但区间受到限制。今日操作上建议上方关注1275美元一线压力，下方关注1260美元一线支撑。重点关注晚间ADP
     * 数据。具体走势不懂的可微信：tolstys咨询我。#&title=现货黄金&href=&o_class=quotes&o_action=detail&o_id=XAU&#
     * type : point
     */

    private String o_action;
    private String o_class;
    private int o_id;

    private String parent_member_name;
    private String point_name;
    private String point_picture;
    private int root_id;
    private long time;
    private String title;

    public String getO_action() {
        return o_action;
    }

    public void setO_action(String o_action) {
        this.o_action = o_action;
    }

    public String getO_class() {
        return o_class;
    }

    public void setO_class(String o_class) {
        this.o_class = o_class;
    }

    public int getO_id() {
        return o_id;
    }

    public void setO_id(int o_id) {
        this.o_id = o_id;
    }

    public String getParent_member_name() {
        return parent_member_name;
    }

    public void setParent_member_name(String parent_member_name) {
        this.parent_member_name = parent_member_name;
    }

    public String getPoint_name() {
        return point_name;
    }

    public void setPoint_name(String point_name) {
        this.point_name = point_name;
    }

    public String getPoint_picture() {
        return point_picture;
    }

    public void setPoint_picture(String point_picture) {
        this.point_picture = point_picture;
    }

    public int getRoot_id() {
        return root_id;
    }

    public void setRoot_id(int root_id) {
        this.root_id = root_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
