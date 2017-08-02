package com.jyh.kxt.trading.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.trading.adapter.AuthorAdapter;
import com.jyh.kxt.trading.json.ViewpointJson;
import com.jyh.kxt.trading.presenter.AuthorPresenter;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.util.LogUtil;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.listview.PinnedSectionListView;
import com.library.widget.listview.PullPinnedListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:作者详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/1.
 */

public class AuthorActivity extends BaseActivity implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener {
    @BindView(R.id.pl_content) public PullPinnedListView plContent;
    private PinnedSectionListView refreshableView;
    @BindView(R.id.iv_break) ImageView ivBreak;
    @BindView(R.id.v_like) View vLike;
    @BindView(R.id.rl_head_title_bar) RelativeLayout rlHeadTitleBar;
    @BindView(R.id.iv_photo) RoundImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_fans) TextView tvFans;
    @BindView(R.id.tv_article) TextView tvArticle;
    @BindView(R.id.ll_layout_desc) LinearLayout llLayoutDesc;
    @BindView(R.id.pl_list_rootView) PageLoadLayout plRootView;
    @BindView(R.id.error_break) ImageView errorBreak;

    private AuthorPresenter presenter;

    private View headView;
    private TextView tvInfo;
    private String authorId;

    private float downYPoint;
    private float upYPoint;
    private boolean isPullUp;
    private int statusHeight;

    public AuthorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_author, StatusBarColor.NO_COLOR);

        plContent.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refreshableView = plContent.getRefreshableView();
        refreshableView.setDividerHeight(0);
        plContent.setOnItemClickListener(this);
        plContent.setOnRefreshListener(this);
        refreshableView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        startHeadAnimation();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                listUpdateScroll();
            }
        });
        refreshableView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        listUpdateScroll();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        downYPoint = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_UP ||
                            event.getAction() == MotionEvent.ACTION_CANCEL) {
                        upYPoint = event.getY();
                        isPullUp = downYPoint - upYPoint > 0;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        refreshableView.setmShadowTopSpace(SystemUtil.dp2px(this, 68));

        statusHeight = SystemUtil.getStatusHeight(this);
        rlHeadTitleBar.getBackground().setAlpha(0);

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        authorId = getIntent().getStringExtra(IntentConstant.O_ID);

        presenter = new AuthorPresenter(this, authorId);
        initHeadView();
        loadWait();
        presenter.init();
    }

    /**
     * 初始化布局
     *
     * @param authorDetailsJson
     */
    public void setView(AuthorDetailsJson authorDetailsJson) {
        setHeadView(authorDetailsJson);
        try {
            List<AuthorNewsJson> news = authorDetailsJson.getList();
            List<ViewpointJson> viewpoints = authorDetailsJson.getViews();
            List<AuthorNewsJson> data = null;
            List<ViewpointJson> data2 = null;
            if (news == null || news.size() == 0) {
            } else {
                if (news.size() > VarConstant.LIST_MAX_SIZE) {
                    presenter.setMore_article(true);
                    presenter.setLastId_article(news.get(VarConstant.LIST_MAX_SIZE - 1).getO_id());
                    data = new ArrayList<>(news.subList(0, VarConstant.LIST_MAX_SIZE));
                } else {
                    presenter.setMore_article(false);
                    presenter.setLastId_article("");
                    data = new ArrayList<>(news);
                }
            }
            if (viewpoints == null || viewpoints.size() == 0) {
            } else {
                if (viewpoints.size() > VarConstant.LIST_MAX_SIZE) {
                    presenter.setMore_article(true);
                    presenter.setLastId_article(viewpoints.get(VarConstant.LIST_MAX_SIZE - 1).getAuthor_id());
                    data2 = new ArrayList<>(viewpoints.subList(0, VarConstant.LIST_MAX_SIZE));
                } else {
                    presenter.setMore_article(false);
                    presenter.setLastId_article("");
                    data2 = new ArrayList<>(viewpoints);
                }
            }

            if (adapter == null) {
                adapter = new AuthorAdapter(this, data2, data, AuthorAdapter.TYPE_VIEWPOINT);
                plContent.setAdapter(adapter);
            } else {
                adapter.setViewpointData(data2);
                adapter.setArticleData(data);
            }

            if (refreshableView.getHeaderViewsCount() <= 1) {
                refreshableView.addHeaderView(headView);
            }
            loadOver();
        } catch (Exception e) {
            e.printStackTrace();
            plRootView.loadError();
        }
    }


    @OnClick({R.id.iv_break, R.id.v_like, R.id.error_break})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
            case R.id.error_break:
                onBackPressed();
                break;
            case R.id.v_like:
                if (LoginUtils.isLogined(this)) {
                    presenter.attention(vLike.isSelected());
                } else {
                    startActivity(new Intent(this, LoginOrRegisterActivity.class));
                }
                break;
        }
    }

    private void setHeadView(AuthorDetailsJson authorDetailsJson) {
        tvName.setText(authorDetailsJson.getName());
        tvFans.setText("粉丝 " + authorDetailsJson.getNum_fans());
        tvArticle.setText("文章 " + authorDetailsJson.getArticle_num());
        tvInfo.setText(authorDetailsJson.getIntroduce());

        //关注
        String is_follow = authorDetailsJson.getIs_follow();
        if (is_follow == null) {
            vLike.setSelected(false);
        } else if (is_follow.equals("1")) {
            vLike.setSelected(true);
        } else {
            vLike.setSelected(false);
        }

        Glide.with(this)
                .load(authorDetailsJson.getPicture())
                .asBitmap()
                .override(120, 120)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivPhoto.setImageBitmap(resource);
                    }
                });
    }

    private void startHeadAnimation() {
        listUpdateScroll();
    }

    private void listUpdateScroll() {
        int scrollY = getScrollY();

        int actionBarHeight = SystemUtil.dp2px(getContext(), 68);

        if (scrollY > 0 && scrollY < actionBarHeight) {
            //缩放
            float scaleVal = (float) (actionBarHeight - scrollY) / (float) actionBarHeight;
            LogUtil.e(LogUtil.TAG, "scrollY" + scrollY + "   actionBarHeight" + actionBarHeight + "    scaleVal" +
                    scaleVal);
            if (scaleVal > 0.4f) {
                ivPhoto.setScaleX(scaleVal);
                ivPhoto.setScaleY(scaleVal);
            }
            if (scaleVal > 0.6f) {
                tvName.setScaleX(scaleVal);
                tvName.setScaleY(scaleVal);
            }

            ivPhoto.setTranslationX(scrollY / 6);
            tvName.setTranslationX(-scrollY / 3);

            if (actionBarHeight - SystemUtil.dp2px(this, 15) >= scrollY) {
                Log.e(TAG, "ivPhoto: " + scrollY);
                ivPhoto.setTranslationY(-scrollY);
            }
            if (actionBarHeight - SystemUtil.dp2px(this, 30) >= scrollY) {
                tvName.setTranslationY(-scrollY);
                Log.e(TAG, "tvName: " + scrollY);
            }

            llLayoutDesc.setAlpha(scaleVal);
            tvInfo.setAlpha(scaleVal);

            int bgAlphaVal = (int) ((1 - scaleVal) * 255);
            rlHeadTitleBar.getBackground().setAlpha(bgAlphaVal);
        } else if (scrollY > actionBarHeight) {

            ivPhoto.setScaleX(0.4f);
            ivPhoto.setScaleY(0.4f);
            tvName.setScaleX(0.6f);
            tvName.setScaleY(0.6f);

            ivPhoto.setTranslationY(-(actionBarHeight - SystemUtil.dp2px(this, 15)));
            tvName.setTranslationY(-(actionBarHeight - SystemUtil.dp2px(this, 30)));

//            ivPhoto.setTranslationX(actionBarHeight);
//            tvName.setTranslationX(-actionBarHeight);

            llLayoutDesc.setAlpha(0);
            tvInfo.setAlpha(0);

            rlHeadTitleBar.getBackground().setAlpha(255);

        } else if (scrollY == 0) {
            ivPhoto.setScaleX(1);
            ivPhoto.setScaleY(1);
            tvName.setScaleX(1);
            tvName.setScaleY(1);

            ivPhoto.setTranslationX(0);
            tvName.setTranslationX(0);

            ivPhoto.setTranslationY(0);
            tvName.setTranslationY(0);

            llLayoutDesc.setAlpha(1);
            tvInfo.setAlpha(1);

            rlHeadTitleBar.getBackground().setAlpha(0);

        }
    }

    public int getScrollY() {
        int h = 0;
        try {
            int top = headView.getTop();
            h = Math.abs(top);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }

    public void initHeadView() {
        headView = LayoutInflater.from(this).inflate(R.layout.layout_author_head, null, false);
        tvInfo = (TextView) headView.findViewById(R.id.tv_info);

        tvName = (TextView) findViewById(R.id.tv_name);
        ivPhoto = (RoundImageView) findViewById(R.id.iv_photo);
        tvFans = (TextView) findViewById(R.id.tv_fans);
        tvArticle = (TextView) findViewById(R.id.tv_article);
        llLayoutDesc = (LinearLayout) findViewById(R.id.ll_layout_desc);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_ATTENTION_OTHER:
                boolean isAttentionOther = (boolean) eventBus.intentObj;
                vLike.setSelected(isAttentionOther);
                break;
        }
    }

    public void loadWait() {
        errorBreak.setVisibility(View.GONE);
        plRootView.loadWait();
    }


    public void loadError() {
        errorBreak.setVisibility(View.VISIBLE);
        plRootView.loadError();
    }

    public void loadOver() {
        errorBreak.setVisibility(View.GONE);
        plRootView.loadOver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mQueue.cancelAll(AuthorPresenter.class.getName());
    }

    /**
     * 关注
     *
     * @param isFollow
     */
    public void attention(boolean isFollow) {
        vLike.setSelected(!isFollow);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List data = adapter.getData();
        int clickPosition = position - 1;
        if (data != null && data.size() >= clickPosition) {
            Object bean = data.get(clickPosition);
            if (bean instanceof ViewpointJson) {
                ViewpointJson viewpoint = (ViewpointJson) bean;
            } else {
                AuthorNewsJson newsJson = (AuthorNewsJson) bean;
                JumpUtils.jump(this, newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(), newsJson.getHref());
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }
}
