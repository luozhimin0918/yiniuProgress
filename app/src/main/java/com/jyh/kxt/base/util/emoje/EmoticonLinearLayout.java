package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.EmojeBean;
import com.library.util.SystemUtil;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/8.
 */

public class EmoticonLinearLayout extends LinearLayout {

    public void onChangeTheme() {
        try {
            selectGroup(groupViewPagerPosition);
            for (View view : mGroupLineList) {
                int bgColor = ContextCompat.getColor(getContext(), R.color.line_color3);
                view.setBackgroundColor(bgColor);
            }
            int lineColor = ContextCompat.getColor(getContext(), R.color.line_color);
            viewLine.setBackgroundColor(lineColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnItemClick {
        void itemEmoJeClick(EmojeBean emojeBean);

        void deleteEmoJeClick();
    }

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    private EmoticonViewPager evpEmoJePage;
    private LinearLayout mLlLabel;
    private LinearLayout mLlCircle;
    private View viewLine;

    private List<String> mGroupNameList;
    private List<TextView> mGroupNameViewList = new ArrayList<>();
    private List<View> mGroupLineList = new ArrayList<>();
    private int groupViewPagerPosition = 0;
    private boolean isOnlyAllowSmallEmoJe = false;

    public EmoticonLinearLayout(Context context) {
        super(context, null);
    }

    public EmoticonLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmoticonLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initEmoticon();
    }

    public void setOnlyAllowSmallEmoJe(boolean isOnlyAllowSmallEmoJe) {
        this.isOnlyAllowSmallEmoJe = isOnlyAllowSmallEmoJe;
    }

    private void initEmoticon() {
        evpEmoJePage = (EmoticonViewPager) findViewWithTag("evp_emoje");
        mLlCircle = (LinearLayout) findViewWithTag("ll_circle");
        mLlLabel = (LinearLayout) findViewWithTag("ll_label");
        viewLine = findViewById(R.id.view_line);

        /**
         * 获得数据库最新数据
         */
        DaoSession daoSessionRead = DBManager.getInstance(getContext()).getDaoSessionRead();
        Database mDataBase = daoSessionRead.getDatabase();

        mGroupNameList = new ArrayList<>();
        Cursor cursor = mDataBase.rawQuery(
                "SELECT GROUP_CHINESE_NAME FROM EMOJE_BEAN WHERE 1=1 GROUP BY GROUP_CHINESE_NAME ORDER BY _ID ASC",
                new String[]{});

        while (cursor.moveToNext()) {
            String groupName = cursor.getString(cursor.getColumnIndex("GROUP_CHINESE_NAME"));
            mGroupNameList.add(groupName);

            if (isOnlyAllowSmallEmoJe) {
                if ("可可".equals(groupName) || "米亚".equals(groupName) || "茉晗".equals(groupName)) {
                    mGroupNameList.remove(groupName);
                }
            }
        }
        cursor.close();

        /**
         * 初始化下面的标签组
         */
        initLabelLayout();

        /**
         * 初始化ViewPager
         */
        evpEmoJePage.initViewPager(mGroupNameList);

    }

    public void itemEmoJeClick(EmojeBean emojeBean) {
        if (onItemClick != null) {
            onItemClick.itemEmoJeClick(emojeBean);
        }
    }

    /**
     * 删除EditText
     */
    public void deleteEmoJeClick() {
        if (onItemClick != null) {
            onItemClick.deleteEmoJeClick();
        }
    }

    /**
     * Group 里面的ViewPage 滑动  改变圆圈数量
     *
     * @param position
     */
    public void onGroupItemPageChangeListener(int position) {
        for (int i = 0; i < mLlCircle.getChildCount(); i++) {
            ImageView ivDotted = (ImageView) mLlCircle.getChildAt(i);
            int color = ContextCompat.getColor(getContext(), R.color.line_color3);
            CircleDrawable circleDrawable = new CircleDrawable(color);
            ivDotted.setImageDrawable(circleDrawable);
        }

        ImageView ivDotted = (ImageView) mLlCircle.getChildAt(position);

        int color = ContextCompat.getColor(getContext(), R.color.font_color9);
        CircleDrawable circleDrawable = new CircleDrawable(color);
        ivDotted.setImageDrawable(circleDrawable);
    }

    /**
     * Group 滑动  切换下面的组选中背景色
     *
     * @param position
     * @param groupList
     */
    public void onGroupPageChangeListener(int position, List<ViewPager> groupList) {
        //创建新的 mLlCircle
        mLlCircle.removeAllViews();

        ViewPager viewPager = groupList.get(position);
        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            ImageView ivDotted = new ImageView(getContext());
            int dottedSize = SystemUtil.dp2px(getContext(), 6);

            LayoutParams params = new LayoutParams(dottedSize, dottedSize);
            params.setMargins(8, 0, 8, 0);
            ivDotted.setLayoutParams(params);

            int color = ContextCompat.getColor(getContext(), R.color.line_color3);
            CircleDrawable circleDrawable = new CircleDrawable(color);
            ivDotted.setImageDrawable(circleDrawable);

            mLlCircle.addView(ivDotted);
        }

        ImageView ivDotted = (ImageView) mLlCircle.getChildAt(viewPager.getCurrentItem());


        int color = ContextCompat.getColor(getContext(), R.color.font_color9);
        CircleDrawable circleDrawable = new CircleDrawable(color);
        ivDotted.setImageDrawable(circleDrawable);

        //改变选择块背景
        selectGroup(position);
    }

    private void initLabelLayout() {
        int minWidth = SystemUtil.dp2px(getContext(), 62);
        for (int i = 0; i < mGroupNameList.size(); i++) {
            String itemName = mGroupNameList.get(i);

            TextView itemTextView = new TextView(getContext());

            itemTextView.setText(itemName);
            LayoutParams itemLp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
            itemTextView.setLayoutParams(itemLp);

            itemTextView.setMinWidth(minWidth);
            itemTextView.setGravity(Gravity.CENTER);

            mGroupNameViewList.add(itemTextView);
            mLlLabel.addView(itemTextView);

            //右边的线
            View splitView = new View(getContext());
            int bgColor = ContextCompat.getColor(getContext(), R.color.line_color3);

            LayoutParams splitLp = new LayoutParams(
                    1,
                    LayoutParams.MATCH_PARENT);

            splitLp.setMargins(0, 20, 0, 20);
            splitLp.gravity = Gravity.CENTER;

            splitView.setBackgroundColor(bgColor);
            splitView.setLayoutParams(splitLp);

            mLlLabel.addView(splitView);

            final int finalI = i;
            itemTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    evpEmoJePage.setCurrentItem(finalI);
                }
            });

            mGroupLineList.add(splitView);
        }
    }

    //当前选中Group
    private void selectGroup(int select) {
        int color = ContextCompat.getColor(getContext(), R.color.theme1);

        for (int i = 0; i < mGroupNameViewList.size(); i++) {
            TextView labelView = mGroupNameViewList.get(i);
            labelView.setBackgroundColor(color);
        }

        TextView labelView = mGroupNameViewList.get(select);
        int bgColor = ContextCompat.getColor(getContext(), R.color.line_color3);
        labelView.setBackgroundColor(bgColor);
        groupViewPagerPosition = select;
    }


    private class CircleDrawable extends GradientDrawable {
        public CircleDrawable(int mColor) {

            int dottedSize = SystemUtil.dp2px(getContext(), 3);
            setOrientation(Orientation.LEFT_RIGHT);
            setColor(mColor);
            setCornerRadius(dottedSize);
        }
    }
}
