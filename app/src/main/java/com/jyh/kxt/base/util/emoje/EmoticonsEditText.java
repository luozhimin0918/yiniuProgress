package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.EmojeBean;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/5.
 */

public class EmoticonsEditText extends EditText {

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
                Log.e("beforeTextChanged", "beforeTextChanged() called with: s = [" + s + "], start = [" + start +
                        "], count = [" +
                        count + "], after = [" + after + "]");


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
                Log.e("onTextChanged", "onTextChanged() called with: s = [" + s + "], start = [" + start + "], before" +
                        " = [" +
                        before + "], count = [" + count + "]");

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

        int selectionPosition = getSelectionStart();
        getText().delete(selectionPosition - 1, selectionPosition);


//        isAddChart = true;
//        changedState = 1;
//
//        String currentContent = getText().toString();
//
//        String cursorFront = currentContent.substring(0, getSelectionStart());
//        int lastIndexPosition = cursorFront.length() - 1;
//        char lastChart = cursorFront.charAt(lastIndexPosition);
//
//        int startIndex = 0;
//        if (']' == lastChart) {
//
//            for (int indexPosition = lastIndexPosition - 1; indexPosition > 0; indexPosition--) {
//                char singleChart = cursorFront.charAt(indexPosition);
//                if (']' == singleChart) {//错误的 ] 括号 //只删除这一个值
//                    startIndex = lastIndexPosition;
//                    currentContent = getText().delete(lastIndexPosition - 1, lastIndexPosition).toString();
//                    break;
//                }
//                if ('[' == singleChart) {
//                    startIndex = indexPosition;
//                    currentContent = getText().delete(startIndex, lastIndexPosition + 1).toString();
//                    break;
//                }
//            }
//        } else {
//            int selectionStart = getSelectionStart();
//            currentContent = getText().delete(selectionStart - 1, selectionStart).toString();
//            startIndex = selectionStart - 1;
//        }
//
//        setText(currentContent);
//        setSelection(startIndex);


    }
}
