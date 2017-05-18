package com.jyh.kxt.base.widget;

/**
 * Created by DaiYao on 2016/9/28.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.jyh.kxt.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 流式布局的RadioGroup
 */
public class OptionLayout extends FrameLayout implements View.OnClickListener {

    public static enum SelectMode {
        RadioMode, CheckMode
    }

    public static abstract class OnItemCheckBoxClick {
        public abstract void onItemClick(int position, CheckBox mCheckBox);

        //条件错误回调 0 低于最小限制   1 高出最大限制
        public void onConditionError(int position, CheckBox mCheckBox, int errorType) {

        }
    }

    //计算出来的宽度
    private int itemWidth;

    private int itemHeight;

    //行间距
    private int rowSpace;
    //列间距
    private int columnSpace;

    private int checkBoxBackground;
    private int defaultFontColor;
    private int selectedFontColor;

    //一列多少个按钮
    private int columnCount;

    private List<String> radioTxtArray;

    private boolean disabledClick = false;

    //0 默认类型点击之后重选   1 替换类型选新的CheckBox 之后将旧的给移除
    private SelectMode selectMode;


    //最大最小的选择数量
    private int minSelectCount = 0;
    private int maxSelectCount = 0;

    /**
     * CheckBox 单击
     */
    private OnItemCheckBoxClick onItemCheckBoxClick;

    /**
     * 保存所有的CheckBox
     */
    private List<CheckBox> allCheckBox = new ArrayList<>();

    public void setOnItemCheckBoxClick(OnItemCheckBoxClick onItemCheckBoxClick) {
        this.onItemCheckBoxClick = onItemCheckBoxClick;
    }

    public OptionLayout(Context context) {
        this(context, null);
    }

    public OptionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public OptionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(attrs);
    }

    private void initLayout(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.OptionLayout, 0, 0);
            rowSpace = a.getDimensionPixelSize(R.styleable.OptionLayout_rowSpace, 0);
            columnSpace = a.getDimensionPixelSize(R.styleable.OptionLayout_columnSpace, 0);
            itemHeight = a.getDimensionPixelSize(R.styleable.OptionLayout_itemHeight, 0);
            columnCount = a.getDimensionPixelSize(R.styleable.OptionLayout_columnCount, 3);

            checkBoxBackground = a.getResourceId(R.styleable.OptionLayout_checkBoxBackground, 0);

            defaultFontColor = a.getColor(R.styleable.OptionLayout_defaultFontColor,
                    ContextCompat.getColor(getContext(), R.color.font_color5));

            selectedFontColor = a.getColor(R.styleable.OptionLayout_selectedFontColor,
                    ContextCompat.getColor(getContext(), R.color.font_color5));

            int arrayId = a.getResourceId(R.styleable.OptionLayout_array, 0);
            if (arrayId != 0) {
                generateCheckBox(arrayId);
            }
            a.recycle();//回收内存
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight;

        itemWidth = (measureWidth - (columnCount - 1) * columnSpace) / columnCount;

        int rowCount = 1;

        int childCount = getChildCount();

        for (int index = 0; index < childCount; index++) {
            CheckBox child = (CheckBox) getChildAt(index);

            if (index != 0 && index % columnCount == 0) {
                rowCount++;
            }

            if (radioTxtArray != null && radioTxtArray.size() != 0) {
                String itemRadioTxt = radioTxtArray.get(index);
                child.setText(itemRadioTxt);
                child.setGravity(Gravity.CENTER);

                ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                layoutParams.width = itemWidth;
                layoutParams.height = itemHeight;

                child.setLayoutParams(layoutParams);
            }
        }
        measureHeight = rowCount * itemHeight + rowCount * rowSpace - rowSpace;
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        int xPoint = 0;
        int yPoint = 0;

        int rowCount = 1;
        int edge = 0;

        for (int index = 0; index < childCount; index++) {
            if (index != 0 && index % columnCount == 0) {
                xPoint = 0;
                yPoint = rowCount * itemHeight + rowCount * rowSpace;
                rowCount++;
            }


            if (xPoint == 0) {
                edge = 0;
            } else {
                edge += columnSpace;
            }

            CheckBox child = (CheckBox) getChildAt(index);
            child.layout(
                    xPoint * itemWidth + edge,
                    yPoint,
                    xPoint * itemWidth + itemWidth + edge,
                    yPoint + itemHeight);

            xPoint++;
        }
    }


    /**
     * 设置那几项是选中的
     *
     * @param index
     */
    public void setSelectItemIndex(int index) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            CheckBox checkBox = (CheckBox) this.getChildAt(i);
            if (index == i) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
    }


    /**
     * 设置默认选中的集合
     */
    public void setSelectItemIndex(Set<String> set) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            CheckBox checkBox = (CheckBox) this.getChildAt(i);
            if (set.contains(radioTxtArray.get(i))) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
    }

    /**
     * 生成列表信息
     *
     * @param radioTxtArray
     */
    public void generateCheckBox(List<String> radioTxtArray) {
        this.radioTxtArray = radioTxtArray;
        ColorStateList colorStateList = createColorStateList();

        for (int i = 0; i < radioTxtArray.size(); i++) {
            CheckBox checkBox = new CheckBox(getContext());

            checkBox.setBackgroundResource(checkBoxBackground);

            checkBox.setTextColor(colorStateList);

            checkBox.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            checkBox.setOnClickListener(this);

            allCheckBox.add(checkBox);
            addView(checkBox);
        }
    }

    public void generateCheckBox(int arrayRes) {
        String[] stringArray = getContext().getResources().getStringArray(arrayRes);
        List<String> strings = Arrays.asList(stringArray);
        generateCheckBox(strings);
    }

    /**
     * 对TextView设置不同状态时其文字颜色。
     */
    private ColorStateList createColorStateList() {
        int[] colors = new int[]
                {
                        selectedFontColor,
                        defaultFontColor
                };

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    public HashSet<String> getSelectedMap() {
        HashSet<String> checkBoxVal = new HashSet<>();
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            CheckBox myCheckBox = (CheckBox) getChildAt(index);
            if (myCheckBox.isChecked()) {
                checkBoxVal.add(myCheckBox.getText().toString());
            }
        }
        return checkBoxVal;
    }

    /**
     * 是否可点击重选
     *
     * @param disabledClick
     */
    public void disabledClick(boolean disabledClick) {
        this.disabledClick = disabledClick;
    }


    @Override
    public void onClick(View v) {
        int selectCount = 0;

        CheckBox checkBox = (CheckBox) v;

        //默认为0不包括  1点击是全部  2选中的包括有全部
        int selectedIncludeAll = 0;

        for (int index = 0; index < allCheckBox.size(); index++) {
            CheckBox myCheckBox = (CheckBox) getChildAt(index);
            if (myCheckBox.isChecked()) {

                if ("全部".equals(myCheckBox.getText())) {
                    selectedIncludeAll = 2;
                }

                if (selectMode == SelectMode.CheckMode) {
                    selectCount++;
                } else {
                    selectCount = minSelectCount;
                    myCheckBox.setChecked(false);
                }
            }
        }

        if ("全部".equals(checkBox.getText())) {
            selectedIncludeAll = 1;
        }

        //如果是多选模式
        //1. 点击选中了全部, 则将除全部之外的所有CheckBox重置
        //2. 如果选中了其他, 但是全部还是选中状态, 则替换所有全部
        if (selectMode == SelectMode.CheckMode) {
            if (selectedIncludeAll == 1) {
                for (int index = 0; index < allCheckBox.size(); index++) {
                    CheckBox myCheckBox = (CheckBox) getChildAt(index);
                    if (myCheckBox.isChecked()) {
                        if (!"全部".equals(myCheckBox.getText())) {
                            myCheckBox.setChecked(false);
                            selectCount--;
                        }
                    }
                }
            }
            if (selectedIncludeAll == 2) {
                for (int index = 0; index < allCheckBox.size(); index++) {
                    CheckBox myCheckBox = (CheckBox) getChildAt(index);
                    if (myCheckBox.isChecked()) {
                        if ("全部".equals(myCheckBox.getText())) {
                            myCheckBox.setChecked(false);
                            selectCount--;
                        }
                    }
                }
            }
        }


        if (selectMode == SelectMode.RadioMode) {
            checkBox.setChecked(true);
        }

        //如果小于最小的选择数
        if (selectCount < minSelectCount) {

            if (selectMode == SelectMode.CheckMode) {
                checkBox.setChecked(true);
            }

            if (onItemCheckBoxClick != null) {
                onItemCheckBoxClick.onConditionError(allCheckBox.indexOf(v), (CheckBox) v, 0);
            }
            return;
        }

        //如果大于最大的选择数
        if (selectCount > maxSelectCount) {
            checkBox.setChecked(false);

            if (onItemCheckBoxClick != null) {
                onItemCheckBoxClick.onConditionError(allCheckBox.indexOf(v), (CheckBox) v, 1);
            }
            return;
        }
        if (onItemCheckBoxClick != null) {
            onItemCheckBoxClick.onItemClick(allCheckBox.indexOf(v), (CheckBox) v);
        }
    }

    public void simpleInitConfig() {
        setSelectItemIndex(0);
        setMinSelectCount(1);
        setMaxSelectCount(1);
    }

    public void setMinSelectCount(int minSelectCount) {
        this.minSelectCount = minSelectCount;
    }

    public void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    public void setSelectMode(SelectMode selectMode) {
        this.selectMode = selectMode;
    }

}