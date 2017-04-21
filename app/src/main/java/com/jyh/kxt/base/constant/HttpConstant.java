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
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_VERSION, com.jyh.kxt.base.constant.VarConstant.HTTP_VERSION_VALUE);
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_SYSTEM, com.jyh.kxt.base.constant.VarConstant.HTTP_SYSTEM_VALUE);
            JWT = com.jyh.kxt.base.constant.VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, object.toString());
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
    public static final String NEWS_LIST = BASE_URL + "news/list";//要闻列表
    public static final String NEWS_CONTENT = BASE_URL + "news/detail";//要闻详情
    public static final String VIDEO_NAV = BASE_URL + "video/nav" + JWT;//版本信息
    public static final String VIDEO_LIST = BASE_URL + "video/list";//视听列表

    public static final String CJRL = BASE_URL + "cjrl/data" + JWT;

}
