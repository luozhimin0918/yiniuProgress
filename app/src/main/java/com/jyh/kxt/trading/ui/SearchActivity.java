package com.jyh.kxt.trading.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.widget.SearchEditText;
import com.jyh.kxt.search.adapter.AutoCompleteAdapter;
import com.jyh.kxt.search.adapter.QuoteAdapter;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.search.util.AutoCompleteUtils;
import com.jyh.kxt.trading.presenter.SearchPresenter;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:发布观点 搜索
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/21.
 */

public class SearchActivity extends BaseActivity implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2,
        PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.iv_break) ImageView ivBreak;
    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.tv_break) TextView tvBreak;

    @BindView(R.id.sv_before) ScrollView svBefore;
    @BindView(R.id.ll_history) LinearLayout llHistory;
    @BindView(R.id.ll_hot) LinearLayout llHot;
    @BindView(R.id.iv_del) ImageView ivHistoryDel;
    @BindView(R.id.lv_history) ListView lvHistory;
    @BindView(R.id.lv_hot) ListView lvHot;

    @BindView(R.id.pl_after) public PageLoadLayout plAfter;
    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;

    private SearchPresenter presenter;
    private String searchKey;
    private QuoteAdapter marketSearchAdapter, historyAdapter, hotAdapter;
    private AlertDialog delPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_search, StatusBarColor.THEME1);

        presenter = new SearchPresenter(this);
        presenter.init();
        initView();
    }

    /**
     * 初始化布局
     *
     * @param historys
     * @param hots
     */
    public void initView(List<QuoteItemJson> historys, List<QuoteItemJson> hots) {
        if ((historys == null || historys.size() == 0) && (hots == null || hots.size() == 0)) {
            svBefore.setVisibility(View.GONE);
        } else {
            svBefore.setVisibility(View.VISIBLE);
            if (historys != null && historys.size() > 0) {
                //显示历史搜索
                llHistory.setVisibility(View.VISIBLE);
                int size = historys.size();
                if (size > 5) {
                    historys = new ArrayList<>(historys.subList(0, 5));
                }
                historyAdapter = new QuoteAdapter(this, historys);
                lvHistory.setAdapter(historyAdapter);
                setListViewHeightBasedOnChildren(lvHistory);
            } else {
                //隐藏历史搜索
                llHistory.setVisibility(View.GONE);
            }
            if (hots != null && hots.size() > 0) {
                //显示行情推荐
                llHot.setVisibility(View.VISIBLE);
                hotAdapter = new QuoteAdapter(this, hots);
                lvHot.setAdapter(hotAdapter);
                setListViewHeightBasedOnChildren(lvHot);
            } else {
                //隐藏行情推荐
                llHot.setVisibility(View.GONE);
            }
        }
    }

    private void initView() {
        plvContent.setOnRefreshListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plvContent.setOnItemClickListener(this);
        lvHistory.setOnItemClickListener(this);
        lvHot.setOnItemClickListener(this);

        plAfter.setOnAfreshLoadListener(this);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //监听软键盘搜索按钮
                    String key = edtSearch.getText();
                    searchKey = key;
                    plAfter.loadWait();
                    presenter.search(key);
                    edtSearch.dismissDropDown();
                    return true;
                }
                return false;
            }
        });

        edtSearch.setAdapter(new AutoCompleteAdapter(AutoCompleteUtils.getData(this), this));
        edtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter adapter = edtSearch.getAdapter();
                if (adapter != null) {
                    QuoteItemJson item = (QuoteItemJson) adapter.getItem(position);
                    String name = item.getName();
                    searchKey = name;
                    edtSearch.setText(name);
                    plAfter.loadWait();
                    presenter.search(name);
                }
            }
        });
    }

    @OnClick({R.id.iv_break, R.id.tv_break, R.id.iv_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.tv_break:
                onBackPressed();
                break;
            case R.id.iv_del:
                if (delPop == null)
                    delPop = new AlertDialog.Builder(this)
                            .setTitle("温馨提示")
                            .setMessage("是否删除搜索记录")
                            .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    presenter.clearHistory();
                                    historyAdapter.dataList.clear();
                                    historyAdapter.notifyDataSetChanged();
                                    llHistory.setVisibility(View.GONE);
                                }
                            }).setPositiveButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                if(delPop.isShowing()) return;
                delPop.show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        QuoteItemJson quote;
        if (parent == lvHistory) {
            quote = historyAdapter.getItem(position);
        } else if (parent == lvHot) {
            quote = hotAdapter.getItem(position);
        } else {
            quote = marketSearchAdapter.getItem(position - 1);
        }
        intent.putExtra("search", quote);
        presenter.addHistory(quote);
        if (historyAdapter != null) {
            historyAdapter.addData(quote);
        }
        setResult(RESULT_OK, intent);
        onBackPressed();
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
        plAfter.loadWait();
        presenter.search(searchKey);
    }

    public void initSearchData(String info) {
        svBefore.setVisibility(View.GONE);
        plAfter.setVisibility(View.VISIBLE);
        if (info == null || info.equals("")) {
            plAfter.setNullText(getString(R.string.error_search_null));
            plAfter.setNullImgId(R.mipmap.icon_search_null);
            plAfter.setNullTextColor(R.color.font_color8);
            plAfter.loadEmptyData();
        } else {
            List<QuoteItemJson> marketItemBeens = JSON.parseArray(info, QuoteItemJson.class);
            if (marketItemBeens == null || marketItemBeens.size() == 0) {
                plAfter.setNullText(getString(R.string.error_search_null));
                plAfter.setNullImgId(R.mipmap.icon_search_null);
                plAfter.setNullTextColor(R.color.font_color8);
                plAfter.loadEmptyData();
            } else {
                List data = disposeData(marketItemBeens);
                if (marketSearchAdapter == null) {
                    marketSearchAdapter = new QuoteAdapter(this, data);
                    marketSearchAdapter.setSearchKey(searchKey);
                    plvContent.setAdapter(marketSearchAdapter);
                } else {
                    marketSearchAdapter.setSearchKey(searchKey);
                    marketSearchAdapter.setData(data);
                }
                AutoCompleteUtils.saveData(this, marketItemBeens);
                edtSearch.setData(AutoCompleteUtils.getData(this));
                plAfter.loadOver();
            }
        }
    }

    public void refresh(String info) {
        if (info != null && !info.equals("")) {
            try {
                List<QuoteItemJson> marketItemBeens = JSON.parseArray(info, QuoteItemJson.class);
                if (marketItemBeens != null && marketItemBeens.size() > 0) {
                    List data = disposeData(marketItemBeens);
                    if (marketSearchAdapter == null) {
                        marketSearchAdapter = new QuoteAdapter(this, data);
                        plvContent.setAdapter(marketSearchAdapter);
                    } else {
                        marketSearchAdapter.setData(data);
                    }
                    AutoCompleteUtils.saveData(this, marketItemBeens);
                    edtSearch.setData(AutoCompleteUtils.getData(this));
                    marketSearchAdapter.setSearchKey(searchKey);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(String info) {
        if (info != null && !info.equals("")) {
            try {
                List<QuoteItemJson> marketItemBeen = JSON.parseArray(info, QuoteItemJson.class);
                if (marketItemBeen == null || marketItemBeen.size() == 0) {
                } else {
                    List list = disposeData(marketItemBeen);
                    marketSearchAdapter.addData(list);
                    marketSearchAdapter.setSearchKey(searchKey);
                    AutoCompleteUtils.saveData(this, marketItemBeen);
                    edtSearch.setData(AutoCompleteUtils.getData(this));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    /**
     * 数据处理
     *
     * @param data
     * @return
     */
    private List disposeData(List data) {
        List newData;
        int size = data.size();
        if (size > VarConstant.LIST_MAX_SIZE) {
            newData = new ArrayList(data.subList(0, VarConstant.LIST_MAX_SIZE));
            int lastPosition = VarConstant.LIST_MAX_SIZE - 1;
            QuoteItemJson quoteItemJson = (QuoteItemJson) data.get(lastPosition);
            presenter.setLastId(quoteItemJson.getId());
            presenter.setMore(true);
        } else {
            newData = new ArrayList(data);
            presenter.setLastId("");
            presenter.setMore(false);
        }
        return newData;
    }

    /**
     * 动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
