package com.jyh.kxt.index.json;

/**
 * Created by Mr'Dai on 2017/6/23.
 */

public class PatchJson {
    private String url;
    private String patch_code;
    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPatch_code() {
        return patch_code;
    }

    public void setPatch_code(String patch_code) {
        this.patch_code = patch_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PatchJson{" +
                "url='" + url + '\'' +
                ", patch_code='" + patch_code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
