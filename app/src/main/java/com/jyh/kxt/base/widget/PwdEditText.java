package com.jyh.kxt.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jyh.kxt.R;
import com.library.util.SystemUtil;

/**
 * 项目名:Kxt
 * 类描述:密码输入框
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class PwdEditText extends RelativeLayout {

    private boolean showPwd = false;//是否显示密码 默认不显示
    private int lineColor;//线段颜色
    private int background;//背景色
    private int hintColor;
    private int textColor;
    private float textSize = 16;
    private String text = "", hint = "";


    private ImageView eyeView;
    private EditText editText;

    public PwdEditText(Context context) {
        super(context);
        init(null);
    }

    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PwdEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        initValues(attrs);

        drawView();
    }

    private void initValues(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PwdEditText);
        textSize = SystemUtil.px2sp(getContext(), getResources().getDimension(R.dimen.content_font_size));
        showPwd = array.getBoolean(R.styleable.PwdEditText_showPwd, false);
        lineColor = array.getColor(R.styleable.PwdEditText_lineColor, ContextCompat.getColor(getContext(), R.color.line_color3));
        background = array.getColor(R.styleable.PwdEditText_background, ContextCompat.getColor(getContext(), R.color.theme1));
        text = array.getString(R.styleable.PwdEditText_text);
        hint = array.getString(R.styleable.PwdEditText_hint);
        textColor = array.getColor(R.styleable.PwdEditText_textColor, Color.GRAY);
        hintColor = array.getColor(R.styleable.PwdEditText_hintColor, Color.GRAY);
    }

    private void drawView() {

        setBackgroundColor(background);

        setLine();

        setEditText();

        setEyeView();

    }

    /**
     * 眼睛
     */
    private void setEyeView() {
        eyeView = new ImageView(getContext());
        eyeView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_pwd_eye));
        eyeView.setSelected(showPwd);

        LayoutParams eyeParams = new LayoutParams(48, 29);//宽高比32:19
        eyeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        eyeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        eyeParams.setMargins(0, 0, 48, 0);

        eyeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eyeView.setSelected(!showPwd);
                if (showPwd) {
                    showPwd = false;
                    editText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    showPwd = true;
                    editText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
                }
            }
        });

        addView(eyeView, eyeParams);

    }

    /**
     * 输入框
     */
    private void setEditText() {
        editText = new EditText(getContext());
        editText.setSingleLine(true);
        editText.setBackground(null);
        editText.setHint(hint);
        editText.setHintTextColor(hintColor);
        editText.setText(text);
        editText.setTextColor(textColor);
        editText.setTextSize(textSize);
        LayoutParams edtParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        edtParams.setMargins(0, 1, 106, 1);
        editText.setPadding(48, 0, 48, 0);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        if (showPwd) {
            editText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            editText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        }
        addView(editText, edtParams);
    }

    /**
     * 上下边境线
     */
    private void setLine() {
        View viewTop = new View(getContext());
        viewTop.setBackgroundColor(lineColor);
        LayoutParams topParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        addView(viewTop, topParams);

        View viewBottom = new View(getContext());
        viewBottom.setBackgroundColor(lineColor);
        LayoutParams bottomParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(viewBottom, bottomParams);
    }

    public EditText getEditText() {
        return editText;
    }
}
