package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.index.presenter.BrowerHistoryPresenter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.library.widget.PageLoadLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.taobao.accs.ACCSManager.mContext;

/**
 * 项目名:Kxt
 * 类描述:浏览记录
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/4.
 */

public class BrowerHistoryActivity extends BaseActivity {

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.lv_content) public FastInfoPullPinnedListView lvContent;

    private BrowerHistoryPresenter browerHistoryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_browerhistory, StatusBarColor.THEME1);
        tvBarTitle.setText("浏览记录");
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_brower_clear));
        browerHistoryPresenter = new BrowerHistoryPresenter(this);
        FastInfoPinnedListView refreshableView = lvContent.getRefreshableView();
        refreshableView.addFooterListener(browerHistoryPresenter);
        plRootView.setOnAfreshLoadListener(browerHistoryPresenter);
        lvContent.setOnRefreshListener(browerHistoryPresenter);

        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = browerHistoryPresenter.adapter.getItem(position - 1);
                if (item instanceof String) return;
                if (item instanceof NewsJson) {
                    NewsJson newsJson = (NewsJson) item;
                    Intent intent = null;
                    if (TextUtils.isEmpty(newsJson.getHref())) {
                        intent = new Intent(getContext(), NewsContentActivity.class);
                        intent.putExtra(IntentConstant.O_ID, newsJson.getO_id());
                    } else {
                        intent = new Intent(getContext(), WebActivity.class);
                        intent.putExtra(IntentConstant.WEBURL, newsJson.getHref());
                    }

                    getContext().startActivity(intent);

                }
                return;
            }
        });

        browerHistoryPresenter.initData();
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                //清除浏览记录
                browerHistoryPresenter.clear(lvContent);
                break;
        }
    }

    /**
     * 加载为空
     */
    public void loadEmptyData() {
        plRootView.loadEmptyData();
    }
}
