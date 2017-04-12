package com.jyh.kxt.base.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/4/12.
 */

public class StarView extends LinearLayout {
    public StarView(Context context) {
        super(context);
        initStar();
    }

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStar();
    }

    public StarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStar();
    }

    private void initStar() {
        setOrientation(LinearLayout.HORIZONTAL);

        int starSize = (int) getResources().getDimension(R.dimen.star_size);
        int starMargins = (int) getResources().getDimension(R.dimen.star_margins);

        for (int i = 0; i < 3; i++) {
            AppCompatImageView starView = new AppCompatImageView(getContext());
            starView.setImageResource(R.mipmap.icon_star);
            LayoutParams starParams = new LayoutParams(starSize, starSize);
            starParams.setMargins(0, 0, starMargins, 0);
            addView(starView, starParams);

            ColorStateList colorStateList = getResources().getColorStateList(R.color.star_color1);
            starView.setImageTintList(colorStateList);
        }
    }

    public void refreshConfig(int selectCount, boolean isMatter) {
        for (int i = 0; i < selectCount; i++) {
            if (i < selectCount) {
                ImageView childView = (ImageView) getChildAt(i);

                ColorStateList colorStateList = getResources().getColorStateList
                        (isMatter ? R.color.star_color3 : R.color.star_color2);

                childView.setImageTintList(colorStateList);
            }
        }
    }
}
