package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.utils.EmoJeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mr'Dai on 2017/9/7.
 */

public class EmoticonReplaceTextView extends TextView {


    public EmoticonReplaceTextView(Context context) {
        this(context, null);
    }

    public EmoticonReplaceTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmoticonReplaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void convertEmoJeToText(SpannableStringBuilder currentSpannable, String replaceText) {
        Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(currentSpannable.toString());
        try {
            int matcherContentLength = 0;//匹配的内容长度
            while (matcher.find()) {
                String group = matcher.group(1);

                String emoJeName;

                String[] splitGroup = group.split(",");
                if (splitGroup.length == 2) {
                    emoJeName = splitGroup[0];
                } else if (splitGroup.length == 1) {
                    emoJeName = splitGroup[0];
                } else {
                    continue;
                }

                EmojeBean emoJeBean = EmoJeUtil.getInstance().getEmoJeBean(getContext(),emoJeName);
                if (emoJeBean != null) { //是否本地存在这个EmoJe表情
                    int matcherStart = matcher.start() - matcherContentLength;
                    int matcherEnd = matcher.end() - matcherContentLength;
                    currentSpannable.replace(
                            matcherStart, //这里因为没有加上中括号
                            matcherEnd,
                            replaceText);
                    matcherContentLength += matcher.end() - matcher.start() - replaceText.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setText(currentSpannable);
    }
}
