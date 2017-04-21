package com.jyh.kxt.index.json;

/**
 * 项目名:Kxt
 * 类描述:Socket
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class SocketJson {
    private String server;
    private String token;

    public SocketJson() {
    }

    public SocketJson(String server, String token) {

        this.server = server;
        this.token = token;
    }

    public String getServer() {

        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
