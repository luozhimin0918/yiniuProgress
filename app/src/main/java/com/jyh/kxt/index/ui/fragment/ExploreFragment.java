package com.jyh.kxt.index.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.adapter.ActivityAdapter;
import com.jyh.kxt.explore.adapter.AuthorAdapter;
import com.jyh.kxt.explore.adapter.TopicAdapter;
import com.jyh.kxt.explore.json.ActivityJson;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.json.TopicJson;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.jyh.kxt.explore.ui.AuthorListActivity;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.jyh.kxt.index.presenter.ExplorePresenter;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.adapter.BtnAdapter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.LibActivity;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.viewpager.BannerLayout;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-探索
 */
public class ExploreFragment extends BaseFragment implements PullToRefreshListView.OnRefreshListener2, AdapterView.OnItemClickListener {

    @BindView(R.id.iv_bar_break) RoundImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.plv_content) PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;

    private ExplorePresenter explorePresenter;
    private LinearLayout homeHeadView;//头部布局
    private BannerLayout carouseView;
    private NewsAdapter newsAdapter;
    private AuthorAdapter authorAdapter;
    private BtnAdapter btnAdapter;
    private TopicAdapter topicAdapter;
    private ActivityAdapter activitysAdapter;

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

        changeUserImg(LoginUtils.getUserInfo(getContext()));

        plvContent.setOnItemClickListener(this);

        plRootView.loadWait();
        explorePresenter.init();

    }

    @OnClick(R.id.iv_bar_break)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bar_break:
                ((MainActivity) getActivity()).showUserCenter();
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
    public void addSlide(final List<SlideJson> slides) {
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
                SlideJson slideJson = slides.get(position);
                JumpUtils.jumpDetails(getActivity(), slideJson.getO_class(), slideJson.getO_id(), slideJson.getHref());
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

        GridLayoutManager manager = new GridLayoutManager(mContext, 4) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        manager.setOrientation(GridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(btnAdapter = new BtnAdapter(shortcuts, mContext));

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

        LinearLayoutManager manager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvContent.setLayoutManager(manager);

        rvContent.setAdapter(topicAdapter = new TopicAdapter(mContext, topics));

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

        LinearLayoutManager manager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvContent.setLayoutManager(manager);

        rvContent.setAdapter(activitysAdapter = new ActivityAdapter(mContext, activitys));

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

        authorAdapter = new AuthorAdapter(mContext, authors);
        rvContent.setAdapter(authorAdapter);

        authorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                String authorId = authors.get(position).getId();
                Intent authorIntent = new Intent(getContext(), AuthorActivity.class);
                authorIntent.putExtra(IntentConstant.O_ID, authorId);
                startActivity(authorIntent);
            }
        });

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更多名家
                Intent authorIntent = new Intent(getContext(), AuthorListActivity.class);
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
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(getContext(), articles);
            plvContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(articles);
        }
        newsAdapter.setIsSplice(false);
        if (plvContent.isRefreshing()) {
            plvContent.onRefreshComplete();
        }
        plRootView.loadOver();
    }

    /**
     * 添加分割线
     */
    public void addLineView() {
        Context mContext = getContext();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_line, null);
        int lineHeight = (int) mContext.getResources().getDimension(R.dimen.line_height);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                lineHeight);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));

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

    /**
     * 无更多数据
     */
    public void noMoreData() {
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
                ToastView.makeText3(getContext(), getContext().getString(R.string.no_data));
            }
        }, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = position - 2;
        NewsJson newsJson = newsAdapter.dataList.get(index);
        JumpUtils.jumpDetails(getActivity(), newsJson.getO_class(), newsJson.getO_id(), newsJson.getHref());
        //保存浏览记录
        BrowerHistoryUtils.save(getContext(), newsJson);

        //单条刷新,改变浏览状态
        newsAdapter.getView(index, view, parent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(explorePresenter.getClass().getName());
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeUserImg(UserJson user) {
        if (user == null) {
            ivBarBreak.setImageResource(R.mipmap.icon_user_def_photo);
        } else {
            Glide.with(getContext()).load(user.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap
                    .icon_user_def_photo).into(ivBarBreak);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_LOGIN:
                UserJson userJson = (UserJson) eventBus.intentObj;
                changeUserImg(userJson);
                break;
            case EventBusClass.EVENT_LOGOUT:
                changeUserImg(null);
                break;
            case EventBusClass.EVENT_CHANGEUSERINFO:
                changeUserImg((UserJson) eventBus.intentObj);
                break;
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();

        if (plvContent != null)
            plvContent.setDividerNull();

        if (newsAdapter != null) newsAdapter.notifyDataSetChanged();
        if (authorAdapter != null) authorAdapter.notifyDataSetChanged();
        if (topicAdapter != null) topicAdapter.notifyDataSetChanged();
        if (activitysAdapter != null) activitysAdapter.notifyDataSetChanged();
    }
}
