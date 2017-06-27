package com.jyh.kxt.index.json;

/**
 * Created by Mr'Dai on 2017/6/23.
 */

public class PatchJson {
    private String download_url;
    private String description;
    private String patch_code;

    //补丁路径
    private String patch_path;


    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPatch_code() {
        return patch_code;
    }

    public void setPatch_code(String patch_code) {
        this.patch_code = patch_code;
    }

    public String getPatch_path() {
        return patch_path;
    }

    public void setPatch_path(String patch_path) {
        this.patch_path = patch_path;
    }

    @Override
    public String toString() {
        return "PatchJson{" +
                "download_url='" + download_url + '\'' +
                ", description='" + description + '\'' +
                ", patch_code='" + patch_code + '\'' +
                ", patch_path='" + patch_path + '\'' +
                '}';
    }
}
