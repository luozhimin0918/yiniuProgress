package com.jyh.kxt.main.json;

import com.jyh.kxt.index.json.TypeDataJson;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/7.
 */

public class PreloadIndex {
    public static PreloadIndex mPreloadIndex = new PreloadIndex();

    public static PreloadIndex getInstance() {
        return mPreloadIndex;
    }

    private List<TypeDataJson> typeDataList;

    public List<TypeDataJson> getTypeDataList() {
        return typeDataList;
    }

    public void setTypeDataList(List<TypeDataJson> typeDataList) {
        this.typeDataList = typeDataList;
    }
}
