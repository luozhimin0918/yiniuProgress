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

    public interface OnItemCheckBoxClick {
        void onItemClick(int position, CheckBox mCheckBox);
    }

    //计算出来的宽度
    private int itemWidth;

    private int itemHeight;

    //行间距
    private int rowSpace;
    //列间距
    private int columnSpace;

    //一列多少个按钮
    private int columnCount;

    private List<String> radioTxtArray;

    private boolean disabledClick = false;

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

            checkBox.setBackgroundResource(R.drawable.selector_option_layout);
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
                        ContextCompat.getColor(getContext(), R.color.font_color2),
                        ContextCompat.getColor(getContext(), R.color.font_color2)
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
        if (onItemCheckBoxClick != null) {
            onItemCheckBoxClick.onItemClick(allCheckBox.indexOf(v), (CheckBox) v);
        }
    }
}