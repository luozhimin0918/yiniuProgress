package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.widget.night.heple.SkinnableEditText;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/5.
 */

public class EmoticonsEditText extends SkinnableEditText {

    public interface OnTextChangedListener {
        void onTextChanged(CharSequence s, int start, int before, int count);
    }

    private OnTextChangedListener onTextChangedListener;

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    private List mEmoJeNameList = new ArrayList<>();

    public EmoticonsEditText(Context context) {
        super(context);
        init();
    }

    public EmoticonsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmoticonsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    /**
     * 改变状态值  (0 没发生改变  1 我调用的SetText  )
     */
    private int changedState = 0;


    private int textDeleteStart, textDeleteEnd;

    private void init() {
        if (isInEditMode()) {
            return;
        }
        textDeleteStart = textDeleteEnd = -1;

        DaoSession daoSessionRead = DBManager.getInstance(getContext()).getDaoSessionRead();
        Database mDataBase = daoSessionRead.getDatabase();

        Cursor cursor = mDataBase.rawQuery(
                "SELECT NAME FROM EMOJE_BEAN",
                new String[]{});

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            mEmoJeNameList.add(name);
        }
        cursor.close();

        addTextChangedListener(new TextWatcher() {
            /**
             * 改变之前进行处理
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s == null || s.toString().length() == 0) {
                    return;
                }

                textDeleteStart = -1;
                textDeleteEnd = -1;

                if (changedState == 1) {
                    changedState = 0;
                    return;
                }


                if (count > 0 && after == 0) { //删除代码

                    char itemChar = s.charAt(start);

                    if (']' == itemChar) { //如果删除的是括号则判断
                        for (int indexPosition = start - 1; indexPosition >= 0; indexPosition--) {

                            char singleChart = s.charAt(indexPosition);
                            if ('[' == singleChart) { //去掉中括号
                                String emoJeChar = s.subSequence(indexPosition + 1, start).toString();
                                if (mEmoJeNameList.contains(emoJeChar)) {
                                    textDeleteStart = indexPosition; //合法的EmoJe
                                    textDeleteEnd = start;
                                }
                                break;
                            }
                        }
                    }
                }
                if (count == 0 && after > 0) { //增加代码

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (onTextChangedListener != null) {
                    onTextChangedListener.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textDeleteStart != -1 && textDeleteEnd != -1) {
                    changedState = 1;

                    getText().delete(textDeleteStart, textDeleteEnd).toString();
                }
            }
        });
    }


    public void itemEmoJeClick(EmojeBean emojeBean) {

        CharSequence addEmoJeStr = "[" + emojeBean.getName() + "]";

        int index = getSelectionStart();
        Editable editable = getText();

        editable.insert(index, addEmoJeStr);
    }

    public void deleteEmoJeClick() {

        Editable text = getText();
        if (text == null || text.toString().length() == 0) {
            return;
        }

        int selectionPosition = getSelectionStart();
        text.delete(selectionPosition - 1, selectionPosition);
    }
}
