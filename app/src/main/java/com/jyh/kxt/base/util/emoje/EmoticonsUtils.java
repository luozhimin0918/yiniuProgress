package com.jyh.kxt.base.util.emoje;

import android.content.Context;

import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.EmojeBean;
import com.library.util.SPUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmoticonsUtils {

    /**
     * 初始化表情数据库
     *
     * @param context
     */
    public static void initEmoticonsDB(Context context) {

        Boolean isInitEmoJe = SPUtils.getBoolean(context, SpConstant.EMOJE_IS_INIT);
        if (!isInitEmoJe) {
            try {
                String[] assetsFileList = new String[]{
                        "paobing",
                        "default",
                        "keke",
                        "miya",
                        "mohan",
                        "baolixiong"
                };
                String[] groupChineseNameArray = new String[]{
                        "炮兵",
                        "默认",
                        "可可",
                        "米亚",
                        "茉晗",
                        "熊"
                };

                List<EmojeBean> allEmoJeList = new ArrayList<>();

                for (int i = 0; i < assetsFileList.length; i++) {
                    String[] list = context.getAssets().list(assetsFileList[i]);
                    for (String itemName : list) {
                        EmojeBean emojeBean = new EmojeBean();

                        String[] splitItemName = itemName.split("\\.");
                        emojeBean.setName(splitItemName[0]);
                        emojeBean.setSuffixName(splitItemName[1]);

                        emojeBean.setGroupName(assetsFileList[i]);
                        emojeBean.setGroupChineseName(groupChineseNameArray[i]);
                        emojeBean.setUrl("file:///android_asset/" + assetsFileList[i] + "/" + itemName);
                        allEmoJeList.add(emojeBean);
                    }
                }

                DaoSession daoSessionWrit = DBManager.getInstance(context).getDaoSessionWrit();
                daoSessionWrit.getEmojeBeanDao().insertInTx(allEmoJeList);
                SPUtils.save(context, SpConstant.EMOJE_IS_INIT, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
