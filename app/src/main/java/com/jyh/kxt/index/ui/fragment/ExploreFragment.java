package com.jyh.kxt.index.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.explore.adapter.ActivityAdapter;
import com.jyh.kxt.explore.adapter.AuthorAdapter;
import com.jyh.kxt.explore.adapter.TopicAdapter;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.jyh.kxt.index.presenter.ExplorePresenter;
import com.jyh.kxt.main.adapter.BtnAdapter;
import com.jyh.kxt.explore.json.ActivityJson;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.explore.json.TopicJson;
import com.library.base.LibActivity;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.viewpager.BannerLayout;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 首页-探索
 */
public class ExploreFragment extends BaseFragment implements PullToRefreshListView.OnRefreshListener2 {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.plv_content) PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;

    private ExplorePresenter explorePresenter;
    private LinearLayout homeHeadView;//头部布局
    private BannerLayout carouseView;
    private NewsAdapter newsAdapter;

    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_explore, LibActivity.StatusBarColor.THEME1);
        explorePresenter = new ExplorePresenter(this);
        ivBarBreak.setImageResource(R.mipmap.icon_user_def_photo);
        tvBarTitle.setText("探索");

        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnRefreshListener(this);

        explorePresenter.init();

    }

    @OnClick(R.id.iv_bar_break)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bar_break:
                break;
        }
    }

    /**
     * 初始化头部布局
     */
    public void initHeadView() {
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
     * @param slides
     */
    public void addSlide(List<SlideJson> slides) {
        Context mContext = getContext();
        int currentItem = 0;
        if (carouseView != null) {
            currentItem = carouseView.getViewPager().getCurrentItem();
        }

        int carouselHeight = (int) mContext.getResources().getDimension(R.dimen.index_slide);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                carouselHeight);
        carouseView = (BannerLayout) LayoutInflater.from(mContext).inflate(R.layout.news_header_slide, null);
        carouseView.setLayoutParams(params);

        List<String> carouseList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        int size = slides.size();
        for (int i = 0; i < size; i++) {
            SlideJson slideJson = slides.get(i);
            String carouselItem = slideJson.getPicture();
            carouseList.add(HttpConstant.IMG_URL + carouselItem);
            titles.add(slideJson.getTitile());
        }
        carouseView.setViewUrls(carouseList, titles, currentItem);
        homeHeadView.addView(carouseView);

        carouseView.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
    }

    /**
     * 添加按钮
     *
     * @param shortcuts
     */
    public void addShortcut(List<SlideJson> shortcuts) {
        Context mContext = getContext();
        RecyclerView recyclerView = new RecyclerView(mContext);
        int carouselHeight = (int) mContext.getResources().getDimension(R.dimen.index_btn_height);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                carouselHeight);
        recyclerView.setLayoutParams(params);

        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(new BtnAdapter(shortcuts, mContext));

        homeHeadView.addView(recyclerView);

        addLineView();
    }

    /**
     * 添加专题
     *
     * @param topics
     */
    public void addTopic(List<TopicJson> topics) {

        Context mContext = getContext();
        View topicView = LayoutInflater.from(mContext).inflate(R.layout.explore_header, null);
        TextView tv1 = (TextView) topicView.findViewById(R.id.tv_title1);
        TextView tv2 = (TextView) topicView.findViewById(R.id.tv_title2);
        TextView tvMore = (TextView) topicView.findViewById(R.id.tv_more);

        tv1.setText("专题");

        RecyclerView rvContent = (RecyclerView) topicView.findViewById(R.id.rv_content);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvContent.setLayoutManager(manager);

        rvContent.setAdapter(new TopicAdapter(mContext, topics));

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更多专题
                Intent topicIntent = new Intent(getContext(), MoreActivity.class);
                topicIntent.putExtra(IntentConstant.TYPE, VarConstant.EXPLORE_TOPIC);
                startActivity(topicIntent);
            }
        });

        homeHeadView.addView(topicView);

        addLineView();
    }

    /**
     * 添加活动
     *
     * @param activitys
     */
    public void addActivity(List<ActivityJson> activitys) {

        Context mContext = getContext();
        View activityView = LayoutInflater.from(mContext).inflate(R.layout.explore_header, null);
        TextView tv1 = (TextView) activityView.findViewById(R.id.tv_title1);
        TextView tv2 = (TextView) activityView.findViewById(R.id.tv_title2);
        TextView tvMore = (TextView) activityView.findViewById(R.id.tv_more);

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更多活动
                Intent activityIntent = new Intent(getContext(), MoreActivity.class);
                activityIntent.putExtra(IntentConstant.TYPE, VarConstant.EXPLORE_ACTIVITY);
                startActivity(activityIntent);
            }
        });

        tv1.setText("活动");

        RecyclerView rvContent = (RecyclerView) activityView.findViewById(R.id.rv_content);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvContent.setLayoutManager(manager);

        rvContent.setAdapter(new ActivityAdapter(mContext, activitys));

        homeHeadView.addView(activityView);

        addLineView();
    }

    /**
     * 添加作者
     *
     * @param authors
     */
    public void addAuthor(final List<AuthorJson> authors) {
        Context mContext = getContext();
        View authorView = LayoutInflater.from(mContext).inflate(R.layout.explore_header, null);
        TextView tv1 = (TextView) authorView.findViewById(R.id.tv_title1);
        TextView tv2 = (TextView) authorView.findViewById(R.id.tv_title2);
        TextView tvMore = (TextView) authorView.findViewById(R.id.tv_more);
        RecyclerView rvContent = (RecyclerView) authorView.findViewById(R.id.rv_content);

        tv1.setText("名家专栏");

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvContent.setLayoutManager(manager);

        AuthorAdapter adapter = new AuthorAdapter(mContext, authors);
        rvContent.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                String authorId = authors.get(position).getId();
                Intent authorIntent = new Intent(getContext(), AuthorActivity.class);
                authorIntent.putExtra(IntentConstant.ID, authorId);
                startActivity(authorIntent);
            }
        });

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更多名家
                Intent authorIntent = new Intent(getContext(), MoreActivity.class);
                authorIntent.putExtra(IntentConstant.TYPE, VarConstant.EXPLORE_BLOG_WRITER);
                startActivity(authorIntent);
            }
        });


        homeHeadView.addView(authorView);
    }

    /**
     * 添加文章
     *
     * @param articles
     */
    public void addArticle(List<NewsJson> articles) {
        if (homeHeadView != null)
            plvContent.getRefreshableView().removeHeaderView(homeHeadView);
        plvContent.getRefreshableView().addHeaderView(homeHeadView);
        newsAdapter = null;
        newsAdapter = new NewsAdapter(getContext(), articles);
        plvContent.setAdapter(newsAdapter);
        if (plvContent.isRefreshing()) {
            plvContent.onRefreshComplete();
        }
    }

    /**
     * 添加分割线
     */
    public void addLineView() {
        Context mContext = getContext();
        View view = new View(mContext);
        int lineHeight = (int) mContext.getResources().getDimension(R.dimen.line_height);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                lineHeight);
        view.setBackgroundResource(R.color.line_color2);

        view.setLayoutParams(params);

        homeHeadView.addView(view);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        //刷新
        explorePresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        //加载更多
        explorePresenter.loadMore();
    }

    /**
     * 加载更多
     *
     * @param newsJsons
     */
    public void loadMore(List<NewsJson> newsJsons) {
        if (newsJsons == null || newsJsons.size() == 0) {
            ToastView.makeText3(getContext(), getString(R.string.no_data));
        } else {
            newsAdapter.addData(newsJsons);
        }
        plvContent.onRefreshComplete();
    }
}
