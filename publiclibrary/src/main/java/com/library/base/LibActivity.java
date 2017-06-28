package com.library.base;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.library.R;
import com.library.base.http.RequestQueueUtil;
import com.library.manager.ActivityManager;
import com.library.util.StatusBarCompat;
import com.library.util.SystemUtil;

import butterknife.ButterKnife;

/**
 * Created by DaiYao on 2016/5/15 0015.
 */
public abstract class LibActivity extends AppCompatActivity implements LayoutInflaterFactory {

    protected final String TAG = this.getClass().getName();

    public enum StatusBarColor {
        NO_COLOR(-1), BLACK(R.color.black), THEME1(R.color.theme1);

        public int color;

        StatusBarColor(int color) {
            if (color == R.color.theme1) {
                if (Build.VERSION.SDK_INT < 23) {//6.0以下调用的拍照方法
                    this.color = R.color.statusColor;
                } else {
                    this.color = color;
                }
            } else {
                this.color = color;
            }
        }
    }

    protected StatusBarColor statusBarColor;
    protected RequestQueue mQueue;

    /**
     * onCreate init Layout and init Function Parameter
     * init Layout employ {@link #setBindingView(int)} DataBinding
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory(layoutInflater, this);
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        this.setContentView(layoutResID, StatusBarColor.BLACK);
    }

    /**
     * @param layoutResID
     * @param statusBarColor -1 则不填充标题栏
     */
    protected void setContentView(int layoutResID, StatusBarColor statusBarColor) {
        super.setContentView(layoutResID);

        mQueue = RequestQueueUtil.newRequestQueue(this);
        this.statusBarColor = statusBarColor;
        ButterKnife.bind(this);
        StatusBarCompat.compat(this, statusBarColor.color);
    }

    protected void setBindingView(int layoutResID) {
        setBindingView(layoutResID, StatusBarColor.BLACK);
    }

    protected void setBindingView(int layoutResID, StatusBarColor statusBarColor) {
        mQueue = RequestQueueUtil.newRequestQueue(this);
        this.statusBarColor = statusBarColor;
        DataBindingUtil.setContentView(this, layoutResID);
        StatusBarCompat.compat(this, statusBarColor.color);
        ButterKnife.bind(this);
    }

    RequestQueue.RequestFilter mRequestFilter = new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            Object mRequestTag = request.getTag();
            return mRequestTag == null;
        }
    };

    @Override
    protected void onDestroy() {
        if (mQueue != null) {
            mQueue.cancelAll(mRequestFilter);
        }
        ActivityManager.getInstance().popOneActivity(this);
        try {
            SystemUtil.closeSoftInputWindow(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
