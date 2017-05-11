package com.jyh.kxt.base.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Mr'Dai on 2017/5/5.
 */
@Entity(nameInDb = "EMOJE_BEAN")
public class EmojeBean {

    @Id(autoincrement = true) private Long id;
    /**
     * 图片名称
     */
    @Unique private String name;

    /**
     * 后缀名
     */
    private String suffixName;
    /**
     * 匹配网络URL
     */
    private String gifUrl;
    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组标签  中文名字
     */
    private String groupChineseName;

    /**
     * 预览的Url 指定的一个PNG图片
     */
    private String pngUrl;


    @Generated(hash = 176986286)
    public EmojeBean(Long id, String name, String suffixName, String gifUrl,
            String groupName, String groupChineseName, String pngUrl) {
        this.id = id;
        this.name = name;
        this.suffixName = suffixName;
        this.gifUrl = gifUrl;
        this.groupName = groupName;
        this.groupChineseName = groupChineseName;
        this.pngUrl = pngUrl;
    }

    @Generated(hash = 1551917151)
    public EmojeBean() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffixName() {
        return suffixName;
    }

    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupChineseName() {
        return groupChineseName;
    }

    public String getPngUrl() {
        return pngUrl;
    }

    public void setPngUrl(String pngUrl) {
        this.pngUrl = pngUrl;
    }

    public void setGroupChineseName(String groupChineseName) {
        this.groupChineseName = groupChineseName;
    }
}
