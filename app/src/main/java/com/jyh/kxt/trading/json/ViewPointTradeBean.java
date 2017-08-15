package com.jyh.kxt.trading.json;

import com.jyh.kxt.base.dao.ListConverter;
import com.jyh.kxt.base.dao.ShareDictConverter;
import com.jyh.kxt.base.dao.ViewPointConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/1.
 * {
 * content:行情简述$超链接$  //内包含超链接 超链接内容用#&<  >&# 包含，格式数据包含 title href,o_class,o_action,o_id    示例格式 #&<
 * title=文字&href=&o_class=&o_action=&o_id=  >&#
 * num_good:200,
 * num_commit:200,
 * author_name:快讯通,
 * author_id:108108,
 * author_img:头像url
 * time:159039321,
 * href:"",
 * o_class:trading,
 * o_action:detail,
 * o_id:108108,
 * report:["垃圾营销","内容抄袭","违法信息","低俗信息","虚假",],
 * //转发内容
 * forward:{
 * //格式参考观点的格式[图片],[图片] },
 * picture：[ img1url,img2url,img3url ],
 * shareDict:{url:http://www.baidu.com,
 * img:http://www.baidu.com/icon.png,
 * title:行情描述,
 * descript:行情描述 },
 * <p>
 * }
 */

@Entity(nameInDb = "POINT_BEAN")
public class ViewPointTradeBean {

    /**
     * 自定义的  格式发生改变  时不动
     */
    @Transient
    public int itemViewType = 1;
    @Transient
    public boolean isSel;
    @Transient
    public boolean isFavour = false; //是否赞
    @Transient
    public boolean isCollect = false; //是否收藏

    public String content;
    public int num_good;
    public int num_comment;
    public String author_name;
    public String author_id;
    public String author_img;
    public long time;
    public String href;

    @Convert(columnType = String.class, converter = ShareDictConverter.class)
    public ShareDictBean shareDict;

    @Unique
    public String o_id;
    public String o_class;
    public String o_action;
    public String is_top;

    @Convert(columnType = String.class, converter = ListConverter.class)
    public List<String> report;

    @Convert(columnType = String.class, converter = ListConverter.class)
    public List<String> picture;

    @Convert(columnType = String.class, converter = ViewPointConverter.class)
    public ViewPointTradeBean forward;

    public String is_follow;
    @Transient
    public List<CommentDetailBean> comment;
    @Generated(hash = 1344545633)
    public ViewPointTradeBean(String content, int num_good, int num_comment, String author_name,
            String author_id, String author_img, long time, String href, ShareDictBean shareDict,
            String o_id, String o_class, String o_action, String is_top, List<String> report,
            List<String> picture, ViewPointTradeBean forward, String is_follow) {
        this.content = content;
        this.num_good = num_good;
        this.num_comment = num_comment;
        this.author_name = author_name;
        this.author_id = author_id;
        this.author_img = author_img;
        this.time = time;
        this.href = href;
        this.shareDict = shareDict;
        this.o_id = o_id;
        this.o_class = o_class;
        this.o_action = o_action;
        this.is_top = is_top;
        this.report = report;
        this.picture = picture;
        this.forward = forward;
        this.is_follow = is_follow;
    }
    @Generated(hash = 1862830762)
    public ViewPointTradeBean() {
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getNum_good() {
        return this.num_good;
    }
    public void setNum_good(int num_good) {
        this.num_good = num_good;
    }
    public int getNum_comment() {
        return this.num_comment;
    }
    public void setNum_comment(int num_comment) {
        this.num_comment = num_comment;
    }
    public String getAuthor_name() {
        return this.author_name;
    }
    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
    public String getAuthor_id() {
        return this.author_id;
    }
    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }
    public String getAuthor_img() {
        return this.author_img;
    }
    public void setAuthor_img(String author_img) {
        this.author_img = author_img;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getHref() {
        return this.href;
    }
    public void setHref(String href) {
        this.href = href;
    }
    public ShareDictBean getShareDict() {
        return this.shareDict;
    }
    public void setShareDict(ShareDictBean shareDict) {
        this.shareDict = shareDict;
    }
    public String getO_class() {
        return this.o_class;
    }
    public void setO_class(String o_class) {
        this.o_class = o_class;
    }
    public String getO_action() {
        return this.o_action;
    }
    public void setO_action(String o_action) {
        this.o_action = o_action;
    }
    public String getO_id() {
        return this.o_id;
    }
    public void setO_id(String o_id) {
        this.o_id = o_id;
    }
    public String getIs_top() {
        return this.is_top;
    }
    public void setIs_top(String is_top) {
        this.is_top = is_top;
    }
    public List<String> getReport() {
        return this.report;
    }
    public void setReport(List<String> report) {
        this.report = report;
    }
    public List<String> getPicture() {
        return this.picture;
    }
    public void setPicture(List<String> picture) {
        this.picture = picture;
    }
    public ViewPointTradeBean getForward() {
        return this.forward;
    }
    public void setForward(ViewPointTradeBean forward) {
        this.forward = forward;
    }
    public String getIs_follow() {
        return this.is_follow;
    }
    public void setIs_follow(String is_follow) {
        this.is_follow = is_follow;
    }
}
