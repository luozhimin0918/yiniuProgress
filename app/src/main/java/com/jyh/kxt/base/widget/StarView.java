package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/4/12.
 */

public class StarView extends LinearLayout {
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

        int starSize = (int) getResources().getDimension(R.dimen.star_size);
        int starMargins = (int) getResources().getDimension(R.dimen.star_margins);

        for (int i = 0; i < 3; i++) {
            ImageView starView = new ImageView(getContext());

            int color = ContextCompat.getColor(getContext(), R.color.star_color1);

            Drawable drawable = getResources().getDrawable(R.mipmap.icon_star);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            starView.setImageDrawable(drawable);

            LayoutParams starParams = new LayoutParams(starSize, starSize);
            starParams.setMargins(0, 0, starMargins, 0);
            addView(starView, starParams);
        }
    }

    public void refreshConfig(int selectCount, boolean isMatter) {
        for (int i = 0; i < selectCount; i++) {
            if (i < selectCount) {
                ImageView starView = (ImageView) getChildAt(i);

                int color = ContextCompat.getColor(getContext(), R.color.star_color2);

                Drawable drawable = getResources().getDrawable(R.mipmap.icon_star);
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                starView.setImageDrawable(drawable);
            }
        }
    }
}
