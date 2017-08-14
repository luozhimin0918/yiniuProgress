package com.jyh.kxt.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.manager.ConnectivityMonitor;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.search.adapter.AutoCompleteAdapter;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:搜索输入框
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/19.
 */

public class SearchEditText extends RelativeLayout {

    private Context context;

    private float searchImgSize = 25;
    private float clearImgSize = 30;
    private float marginLeft = 14;
    private float marginRight = 30;
    private float drawablePadding = 15;
    private Drawable bgDrawable;
    private float textSize = 32;
    private int textColor;
    private int hintColor;
    private String text = "", hint = "";
    private AutoCompleteTextView editText;
    private ImageView clearView;
    private int edtMarginLeft;
    private int edtMarginRight;

    private boolean isShowClearBtn = true;//是否显示清除按钮
    private ImageView searchView;
    private AutoCompleteAdapter adapter;

    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        initValue(attrs);
        drawView();
    }

    private void initValue(AttributeSet attrs) {

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SearchEditText);
        bgDrawable = array.getDrawable(R.styleable.SearchEditText_searchBackground);

        text = array.getString(R.styleable.SearchEditText_searchText);
        hint = array.getString(R.styleable.SearchEditText_searchHint);
        textSize = SystemUtil.px2sp(getContext(), getResources().getDimension(R.dimen.content_font_size));
        textColor = array.getColor(R.styleable.SearchEditText_searchTextColor, Color.GRAY);
        hintColor = array.getColor(R.styleable.SearchEditText_searchHintColor, Color.GRAY);
        searchImgSize = array.getDimension(R.styleable.SearchEditText_searchLeftImgSize, 35);
        clearImgSize = array.getDimension(R.styleable.SearchEditText_searchRightImgSize, 40);
        marginLeft = array.getDimension(R.styleable.SearchEditText_searchMarginLeft, 20);
        marginRight = array.getDimension(R.styleable.SearchEditText_searchMarginRight, 40);
        drawablePadding = array.getDimension(R.styleable.SearchEditText_searchDrawablePadding, 15);

        edtMarginLeft = (int) (marginLeft + searchImgSize + drawablePadding);
        edtMarginRight = (int) (marginRight + clearImgSize + drawablePadding);
    }

    private void drawView() {
        setBackground(bgDrawable);
        drawSearchView();
        if (isShowClearBtn) {
            drawClearView();
        }
        drawInputView();
    }

    /**
     * 搜索图标
     */
    private void drawSearchView() {
        searchView = new ImageView(context);
        searchView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_search_edt_search));
        LayoutParams searchParams = new LayoutParams((int) searchImgSize, (int) searchImgSize);
        searchParams.leftMargin = (int) marginLeft;
        searchParams.addRule(RelativeLayout.CENTER_VERTICAL);
        searchParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        addView(searchView, searchParams);
    }

    /**
     * 输入控件
     */
    private void drawInputView() {
        editText = new AutoCompleteTextView(context);
        editText.setThreshold(1);//设置从第几个字符开始出现提示
        editText.setBackground(null);
        editText.setTextSize(textSize);
        editText.setTextColor(textColor);
        editText.setHintTextColor(hintColor);
        editText.setHint(hint);
        editText.setDropDownHeight(SystemUtil.getScreenDisplay(context).heightPixels / 3);//设置下拉列表高度
        editText.setSingleLine(true);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        RelativeLayout.LayoutParams editParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        editParams.leftMargin = edtMarginLeft;
        editParams.rightMargin = edtMarginRight;

        editText.setPadding(0, 0, 0, 0);

        addView(editText, editParams);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (editText != null) editText.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 清除控件
     */
    private void drawClearView() {
        clearView = new ImageView(context);
        clearView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_search_edt_clear));
        LayoutParams clearParams = new LayoutParams((int) clearImgSize, (int) clearImgSize);
        clearParams.rightMargin = (int) marginRight;
        clearParams.addRule(RelativeLayout.CENTER_VERTICAL);
        clearParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(clearView, clearParams);
        clearView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        clearView.setVisibility(GONE);
    }

    public void setText(String text) {
        if (text == null) text = "";
        editText.setText(text);
        editText.setSelection(text.length());
    }

    public String getText() {
        return editText.getText().toString();
    }

    /**
     * 是否显示清除控件
     *
     * @param showClearBtn
     */
    public void setShowClearBtn(boolean showClearBtn) {
        isShowClearBtn = showClearBtn;
        edtMarginLeft = (int) (marginLeft + searchImgSize + drawablePadding);
        edtMarginRight = (int) (marginRight);
        invalidate();
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        editText.setOnEditorActionListener(onEditorActionListener);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }

    public void setHint(String hint) {
        if (editText != null)
            editText.setHint(hint);
    }

    /**
     * 关闭下拉提示框
     */
    public void dismissDropDown() {
        if (editText != null)
            editText.dismissDropDown();
    }

    public ListAdapter getAdapter() {
        if (editText != null)
            return editText.getAdapter();
        return null;
    }

    public class TextWatcher implements android.text.TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                clearView.setVisibility(GONE);
            } else {
                clearView.setVisibility(VISIBLE);
            }
        }
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (editText != null)
            editText.setTextColor(textColor);
        invalidate();
    }

    public void setHintColor(int hintColor) {
        this.hintColor = hintColor;
        if (editText != null)
            editText.setHintTextColor(hintColor);
        invalidate();
    }

    public void changeImg() {
        if (clearView != null)
            clearView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_search_edt_clear));
        if (searchView != null)
            searchView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_search_edt_search));
        invalidate();
    }

    public void setAdapter(AutoCompleteAdapter adapter) {
        this.adapter = adapter;
        if (editText != null && adapter != null)
            editText.setAdapter(adapter);
    }

    public void setData(List<QuoteItemJson> data) {
        if (adapter != null) {
            if (adapter.dataList == null) {
                adapter.dataList = new ArrayList<>();
            }
            adapter.dataList.clear();
            adapter.dataList.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }

    public void addData(List<QuoteItemJson> data) {
        if (adapter != null) {
            if (adapter.dataList == null) {
                adapter.dataList = new ArrayList<>();
            }
            adapter.dataList.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }

    public void addData(QuoteItemJson data) {
        if (adapter != null) {
            if (adapter.dataList == null) {
                adapter.dataList = new ArrayList<>();
            }
            adapter.dataList.add(data);
            adapter.notifyDataSetChanged();
        }
    }

}
