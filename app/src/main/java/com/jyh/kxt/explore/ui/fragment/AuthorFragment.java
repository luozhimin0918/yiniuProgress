package com.jyh.kxt.explore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.adapter.AuthorHeadContentAdapter;
import com.jyh.kxt.explore.adapter.AuthorHeadViewAdapter;
import com.jyh.kxt.explore.adapter.AuthorListAdapter;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.presenter.AuthorFragmentPresenter;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.jyh.kxt.index.ui.AttentionActivity;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.recycler.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:名家专栏-名家
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/9.
 */

public class AuthorFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2,
        AdapterView.OnItemClickListener {
    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private AuthorFragmentPresenter presenter;
    public AuthorListAdapter adapter;

    //头部相关
    private View headView;
    private View line;
    private ArrayList<RecyclerView> views;
    private ArrayList<ImageView> points;
    private int position;
    private AuthorHeadContentAdapter headAdapter;
    private TextView tvTitle;
    private TextView tvMyAttention;
    private ImageView ivAttention;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_explore_author);
        presenter = new AuthorFragmentPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.BOTH);
        plvContent.setOnRefreshListener(this);
        plvContent.setOnItemClickListener(this);

        presenter.init();
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init();
    }

    public void init(List<AuthorJson> authorList) {
        if (authorList == null || authorList.size() == 0) {
            plRootView.loadEmptyData();
        } else {
            if (adapter == null) {
                adapter = new AuthorListAdapter(authorList, getContext());
                plvContent.setAdapter(adapter);
            } else {
                adapter.setData(authorList);
            }
            if (headView != null)
                plvContent.getRefreshableView().removeHeaderView(headView);
            plvContent.getRefreshableView().addHeaderView(headView);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }

    public void refresh(List<AuthorJson> authorList) {
        if (authorList != null && authorList.size() != 0) {
            adapter.setData(authorList);
        }
    }

    public void loadMore(List<AuthorJson> authorList) {
        if (authorList != null && authorList.size() != 0) {
            adapter.addData(authorList);
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (plvContent != null) plvContent.setDividerNull();
        if (adapter != null) adapter.notifyDataSetChanged();
        changeHeadTheme();
    }

    /**
     * 更改头部布局模式
     */
    private void changeHeadTheme() {
        if (headAdapter != null)
            headAdapter.notifyDataSetChanged();
        if (line != null)
            line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));
        if (views != null)
            for (RecyclerView view : views) {
                DividerGridItemDecoration decor = new DividerGridItemDecoration(getContext());
                decor.setSpanCount(3);
                view.addItemDecoration(decor);
            }
        if (points != null) {
            int size = points.size();
            for (int i = 0; i < size; i++) {
                ImageView imageView = points.get(i);
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.sel_dian));
                if (i == position)
                    imageView.setSelected(true);
                else
                    imageView.setSelected(false);
            }
        }

    }

    /**
     * 添加头部布局
     *
     * @param authors
     */
    public void addHeadView(List<AuthorJson> authors) {
        if (headView == null) {
            headView = LayoutInflater.from(getContext()).inflate(R.layout.layout_explore_author_head, null);
            line = headView.findViewById(R.id.v_line);
            headView.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);
            tvTitle = (TextView) headView.findViewById(R.id.tv_title);
            tvMyAttention = (TextView) headView.findViewById(R.id.tv_myattention);
            tvMyAttention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //我的关注
                    if (LoginUtils.isLogined(getContext())) {
                        startActivity(new Intent(getContext(), AttentionActivity.class));
                    } else {
                        presenter.showLoginDialog();
                    }
                }
            });
            ivAttention = (ImageView) headView.findViewById(R.id.iv_myattention);
        }
        ViewPager vpContent = (ViewPager) headView.findViewById(R.id.vp_content);
        final LinearLayout llDian = (LinearLayout) headView.findViewById(R.id.ll_dian);
        llDian.removeAllViews();
        int size = authors.size();
        int pageCount = size / 6;
        int pageNum = size % 6;
        if (pageNum != 0)
            pageCount++;
        views = new ArrayList<>();
        points = new ArrayList<>();
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);

        for (int i = 0; i < pageCount; i++) {
            initPageView(authors, llDian, size, pageCount, views, params, i);
        }
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AuthorFragment.this.position = position;
                int childCount = llDian.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = llDian.getChildAt(i);
                    if (i == position) {
                        childAt.setSelected(true);
                    } else {
                        childAt.setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpContent.setAdapter(new AuthorHeadViewAdapter(getContext(), views));
        int childCount = llDian.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = llDian.getChildAt(i);
            if (i == 0) {
                childAt.setSelected(true);
            } else {
                childAt.setSelected(false);
            }
        }
    }

    /**
     * 初始化头部每页布局
     *
     * @param authors
     * @param llDian
     * @param size
     * @param pageCount
     * @param views
     * @param params
     * @param i         页码
     */
    private void initPageView(List<AuthorJson> authors, LinearLayout llDian, int size, int pageCount, List<RecyclerView> views, ViewGroup
            .LayoutParams params, int i) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3) {
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
        recyclerView.setLayoutParams(params);

        List<AuthorJson> authorJsons = null;
        if (i == pageCount - 1) {
            //最后一页
            authorJsons = authors.subList(i * 6, size);
        } else {
            authorJsons = authors.subList(i * 6, (i + 1) * 6);
        }

        ImageView dian = new ImageView(getContext());
        dian.setImageResource(R.drawable.sel_dian);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8, 8);
        layoutParams.rightMargin = 10;
        dian.setLayoutParams(layoutParams);

        llDian.addView(dian);
        points.add(dian);

        DividerGridItemDecoration decor = new DividerGridItemDecoration(getContext());
        decor.setSpanCount(3);
        recyclerView.addItemDecoration(decor);

        headAdapter = new AuthorHeadContentAdapter(getContext(), authorJsons);
        headAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                String authorId = headAdapter.getData().get(position).getId();
                Intent authorIntent = new Intent(getContext(), AuthorActivity.class);
                authorIntent.putExtra(IntentConstant.O_ID, authorId);
                startActivity(authorIntent);
            }
        });
        recyclerView.setAdapter(headAdapter);
        views.add(recyclerView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 1) {
            String authorId = adapter.getData().get(position - 2).getId();
            Intent authorIntent = new Intent(getContext(), AuthorActivity.class);
            authorIntent.putExtra(IntentConstant.O_ID, authorId);
            startActivity(authorIntent);
        }
    }
}
