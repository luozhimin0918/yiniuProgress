package com.jyh.kxt.base.utils;

import android.content.Context;

import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.dao.EmojeBeanDao;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/10.
 * EmoJe表情管理Url
 */

public class EmoJeUtil {
    private static EmoJeUtil mEmoJeUtil;

    public static EmoJeUtil getInstance() {
        if (mEmoJeUtil == null) {
            mEmoJeUtil = new EmoJeUtil();
        }
        return mEmoJeUtil;
    }


    private List<EmojeBean> emoJeList;

    public List<EmojeBean> loadAllEmoJe(Context mContext) {
        EmojeBeanDao emojeBeanDao = DBManager.getInstance(mContext).getDaoSessionRead().getEmojeBeanDao();
        emoJeList = emojeBeanDao.loadAll();
        return emoJeList;
    }

    public EmojeBean getEmoJeBean(String emoJeName) {
        for (EmojeBean emojeBean : emoJeList) {
            if (emojeBean.getName().equals(emoJeName)) {
                return emojeBean;
            }
        }
        return null;
    }
}
