package com.jyh.kxt.trading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.trading.json.ViewPointBean;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.widget.listview.PinnedSectionListView;
import com.library.widget.tablayout.NavigationTabLayout;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.HashMap;
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

    private int viewTypeCount = 3;
    private int navigationTabClickPosition = 0;

    private List<ViewPointTradeBean> dataList;
    private HashMap<String, List<ViewPointTradeBean>> pointListMap = new HashMap<>();

    private TradeHandlerUtil mTradeHandlerUtil;
    private SimplePopupWindow functionPopupWindow;

    public ViewpointAdapter(Context mContext, List<ViewPointTradeBean> viewPointTradeBeanList, String typeMap) {

        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);

        dataList = new ArrayList<>(viewPointTradeBeanList);
        pointListMap.put(typeMap, viewPointTradeBeanList);

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

        ViewPointTradeBean viewPointTradeBean = dataList.get(position);
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

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, ViewPointDetailActivity.class));
                        }
                    });
                    break;
                case 2:
                    convertView = mInflater.inflate(com.library.R.layout.view_point_nodata, parent, false);
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
                break;
            case 1:
                viewHolder1.setData(viewPointTradeBean);

                viewHolder1.tvContent.convertToGif(viewPointTradeBean.content);
                viewHolder1.tvNickName.setText(viewPointTradeBean.author_name);

                CharSequence formatCreateTime = DateFormat.format("MM-dd HH:mm", viewPointTradeBean.time * 1000);
                viewHolder1.tvTime.setText(formatCreateTime.toString());

                viewHolder1.setAuthorImage(viewPointTradeBean.author_img);

                viewHolder1.tvZanView.setText(viewPointTradeBean.num_good + "");
                viewHolder1.tvPinLunView.setText(viewPointTradeBean.num_commit + "");
                viewHolder1.initTradeHandler();

//                viewHolder1.gridViewNotifyDataSetChanged(strings);
                break;
            case 2:
                if (navigationTabClickPosition == 2) {
                    boolean loginEd = LoginUtils.isLogined(mContext);
                    if (!loginEd) {
                        viewHolder2.tvNoDataText.setText("登录后查看关注?");
                        viewHolder2.tvNoDataText.setTextColor(ContextCompat.getColor(mContext, R.color.blue1));
                        viewHolder2.tvNoDataText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
                            }
                        });
                    } else {
                        viewHolder2.tvNoDataText.setText("暂无关注数据喔");
                    }
                }
                break;
        }


        return convertView;
    }


    private String requestNavigationType = null;

    class ViewHolder0 {
        @BindView(R.id.ntl_title_view) NavigationTabLayout navigationTabLayout;

        ViewHolder0(View contentView) {
            ButterKnife.bind(this, contentView);
        }
    }

    class ViewHolder2 {
        @BindView(R.id.nodata_img) ImageView ivNoDataImage;
        @BindView(R.id.nodata_text) TextView tvNoDataText;

        ViewHolder2(View contentView) {
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

        @BindView(R.id.view_point_zan_tv) TextView tvZanView;
        @BindView(R.id.view_point_pl_tv) TextView tvPinLunView;
        @BindView(R.id.view_point_fx_tv) TextView tvShareView;

        private ViewPointTradeBean viewPointTradeBean;

        @OnClick({R.id.view_point_zan_layout, R.id.view_point_pl_layout, R.id.view_point_fx_layout})
        public void itemNavFunction(View view) {
            switch (view.getId()) {
                case R.id.view_point_zan_layout:
                    if (viewPointTradeBean.isFavour) {
                        ToastView.makeText(mContext,"您已经赞过了");
                    } else {
                        mTradeHandlerUtil.saveState(mContext, viewPointTradeBean.o_id, 1);
                        viewPointTradeBean.isFavour = true;
                        initTradeHandler();

                    }
                    break;
                case R.id.view_point_pl_layout:

                    break;
                case R.id.view_point_fx_layout:
                    functionPopupWindow = new SimplePopupWindow((Activity) mContext);

                    functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {
                        @Override
                        public void onCreateView(View popupView) {

                        }

                        @Override
                        public void onDismiss() {

                        }
                    });
                    functionPopupWindow.show(R.layout.pop_point_share);
                    break;
            }
        }


        public void setData(ViewPointTradeBean viewPointTradeBean) {
            this.viewPointTradeBean = viewPointTradeBean;
        }

        ViewHolder1(View contentView) {
            ButterKnife.bind(this, contentView);

            initGridView();

            /**
             * 初始化右上角的点击事件
             */
            mFunctionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    functionPopupWindow = new SimplePopupWindow((Activity) mContext);
                    functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {
                        View.OnClickListener functionListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                switch (view.getId()) {
                                    case R.id.point_function_sc:
                                        ToastView.makeText(mContext, "收藏成功");
                                        break;
                                    case R.id.point_function_gz:
                                        break;
                                    case R.id.point_function_jb:

                                        break;
                                    case R.id.point_function_qx:
                                        functionPopupWindow.dismiss();
                                        break;
                                }
                            }
                        };

                        @Override
                        public void onCreateView(View popupView) {
                            popupView.findViewById(R.id.point_function_sc).setOnClickListener(functionListener);
                            popupView.findViewById(R.id.point_function_gz).setOnClickListener(functionListener);
                            popupView.findViewById(R.id.point_function_jb).setOnClickListener(functionListener);
                            popupView.findViewById(R.id.point_function_qx).setOnClickListener(functionListener);
                        }

                        @Override
                        public void onDismiss() {

                        }
                    });
                    functionPopupWindow.show(R.layout.pop_point_function);
                }
            });
        }

        void initTradeHandler() {
            if (viewPointTradeBean.isFavour) {
                Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_zan2);
                tvZanView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            } else {
                Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_zan1);
                tvZanView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            }
        }

        void setAuthorImage(String authorImageUrl) {
            Glide.with(mContext)
                    .load(authorImageUrl)
                    .asBitmap()
                    .override(80, 80).into(
                    new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                glideAnimation) {
                            rivUserAvatar.setImageBitmap(resource);
                        }
                    });
        }

        /**
         * 初始化GridView 控件
         */
        void initGridView() {
            gridPictureLayout.setMotionEventSplittingEnabled(false);
            gridPictureLayout.setNumColumns(3);
            gridPictureLayout.setBackgroundColor(Color.TRANSPARENT);
            gridPictureLayout.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridPictureLayout.setCacheColorHint(0);
            gridPictureLayout.setSelector(new ColorDrawable(Color.TRANSPARENT));
            gridPictureLayout.setGravity(Gravity.CENTER);
            gridPictureLayout.setVerticalScrollBarEnabled(false);
        }

        void gridViewNotifyDataSetChanged(List<String> gridList) {
            BaseListAdapter<String> pictureAdapter = new BaseListAdapter<String>(gridList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder mViewHolder;

                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.view_viewpoint_item_picture, parent, false);
                        mViewHolder = new ViewHolder();
                        convertView.setTag(mViewHolder);

                        mViewHolder.mPictureView = (ImageView) convertView.findViewById(R.id.item_picture);
                    } else {
                        mViewHolder = (ViewHolder) convertView.getTag();
                    }
                    mViewHolder.mPictureView.setBackgroundColor(Color.BLUE);
                    return convertView;
                }

                class ViewHolder {
                    ImageView mPictureView;
                }
            };
            gridPictureLayout.setAdapter(pictureAdapter);
        }


    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        ViewPointTradeBean viewPointTradeBean = dataList.get(position);
        return viewPointTradeBean.getItemViewType();
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }

    /**
     * 刷新Adapter替换 List 数组
     *
     * @param tradeList
     * @param fromSource
     */
    private void replaceDataAndNotifyDataSetChanged(List<ViewPointTradeBean> tradeList, int fromSource) {
        dataList.clear();

        if (tradeList == null) {
            tradeList = new ArrayList<>();
        }

        if (tradeList.size() == 0) {
            ViewPointTradeBean viewPointTradeBean = new ViewPointTradeBean();
            viewPointTradeBean.setItemViewType(2);
            tradeList.add(viewPointTradeBean);
        }

        if (fromSource == 0) {
            ViewPointTradeBean viewPointTradeBean = new ViewPointTradeBean();
            viewPointTradeBean.setItemViewType(0);
            tradeList.add(0, viewPointTradeBean);
        }
        dataList.addAll(tradeList);

        //对 List 进行赞的遍历
        TradeHandlerUtil.getInstance().listCheckState(dataList);

        notifyDataSetChanged();
    }

    @Override
    public void onTabSelect(int position, int clickId) {
        navigationTabClickPosition = position;
        requestNavigationSwitch(position);
    }

    /**
     * 点击导航栏Tab 进行数据切换
     *
     * @param position
     */
    private void requestNavigationSwitch(int position) {
        switch (position) {
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

        if (position == 2) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            if (userInfo == null) {
                if (viewPointTradeBeen == null) {
                    ArrayList<ViewPointTradeBean> tradeList = new ArrayList<>();

                    replaceDataAndNotifyDataSetChanged(tradeList, 0);
                }
                return;
            } else {
                uId = userInfo.getUid();
            }
        }

        if (viewPointTradeBeen == null) {
            //请求网络显示转圈
            IBaseView iBaseView = (IBaseView) mContext;

            VolleyRequest mVolleyRequest = new VolleyRequest(iBaseView.getContext(), iBaseView.getQueue());
            mVolleyRequest.setTag(getClass().getName());

            JSONObject mainParam = mVolleyRequest.getJsonParam();
            mainParam.put("type", requestNavigationType);
            if (uId != null) {
                mainParam.put("uid", uId);
            }

            mVolleyRequest.doGet(HttpConstant.TRADE_MAIN, mainParam, new HttpListener<String>() {
                @Override
                protected void onResponse(String manJson) {
                    ViewPointBean viewPointBean = JSONObject.parseObject(manJson, ViewPointBean.class);

                    List<ViewPointTradeBean> tradeList = viewPointBean.getTrade();
                    pointListMap.put(requestNavigationType, tradeList);
                    replaceDataAndNotifyDataSetChanged(tradeList, 0);
                }
            });
        } else {
            replaceDataAndNotifyDataSetChanged(viewPointTradeBeen, 1);
        }
    }

}
