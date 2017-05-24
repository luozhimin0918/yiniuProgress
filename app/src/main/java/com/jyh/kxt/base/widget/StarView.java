package com.jyh.kxt.base.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/4/12.
 */

public class StarView extends LinearLayout {

    private final int STAR_COUNT = 3;

    private int starSize, starMargins;

    public StarView(Context context) {
        this(context, null);
    }

    public StarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStar();
    }

    private void initStar() {
        setOrientation(LinearLayout.HORIZONTAL);

        starSize = (int) getResources().getDimension(R.dimen.star_size);
        starMargins = (int) getResources().getDimension(R.dimen.star_margins);

        for (int i = 0; i < STAR_COUNT; i++) {
            ImageView starView = new ImageView(getContext());

            starView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_star1));

            LayoutParams starParams = new LayoutParams(starSize, starSize);
            starParams.setMargins(0, 0, starMargins, 0);
            addView(starView, starParams);
        }
    }

    public void setImportance(String importance) {

        int selectCount = 0; //等级  高中低
        boolean isHigh = false; //是否是 => 高
        switch (importance) {
            case "低":
                selectCount = 1;
                break;
            case "中":
                selectCount = 2;
                break;
            case "高":
                selectCount = 3;
                isHigh = true;
                break;
        }


        for (int i = 0; i < STAR_COUNT; i++) {
            ImageView starView = (ImageView) getChildAt(i);

            if (i < selectCount) {
                starView.setImageDrawable(isHigh ? ContextCompat.getDrawable(getContext(), R.mipmap.icon_star3) : ContextCompat
                        .getDrawable(getContext(), R.mipmap.icon_star2));
            } else {
                starView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_star1));
            }
        }
    }
}
