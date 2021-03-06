package com.jyh.kxt.chat.presenter;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.LetterActivity;
import com.jyh.kxt.chat.adapter.LetterListAdapter;
import com.jyh.kxt.chat.json.LetterJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class LetterPresenter extends BasePresenter {

    @BindObject LetterActivity letterActivity;
    private VolleyRequest request;

    public LetterPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
    }

    public void init() {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());
        request.doGet(HttpConstant.MSG_USERCENTER, jsonParam, new HttpListener<LetterJson>() {
            @Override
            protected void onResponse(LetterJson letterJson) {
                letterActivity.init(letterJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                letterActivity.plRootView.loadError();
            }
        });
    }

    public void refresh() {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());
        request.doGet(HttpConstant.MSG_USERCENTER, jsonParam, new HttpListener<LetterJson>() {
            @Override
            protected void onResponse(LetterJson letterJson) {
                letterActivity.refresh(letterJson);
                letterActivity.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        letterActivity.plContent.onRefreshComplete();
                    }
                }, 200);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                letterActivity.plRootView.loadError();
                letterActivity.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        letterActivity.plContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    private static final int SCROLL_MIN_DISTANCE_X = 15;
    private static final int SCROLL_MIN_DISTANCE_Y = 30;


    private boolean isLeftScroll = false;
    private boolean isVerticalScroll = true;

    private boolean isSingleTapUp = false;

    public void scrollListener(final PullToRefreshListView refreshableView, final LetterListAdapter adapter) {

        final GestureDetector mGestureDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                isSingleTapUp = true;
                return false;
            }


            // Log.e("onScroll", "onScroll() called with:
            // e1 = [" + e1.getX() + "],
            // e2 = [" + e2.getX() + "],
            // distanceX = [" distanceX + "],
            // distanceY = [" distanceY + "]");
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                if (distanceX > SCROLL_MIN_DISTANCE_X || isLeftScroll) {//拦截掉ListView的滑动事件
                    if (isVerticalScroll) {
                        return false;
                    }

                    isLeftScroll = true;
                    int adapterViewPosition = refreshableView.getRefreshableView().pointToPosition((int) e1.getX(), (int) e1.getY());
                    adapter.translationContentView(adapterViewPosition, distanceX);

                    refreshableView.setMode(PullToRefreshBase.Mode.DISABLED);

                    return true;
                }
                isVerticalScroll = true;
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                return false;
            }
        });

        refreshableView.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    mGestureDetector.onTouchEvent(event);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isSingleTapUp = false;
                            adapter.downHindContentView();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            isLeftScroll = false;
                            isVerticalScroll = false;
                            refreshableView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            adapter.upContentView();
                            return !isSingleTapUp;
                    }
                    return isLeftScroll;
                } catch (Exception e) {
                    e.printStackTrace();
                    return isLeftScroll;
                }
            }
        });
    }

    private boolean isOnItemClick() {
        return true;
    }
}
