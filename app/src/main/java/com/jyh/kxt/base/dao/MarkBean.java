package com.jyh.kxt.base.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Mr'Dai on 2017/8/14.
 */

@Entity(nameInDb = "MARK_BEAN")
public class MarkBean {
    /**
     * 用户的ID
     */
    private String userId;
    /**
     * 保存对象的ID
     */
    @Unique
    private String oId;

    /**
     * 上传状态
     */
    private int upState = 0;
    /**
     * 是否收藏 0 无 1 有
     */
    private int collectState = 0;
    /**
     * 是否关注 0 无 1 有
     */
    private int attentionState = 0;
    /**
     * 是否点赞 0 无 1 有
     */
    private int favourState = 0;
    @Generated(hash = 1544188347)
    public MarkBean(String userId, String oId, int upState, int collectState,
            int attentionState, int favourState) {
        this.userId = userId;
        this.oId = oId;
        this.upState = upState;
        this.collectState = collectState;
        this.attentionState = attentionState;
        this.favourState = favourState;
    }
    @Generated(hash = 1968029817)
    public MarkBean() {
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getOId() {
        return this.oId;
    }
    public void setOId(String oId) {
        this.oId = oId;
    }
    public int getUpState() {
        return this.upState;
    }
    public void setUpState(int upState) {
        this.upState = upState;
    }
    public int getCollectState() {
        return this.collectState;
    }
    public void setCollectState(int collectState) {
        this.collectState = collectState;
    }
    public int getAttentionState() {
        return this.attentionState;
    }
    public void setAttentionState(int attentionState) {
        this.attentionState = attentionState;
    }
    public int getFavourState() {
        return this.favourState;
    }
    public void setFavourState(int favourState) {
        this.favourState = favourState;
    }


}
