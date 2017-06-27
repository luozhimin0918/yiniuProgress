package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.adapter.AttentionArticleAdapter;
import com.jyh.kxt.index.presenter.AttentionArticlePresenter;
import com.jyh.kxt.index.ui.AttentionActivity;
import com.jyh.kxt.main.json.NewsJson;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:我的关注-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/17.
 */

public class AttentionArticleFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, AdapterView
        .OnItemClickListener, PullToRefreshBase.OnRefreshListener2, DelNumListener {

    @BindView(R.id.iv_selAll) public ImageView ivSelAll;
    @BindView(R.id.tv_selAll) TextView tvSelAll;
    @BindView(R.id.ll_selAll) LinearLayout llSelAll;
    @BindView(R.id.tv_del) TextView tvDel;
    @BindView(R.id.ll_del) public LinearLayout llDel;
    private AttentionArticlePresenter presenter;

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private AttentionArticleAdapter adapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_pulltorefresh_list);

        presenter = new AttentionArticlePresenter(this);
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnItemClickListener(this);
        plvContent.setOnRefreshListener(this);

        plRootView.loadWait();
        presenter.init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(presenter.getClass().getName());
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsJson newsJson = adapter.getData().get(position - 1);
        JumpUtils.jump((BaseActivity) getActivity(), newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(),
                newsJson.getHref());
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }


    public void init(List<NewsJson> newsJsons) {
        if (adapter == null) {
            adapter = new AttentionArticleAdapter(newsJsons, getContext());
            adapter.setSelListener(this);
            plvContent.setAdapter(adapter);
        } else {
            adapter.setData(newsJsons);
        }
        plRootView.loadOver();
    }

    public void refresh(List<NewsJson> news) {
        adapter.setData(news);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(List<NewsJson> news) {
        adapter.addData(news);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    /**
     * 编辑模式
     *
     * @param isEdit
     */
    public void editMode(boolean isEdit) {
        if (adapter != null) {
            adapter.setEdit(isEdit);
            if (isEdit) {
                llDel.setVisibility(View.VISIBLE);
            } else {
                llDel.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.ll_selAll, R.id.tv_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_selAll:
                //全选
                if (ivSelAll.isSelected()) {
                    ivSelAll.setSelected(false);
                } else {
                    ivSelAll.setSelected(true);
                }
                presenter.selAll(ivSelAll.isSelected(), this, adapter);
                break;
            case R.id.tv_del:
                //删除
                presenter.del(this, adapter);
                break;
        }
    }

    @Override
    public void delItem(Integer num) {
//        try {
//            tvDel.setText("删除(" + num + ")");
//            ((AttentionActivity) getActivity()).tvBarFunction.setText("编辑");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void delError() {

    }

    @Override
    public void delSuccessed() {

    }

    @Override
    public void delAll(boolean isAll) {
        ivSelAll.setSelected(isAll);
    }

    @Override
    public void quitEdit() {

    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            List<NewsJson> data = adapter.getData();
            if (data == null || data.size() == 0) {
                plRootView.loadEmptyData();
            }
        }
    }
}
