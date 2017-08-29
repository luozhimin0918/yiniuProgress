package com.jyh.kxt.index.presenter;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.index.adapter.LetterListAdapter;
import com.jyh.kxt.index.json.LetterListJson;
import com.jyh.kxt.index.ui.LetterActivity;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

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
        request.setTag(getClass().getName());
    }

    public void init() {
        List<LetterListJson> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new LetterListJson("" + i, false));
        }
        letterActivity.init(list);
    }

    public void refresh() {

    }

    public void loadMore() {

    }

    float dx, dy;

    float downX, downY;
    int maxDx = SystemUtil.dp2px(mContext, 60);
    int position;
    boolean isShowDelBtn;
    float dX, dY;

    public void scrollListener(final ListView refreshableView, final LetterListAdapter adapter) {

        final GestureDetector mGestureDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
            public boolean isShowDelBtn;
            public int position;

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (position - 1 == 0) return false;
                if (adapter != null) {
                    if (isShowDelBtn) {
                        dx = (dx > maxDx / 2) ? maxDx : 0;
                        adapter.hideDel(position, dx, 1);
                    } else {
                        dx = (dx < -maxDx / 2) ? -maxDx : 0;
                        adapter.showDel(position, dx, 1);
                    }
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                dX = distanceX;
                dY = distanceY;
                try {
                    if (position - 1 == 0) return false;
                    if (adapter != null) {
                        if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                            if (isShowDelBtn) {
                                adapter.hideDel(position, dx, 0);
                            } else {
                                adapter.showDel(position, dx, 0);
                            }
                            return true;
                        } else {
                            if (adapter.delPosition != -1 && adapter.delPosition != 0) {
                                adapter.dataList.get(adapter.delPosition).setShowDelBtn(false);
                                int delIndex = adapter.delPosition + 1 - refreshableView.getFirstVisiblePosition();
                                adapter.dataList.get(adapter.delPosition).setShowDelBtn(false);
                                if (delIndex >= 0) {
                                    ViewGroup delBtnItem = (ViewGroup) ((ViewGroup) refreshableView.getChildAt(delIndex))
                                            .getChildAt(0);
                                    adapter.hideDel(delBtnItem);
                                }
                            }
                            return false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        refreshableView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            downY = event.getY();
                            position = refreshableView.pointToPosition((int) downX, (int) downY);
                            isShowDelBtn = adapter.dataList.get(position - 1).isShowDelBtn();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float x = event.getX();
                            float y = event.getY();
                            dx = x - downX;
                            dy = y - downY;
//                            if (position - 1 == 0) return false;
//                            if (adapter != null) {
//                                if (dy < 50 && dy > -50) {
//                                    if (isShowDelBtn) {
//                                        adapter.hideDel(position, dx, 0);
//                                    } else {
//                                        adapter.showDel(position, dx, 0);
//                                    }
//                                } else {
//                                    if (adapter.delPosition != -1) {
//                                        adapter.dataList.get(adapter.delPosition).setShowDelBtn(false);
//                                        int delIndex = adapter.delPosition + 1 - refreshableView.getFirstVisiblePosition();
//                                        adapter.dataList.get(adapter.delPosition).setShowDelBtn(false);
//                                        if (delIndex >= 0) {
//                                            ViewGroup delBtnItem = (ViewGroup) ((ViewGroup) refreshableView.getChildAt(delIndex))
//                                                    .getChildAt(0);
//                                            adapter.hideDel(delBtnItem);
//                                        }
//                                    }
//                                }
//                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (position - 1 == 0) return false;
                            if (Math.abs(dX) > Math.abs(dY)) {
                                if (adapter != null) {
                                    if (isShowDelBtn) {
                                        dx = (dx > maxDx / 2) ? maxDx : 0;
                                        adapter.hideDel(position, dx, 1);
                                    } else {
                                        dx = (dx < -maxDx / 2) ? -maxDx : 0;
                                        adapter.showDel(position, dx, 1);
                                    }
                                }
                            }
                            break;
                    }
                    return mGestureDetector.onTouchEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                    return mGestureDetector.onTouchEvent(event);
                }
            }
        });
    }
}
