package com.jyh.kxt.trading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.base.widget.ThumbView3;
import com.jyh.kxt.trading.json.ViewPointBean;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.listview.PinnedSectionListView;
import com.library.widget.listview.PullPinnedListView;
import com.library.widget.tablayout.NavigationTabLayout;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointAdapter extends BaseAdapter implements
        PinnedSectionListView.PinnedSectionListAdapter, NavigationTabLayout.OnTabSelectListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private PullPinnedListView mPullPinnedListView;

    private int viewTypeCount = 3;
    private List<ViewPointTradeBean> dataList;

    private TradeHandlerUtil mTradeHandlerUtil;
    private PopupWindow functionPopupWindow;
    private ArticleContentPresenter articleContentPresenter;

    private NavigationTabLayout tabLayout;
    private int navigationOldTabPosition = 0;
    public int navigationTabClickPosition = 0;
    public HashMap<String, List<ViewPointTradeBean>> pointListMap = new HashMap<>();
    public HashMap<Integer, PinnedSectionListView.PinnedOffset> listViewOffset = new HashMap<>();

    public ViewpointAdapter(Context mContext, List<ViewPointTradeBean> viewPointTradeBeanList) {

        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);

        articleContentPresenter = new ArticleContentPresenter(mContext);
        dataList = new ArrayList<>(viewPointTradeBeanList);
        pointListMap.put("chosen", viewPointTradeBeanList);

        mTradeHandlerUtil = TradeHandlerUtil.getInstance();
        replaceDataAndNotifyDataSetChanged(viewPointTradeBeanList, 0);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder0 viewHolder0 = null;
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;

        final ViewPointTradeBean viewPointTradeBean = dataList.get(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            switch (itemViewType) {
                case 0:
                    convertView = mInflater.inflate(R.layout.view_viewpoint_function_nav, parent, false);
                    viewHolder0 = new ViewHolder0(convertView);
                    convertView.setTag(viewHolder0);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.view_viewpoint_item1, parent, false);
                    viewHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(viewHolder1);
                    break;
                case 2:
                    convertView = mInflater.inflate(R.layout.view_point_nodata, parent, false);
                    viewHolder2 = new ViewHolder2(convertView);
                    convertView.setTag(viewHolder2);
                    break;
            }
        } else {
            switch (itemViewType) {
                case 0:
                    viewHolder0 = (ViewHolder0) convertView.getTag();
                    break;
                case 1:
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
                case 2:
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }

        switch (itemViewType) {
            case 0:
                viewHolder0.navigationTabLayout.setData(R.array.nav_trading_menu);
                viewHolder0.navigationTabLayout.setOpenContainerAnimator(false);
                viewHolder0.navigationTabLayout.setOnTabSelectListener(this);
                viewHolder0.navigationTabLayout.setCurrentTab(navigationTabClickPosition);

                viewHolder0.navigationTabLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));
                viewHolder0.navigationTabLayout.setIndicatorColor(ContextCompat.getColor(mContext, R.color.indicator_color));
                viewHolder0.navigationTabLayout.setTextSelectColor(ContextCompat.getColor(mContext, R.color.tabSelColor));
                viewHolder0.navigationTabLayout.setTextUnselectColor(ContextCompat.getColor(mContext, R.color.tabSelColor));
                viewHolder0.line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color7));

                this.tabLayout = viewHolder0.navigationTabLayout;
                break;
            case 1:
                viewHolder1.setData(viewPointTradeBean);
                viewHolder1.setItemViewColor(convertView);

                viewHolder1.tvContent.convertToGif(viewPointTradeBean.content);
                viewHolder1.tvContent.setMaxLines(3);
                viewHolder1.tvNickName.setText(viewPointTradeBean.author_name);

                CharSequence formatCreateTime = DateFormat.format("MM-dd HH:mm", viewPointTradeBean.time * 1000);
                viewHolder1.tvTime.setText(formatCreateTime.toString());

                viewHolder1.tvZanView.setText(String.valueOf(viewPointTradeBean.num_good));
                viewHolder1.tvPinLunView.setText(String.valueOf(viewPointTradeBean.num_comment));

                articleContentPresenter.setAuthorImage(viewHolder1.rivUserAvatar, viewPointTradeBean.author_img);
                articleContentPresenter.initTradeHandler(viewHolder1.tvZanView, viewPointTradeBean.isFavour);

                articleContentPresenter.initTransmitView(
                        viewHolder1.rlTransmitLayout,
                        viewHolder1.tvTransmitView,
                        viewPointTradeBean.forward);

                articleContentPresenter.setPictureAdapter(viewHolder1.gridPictureLayout, viewPointTradeBean.picture);
                break;
            case 2:
                viewHolder2.tvNoDataText.setTextColor(Color.parseColor("#2A87F0"));
                if (navigationTabClickPosition == 2) {
                    boolean loginEd = LoginUtils.isLogined(mContext);
                    if (!loginEd) {
                        viewHolder2.tvNoDataText.setText("登录同步关注");
                        viewHolder2.tvNoDataText.setBackgroundResource(R.drawable.bg_point_login);
                        int padding = SystemUtil.dp2px(mContext, 3);
                        viewHolder2.tvNoDataText.setPadding(padding, padding, padding, padding);
                        viewHolder2.tvNoDataText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                            }
                        });
                    } else {
                        viewHolder2.tvNoDataText.setText("暂无关注");
                        viewHolder2.tvNoDataText.setBackgroundColor(Color.TRANSPARENT);
                    }
                } else {
                    viewHolder2.tvNoDataText.setText("暂无任何数据");
                    viewHolder2.tvNoDataText.setBackgroundColor(Color.TRANSPARENT);
                    viewHolder2.tvNoDataText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    });
                }
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPointTradeBean.content != null) {
                    Intent intent = new Intent(mContext, ViewPointDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, viewPointTradeBean.o_id);
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }


    private String requestNavigationType = null;

    public void bindListView(PullPinnedListView mPullPinnedListView) {
        this.mPullPinnedListView = mPullPinnedListView;
        mPullPinnedListView.setAdapter(this);
    }

    class ViewHolder0 {
        @BindView(R.id.ntl_title_view) NavigationTabLayout navigationTabLayout;
        @BindView(R.id.v_line) View line;

        ViewHolder0(View contentView) {
            ButterKnife.bind(this, contentView);
        }
    }

    class ViewHolder1 {
        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.viewpoint_function_share) ImageView mFunctionView;
        @BindView(R.id.viewpoint_title) EmoticonSimpleTextView tvContent;
        @BindView(R.id.viewpoint_picture_layout) GridView gridPictureLayout;
        @BindView(R.id.viewpoint_function_zan) LinearLayout viewpointFunctionZan;
        @BindView(R.id.rl_content_item) RelativeLayout rlContentItem;

        @BindView(R.id.viewpoint_transmit_layout) RelativeLayout rlTransmitLayout;
        @BindView(R.id.viewpoint_transmit_text) EmoticonSimpleTextView tvTransmitView;

        @BindView(R.id.thumb_view_zan)   ThumbView3 mThumbView3;
        @BindView(R.id.view_point_pl_tv) TextView tvPinLunView;
        @BindView(R.id.view_point_fx_tv) ImageView tvShareView;
        @BindView(R.id.view_point_zan_tv) TextView tvZanView;

        @BindView(R.id.viewpoint_space) View viewSpace;

        @BindView(R.id.viewpoint_line) View viewLine1;
        @BindView(R.id.view_line1) View viewLine2;
        @BindView(R.id.view_line2) View viewLine3;


        private ViewPointTradeBean viewPointTradeBean;

        public void setItemViewColor(View convertView) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));

            tvNickName.setTextColor(ContextCompat.getColor(mContext, R.color.font_color62));
            tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color9));
            tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
            tvTransmitView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
            viewSpace.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));

            rlTransmitLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.view_point_transmit_content));

            viewLine1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));
            viewLine2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));
            viewLine3.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));

            int paddingVal = SystemUtil.dp2px(mContext, 8);
            tvZanView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
            tvZanView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color9));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvZanView, R.mipmap
                    .icon_point_zan1, 0, 0, 0);
            tvPinLunView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
            tvPinLunView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color9));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvPinLunView, R.mipmap
                    .icon_point_pl, 0, 0, 0);
            tvShareView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_point_fx));
        }

        @OnClick({R.id.thumb_view_zan, R.id.view_point_pl_layout, R.id.view_point_fx_layout})
        public void itemNavFunction(View view) {
            switch (view.getId()) {
                case R.id.thumb_view_zan:
                    if (viewPointTradeBean.isFavour) {
                        ToastView.makeText(mContext, "您已经赞过了");
                    } else {
                        boolean isSaveSuccess = mTradeHandlerUtil.saveState(mContext, viewPointTradeBean, 1, true);
                        if (isSaveSuccess) {
                            mThumbView3.startGiveAnimation();
                            viewPointTradeBean.isFavour = true;
                            articleContentPresenter.initTradeHandler(tvZanView, true);

                            viewPointTradeBean.num_good += 1;
                            tvZanView.setText(String.valueOf(viewPointTradeBean.num_good));
                        }
                    }
                    break;
                case R.id.view_point_pl_layout:
                    Intent intent = new Intent(mContext, ViewPointDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, viewPointTradeBean.o_id);
                    mContext.startActivity(intent);
                    break;
                case R.id.view_point_fx_layout:
                    functionPopupWindow = articleContentPresenter.shareToPlatform(viewPointTradeBean.shareDict);
                    break;
            }
        }


        public void setData(ViewPointTradeBean viewPointTradeBean) {
            this.viewPointTradeBean = viewPointTradeBean;
        }

        ViewHolder1(View contentView) {
            ButterKnife.bind(this, contentView);

            articleContentPresenter.initGridView(gridPictureLayout);
            /**
             * 初始化右上角的点击事件
             */
            mFunctionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    articleContentPresenter.showFunctionWindow(viewPointTradeBean);
                }
            });

            /**
             * 点击头像
             */
            rivUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AuthorActivity.class);
                    intent.putExtra(IntentConstant.O_ID, viewPointTradeBean.author_id);
                    mContext.startActivity(intent);
                }
            });

            /**
             * 点击昵称
             */
            tvNickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AuthorActivity.class);
                    intent.putExtra(IntentConstant.O_ID, viewPointTradeBean.author_id);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class ViewHolder2 {
        @BindView(R.id.nodata_img) ImageView ivNoDataImage;
        @BindView(R.id.nodata_text) TextView tvNoDataText;

        ViewHolder2(View contentView) {
            ButterKnife.bind(this, contentView);
        }
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        ViewPointTradeBean viewPointTradeBean = dataList.get(position);
        return viewPointTradeBean.itemViewType;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }

    /**
     * 刷新Adapter替换 List 数组
     *
     * @param tradeList
     * @param fromSource 0表示不存在Tab切换按钮数据  1表示存在
     */
    private void replaceDataAndNotifyDataSetChanged(List<ViewPointTradeBean> tradeList, int fromSource) {
        dataList.clear();
        comparisonData(tradeList, fromSource);
        dataList.addAll(tradeList);
        notifyDataSetChanged();
    }

    private void comparisonData(List<ViewPointTradeBean> tradeList, int fromSource) {
        if (tradeList == null) {
            tradeList = new ArrayList<>();
        }
        if (mPullPinnedListView != null) {
            mPullPinnedListView.setMode(PullToRefreshBase.Mode.BOTH);
        }

        if (tradeList.size() == 0 && fromSource == 0) {//如果没有数据, 则添加类型为2的空数据
            ViewPointTradeBean viewPointTradeBean = new ViewPointTradeBean();
            viewPointTradeBean.itemViewType = 2;
            tradeList.add(viewPointTradeBean);

            if (mPullPinnedListView != null) {
                mPullPinnedListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        } else if ((tradeList.size() == 2 || tradeList.size() == 1) && fromSource == 1) {
            if (mPullPinnedListView != null) {
                mPullPinnedListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }

        if (fromSource == 0) { //如果来源是0 表示初次使用这个数据源  1 则表示读取的缓存数据源
            ViewPointTradeBean viewPointTradeBean = new ViewPointTradeBean();
            viewPointTradeBean.itemViewType = 0;
            tradeList.add(0, viewPointTradeBean);
        }
        TradeHandlerUtil.getInstance().listCheckState(mContext, tradeList);   //对 List 进行赞的遍历
    }

    @Override
    public void onTabSelect(int position, int clickId) {
        PinnedSectionListView refreshableView = mPullPinnedListView.getRefreshableView();
        PinnedSectionListView.PinnedOffset mPinnedOffset = refreshableView.getPinnedScrollY();
        listViewOffset.put(navigationTabClickPosition, mPinnedOffset);//保存旧的Y轴偏移

        navigationTabClickPosition = position;
        requestNavigationSwitch(position, 0);
    }

    /**
     * 点击导航栏Tab 进行数据切换
     * fromSource 0 默认请求 requestNavigationType  1 来自登录之后的数据刷新
     */
    private void requestNavigationSwitch(final int tabClickPosition, final int fromSource) {
        switch (tabClickPosition) {
            case 0:
                requestNavigationType = "chosen";
                break;
            case 1:
                requestNavigationType = "all";
                break;
            case 2:
                requestNavigationType = "follow";
                break;
        }

        List<ViewPointTradeBean> viewPointTradeBeen = pointListMap.get(requestNavigationType);

        //如果点击的是关注
        String uId = null;
        if (tabClickPosition == 2) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            if (userInfo == null) {
                if (viewPointTradeBeen == null) {
                    ArrayList<ViewPointTradeBean> tradeList = new ArrayList<>();
                    replaceDataAndNotifyDataSetChanged(tradeList, 0);
                    navigationOldTabPosition = navigationTabClickPosition;
                    return;
                }
            } else {
                uId = userInfo.getUid();
            }
        }
        if (viewPointTradeBeen == null) {

            final IBaseView iBaseView = (IBaseView) mContext;
            iBaseView.showWaitDialog(null);

            //请求网络显示转圈
            VolleyRequest mVolleyRequest = new VolleyRequest(iBaseView.getContext(), iBaseView.getQueue());
            mVolleyRequest.setTag(getClass().getName());

            JSONObject mainParam = mVolleyRequest.getJsonParam();
            mainParam.put("type", requestNavigationType);
            if (uId != null) {
                mainParam.put("uid", uId);
            }

            mVolleyRequest.doPost(HttpConstant.TRADE_MAIN, mainParam, new HttpListener<String>() {
                @Override
                protected void onResponse(String manJson) {
                    navigationOldTabPosition = navigationTabClickPosition;

                    ViewPointBean viewPointBean = JSONObject.parseObject(manJson, ViewPointBean.class);
                    if (viewPointBean == null) {
                        ArrayList<ViewPointTradeBean> tradeList = new ArrayList<>();
                        replaceDataAndNotifyDataSetChanged(tradeList, 0);
                        return;
                    }

                    List<ViewPointTradeBean> tradeList = viewPointBean.getTrade();
                    pointListMap.put(requestNavigationType, tradeList);

                    if (fromSource == 0 || (fromSource == 1 && navigationTabClickPosition == 2)) {
                        replaceDataAndNotifyDataSetChanged(tradeList, 0);
                    } else if (fromSource == 1 && navigationTabClickPosition != 2) {
                        comparisonData(tradeList, 0);
                    }
                    iBaseView.dismissWaitDialog();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    iBaseView.dismissWaitDialog();
                    ToastView.makeText3(mContext, "请检查网络~");
                    if (tabLayout != null) {
                        navigationTabClickPosition = navigationOldTabPosition;
                        tabLayout.setCurrentTab(navigationTabClickPosition);
                    }
                }
            });
        } else {
            replaceDataAndNotifyDataSetChanged(viewPointTradeBeen, 1);
        }

        PinnedSectionListView.PinnedOffset pinnedOffset = listViewOffset.get(tabClickPosition);
        if (pinnedOffset == null) {
            mPullPinnedListView.getRefreshableView().setSelection(0);
        } else {
            mPullPinnedListView.getRefreshableView().setSelection(pinnedOffset.position);
        }

    }


    /**
     * 加载更多数据
     *
     * @param newTradeBeanList
     */
    public void refreshAdapterData(List<ViewPointTradeBean> newTradeBeanList, PullToRefreshBase.Mode mode) {

        //对 List 进行赞的遍历
        TradeHandlerUtil.getInstance().listCheckState(mContext, newTradeBeanList);

        switch (navigationTabClickPosition) {
            case 0:
                requestNavigationType = "chosen";
                break;
            case 1:
                requestNavigationType = "all";
                break;
            case 2:
                requestNavigationType = "follow";
                break;
        }
        List<ViewPointTradeBean> tradeBeanList = pointListMap.get(requestNavigationType);
        if (mode == PullToRefreshBase.Mode.PULL_FROM_START) {
            tradeBeanList.clear();
            dataList.clear();

            tradeBeanList.addAll(newTradeBeanList); //存放到Map 中
            replaceDataAndNotifyDataSetChanged(tradeBeanList, 0);
        } else {
            tradeBeanList.addAll(newTradeBeanList); //存放到Map 中
            dataList.addAll(newTradeBeanList); //将Map 中的数据放到当前List中
        }
        notifyDataSetChanged();
    }

    /**
     * 通过EventBus 接到通知后赞数量+1
     *
     * @param intentObj
     */
    public void handlerEventBus(TradeHandlerUtil.EventHandlerBean intentObj) {
        Iterator<String> iterator = pointListMap.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();

            List<ViewPointTradeBean> tradeBeanList = pointListMap.get(next);
            for (ViewPointTradeBean viewPointTradeBean : tradeBeanList) {
                if (intentObj.tradeId.equals(viewPointTradeBean.o_id)) {
                    if (intentObj.favourState == 1) {
                        viewPointTradeBean.isFavour = true;
                        viewPointTradeBean.num_good += 1;
                    } else {

                    }
                    if (intentObj.collectState == 1) {
                        viewPointTradeBean.isCollect = true;
                    } else {
                        viewPointTradeBean.isCollect = false;
                    }

                    if (intentObj.commentState == 1) {
                        viewPointTradeBean.num_comment += 1;
                    }
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 删除
     *
     * @param o_id
     */
    public void delViewpoint(String o_id) {
        Iterator<String> iterator = pointListMap.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();

            List<ViewPointTradeBean> tradeBeanList = pointListMap.get(next);
            Iterator<ViewPointTradeBean> iterator1 = tradeBeanList.iterator();
            while (iterator1.hasNext()) {
                ViewPointTradeBean next1 = iterator1.next();
                if (next1.o_id != null && next1.o_id.equals(o_id)) {
                    iterator1.remove();
                }
            }
        }
        Iterator<ViewPointTradeBean> dataIterator = dataList.iterator();
        while (dataIterator.hasNext()) {
            ViewPointTradeBean next = dataIterator.next();
            if (next != null && next.o_id != null && next.o_id.equals(o_id)) {
                dataIterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 置顶
     *
     * @param id
     */
    public void setTop(String id) {
        Iterator<String> iterator = pointListMap.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();

            List<ViewPointTradeBean> tradeBeanList = pointListMap.get(next);
            Iterator<ViewPointTradeBean> iterator1 = tradeBeanList.iterator();
            while (iterator1.hasNext()) {
                ViewPointTradeBean next1 = iterator1.next();
                if (next1.o_id != null && next1.o_id.equals(id)) {
                    if ("1".equals(next1.is_top)) {
                        next1.setIs_top("0");
                    } else {
                        next1.setIs_top("1");
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 退出或者登录之后 关注界面所发生的改变
     */
    public void loginAccount() {
        List<ViewPointTradeBean> followList = pointListMap.get("follow");
        if (followList != null) {//已经初始化过了
            //同步关注信息
            pointListMap.remove("follow");
            requestNavigationSwitch(2, 1);
        }
    }

    public void exitAccount() {
        List<ViewPointTradeBean> followList = pointListMap.get("follow");
        if (followList != null) {//已经初始化过了
            followList.clear();
            if (navigationTabClickPosition == 2) {
                replaceDataAndNotifyDataSetChanged(followList, 0);
            } else {
                comparisonData(followList, 0);
                pointListMap.put("follow", followList);
            }
        }
    }
}
