package com.jyh.kxt.main.json;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/11/7.
 */

public class AuthorBean {

    /**
     * id : 5
     * object_id : 1739
     * cate_name : 名家专栏
     * target_cate : yaowen
     * position : 8
     * status : 1
     * title : 思金融雅
     * introduce :
     * 本人毕业于中山大学财经系，从事金融行业五年有余。资深金融分析师，专注于黄金、原油、白银、天然气的行情分析。是众多财经门户特约撰稿人、首席分析师评论员。有过硬的技术功底及市场预判能力。为不同的投资者私人定制投资策略。本着利己则活，利他则久的理念，引导客户长期稳定的收益！多年实盘操作经验，操作风格稳健，风控意识强烈！
     * picture : http://img.kxt.com/Member/69313/avatar/5982c05f52a72.jpg
     */

    private String id;
    private String object_id;
    private String cate_name;
    private String target_cate;
    private String position;
    private String status;
    private String title;
    private String introduce;
    private String picture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public String getTarget_cate() {
        return target_cate;
    }

    public void setTarget_cate(String target_cate) {
        this.target_cate = target_cate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
