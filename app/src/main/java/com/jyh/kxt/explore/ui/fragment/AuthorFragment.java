package com.jyh.kxt.explore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RollDotViewPager;
import com.jyh.kxt.base.custom.RollViewPager;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.adapter.AuthorHeadContentAdapter;
import com.jyh.kxt.explore.adapter.AuthorListAdapter;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.presenter.AuthorFragmentPresenter;
import com.jyh.kxt.explore.ui.AuthorActivity;
import com.jyh.kxt.index.ui.AttentionActivity;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:名家专栏-名家
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/9.
 */

public class AuthorFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase
        .OnRefreshListener2,
        AdapterView.OnItemClickListener {
    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private AuthorFragmentPresenter presenter;
    public AuthorListAdapter adapter;
    private RollDotViewPager rollDotViewPager;

    //头部相关
    private View headView;
    private View line;
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
        plvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
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
            if (headView != null) {
                plvContent.getRefreshableView().removeHeaderView(headView);
            }
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
        if (headAdapter != null) {
            headAdapter.notifyDataSetChanged();
        }
        if (line != null) {
            line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));
        }
        if (rollDotViewPager != null) {
            rollDotViewPager.onChangeTheme();
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

        rollDotViewPager = (RollDotViewPager) headView.findViewById(R.id.rdvp_content);
        rollDotViewPager.setViewPageToDotAbout();
        rollDotViewPager.addLineTop();
        RollViewPager rollViewPager = rollDotViewPager.getRollViewPager();
        rollViewPager.setGridMaxCount(6).setDataList(authors).setGridViewItemData(new RollViewPager.GridViewItemData() {
            @Override
            public void itemData(List dataSubList, GridView gridView) {
                headAdapter = new AuthorHeadContentAdapter(getContext(), dataSubList);
                gridView.setAdapter(headAdapter);
            }
        });
        rollDotViewPager.build();
    }

  /*  private void initPageView(List<AuthorJson> authors, LinearLayout llDian, int size, int pageCount,
  List<RecyclerView> views, ViewGroup
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
    }*/
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
