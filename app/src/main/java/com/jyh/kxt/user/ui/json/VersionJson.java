package com.jyh.kxt.user.ui.json;

/**
 * Created by Mr'Dai on 2017/5/24.
 */

public class VersionJson {
//    versionName:"",
//    versionCode:"",
//    size:"",
//    url:"",
//    content:""
    private String versionName;
    private int versionCode;
    private String size;
    private String url;
    private String content;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
