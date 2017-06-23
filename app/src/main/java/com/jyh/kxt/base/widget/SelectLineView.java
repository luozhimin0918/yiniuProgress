package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:线性选择器
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/23.
 */

public class SelectLineView extends LinearLayout {

    private Context context;
    private ImageView pointOne;
    private ImageView pointTwo;
    private ImageView pointThree;
    private View line1;
    private View line2;

    private int position=1;

    public SelectLineView(Context context) {
        this(context, null);
    }

    public SelectLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        drawView();
    }

    private void drawView() {
        RelativeLayout rootView = new RelativeLayout(context);
        rootView.setPadding(0, 0, 0, 20);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        rootView.setLayoutParams(rootParams);
        //画第一个点
        pointOne = new ImageView(context);
        pointOne.setId(R.id.point_one);
        RelativeLayout.LayoutParams pointOneParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        pointOneParams.leftMargin = 20;
        pointOneParams.addRule(RelativeLayout.CENTER_VERTICAL);
        pointOneParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        pointOne.setLayoutParams(pointOneParams);
        //画第二个点
        pointTwo = new ImageView(context);
        pointTwo.setId(R.id.point_two);
        RelativeLayout.LayoutParams pointTwoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        pointTwoParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        pointTwo.setLayoutParams(pointTwoParams);
        //画第三个点
        pointThree = new ImageView(context);
        pointThree.setId(R.id.point_three);
        RelativeLayout.LayoutParams pointThreeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        pointThreeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        pointThreeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        pointThreeParams.rightMargin = 20;
        pointThree.setLayoutParams(pointThreeParams);

        //画第一条线段
        line1 = new View(context);
        line1.setId(R.id.line_one);
        RelativeLayout.LayoutParams line1Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
        line1Params.addRule(RelativeLayout.CENTER_VERTICAL);
        line1Params.addRule(RelativeLayout.RIGHT_OF, R.id.point_one);
        line1Params.addRule(RelativeLayout.LEFT_OF, R.id.point_two);

        line1.setLayoutParams(line1Params);
        //画第二条线段
        line2 = new View(context);
        line2.setId(R.id.line_two);
        RelativeLayout.LayoutParams line2Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
        line2Params.addRule(RelativeLayout.CENTER_VERTICAL);
        line2Params.addRule(RelativeLayout.RIGHT_OF, R.id.point_two);
        line2Params.addRule(RelativeLayout.LEFT_OF, R.id.point_three);

        line2.setLayoutParams(line2Params);

        pointOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_sel));
        pointTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_checked));
        pointThree.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_def));
        line1.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color5));
        line2.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color4));
//        TextView textView1 = new TextView(context);
//        textView1.setText("小");
//        textView1.setTextSize(13);
//        textView1.setTextColor(ContextCompat.getColor(context, R.color.font_color60));
//        RelativeLayout.LayoutParams text1Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
//                .LayoutParams.WRAP_CONTENT);
//        text1Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        text1Params.addRule(RelativeLayout.BELOW, R.id.point_one);
//        text1Params.topMargin = 10;
//        textView1.setLayoutParams(text1Params);
//
//        TextView textView2 = new TextView(context);
//        textView2.setText("中");
//        textView2.setTextSize(13);
//        textView2.setTextColor(ContextCompat.getColor(context, R.color.font_color60));
//        RelativeLayout.LayoutParams text2Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
//                .LayoutParams.WRAP_CONTENT);
//        text2Params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        text2Params.addRule(RelativeLayout.BELOW, R.id.point_two);
//        text2Params.topMargin = 10;
//        textView2.setLayoutParams(text2Params);
//
//        TextView textView3 = new TextView(context);
//        textView3.setText("大");
//        textView3.setTextSize(13);
//        textView3.setTextColor(ContextCompat.getColor(context, R.color.font_color60));
//        RelativeLayout.LayoutParams text3Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
//                .LayoutParams.WRAP_CONTENT);
//        text3Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        text3Params.addRule(RelativeLayout.BELOW, R.id.point_three);
//        text3Params.topMargin = 10;
//        textView3.setLayoutParams(text3Params);
//        rootView.addView(textView1);
//        rootView.addView(textView2);
//        rootView.addView(textView3);

        rootView.addView(pointOne);
        rootView.addView(pointTwo);
        rootView.addView(pointThree);
        rootView.addView(line1);
        rootView.addView(line2);

        addView(rootView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int itemWidth = getWidth() / 3;
            float touchX = event.getX();
            int position = 0;
            if (touchX < itemWidth) {
                //点击第一个点
                position = 0;
            } else if (touchX > itemWidth && touchX < itemWidth * 2) {
                //点击第二个点
                position = 1;
            } else {
                //点击第三个点
                position = 2;
            }
            this.position = position;
            changeViewStatus(position);
            if (selectItemListener == null) return super.onTouchEvent(event);
            selectItemListener.selectItem(position);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void changeViewStatus(int position) {
        switch (position) {
            case 0:
                pointOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_checked));
                pointTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_def));
                pointThree.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_def));
                line1.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color4));
                line2.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color4));
                break;
            case 1:
                pointOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_sel));
                pointTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_checked));
                pointThree.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_def));
                line1.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color5));
                line2.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color4));
                break;
            case 2:
                pointOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_sel));
                pointTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_sel));
                pointThree.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_checked));
                line1.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color5));
                line2.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color5));
                break;
        }
    }

    public void changeTheme() {
        switch (position) {
            case 0:
                pointOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_checked));
                pointTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_def));
                pointThree.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_def));
                line1.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color4));
                line2.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color4));
                break;
            case 1:
                pointOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_sel));
                pointTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_checked));
                pointThree.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_def));
                line1.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color5));
                line2.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color4));
                break;
            case 2:
                pointOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_sel));
                pointTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_sel));
                pointThree.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_point_checked));
                line1.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color5));
                line2.setBackgroundColor(ContextCompat.getColor(context, R.color.line_color5));
                break;
        }
    }

    private SelectItemListener selectItemListener;

    public void setSelectItemListener(SelectItemListener selectItemListener) {
        this.selectItemListener = selectItemListener;
    }

    public interface SelectItemListener {
        void selectItem(int positionm);
    }
}
