package com.jyh.kxt.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.library.util.SystemUtil;

/**
 * 项目名:KxtProfessional
 * 类描述:多功能输入框
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/11.
 */

public class FunctionEditText extends LinearLayout {

    private LinearLayout topLayout;//顶部布局(主体)
    private View bottomLine;//底线
    private EditText edt;//输入框
    private LinearLayout clearBtn;//删除按钮
    private LinearLayout functionImgBtn;//功能按钮
    private LinearLayout functionTxtBtn;//功能按钮
    private TextView tvFunction;
    private ImageView ivFunction;
    private View functionTxtLine;


    private VerifyCode checkCodeView;//验证码
    /**
     * DEFAULT,文本-删除按钮，默认
     * CHECK_CODE,文本-删除按钮-验证码
     * TEXT,文本-删除按钮-文本
     * IMAGE文本-删除按钮-图片
     */
    private int type = 0;
    private boolean clearShowWhenAll = false;//是否输入完才显示删除(false 只要有输入就显示删除按钮)
    private boolean inputOver = false;
    private boolean showTxtLine = false;

    private int backgroundColor;//背景色
    private int lineColor;//底线颜色
    private int edtTextColor;//输入文本颜色
    private int edtHintColor;//提示文本颜色
    private int functionTextColor;//功能文本颜色

    private int functionTxtLineColor;//功能文字前分割线颜色
    private float edtTextSize;//输入文本字体大小

    private float functionTextSize;//功能文本字体大小
    private Drawable clearDrawble;//删除图片

    private Drawable functionDrawble;//功能图片
    private String edtText;//输入文本
    private String hintText;//提示文本

    private String functionText;//功能文本
    private int width, height;
    private int imageSize;
    private ImageView ivClear;

    public FunctionEditText(Context context) {
        this(context, null);
    }

    public FunctionEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunctionEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setOrientation(LinearLayout.VERTICAL);
        topLayout = new LinearLayout(context);
        topLayout.setOrientation(HORIZONTAL);
        topLayout.setGravity(Gravity.CENTER_VERTICAL);
        topLayout.setPadding(20, 0, 20, 0);
        LinearLayout.LayoutParams topParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        topParams.weight = 1;

        initAttrs(context, attrs);

        initEditText(context);
        initClearBtn(context);

        switch (type) {
            case 0:
                //文本-删除按钮
                break;
            case 1:
                //文本-删除按钮-验证码
                initCheckCodeView(context);
                break;
            case 2:
                //文本-删除按钮-文本
                initFunctionTextView(context);
                break;
            case 3:
                //文本-删除按钮-图片
                initFunctionImage(context);
                break;
            case 4:
                //文本-删除按钮-图片-文本
                initFunctionImage(context);
                initFunctionTextView(context);
            default:
                //文本-删除按钮
                break;
        }

        //添加底线
        bottomLine = new View(context);
        bottomLine.setBackgroundColor(lineColor);
        addView(topLayout, topParams);
        addView(bottomLine, LayoutParams.MATCH_PARENT, SystemUtil.dp2px(context, 1));
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.FunctionEditText);
        type = array.getInt(R.styleable.FunctionEditText_functionEditTextType, 0);
        clearShowWhenAll = array.getBoolean(R.styleable.FunctionEditText_functionEditTextClearType, false);
        lineColor = array.getColor(R.styleable.FunctionEditText_functionEditTextLineColor, ContextCompat.getColor(context, R.color
                .line_color));
        edtTextColor = array.getColor(R.styleable.FunctionEditText_functionEditTextTextColor, ContextCompat.getColor(context, R.color
                .font_color1));
        edtHintColor = array.getColor(R.styleable.FunctionEditText_functionEditTextHintColor, ContextCompat.getColor(context, R.color
                .font_color2));
        functionTextColor = array.getColor(R.styleable.FunctionEditText_functionEditTextFunctionColor, ContextCompat.getColor(context, R
                .color.font_color8));
        functionTxtLineColor = array.getColor(R.styleable.FunctionEditText_functionEditTextFunctionTxtLineColor, ContextCompat.getColor
                (context, R.color.line_color2));
        edtTextSize = array.getDimension(R.styleable.FunctionEditText_functionEditTextTextSize, 42);
        functionTextSize = array.getDimension(R.styleable.FunctionEditText_functionEditTextFunctionTextSize, 36);

        clearDrawble = array.getDrawable(R.styleable.FunctionEditText_functionEditTextClearDrawable);
        functionDrawble = array.getDrawable(R.styleable.FunctionEditText_functionEditTextFunctionDrawable);

        if (clearDrawble == null) {
            clearDrawble = ContextCompat.getDrawable(context, R.mipmap.icon_edt_clear);
        }
        if (functionDrawble == null) {
            functionDrawble = ContextCompat.getDrawable(getContext(), R.drawable.sel_pwd_eye);
        }

        edtText = array.getString(R.styleable.FunctionEditText_functionEditTextText);
        hintText = array.getString(R.styleable.FunctionEditText_functionEditTextHint);
        functionText = array.getString(R.styleable.FunctionEditText_functionEditTextFunction);

        showTxtLine = array.getBoolean(R.styleable.FunctionEditText_functionEditTextShowTxtLine, false);

        imageSize = SystemUtil.dp2px(context, 16);

        array.recycle();
    }

    /**
     * 初始化输入框
     *
     * @param context
     */
    private void initEditText(Context context) {

        edt = new EditText(context);
        edt.setBackground(null);

        edt.setText(edtText);
        edt.setHint(hintText);

        edt.setTextColor(edtTextColor);
        edt.setHintTextColor(edtHintColor);
        edt.setTextSize(TypedValue.COMPLEX_UNIT_PX, edtTextSize);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    clearBtn.setVisibility(GONE);
                    return;
                }
                if (clearShowWhenAll) {
                    clearBtn.setVisibility(inputOver ? VISIBLE : GONE);
                } else {
                    if (s.length() >= 1) {
                        clearBtn.setVisibility(VISIBLE);
                    } else {
                        clearBtn.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        LinearLayout.LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        edt.setLayoutParams(params);
        topLayout.addView(edt);

        edt.setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * 初始化删除按钮
     *
     * @param context
     */
    private void initClearBtn(Context context) {
        clearBtn = new LinearLayout(context);
        clearBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        clearBtn.setGravity(Gravity.CENTER);
        ivClear = new ImageView(context);
        LayoutParams params = new LayoutParams(imageSize, imageSize);
        params.leftMargin = 20;
        clearBtn.addView(ivClear, params);
        Editable text = edt.getText();
        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable text = edt.getText();
                if (text.length() > 0) {
                    edt.requestFocus();
                    edt.setText(text.subSequence(0, text.length() - 1));
                    edt.setSelection(edt.getText().length());
                }
            }
        });
        clearBtn.setLongClickable(true);
        clearBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Editable text = edt.getText();
                if (text == null || !(text.length() > 0)) {
                    return false;
                }
                int size = text.length();
                for (int i = 0; i < size; i++) {
                    Editable text2 = edt.getText();
                    if (text2.length() > 0) {
                        edt.requestFocus();
                        edt.setText(text2.subSequence(0, text2.length() - 1));
                        edt.setSelection(edt.getText().length());
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        ivClear.setImageDrawable(clearDrawble);
        ivClear.setScaleType(ImageView.ScaleType.FIT_XY);
        topLayout.addView(clearBtn);
        clearBtn.setVisibility((text == null || !(text.length() > 0) ? GONE : VISIBLE));
    }

    private boolean showPwd = false;

    /**
     * 初始化功能图片
     *
     * @param context
     */
    private void initFunctionImage(Context context) {
        functionImgBtn = new LinearLayout(context);
        ivFunction = new ImageView(context);
        ivFunction.setBackground(functionDrawble);
        functionImgBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(44, 26);
        layoutParams.leftMargin = 20;
        functionImgBtn.addView(ivFunction, layoutParams);
        edt.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        ivFunction.setSelected(showPwd);
        functionImgBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFunction.setSelected(!showPwd);
                if (showPwd) {
                    showPwd = false;
                    edt.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    showPwd = true;
                    edt.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
                }
                edt.setSelection(edt.getText().length());
            }
        });
        topLayout.addView(functionImgBtn, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .MATCH_PARENT));
    }

    /**
     * 初始化功能文字
     *
     * @param context
     */
    private void initFunctionTextView(Context context) {
        functionTxtBtn = new LinearLayout(context);
        functionTxtBtn.setOrientation(HORIZONTAL);
        functionTxtLine = new View(context);
        LinearLayout.LayoutParams lineParams = new LayoutParams(SystemUtil.dp2px(context, 1), ViewGroup.LayoutParams.MATCH_PARENT);
        lineParams.leftMargin = 20;
        lineParams.bottomMargin = 10;
        lineParams.topMargin = 10;
        functionTxtLine.setBackgroundColor(functionTxtLineColor);
        functionTxtLine.setLayoutParams(lineParams);
        functionTxtBtn.addView(functionTxtLine);
        tvFunction = new TextView(context);
        tvFunction.setText(functionText);
        tvFunction.setTextColor(functionTextColor);
        tvFunction.setTextSize(TypedValue.COMPLEX_UNIT_PX, functionTextSize);
        tvFunction.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = 20;
        functionTxtBtn.addView(tvFunction, params);
        functionTxtBtn.setOnClickListener(onClickListener);
        topLayout.addView(functionTxtBtn, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .MATCH_PARENT));
        functionTxtLine.setVisibility(showTxtLine ? VISIBLE : GONE);
    }

    /**
     * 初始化验证码
     *
     * @param context
     */
    private void initCheckCodeView(Context context) {
        checkCodeView = new VerifyCode(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 100);
        params.leftMargin = 20;
        topLayout.addView(checkCodeView, params);
    }

    /**
     * 判断验证码是否一致 忽略大小写
     */
    public Boolean isEqualsIgnoreCase(String CodeString) {
        return checkCodeView == null ? false : checkCodeView.isEqualsIgnoreCase(CodeString);
    }

    /**
     * 判断验证码是否一致 不忽略大小写
     */
    public Boolean isEquals(String CodeString) {
        return checkCodeView == null ? false : checkCodeView.isEquals(CodeString);
    }

    private OnClickListener onClickListener;

    public void setFunctionClickListener(OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }

    public EditText getEdt() {
        return edt;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        if (bottomLine != null)
            bottomLine.setBackgroundColor(lineColor);
    }

    public int getEdtTextColor() {
        return edtTextColor;
    }

    public void setEdtTextColor(int edtTextColor) {
        this.edtTextColor = edtTextColor;
        if (edt != null)
            edt.setTextColor(edtTextColor);
    }

    public int getEdtHintColor() {
        return edtHintColor;
    }

    public void setEdtHintColor(int edtHintColor) {
        this.edtHintColor = edtHintColor;
        if (edt != null)
            edt.setHintTextColor(edtHintColor);
    }

    public int getFunctionTextColor() {
        return functionTextColor;
    }

    public void setFunctionTextColor(int functionTextColor) {
        if (functionTextColor == 0) {
            return;
        }
        this.functionTextColor = functionTextColor;
        if (tvFunction != null)
            tvFunction.setTextColor(functionTextColor);
    }

    public String getEdtText() {
        return edtText;
    }

    public void setEdtText(String edtText) {
        this.edtText = edtText;
        edt.setText(edtText);
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
        edt.setHint(hintText);
    }

    public String getFunctionText() {
        return functionText;
    }

    public void setFunctionText(String functionText) {
        this.functionText = functionText;
        if (tvFunction != null)
            tvFunction.setText(functionText);
    }

    public LinearLayout getTopLayout() {
        return topLayout;
    }

    public void setTopLayout(LinearLayout topLayout) {
        this.topLayout = topLayout;
    }

    public View getBottomLine() {
        return bottomLine;
    }

    public void setBottomLine(View bottomLine) {
        this.bottomLine = bottomLine;
    }

    public void setEdt(EditText edt) {
        this.edt = edt;
    }

    public LinearLayout getClearBtn() {
        return clearBtn;
    }

    public void setClearBtn(LinearLayout clearBtn) {
        this.clearBtn = clearBtn;
    }

    public LinearLayout getFunctionTxtBtn() {
        return functionTxtBtn;
    }

    public void setFunctionTxtBtn(LinearLayout functionTxtBtn) {
        this.functionTxtBtn = functionTxtBtn;
    }

    public LinearLayout getFunctionImgBtn() {

        return functionImgBtn;
    }

    public void setFunctionImgBtn(LinearLayout functionImgBtn) {
        this.functionImgBtn = functionImgBtn;
    }

    public boolean isShowPwd() {
        return showPwd;
    }

    public void setShowPwd(boolean showPwd) {
        this.showPwd = showPwd;
        if (showPwd) {
            edt.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            edt.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        }
        ivFunction.setSelected(showPwd);
        edt.setSelection(edt.getText().length());
    }

    public VerifyCode getCheckCodeView() {
        return checkCodeView;
    }

    public void setCheckCodeView(VerifyCode checkCodeView) {
        this.checkCodeView = checkCodeView;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isClearShowWhenAll() {
        return clearShowWhenAll;
    }

    public void setClearShowWhenAll(boolean clearShowWhenAll) {
        this.clearShowWhenAll = clearShowWhenAll;
    }

    public boolean isInputOver() {
        return inputOver;
    }

    public void setInputOver(boolean inputOver) {
        this.inputOver = inputOver;
        if (clearBtn != null)
            clearBtn.setVisibility(inputOver ? VISIBLE : GONE);
    }

    public float getEdtTextSize() {
        return edtTextSize;
    }

    public void setEdtTextSize(float edtTextSize) {
        this.edtTextSize = edtTextSize;
        if (edt != null) {
            edt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, edtTextSize);
        }
    }

    public float getFunctionTextSize() {
        return functionTextSize;
    }

    public void setFunctionTextSize(float functionTextSize) {
        this.functionTextSize = functionTextSize;
        if (tvFunction != null) {
            tvFunction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, functionTextSize);
        }
    }

    public Drawable getClearDrawble() {
        return clearDrawble;
    }

    public void setClearDrawble(Drawable clearDrawble) {
        this.clearDrawble = clearDrawble;
        if (ivClear != null && clearDrawble != null) {
            ivClear.setImageDrawable(clearDrawble);
        }
    }

    public Drawable getFunctionDrawble() {
        return functionDrawble;
    }

    public void setFunctionDrawble(Drawable functionDrawble) {
        this.functionDrawble = functionDrawble;
        if (ivFunction != null && functionDrawble != null) {
            ivFunction.setBackground(functionDrawble);
        }
    }

    public View getFunctionTxtLine() {
        return functionTxtLine;
    }

    public void setFunctionTxtLine(View functionTxtLine) {
        this.functionTxtLine = functionTxtLine;
    }

    public boolean isShowTxtLine() {
        return showTxtLine;
    }

    public void setShowTxtLine(boolean showTxtLine) {
        this.showTxtLine = showTxtLine;
        if (functionTxtLine != null)
            functionTxtLine.setVisibility(showTxtLine ? VISIBLE : GONE);
    }

    public int getFunctionTxtLineColor() {
        return functionTxtLineColor;
    }

    public void setFunctionTxtLineColor(int functionTxtLineColor) {
        this.functionTxtLineColor = functionTxtLineColor;
        if (functionTxtLine != null && functionTxtLineColor != 0)
            functionTxtLine.setBackgroundColor(functionTxtLineColor);
    }

    public void reflash() {
        if (functionImgBtn != null) {
            topLayout.removeView(functionImgBtn);
            functionImgBtn = null;
        }
        if (functionTxtBtn != null) {
            topLayout.removeView(functionTxtBtn);
            functionTxtBtn = null;
        }
        if (checkCodeView != null) {
            topLayout.removeView(checkCodeView);
            checkCodeView = null;
        }
        switch (type) {
            case 0:
                //文本-删除按钮
                break;
            case 1:
                //文本-删除按钮-验证码
                initCheckCodeView(getContext());
                break;
            case 2:
                //文本-删除按钮-文本
                initFunctionTextView(getContext());
                break;
            case 3:
                //文本-删除按钮-图片
                initFunctionImage(getContext());
                break;
            case 4:
                //文本-删除按钮-图片-文本
                initFunctionImage(getContext());
                initFunctionTextView(getContext());
                break;
            default:
                break;
        }
        invalidate();
    }
}
