package com.jyh.kxt.base.constant;

import com.library.base.http.VarConstant;
import com.library.util.EncryptionUtils;

import org.json.JSONObject;

/**
 * Created by Mr'Dai on 2017/3/1.
 */

public class HttpConstant {

    private static String JWT;

    static {
        try {
            JSONObject object = new JSONObject();
            object.put(VarConstant.HTTP_VERSION, VarConstant
                    .HTTP_VERSION_VALUE);
            object.put(VarConstant.HTTP_SYSTEM, VarConstant
                    .HTTP_SYSTEM_VALUE);
            JWT = VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY,
                    object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            JWT = "";
        }

    }

    public static final String OFFICIAL = "https//www.kxt.com";//官网

    public static final String BASE_URL = "https://kxtadi.kuaixun56.com/";
    public static final String IMG_URL = "http://img.kuaixun360.com/";
    public static final String FLAG_URL = "http://res.kxt.com/static/home/images/flag/circle/%s.png";

    public static final String CONFIG = BASE_URL + "app/config" + JWT;//配置信息
    public static final String VERSION = BASE_URL + "version/android" + JWT;//版本信息

    public static final String INDEX_MAIN = BASE_URL + "index/main" + JWT;//首页
    public static final String EXPLORE = BASE_URL + "discover/index" + JWT;//探索
    public static final String NEWS_LIST = BASE_URL + "news/list" + VarConstant.HTTP_CONTENT;//要闻列表
    public static final String NEWS_CONTENT = BASE_URL + "news/detail" + VarConstant.HTTP_CONTENT;//要闻详情
    public static final String VIDEO_NAV = BASE_URL + "video/nav" + JWT;//版本信息
    public static final String VIDEO_LIST = BASE_URL + "video/list";//视听列表

    public static final String SOCKET_TOKEN_KX = BASE_URL + "kuaixun/ws" + VarConstant.HTTP_CONTENT;//快讯socketToken
    public static final String SOCKET_TOKEN_HQ = BASE_URL + "quotes/ws";//行情socketToken

    public static final String CJRL = BASE_URL + "cjrl/data" + JWT;

    public static final String RILI = BASE_URL + "data/rili" + VarConstant.HTTP_CONTENT;

    public static final String MARKET_NAV = BASE_URL + "/quotes/nav" + VarConstant.HTTP_CONTENT;
    public static final String MARKET_INDEX = BASE_URL + "/quotes/index" + VarConstant.HTTP_CONTENT;

    public static final String VIDEO_DETAIL = BASE_URL + "/video/detail" + VarConstant.HTTP_CONTENT;
}
