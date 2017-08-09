package com.jyh.kxt.search.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.adapter.SearchTypeAdapter;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.search.json.SearchType;
import com.jyh.kxt.search.presenter.SearchIndexPresenter;
import com.jyh.kxt.base.widget.SearchEditText;
import com.library.base.http.VarConstant;
import com.library.util.RegexValidateUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.flowlayout.FlowLayout;
import com.library.widget.flowlayout.TagAdapter;
import com.library.widget.flowlayout.TagFlowLayout;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:搜索主页
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/7.
 */

public class SearchIndexActivity extends BaseActivity {
    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.tv_break) TextView tvBreak;
    @BindView(R.id.rv_type) RecyclerView rvType;
    @BindView(R.id.iv_del) ImageView ivDel;
    @BindView(R.id.fl_history) TagFlowLayout flHistory;
    @BindView(R.id.ll_history) LinearLayout llHistory;
    @BindView(R.id.rv_content) RecyclerView rvContent;
    @BindView(R.id.iv_icon) ImageView ivIcon;
    @BindView(R.id.rl_more) RelativeLayout rlMore;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;
    @BindView(R.id.layout_start) View startView;
    @BindView(R.id.layout_end) View endView;

    private SearchIndexPresenter presenter;
    private SearchTypeAdapter searchTypeAdapter;
    private List<String> searchHistory;
    private TagAdapter<String> tagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search, StatusBarColor.THEME1);
        presenter = new SearchIndexPresenter(this);
        initView();
        presenter.init();
    }

    private void initView() {
        final List<SearchType> searchTypes = new ArrayList<>();
        searchTypes.add(new SearchType("行情", VarConstant.SEARCH_TYPE_QUOTE));
        searchTypes.add(new SearchType("要闻", VarConstant.SEARCH_TYPE_NEWS));
        searchTypes.add(new SearchType("视听", VarConstant.SEARCH_TYPE_VIDEO));
        searchTypes.add(new SearchType("专栏", VarConstant.SEARCH_TYPE_COLUMNIST));
        searchTypes.add(new SearchType("观点", VarConstant.SEARCH_TYPE_VIEWPOINT));
        searchTypes.add(new SearchType("文章", VarConstant.SEARCH_TYPE_ARTICLE));
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        searchTypeAdapter = new SearchTypeAdapter(this, searchTypes);
        searchTypeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                String text = edtSearch.getText();
                if (RegexValidateUtil.isEmpty(text)) {
                    ToastView.makeText3(getContext(), "搜索内容不能为空");
                } else {
                    Intent intent = new Intent(getContext(), SearchMainActivity.class);
                    intent.putExtra(SearchMainActivity.SEARCH_KEY, text);
                    intent.putExtra(SearchMainActivity.SEARCH_TYPE, searchTypes.get(position).getCode());
                    startActivity(intent);
                }
            }
        });
        rvType.setLayoutManager(manager);
        rvType.setAdapter(searchTypeAdapter);

        flHistory.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String searchKey = searchHistory.get(position);
                edtSearch.setText(searchKey);
                presenter.search(searchKey);
                return false;
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //监听软键盘搜索按钮
                    String key = edtSearch.getText();
                    presenter.search(key);
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.tv_break, R.id.iv_del, R.id.rl_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_break:
                onBackPressed();
                break;
            case R.id.iv_del:
                presenter.delSearchHistory();
                break;
            case R.id.rl_more:
                break;
        }
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (searchTypeAdapter != null)
            searchTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 显示搜索记录
     *
     * @param historys
     */
    public void showHistorySearch(List<String> historys) {
        llHistory.setVisibility(View.VISIBLE);
        if (historys == null || historys.size() == 0) {
            hideHistory();
        } else {
            searchHistory = historys;
            if (tagAdapter == null) {
                tagAdapter = new TagAdapter<String>(searchHistory) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_tv,
                                flHistory, false);
                        tv.setText(s);
                        return tv;
                    }
                };

                flHistory.setAdapter(tagAdapter);
            } else {
                tagAdapter.setTagDatas(searchHistory);
            }
        }
    }

    /**
     * 隐藏搜索记录
     */
    private void hideHistory() {
        llHistory.setVisibility(View.GONE);
    }

    /**
     * 刷新搜索记录
     *
     * @param list
     */
    public void refreshSearchHistory(List<String> list) {
        if (list == null || list.size() == 0) {
            hideHistory();
        } else {
            showHistorySearch(list);
        }
    }
}
