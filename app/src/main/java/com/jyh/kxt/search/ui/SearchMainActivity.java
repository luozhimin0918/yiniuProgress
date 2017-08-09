package com.jyh.kxt.search.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.widget.SearchEditText;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.search.json.SearchType;
import com.jyh.kxt.search.presenter.SearchMainPresenter;
import com.jyh.kxt.search.ui.fragment.SearchItemFragment;
import com.library.base.http.VarConstant;
import com.library.util.RegexValidateUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:搜索结果页
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class SearchMainActivity extends BaseActivity {

    public static final String SEARCH_KEY = "search_key";
    public static final String SEARCH_TYPE = "search_type";

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.tv_break) TextView tvBreak;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_search) ViewPager vpSearch;

    private SearchMainPresenter presenter;
    private String[] tabs;
    private List<Fragment> fragments = new ArrayList<>();
    private String searchKey;
    private String searchType;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search, StatusBarColor.THEME1);

        searchKey = getIntent().getStringExtra(SEARCH_KEY);
        searchType = getIntent().getStringExtra(SEARCH_TYPE);
        if (RegexValidateUtil.isEmpty(searchType)) {
            searchType = VarConstant.SEARCH_TYPE_MAIN;
        }

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

        presenter = new SearchMainPresenter(this);

        plRootView.loadWait();
        presenter.initNavBar();
    }

    @OnClick({R.id.tv_break, R.id.iv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_break:
                onBackPressed();
                break;
            case R.id.iv_more:
                Intent intent = new Intent(this, ClassifyActivity.class);
                intent.putExtra(IntentConstant.ACTIONNAV, tabs);
                intent.putExtra(IntentConstant.INDEX, index);
                startActivityForResult(intent, IntentConstant.REQUESTCODE1);
                break;
        }
    }

    public void initNavBar(List<SearchType> o) {
        if (o == null || o.size() == 0) {
            plRootView.loadEmptyData();
        } else {
            int size = o.size();
            tabs = new String[size];
            for (int i = 0; i < size; i++) {
                SearchType searchType = o.get(i);
                tabs[i] = searchType.getName();
                Fragment fragment = new SearchItemFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SearchItemFragment.SEARCH_KEY, searchKey);
                bundle.putString(SearchItemFragment.SEARCH_TYPE, searchType.getCode());
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
            vpSearch.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragments));
            vpSearch.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    index=position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            stlNavigationBar.setViewPager(vpSearch, tabs);

            for (int i = 0; i < size; i++) {
                if (o.get(i).getCode().equals(searchType)) {
                    stlNavigationBar.setCurrentTab(i);
                }
            }
        }
        plRootView.loadOver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.REQUESTCODE1 && resultCode == Activity.RESULT_OK) {
            int index = data.getIntExtra(IntentConstant.INDEX, 0);
            stlNavigationBar.setCurrentTab(index);
        }
    }
}
