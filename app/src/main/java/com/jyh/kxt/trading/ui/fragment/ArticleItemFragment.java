package com.jyh.kxt.trading.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.adapter.NewsAdapter;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.trading.presenter.ArticleItemPresenter;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.viewpager.BannerLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:名家专栏-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/12.
 */

public class ArticleItemFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2,
        PageLoadLayout.OnAfreshLoadListener {
    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private ArticleItemPresenter presenter;

    private boolean isMain = false;

    private String name;
    private String id;
    private String type;
    private NewsAdapter newsAdapter;
    private View loginLayout;
    private LinearLayout homeHeadView;
    private BannerLayout carouseView;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);
    }

    @Override
    public void userVisibleHint() {
        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);
        id = arguments.getString(IntentConstant.CODE);
        type = arguments.getString(IntentConstant.TYPE);
        presenter = new ArticleItemPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnItemClickListener(this);
        plvContent.setOnRefreshListener(this);

        if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
            boolean isLogined = LoginUtils.isLogined(getContext());
            if (isLogined) {
                hideLoginBar();
                plRootView.loadWait();
                presenter.init(id, isMain, type);
            } else {
                plRootView.loadEmptyData();
                showLoginBar();
            }
        } else {
            hideLoginBar();
            plRootView.loadWait();
            presenter.init(id, isMain, type);
        }
    }

    public void init(List<AuthorNewsJson> list, String slide) {


        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(getContext(), list);
            plvContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(list);
        }

        initHeadViewLayout();

        try {
            if (slide != null && !slide.trim().equals("")) {
                List<SlideJson> slideJsons = JSON.parseArray(slide, SlideJson.class);
                addCarouselView(slideJsons);
                if (homeHeadView != null) {
                    plvContent.getRefreshableView().removeHeaderView(homeHeadView);
                    plvContent.getRefreshableView().addHeaderView(homeHeadView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void refresh(List<AuthorNewsJson> list) {
        if (list != null && list.size() != 0) {
            newsAdapter.setData(list);
        }
    }

    public void loadMore(List<AuthorNewsJson> list) {
        if (list != null && list.size() != 0) {
            newsAdapter.addData(list);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 1) return;
        AuthorNewsJson authorNewsJson = newsAdapter.getData().get(position - 1);
        JumpUtils.jump((BaseActivity) getActivity(), authorNewsJson.getO_class(), authorNewsJson.getO_action(), authorNewsJson.getO_id(),
                authorNewsJson
                        .getHref());
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
            boolean isLogined = LoginUtils.isLogined(getContext());
            if (isLogined) {
                presenter.init(id, isMain, type);
                hideLoginBar();
            } else {
                plRootView.loadEmptyData();
                showLoginBar();
            }
        } else {
            presenter.init(id, isMain, type);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(id + type);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (newsAdapter != null)
            newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void setInitialSavedState(SavedState state) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    private void showLoginBar() {
        loginLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_loginbar, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SystemUtil.dp2px(getContext()
                , 50));
        loginLayout.findViewById(R.id.tv_synchronization).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), LoginOrRegisterActivity.class), 100);
            }
        });
        params.gravity = Gravity.BOTTOM;
        loginLayout.setLayoutParams(params);
        plRootView.addView(loginLayout);
    }

    private void hideLoginBar() {
        if (loginLayout != null)
            plRootView.removeView(loginLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            plRootView.loadWait();
            if (VarConstant.EXPLORE_ARTICLE_LIST_TYPE_FOLLOW.equals(type)) {
                boolean isLogined = LoginUtils.isLogined(getContext());
                if (isLogined) {
                    presenter.init(id, isMain, type);
                    hideLoginBar();
                } else {
                    plRootView.loadEmptyData();
                    showLoginBar();
                }
            } else {
                presenter.init(id, isMain, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    /**
     * 初始化头部布局
     */
    public void initHeadViewLayout() {
        if (homeHeadView == null) {
            AbsListView.LayoutParams headLayoutParams = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT);

            homeHeadView = new LinearLayout(getContext());
            homeHeadView.setOrientation(LinearLayout.VERTICAL);
            homeHeadView.setLayoutParams(headLayoutParams);
        } else {
            homeHeadView.removeAllViews();
        }

    }

    /**
     * 添加轮播图
     *
     * @param carouselList
     */
    public void addCarouselView(final List<SlideJson> carouselList) {

        if (carouselList == null) return;

        int currentItem = 0;
        if (carouseView != null) {
            currentItem = carouseView.getViewPager().getCurrentItem();
        }

        final int carouselHeight = (int) getContext().getResources().getDimension(R.dimen.index_slide);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                carouselHeight);
        carouseView = (BannerLayout) LayoutInflater.from(getContext()).inflate(R.layout.news_header_slide, null);
        carouseView.setLayoutParams(params);

        final List<String> carouseList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < carouselList.size(); i++) {
            SlideJson slideJson = carouselList.get(i);
            String carouselItem = slideJson.getPicture();
            carouseList.add(HttpConstant.IMG_URL + carouselItem);
            titles.add(slideJson.getTitle());
        }
        carouseView.setViewUrls(carouseList, titles, currentItem);
        homeHeadView.addView(carouseView);

        carouseView.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SlideJson slideJson = carouselList.get(position);
                JumpUtils.jump((BaseActivity) getContext(), slideJson.getO_class(), slideJson.getO_action(), slideJson
                        .getO_id(), slideJson
                        .getHref());
            }
        });
    }
}
