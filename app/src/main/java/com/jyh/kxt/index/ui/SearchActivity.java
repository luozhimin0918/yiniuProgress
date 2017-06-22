package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.widget.SearchEditText;
import com.jyh.kxt.index.adapter.SearchBrowerAdapter;
import com.jyh.kxt.index.presenter.SearchPresenter;
import com.jyh.kxt.index.ui.fragment.SearchArticleFragment;
import com.jyh.kxt.index.ui.fragment.SearchVideoFragment;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.VarConstant;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;
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

    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.fl_hot) TagFlowLayout flHot;
    @BindView(R.id.ll_hot) LinearLayout llHot;
    @BindView(R.id.ll_history) LinearLayout llHistory;
    @BindView(R.id.rv_content) RecyclerView rvContent;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;
    @BindView(R.id.layout_search_start) View rootSearchStart;
    @BindView(R.id.layout_search_end) View rootSearchEnd;

    private String[] tabs = new String[]{"文章", "视听"};
    private SearchPresenter searchPresenter;
    private TagAdapter<String> tagAdapter;
    private List<String> flows;

    private List<Fragment> fragmentList = new ArrayList<>();
    public SearchVideoFragment videoFragment;
    public SearchArticleFragment articleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_search, StatusBarColor.THEME1);

        searchPresenter = new SearchPresenter(this);

        String searchKey = getIntent().getStringExtra(IntentConstant.SEARCH_KEY);

        initView();

        if (RegexValidateUtil.isEmpty(searchKey)) {
            searchBefore();
        } else {
            rootSearchStart.setVisibility(View.GONE);
            rootSearchEnd.setVisibility(View.VISIBLE);
            searchPresenter.search(searchKey);
        }

    }

    /**
     * 未搜索前布局
     */
    private void searchBefore() {
        rootSearchStart.setVisibility(View.VISIBLE);
        rootSearchEnd.setVisibility(View.GONE);
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

        edtSearch.addTextChangedListener(edtSearch.new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (RegexValidateUtil.isEmpty(s.toString())) {
                    showHistory();
                    searchBefore();
                }
            }
        });

        flHot.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String searchKey = flows.get(position);
                edtSearch.setText(searchKey);
                searchPresenter.search(searchKey);
                return false;
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContent.setLayoutManager(manager);

        String type = getIntent().getStringExtra(IntentConstant.TYPE);
        if (type != null && VarConstant.VIDEO.equals(type)) {
            fragmentList.add(videoFragment = new SearchVideoFragment());
            fragmentList.add(articleFragment = new SearchArticleFragment());
            tabs = new String[]{"视听", "文章"};
        } else {
            fragmentList.add(articleFragment = new SearchArticleFragment());
            fragmentList.add(videoFragment = new SearchVideoFragment());
            tabs = new String[]{"文章", "视听"};
        }

        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        stlNavigationBar.setViewPager(vpContent, tabs);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(this, screenDisplay.widthPixels / 2));

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
    public void initBrowseHistory(final List<NewsJson> historyData) {
        if (historyData == null || historyData.size() == 0) {
            llHistory.setVisibility(View.GONE);
        } else {
            llHistory.setVisibility(View.VISIBLE);
            SearchBrowerAdapter adapter = new SearchBrowerAdapter(this, historyData);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view) {
                    NewsJson newsJson = historyData.get(position);
                    JumpUtils.jump(SearchActivity.this, newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(), newsJson
                            .getHref());
                }
            });
            rvContent.setAdapter(adapter);
        }
    }

    /**
     * 隐藏历史记录，显示搜索内容
     */
    public void hideHistory() {
        rootSearchStart.setVisibility(View.GONE);
        rootSearchEnd.setVisibility(View.VISIBLE);
    }

    /**
     * 显示历史记录
     */
    public void showHistory() {
        rootSearchStart.setVisibility(View.GONE);
        rootSearchEnd.setVisibility(View.VISIBLE);
    }

    /**
     * 添加历史信息
     *
     * @param searchKey
     */
    public void addHistory(String searchKey) {
        if (llHot.getVisibility() == View.GONE) {
            searchPresenter.initSearchHistory();
        } else {
            tagAdapter.addTagDatas(searchKey);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(searchPresenter.getClass().getName());
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (articleFragment != null)
            articleFragment.onChangeTheme();
        if (videoFragment != null)
            videoFragment.onChangeTheme();
    }
}
