package com.jyh.kxt.trading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.annotation.OnTabSelectListener;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.ArticleContentPresenter;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;
import com.library.widget.flowlayout.OptionFlowLayout;
import com.library.widget.listview.PinnedSectionListView;
import com.library.widget.tablayout.NavigationTabLayout;
import com.library.widget.window.ToastView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:作者详情Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/2.
 */

public class AuthorAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter, NavigationTabLayout
        .OnTabSelectListener {

    public static final int TYPE_VIEWPOINT = 1;
    public static final int TYPE_ARTICLE = 2;

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_NODATA = 3;

    private Context mContext;
    private List<ViewPointTradeBean> viewpoints;
    private List<AuthorNewsJson> news;
    private int type = 0;
    private TradeHandlerUtil mTradeHandlerUtil;
    private SimplePopupWindow functionPopupWindow;
    private ArticleContentPresenter articleContentPresenter;

    public AuthorAdapter(Context context, List<ViewPointTradeBean> viewpoints, List<AuthorNewsJson> news, int type) {
        this.mContext = context;
        this.viewpoints = viewpoints;
        TradeHandlerUtil.getInstance().listCheckState(mContext,viewpoints);
        articleContentPresenter = new ArticleContentPresenter(mContext);
        this.news = news;
        this.type = type;
        mTradeHandlerUtil = TradeHandlerUtil.getInstance();
    }

    @Override
    public int getCount() {
        switch (type) {
            case TYPE_ARTICLE:
                return news == null ? 2 : news.size() + 1;
            case TYPE_VIEWPOINT:
                return viewpoints == null ? 2 : viewpoints.size() + 1;
        }
        return 2;
    }

    @Override
    public Object getItem(int position) {
        try {
            switch (type) {
                case TYPE_ARTICLE:
                    return news == null ? null : news.get(position - 1);
                case TYPE_VIEWPOINT:
                    return viewpoints == null ? null : viewpoints.get(position - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        ArticleViewHolder articleViewHolder = null;
        TitleViewHolder titleViewHolder = null;
        ViewpointViewHolder viewpointViewHolder = null;
        NoDataViewHolder noDataViewHolder = null;
        if (convertView == null) {
            switch (viewType) {
                case TYPE_TITLE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_viewpoint_function_nav2, parent, false);
                    titleViewHolder = new TitleViewHolder(convertView);
                    convertView.setTag(titleViewHolder);
                    break;
                case TYPE_ARTICLE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
                    articleViewHolder = new ArticleViewHolder(convertView);
                    convertView.setTag(articleViewHolder);
                    break;
                case TYPE_VIEWPOINT:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_viewpoint_item1, parent, false);
                    viewpointViewHolder = new ViewpointViewHolder(convertView);
                    convertView.setTag(viewpointViewHolder);

                    break;
                case TYPE_NODATA:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_nodata, parent, false);
                    noDataViewHolder = new NoDataViewHolder(convertView);
                    convertView.setTag(noDataViewHolder);
                    break;
            }

        } else {
            switch (viewType) {
                case TYPE_TITLE:
                    titleViewHolder = (TitleViewHolder) convertView.getTag();
                    break;
                case TYPE_ARTICLE:
                    articleViewHolder = (ArticleViewHolder) convertView.getTag();
                    break;
                case TYPE_VIEWPOINT:
                    viewpointViewHolder = (ViewpointViewHolder) convertView.getTag();
                    break;
                case TYPE_NODATA:
                    noDataViewHolder = (NoDataViewHolder) convertView.getTag();
            }
        }
        try {
            int location = position - 1;
            switch (viewType) {
                case TYPE_TITLE:

                    setTitleTheme(titleViewHolder.ntlTitleView);
                    titleViewHolder.ntlTitleView.setData(R.array.nav_trading);
                    titleViewHolder.ntlTitleView.setAcceptTouchRect(true);
                    titleViewHolder.ntlTitleView.setIndicatorStyle(NavigationTabLayout.STYLE_TRIANGLE);
//                    titleViewHolder.line.setBackgroundColor(ContextCompat.getColor(mContext,R.color.line_color7));

                    if (type == TYPE_VIEWPOINT) {
                        titleViewHolder.ntlTitleView.setCurrentTab(0);
                    } else {
                        titleViewHolder.ntlTitleView.setCurrentTab(1);
                    }
                    titleViewHolder.ntlTitleView.setOnTabSelectListener(this);

                    break;
                case TYPE_ARTICLE:
                    if (type == TYPE_ARTICLE) {
                        AuthorNewsJson news = this.news.get(location);
                        articleViewHolder.tv1.setVisibility(View.GONE);
                        setArticleTheme(articleViewHolder, news);
                        String picture = news.getPicture();
                        Glide.with(mContext).load(picture)
                                .placeholder(R.mipmap.icon_def_news)
                                .override(100, 100)
                                .error(R.mipmap.icon_def_news)
                                .into(articleViewHolder.ivPhoto);
                        String author;
                        String type = news.getType();
                        if ("ad".equals(type)) {
                            author = "广告";
                        } else {
                            author = news.getName();
                            if (!RegexValidateUtil.isEmpty(author)) {
                                author = "文/" + author;
                            } else {
                                author = "";
                            }
                        }
                        articleViewHolder.tvAuthor.setText(author);
                        try {
                            articleViewHolder.tvTime.setText(DateUtils.transformTime(Long.parseLong(news.getCreate_time()) * 1000));
                        } catch (Exception e) {
                            e.printStackTrace();
                            articleViewHolder.tvTime.setText("00:00");
                        }

                    }
                    break;
                case TYPE_VIEWPOINT:
                    if (type == TYPE_VIEWPOINT) {

                        final ViewPointTradeBean viewPointTradeBean = viewpoints.get(location);
                        viewpointViewHolder.setData(viewPointTradeBean);
                        viewpointViewHolder.setItemViewColor(convertView);

                        viewpointViewHolder.tvContent.convertToGif(viewPointTradeBean.content);
                        viewpointViewHolder.tvNickName.setText(viewPointTradeBean.author_name);

                        CharSequence formatCreateTime = DateFormat.format("MM-dd HH:mm", viewPointTradeBean.time * 1000);
                        viewpointViewHolder.tvTime.setText(formatCreateTime.toString());

                        viewpointViewHolder.tvZanView.setText(String.valueOf(viewPointTradeBean.num_good));
                        viewpointViewHolder.tvPinLunView.setText(String.valueOf(viewPointTradeBean.num_comment));

                        articleContentPresenter.setAuthorImage(viewpointViewHolder.rivUserAvatar, viewPointTradeBean.author_img);
                        articleContentPresenter.initTradeHandler(viewpointViewHolder.tvZanView, viewPointTradeBean.isFavour);

                        articleContentPresenter.initTransmitView(
                                viewpointViewHolder.rlTransmitLayout,
                                viewpointViewHolder.tvTransmitView,
                                viewPointTradeBean.forward);

                        articleContentPresenter.setPictureAdapter(viewpointViewHolder.gridPictureLayout, viewPointTradeBean.picture);
                    }
                    break;
                case TYPE_NODATA:
                    noDataViewHolder.rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));
                    noDataViewHolder.idVolleyLoadNodataImg.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_not_data));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void setTop(EmoticonSimpleTextView tvContent, int position, ViewPointTradeBean bean) {

        String content = bean.content;
        if (position == 0 && "1".equals(bean.is_top)) {
            //置顶
            Drawable d = ContextCompat.getDrawable(mContext, R.mipmap.icon_trading_top);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());//设置图片大小

            SpannableStringBuilder spannableBuilder = new SpannableStringBuilder("1" + content);
            spannableBuilder.setSpan(new ImageSpan(d, ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvContent.convertToGif(spannableBuilder);
        } else {
            tvContent.convertToGif(content);
        }

    }

    private void setTitleTheme(NavigationTabLayout ntlTitleView) {
        ntlTitleView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.navigationTablayout_bgcolor));
        ntlTitleView.setTextSelectColor(ContextCompat.getColor(mContext, R.color.font_color4));
        ntlTitleView.setTextUnselectColor(ContextCompat.getColor(mContext, R.color.font_color6));
        ntlTitleView.setIndicatorColor(ContextCompat.getColor(mContext, R.color.theme1));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        } else {

            if (type == TYPE_VIEWPOINT) {
                if (viewpoints == null || viewpoints.size() == 0) {
                    return TYPE_NODATA;
                }
            } else {
                if (news == null || news.size() == 0) {
                    Log.i("nodata", "nodata");
                    return TYPE_NODATA;
                }
            }


            Object item = getItem(position);
            if (item instanceof ViewPointTradeBean) {
                return TYPE_VIEWPOINT;
            } else {
                return TYPE_ARTICLE;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_TITLE;
    }

    public void setViewpointData(List<ViewPointTradeBean> viewpointData) {
        if (viewpoints == null) {
            viewpoints = viewpointData;
        } else {
            viewpoints.clear();
            viewpoints.addAll(viewpointData);
        }

        TradeHandlerUtil.getInstance().listCheckState(viewpointData);
        notifyDataSetChanged();
    }

    public void setArticleData(List<AuthorNewsJson> articleData) {
        if (articleData == null || articleData.size() == 0) {
            news = null;
            notifyDataSetChanged();
            return;
        }
        if (news == null) {
            news = articleData;
        } else {
            news.clear();
            news.addAll(articleData);
        }
        notifyDataSetChanged();
    }

    public void addViewPointData(List<ViewPointTradeBean> viewpointData) {
        viewpoints.addAll(viewpointData);
        TradeHandlerUtil.getInstance().listCheckState(viewpoints);
        notifyDataSetChanged();
    }

    public void addArticleData(List<AuthorNewsJson> news) {
        this.news.addAll(news);
        notifyDataSetChanged();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<AuthorNewsJson> getAuthorData() {
        return news;
    }

    public List<ViewPointTradeBean> getViewpointData() {
        return viewpoints;
    }

    public List getData() {
        if (type == TYPE_VIEWPOINT) {
            return getViewpointData();
        } else {
            return getAuthorData();
        }
    }

    /**
     * 设置内容字体颜色
     *
     * @param holder
     * @param newsJson
     */
    private void setArticleTheme(ArticleViewHolder holder, AuthorNewsJson newsJson) {
        String content = newsJson.getTitle();

        NewsJson news = new NewsJson();
        news.setTitle(newsJson.getTitle());
        news.setType(newsJson.getType());
        news.setPicture(newsJson.getPicture());
        news.setHref(newsJson.getHref());
        news.setO_id(newsJson.getO_id());
        news.setAuthor(newsJson.getName());
        news.setDatetime(newsJson.getCreate_time());
        news.setO_class(newsJson.getO_class());
        news.setO_action(newsJson.getO_action());
        news.setDataType(VarConstant.DB_TYPE_BROWER);

        boolean browered = BrowerHistoryUtils.isBrowered(mContext, news);
        if (browered) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        }
        holder.tvTitle.setText(Html.fromHtml(content));
        holder.tvAuthor.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
    }

    @Override
    public void onTabSelect(int position, int clickId) {
        if (position == 0) {
            type = TYPE_VIEWPOINT;
        } else {
            type = TYPE_ARTICLE;
        }
        if (onTabSelLinstener != null) {
            onTabSelLinstener.tabSel(position);
        }
    }

    static class ArticleViewHolder {
        @BindView(R.id.tv1) TextView tv1;
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_author) TextView tvAuthor;
        @BindView(R.id.tv_time) TextView tvTime;

        ArticleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class TitleViewHolder {
        @BindView(R.id.ntl_title_view) NavigationTabLayout ntlTitleView;
//        @BindView(R.id.v_line) View line;

        TitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewpointViewHolder {
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

        @BindView(R.id.view_point_zan_tv) TextView tvZanView;
        @BindView(R.id.view_point_pl_tv) TextView tvPinLunView;
        @BindView(R.id.view_point_fx_tv) ImageView tvShareView;

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

            int paddingVal= SystemUtil.dp2px(mContext,8);
            tvZanView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
            tvZanView.setTextColor(ContextCompat.getColor(mContext,R.color.font_color9));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvZanView, R.mipmap
                    .icon_point_zan1, 0, 0, 0);
            tvPinLunView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
            tvPinLunView.setTextColor(ContextCompat.getColor(mContext,R.color.font_color9));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvPinLunView, R.mipmap
                    .icon_point_pl, 0, 0, 0);
            tvShareView.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_point_fx));
        }

        @OnClick({R.id.view_point_zan_layout, R.id.view_point_pl_layout, R.id.view_point_fx_layout})
        public void itemNavFunction(View view) {
            switch (view.getId()) {
                case R.id.view_point_zan_layout:
                    if (viewPointTradeBean.isFavour) {
                        ToastView.makeText(mContext, "您已经赞过了");
                    } else {
                        boolean isSaveSuccess = mTradeHandlerUtil.saveState(mContext, viewPointTradeBean, 1, true);
                        if (isSaveSuccess) {
                            viewPointTradeBean.isFavour = true;
                            articleContentPresenter.initTradeHandler(tvZanView, true);

                            viewPointTradeBean.num_good += 1;
                            tvZanView.setText(String.valueOf(viewPointTradeBean.num_good));
                        }
                    }
                    break;
                case R.id.view_point_pl_layout:

                    break;
                case R.id.view_point_fx_layout:
                    functionPopupWindow = new SimplePopupWindow((Activity) mContext);

                    functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {
                        @Override
                        public void onCreateView(View popupView) {
                            articleContentPresenter.shareToPlatform(popupView, viewPointTradeBean.shareDict);
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

        ViewpointViewHolder(View contentView) {
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

    static class NoDataViewHolder {
        @BindView(R.id.ll_rootView) View rootView;
        @BindView(R.id.id_volley_load_nodata_img) ImageView idVolleyLoadNodataImg;
        @BindView(R.id.id_volley_load_nodata) TextView idVolleyLoadNodata;

        NoDataViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private OnTabSelectListener onTabSelLinstener;

    public OnTabSelectListener getOnTabSelLinstener() {
        return onTabSelLinstener;
    }

    public void setOnTabSelLinstener(OnTabSelectListener onTabSelLinstener) {
        this.onTabSelLinstener = onTabSelLinstener;
    }
}

