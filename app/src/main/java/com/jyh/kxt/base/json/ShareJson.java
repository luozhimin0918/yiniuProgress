package com.jyh.kxt.base.json;

import android.graphics.Bitmap;

/**
 * 项目名:Kxt
 * 类描述:分享bean
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/3.
 */

public class ShareJson {
    private String title;//分享标题
    private String shareUrl;//分享Url
    private String discription;//分享详情
    private String thumb;//分享图片
    private Bitmap bitmap;//分享图片
    private String type;//分享布局类型
    private String id;//文章(视听)id
    private String collectType;//收藏类型
    private String goodType;//点赞类型
    private boolean isGood;//点赞状态
    private boolean isFavor;//收藏状态
    private int shareFromSource = -1;//分享来源自哪里 1 行情  2 视听 。。。。自己扩展

    /**
     * @param title       分享标题
     * @param shareUrl    分享Url
     * @param discription 分享详情
     * @param thumb       分享图片
     * @param bitmap      分享图片
     * @param type        分享布局类型
     * @param id          文章(视听)id
     * @param collectType 收藏类型 VarConstant.COLLECT_TYPE_ARTICLE:文章 VarConstant.COLLECT_TYPE_VIDEO:视听
     * @param goodType    点赞类型
     * @param isGood      点赞状态
     * @param isFavor     收藏状态
     */
    public ShareJson(String title, String shareUrl, String discription, String thumb, Bitmap bitmap, String type,
                     String id, String
                             collectType, String goodType, boolean isGood, boolean isFavor) {
        this(title, shareUrl, discription, thumb, bitmap, type, id, collectType, goodType, isGood, isFavor, -1);
    }

    public ShareJson(String title, String shareUrl, String discription, String thumb, Bitmap bitmap, String type,
                     String id, String
                             collectType, String goodType, boolean isGood, boolean isFavor, int shareFromSource) {
        this.title = title;
        this.shareUrl = shareUrl;
        this.discription = discription;
        this.thumb = thumb;
        this.bitmap = bitmap;
        this.type = type;
        this.id = id;
        this.collectType = collectType;
        this.goodType = goodType;
        this.isGood = isGood;
        this.isFavor = isFavor;
        this.shareFromSource = shareFromSource;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

    public boolean isFavor() {

        return isFavor;
    }

    public void setFavor(boolean favor) {
        isFavor = favor;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }

    public int getShareFromSource() {
        return shareFromSource;
    }

    public void setShareFromSource(int shareFromSource) {
        this.shareFromSource = shareFromSource;
    }
}
