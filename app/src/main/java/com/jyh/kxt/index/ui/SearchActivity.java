package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.index.presenter.SearchPresenter;
import com.library.widget.flowlayout.FlowLayout;
import com.library.widget.flowlayout.TagAdapter;
import com.library.widget.flowlayout.TagFlowLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:搜索界面
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/4.
 */

public class SearchActivity extends BaseActivity {

    @BindView(R.id.edt_search) EditText edtSearch;
    @BindView(R.id.fl_hot) TagFlowLayout flHot;
    @BindView(R.id.ll_hot) LinearLayout llHot;
    @BindView(R.id.ll_history) LinearLayout llHistory;
    @BindView(R.id.rv_content) RecyclerView rvContent;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    private final String[] tabs = new String[]{"文章", "视听"};
    private SearchPresenter searchPresenter;
    private TagAdapter<String> tagAdapter;
    private List<String> flows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_search, StatusBarColor.THEME1);

        searchPresenter = new SearchPresenter(this);

        initView();

        //初始化搜索历史
        searchPresenter.initSearchHistory();
        //初始化浏览历史
        searchPresenter.initBrowseHistory();
    }

    private void initView() {
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchPresenter.search(edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        flHot.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                searchPresenter.search(flows.get(position));
                return false;
            }
        });
    }

    @OnClick({R.id.tv_break, R.id.tv_history_more, R.id.iv_clear_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_break:
                onBackPressed();
                break;
            case R.id.tv_history_more:
                //查看更多浏览历史
                searchPresenter.moreBrowseHistory();
                break;
            case R.id.iv_clear_history:
                //删除搜索历史
                searchPresenter.clearSearchHistory();
                break;
        }
    }

    /**
     * 初始化搜索历史
     *
     * @param searchKeys
     */
    public void initSearchHistory(String[] searchKeys) {

        if (searchKeys == null || searchKeys.length == 0) {
            llHot.setVisibility(View.GONE);
        } else {
            llHot.setVisibility(View.VISIBLE);
            flows = new ArrayList<>();
            int size = searchKeys.length;
            for (int i = 0; i < size; i++) {
                flows.add(searchKeys[i]);
            }

            if (tagAdapter == null) {
                tagAdapter = new TagAdapter<String>(flows) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_tv,
                                flHot, false);
                        tv.setText(s);

                        return tv;
                    }
                };

                flHot.setAdapter(tagAdapter);
            } else {
                tagAdapter.setTagDatas(flows);
            }
        }
    }

    /**
     * 初始化浏览历史
     *
     * @param historyData
     */
    public void initBrowseHistory(List historyData) {
        if (historyData == null || historyData.size() == 0) {
            llHistory.setVisibility(View.GONE);
        } else {
            llHistory.setVisibility(View.VISIBLE);
        }
    }

    @OnClick()
    public void onClick() {
    }
}
