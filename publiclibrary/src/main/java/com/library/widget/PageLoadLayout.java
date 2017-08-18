package com.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.library.R;
import com.library.util.NetUtils;

/**
 * Created by DaiYao on 2016/5/21 0021.
 */
public class PageLoadLayout extends FrameLayout implements View.OnClickListener {
    private View llLoadView;
    private Context context;
    private boolean isSuccessLoadOver = false;
    private OnAfreshLoadListener onAfreshLoadListener;
    private LayoutInflater mInflater;
    private int nullImgId = 0;
    private String nullText = null;
    private int nullTextColor;

    public PageLoadLayout(Context context) {
        this(context, null);
    }

    public PageLoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setOnAfreshLoadListener(OnAfreshLoadListener onAfreshLoadListener) {
        this.onAfreshLoadListener = onAfreshLoadListener;
    }

    public void loadWait() {
        loadWait(BgColor.WHITE, null);
    }

    public void loadWait(BgColor bgColor, String desc) {
        removeLoading();
        llLoadView = mInflater.inflate(R.layout.volley_loading, this, false);

        ImageView ivProgress = (ImageView) llLoadView.findViewById(R.id.iv_progress);
        Glide.with(getContext()).load(R.mipmap.loading).asGif().into(ivProgress);

        TextView tvDesc = (TextView) llLoadView.findViewById(R.id.tv_desc);
        if (desc == null) {
            tvDesc.setVisibility(View.GONE);
        } else {
            tvDesc.setText(desc);
        }
        llLoadView.setBackgroundColor(ContextCompat.getColor(context, R.color.theme1));
        updateView();
    }


    public void loadError() {
        removeLoading();
        llLoadView = mInflater.inflate(R.layout.volley_load_error, null);

        ImageView errorImg = (ImageView) llLoadView.findViewById(R.id.iv_error);
        TextView errorTv = (TextView) llLoadView.findViewById(R.id.tv_error);

        if (NetUtils.isNetworkAvailable(context)) {
            errorImg.setImageResource(R.mipmap.icon_error_load);
            errorTv.setText("加载出错！");
        } else {
            errorImg.setImageResource(R.mipmap.icon_error_net);
            errorTv.setText("网络无法连接");
        }

        TextView errorButton = (TextView) llLoadView.findViewById(R.id.id_volley_load_error);
        errorButton.setTag("errorButton");
        errorButton.setOnClickListener(this);

        updateView();
    }

    public View getLoadViewLayout() {
        return llLoadView;
    }

    public void loadEmptyData() {
        removeLoading();
        llLoadView = mInflater.inflate(R.layout.volley_load_nodata, null);

        ImageView imageView = (ImageView) llLoadView.findViewById(R.id.id_volley_load_nodata_img);
        TextView textView = (TextView) llLoadView.findViewById(R.id.id_volley_load_nodata);

        if (nullImgId != 0) {
            imageView.setImageResource(nullImgId);
        }
        if (nullText != null) {
            textView.setText(nullText);
        }
        if(nullTextColor!=-1 && nullTextColor!=0){
            textView.setTextColor(ContextCompat.getColor(getContext(),nullTextColor));
        }

        llLoadView.setOnClickListener(this);
        updateView();
    }

    public void loadOver() {
        removeLoading();
        setSuccessLoadOver(true);
    }

    private void removeLoading() {
        if (llLoadView != null) {
            removeView(llLoadView);
        }
    }

    private void updateView() {
        llLoadView.setOnClickListener(null);
        addView(llLoadView);
    }

    public void addCustomView(int viewRes) {
        llLoadView = mInflater.inflate(viewRes, null);
        addCustomView(llLoadView);
    }

    public void addCustomView(View view) {
        removeLoading();
        llLoadView = view;
        updateView();
    }

    @Override
    public void onClick(View v) {
        if (onAfreshLoadListener != null) {
            onAfreshLoadListener.OnAfreshLoad();
        }
    }

    public int getNullImgId() {
        return nullImgId;
    }

    public void setNullImgId(int nullImgId) {
        this.nullImgId = nullImgId;
    }

    public String getNullText() {
        return nullText;
    }

    public void setNullText(String nullText) {
        this.nullText = nullText;
    }

    public boolean isSuccessLoadOver() {
        return isSuccessLoadOver;
    }

    public void setSuccessLoadOver(boolean successLoadOver) {
        isSuccessLoadOver = successLoadOver;
    }

    public void setNullTextColor(int nullTextColor) {
        this.nullTextColor = nullTextColor;
    }

    public enum BgColor {
        WHITE(Color.WHITE), TRANSPARENT8(Color.parseColor("#88000000")), TRANSPARENT(Color.TRANSPARENT);

        int bgColor;

        BgColor(int bgColor) {
            this.bgColor = bgColor;
        }
    }

    public interface OnAfreshLoadListener {
        void OnAfreshLoad();
    }
}
