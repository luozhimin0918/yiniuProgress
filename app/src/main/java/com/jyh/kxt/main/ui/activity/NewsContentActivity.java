package com.jyh.kxt.main.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentActivity extends BaseActivity {

    @BindView(R.id.rv_message) public PullToRefreshListView ptrLvMessage;

    private NewsContentPresenter newsContentPresenter;
    public CommentPresenter commentPresenter;

    public String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);
        id = getIntent().getStringExtra(IntentConstant.O_ID);//获取传递过来的Id

        newsContentPresenter = new NewsContentPresenter(this);

        commentPresenter = new CommentPresenter(this);//初始化评论相关
        commentPresenter.bindListView(ptrLvMessage);

        newsContentPresenter.requestInitComment();
    }

    @OnClick({R.id.iv_break, R.id.iv_comment, R.id.iv_collect, R.id.iv_ding, R.id.iv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.iv_comment:
                //回复
                break;
            case R.id.iv_collect:
                //收藏
                break;
            case R.id.iv_ding:
                //点赞
                break;
            case R.id.iv_share:
                //分享
                break;
        }
    }

    /**
     * 创建WebView 和 Head头部
     *
     * @param newsContentJson
     */
    public void createWebViewAndHead(NewsContentJson newsContentJson) {
        LinearLayout headView = commentPresenter.getHeadView();
        String webContent = newsContentJson.getContent();
    }
}
