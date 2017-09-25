package com.jyh.kxt.base.utils;

import com.jyh.kxt.base.constant.IntentConstant;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;

import org.json.JSONObject;

/**
 * 项目名:Kxt
 * 类描述:Socket 工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class SocketUtils {

    private static SocketUtils mSocketUtils;

    public static SocketUtils getInstance() {
        if (mSocketUtils == null) {
            mSocketUtils = new SocketUtils();
        }
        return mSocketUtils;
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        String token = "";
        try {
            JSONObject object = new JSONObject();
            object.put(IntentConstant.SOCKET_TIME, "" + System.currentTimeMillis() / 1000);
            object.put(IntentConstant.SOCKET_DOMAIN, VarConstant.SOCKET_DOMAIN);
            object.put(IntentConstant.SOCKET_REMOTE_ADDR, SystemUtil.getHostIP());

            token = Encrypt.encrypt(object.toString(), VarConstant.SOCKET_KEY, 6000);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }


}
