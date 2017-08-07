package com.library.widget.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhy on 15/9/10.
 */
public class OptionFlowLayout extends FlowLayout {

    public OptionFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public OptionFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionFlowLayout(Context context) {
        this(context, null);
    }


    public interface OnTagClickListener {
        boolean onTagClick(View view, int position, FlowLayout parent);
    }

    private OnTagClickListener mOnTagClickListener;

    public void setOnTagClickListener(OnTagClickListener mOnTagClickListener) {
        this.mOnTagClickListener = mOnTagClickListener;
    }

    private int minCount;
    private int maxCount;

    public void setMinOrMaxCheckCount(int minCount, int maxCount) {
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    /**
     * 只接受CheckBox
     *
     * @param viewList
     * @param viewRes
     */
    public void addOptionView(List<String> viewList, int viewRes) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        for (int i = 0; i < viewList.size(); i++) {
            View childView = mInflater.inflate(viewRes, this, false);
            if (childView instanceof CheckBox) {
                CheckBox mCheckBox = (CheckBox) childView;
                addView(mCheckBox);

                mCheckBox.setText(viewList.get(i));
                mCheckBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        executeDefine(v);
                        lastClickView = (CheckBox) v;
                    }
                });
            } else {
                throw new ClassCastException("暂时类型只支持 CheckBox");
            }
        }
    }

    private CheckBox lastClickView;

    public List<String> getCheckBoxText() {
        List<String> checkTextList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            CheckBox childAt = (CheckBox) getChildAt(i);
            if (childAt.isChecked()) {
                checkTextList.add(childAt.getText().toString());
            }
        }
        return checkTextList;
    }

    public void setDefaultOption(int... positionSet) {
        HashSet<Integer> integers = new HashSet<>();
        for (int i = 0; i < positionSet.length; i++) {
            integers.add(positionSet[i]);
        }
        setDefaultOption(integers);
    }

    public void setDefaultOption(Set<Integer> positionSet) {
        for (int i = 0; i < getChildCount(); i++) {
            if (positionSet.contains(i)) {
                CheckBox childAt = (CheckBox) getChildAt(i);
                childAt.setChecked(true);
                lastClickView = childAt;
            }
        }
    }

    //判断当前选中的是否大于小于最大最小值
    private void executeDefine(View v) {
        CheckBox clickView = (CheckBox) v;

        int currentCheckCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            CheckBox childAt = (CheckBox) getChildAt(i);
            if (childAt.isChecked()) {
                currentCheckCount++;
            }
        }
        if (currentCheckCount < minCount) {
            clickView.setChecked(true);
        } else if (currentCheckCount > maxCount) {
            lastClickView.setChecked(false);
            clickView.setChecked(true);
        }
    }
}
