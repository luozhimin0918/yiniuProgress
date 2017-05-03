package com.jyh.kxt.main.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.library.util.DateUtils;
import com.library.widget.PageLoadLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentActivity extends BaseActivity {

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.iv_photo) ImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_type) TextView tvType;
    @BindView(R.id.tv_time) TextView tvTime;
    @BindView(R.id.iv_like) ImageView ivLike;
    @BindView(R.id.lv_content) ListView lvContent;

    private NewsContentPresenter newsContentPresenter;

    public String id = "";
    private boolean isLike;
    private ImageView ivDing;
    private TextView tvDing;
    private LinearLayout newsHeadLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);

        newsContentPresenter = new NewsContentPresenter(this);

        Intent intent = getIntent();
        id = intent.getStringExtra(IntentConstant.O_ID);

        newsContentPresenter.init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        id = intent.getStringExtra(IntentConstant.O_ID);
        newsContentPresenter.init();
    }

    public void initView(NewsContentJson news) {

        initTitle(news);

        initHeadViewLayout();
        addContentView(news);
        lvContent.addHeaderView(newsHeadLayout);
    }

    /**
     * 初始化标题信息
     *
     * @param news
     */
    private void initTitle(NewsContentJson news) {
        tvTitle.setText(news.getTitle());
        Glide.with(this).load(news.getAuthor_image()).error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap.icon_user_def_photo).into
                (ivPhoto);
        tvName.setText(news.getAuthor_name());
        tvType.setText(news.getAuthor_profile());
        String time = "";
        try {
            time = DateUtils.transformTime(Long.parseLong(news.getCreate_time()) * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            time = "00:00";
        }
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLike = !isLike;
                ivLike.setSelected(isLike);
            }
        });
        tvTime.setText(time);
    }

    /**
     * 初始化头部布局
     */
    public void initHeadViewLayout() {
        if (newsHeadLayout == null) {
            AbsListView.LayoutParams headLayoutParams = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT);

            newsHeadLayout = new LinearLayout(this);
            newsHeadLayout.setOrientation(LinearLayout.VERTICAL);
            newsHeadLayout.setLayoutParams(headLayoutParams);
        } else {
            newsHeadLayout.removeAllViews();
        }
    }

    /**
     * 添加内容详情
     *
     * @param news
     */
    private void addContentView(NewsContentJson news) {
        View headContent = LayoutInflater.from(this).inflate(R.layout.layout_news_content_head, null);
        LoadX5WebView wvContent = (LoadX5WebView) headContent.findViewById(R.id.wv_content);
        TextView tvSouce = (TextView) headContent.findViewById(R.id.tv_source);
        ivDing = (ImageView) headContent.findViewById(R.id.iv_ding);
        tvDing = (TextView) headContent.findViewById(R.id.tv_ding);
        ImageView ivPyq = (ImageView) headContent.findViewById(R.id.iv_pyq);
        ImageView ivWx = (ImageView) headContent.findViewById(R.id.iv_wx);
        ImageView ivSina = (ImageView) headContent.findViewById(R.id.iv_sina);

        wvContent.build().loadData(news.getContent());
        tvSouce.setText("<font color='#A1ABB2'>文章来源:</font><font color='#2E3239'>" + news.getSource() + "</font>");
        tvDing.setText(news.getNum_good());
        ivDing.setSelected(news.getIs_good().equals("1") ? true : false);

        ivPyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        newsHeadLayout.addView(headContent);
    }

    @OnClick({R.id.iv_photo, R.id.iv_like})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_photo:
                break;
            case R.id.iv_like:
                isLike = !isLike;
                ivLike.setSelected(isLike);
                break;
        }
    }
}
