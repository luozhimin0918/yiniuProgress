package com.library.bean;

/**
 * @author Mr'Dai
 * @date 2016/5/20 14:28
 * @Title: MobileLibrary
 * @Package com.library.bean
 * @Description:
 */
public class EventBusClass {

    public static final int EVENT_FLASH_FILTRATE = 1;//快讯筛选
    public static final int EVENT_LOGIN = 2;//登录
    public static final int EVENT_LOGOUT = 3;//退出登录
    public static final int EVENT_CLEAR_BROWER = 4;//删除浏览记录
    public static final int EVENT_CHANGEUSERINFO = 5;//更新用户信息
    public static final int EVENT_COLLECT_VIDEO = 6;//视听收藏状态改变
    public static final int EVENT_COLLECT_NEWS = 7;//要闻收藏状态改变
    public static final int EVENT_COLLECT_FLASH = 8;//快讯收藏状态改变

    public static final int EVENT_ATTENTION_AUTHOR_DEL = 9;//关注作者 删除
    public static final int EVENT_ATTENTION_AUTHOR_ADD = 10;//关注作者 添加
    public static final int EVENT_ATTENTION_ARTICLE_DEL = 11;//关注文章 删除
    public static final int EVENT_ATTENTION_ARTICLE_ADD = 12;//关注文章 添加

    public static final int MARKET_OPTION_UPDATE = 13;//行情自选,编辑中删除或者增加

    public static final int EVENT_ATTENTION_OTHER = 14;//关注别人
    public static final int EVENT_MARKET_LOGIN_GONG = 15;//登录之后，行情自选下方登录隐藏
    public static final int EVENT_REQUEST_MAIN_INIT = 16;//请求完成之后发送Event

    public EventBusClass(int fromCode, Object intentObj) {
        this.fromCode = fromCode;
        this.intentObj = intentObj;
    }

    /**
     * 来自哪里Code值
     */
    public int fromCode;

    /**
     * 传输的对象
     */
    public Object intentObj;
}
