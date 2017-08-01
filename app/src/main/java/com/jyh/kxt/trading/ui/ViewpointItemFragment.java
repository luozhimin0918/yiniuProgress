//package com.jyh.kxt.trading.ui;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.AbsListView;
//
//import com.jyh.kxt.R;
//import com.jyh.kxt.base.BaseFragment;
//import com.jyh.kxt.index.ui.MainActivity;
//import com.jyh.kxt.index.ui.fragment.TradingFragment;
//import com.jyh.kxt.trading.adapter.ViewpointAdapter;
//import com.jyh.kxt.trading.json.ViewpointJson;
//import com.library.widget.handmark.PullToRefreshListView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//
///**
// * 项目名:Kxt
// * 类描述:
// * 创建人:苟蒙蒙
// * 创建日期:2017/7/26.
// */
//
//public class ViewpointItemFragment extends BaseFragment {
//    @BindView(R.id.lv_content) PullToRefreshListView lvContent;
//
//    private ViewpointAdapter adapter;
//    private int firstVisibleItem;
//
//    @Override
//    protected void onInitialize(Bundle savedInstanceState) {
//        setContentView(R.layout.fragment_trading_viewpoint_item);
//        List<ViewpointJson> viewpointJsons = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            ViewpointJson viewpointJson = new ViewpointJson();
//            viewpointJson.setAuthor("张三" + i);
//            viewpointJson.setAuthor_id(i + "");
//            viewpointJson.setAuthor_picture("");
//            viewpointJson.setContent
//                    ("哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈");
//            viewpointJson.setNum_commend("111");
//            viewpointJson.setNum_like("2222");
//            viewpointJson.setTime("2017 1 1");
//            List<String> list = new ArrayList<>();
//            for (int j = 0; j < 3 + i; j++) {
//                list.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1617890634,3208398299&fm=200&gp=0.jpg");
//            }
//            viewpointJson.setImgs(list);
//            viewpointJsons.add(viewpointJson);
//        }
//
//        lvContent.setAdapter(adapter = new ViewpointAdapter(getContext(), viewpointJsons));
//
//        setScrollListener();
//    }
//
//    private boolean isUp = false;
//
//    private void setScrollListener() {
//        TradingFragment tradingFragment = null;
//        ViewpointFragment viewpointFragment = null;
//        try {
//            List<Fragment> fragments = ((MainActivity) getContext()).getSupportFragmentManager().getFragments();
//            for (Fragment fragment : fragments) {
//                if (fragment instanceof TradingFragment) {
//                    tradingFragment = (TradingFragment) fragment;
//                    break;
//                }
//            }
//            if (tradingFragment != null) {
//                viewpointFragment = (ViewpointFragment) tradingFragment.getChildFragmentManager().findFragmentByTag("ViewpointFragment");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        final ViewpointFragment finalViewpointFragment = viewpointFragment;
//        lvContent.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.i("onScroll", "....");
//                if (finalViewpointFragment != null) {
//                    if (isUp) {
//                        if (firstVisibleItem == 1)
//                            finalViewpointFragment.onScroll(view.getScrollX(), 1);
//                    } else {
//
//                        finalViewpointFragment.onScroll(view.getScrollX(), -1);
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleIte, int visibleItemCount, int totalItemCount) {
//                firstVisibleItem = firstVisibleIte;
//            }
//        });
//        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                try {
//                    isUp = e2.getRawY() - e1.getRawY() > 0 ? true : false;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    isUp = false;
//                }
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                return false;
//            }
//        });
//        lvContent.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return gestureDetector.onTouchEvent(event);
//            }
//        });
//
//    }
//
//}
