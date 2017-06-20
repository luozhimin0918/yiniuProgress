package com.jyh.kxt.explore.ui;

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
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.explore.adapter.NewsAdapter;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.explore.presenter.AuthorPresenter;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.VarConstant;
import com.library.util.LogUtil;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:作者
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/3.
 */

public class AuthorActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase
        .OnRefreshListener2, AdapterView.OnItemClickListener {
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_list_rootView) public PageLoadLayout plListRootView;
    @BindView(R.id.rl_head_title_bar) public RelativeLayout rlHeadTitleBar;
    @BindView(R.id.error_break) public ImageView ivErrorBreak;

    @BindView(R.id.v_like) View vLike;

    private RoundImageView ivPhoto;
    private TextView tvName;
    private LinearLayout llLayoutDesc;
    private TextView tvFans;
    private TextView tvArticle;
    private TextView tvInfo;

    private AuthorPresenter authorPresenter;
    private boolean isLike;

    private String authorId = "";
    public NewsAdapter newsAdapter;
    private View headView;

    private int statusHeight;

    private float downYPoint = 0;
    private float upYPoint = 0;
    private boolean isPullUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_author, StatusBarColor.NO_COLOR);

        authorPresenter = new AuthorPresenter(this);
        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        plContent.setOnRefreshListener(this);
        plContent.setOnItemClickListener(this);
        plListRootView.setOnAfreshLoadListener(this);


        authorId = getIntent().getStringExtra(IntentConstant.O_ID);
        initHeadView();

        loadWait();
        authorPresenter.init(authorId);

        plContent.getRefreshableView().setOnScrollListener(new AbsListView.OnScrollListener() {
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


        plContent.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
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
        statusHeight = SystemUtil.getStatusHeight(this);
        rlHeadTitleBar.getBackground().setAlpha(0);
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

            ivPhoto.setTranslationX(scrollY);
            tvName.setTranslationX(scrollY);

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

            ivPhoto.setTranslationX(actionBarHeight);
            tvName.setTranslationX(actionBarHeight);

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

    /**
     * 初始化布局
     *
     * @param authorDetailsJson
     */
    public void setView(AuthorDetailsJson authorDetailsJson) {
        setHeadView(authorDetailsJson);
        try {
            List<AuthorNewsJson> list = authorDetailsJson.getList();
            if (list == null || list.size() == 0) {
                plListRootView.loadEmptyData();
            } else {
                if (list.size() < 8) {
                    plContent.noMoreData();
                }

                List<AuthorNewsJson> data;
                if (list.size() > VarConstant.LIST_MAX_SIZE) {
                    authorPresenter.setMore(true);
                    authorPresenter.setLastId(list.get(VarConstant.LIST_MAX_SIZE - 1).getO_id());
                    data = new ArrayList<>(list.subList(0, VarConstant.LIST_MAX_SIZE));
                } else {
                    authorPresenter.setMore(false);
                    authorPresenter.setLastId("");
                    data = new ArrayList<>(list);
                }
                if (newsAdapter == null) {
                    newsAdapter = new NewsAdapter(this, data);
                    plContent.setAdapter(newsAdapter);
                } else {
                    newsAdapter.setData(data);
                }
                if (plContent.getRefreshableView().getHeaderViewsCount() <= 1) {
                    plContent.getRefreshableView().addHeaderView(headView);
                }
                loadOver();
            }
        } catch (Exception e) {
            e.printStackTrace();
            plListRootView.loadError();
        }
    }

    @OnClick({R.id.iv_break, R.id.v_like, R.id.error_break})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.v_like:
                if (LoginUtils.isLogined(this))
                    authorPresenter.attention(vLike.isSelected());
                else
                    startActivity(new Intent(this, LoginOrRegisterActivity.class));
                break;
            case R.id.error_break:
                onBackPressed();
                break;
        }
    }

    public void refresh(AuthorDetailsJson authorDetailsJson) {
        setHeadView(authorDetailsJson);
        try {
            List<AuthorNewsJson> list = authorDetailsJson.getList();
            if (list == null || list.size() == 0) {
            } else {
                List<AuthorNewsJson> data;
                if (list.size() > VarConstant.LIST_MAX_SIZE) {
                    authorPresenter.setMore(true);
                    authorPresenter.setLastId(newsAdapter.getLastId());
                    data = new ArrayList<>(list.subList(0, VarConstant.LIST_MAX_SIZE));
                } else {
                    authorPresenter.setMore(false);
                    authorPresenter.setLastId("");
                    data = new ArrayList<>(list);
                }
                newsAdapter.setData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        plListRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                plContent.onRefreshComplete();
            }
        }, 500);
    }

    /**
     * 加载更多
     *
     * @param newsJsons
     */
    public void loadMore(List<AuthorNewsJson> newsJsons) {
        newsAdapter.addData(newsJsons);
    }

    /**
     * 加载错误,再次加载
     *
     * @param list
     */
    public void reLoadListData(List<AuthorNewsJson> list) {
        List<AuthorNewsJson> data;
        if (list.size() > VarConstant.LIST_MAX_SIZE) {
            authorPresenter.setMore(true);
            authorPresenter.setLastId(newsAdapter.getLastId());
            data = new ArrayList<>(list.subList(0, VarConstant.LIST_MAX_SIZE));
        } else {
            authorPresenter.setMore(false);
            authorPresenter.setLastId("");
            data = new ArrayList<>(list);
        }
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(this, data);
            plContent.setAdapter(newsAdapter);
        } else {
            newsAdapter.setData(data);
        }
    }

    @Override
    public void OnAfreshLoad() {
        loadWait();
        authorPresenter.init(authorId);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        authorPresenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        authorPresenter.loadMore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll(authorPresenter.getClass().getName());
    }


    public void loadWait() {
        ivErrorBreak.setVisibility(View.GONE);
        plListRootView.loadWait();
    }


    public void loadError() {
        ivErrorBreak.setVisibility(View.VISIBLE);
        plListRootView.loadError();
    }

    public void loadOver() {
        ivErrorBreak.setVisibility(View.GONE);
        plListRootView.loadOver();
    }

    /**
     * 关注
     *
     * @param isFollow
     */
    public void attention(boolean isFollow) {
        vLike.setSelected(!isFollow);
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
//        Glide.with(getContext())
//                .load(authorDetailsJson.getPicture())
//                .crossFade(1000)
//                .bitmapTransform(new BlurTransformation(getContext(), 15, 4)) // “23”：设置模糊度(在0.0到25.0之间)，默认”25";
// "4":图片缩放比例,默认“1”。
//                .into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
// glideAnimation) {
//                        headView.setBackground(resource);
//                    }
//                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 2) {
            List<AuthorNewsJson> data = newsAdapter.getData();
            AuthorNewsJson newsJson = data.get(position - 2);
            JumpUtils.jump(this, newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(), newsJson.getHref());
        }
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (plContent != null) {
            plContent.setDividerNull();
        }
        if (newsAdapter != null) {
            newsAdapter.notifyDataSetChanged();
        }
    }

}
