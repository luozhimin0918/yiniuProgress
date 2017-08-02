package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.custom.NavigationTabLayout;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.widget.ThumbView2;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.trading.json.ViewpointJson;
import com.library.base.http.VarConstant;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;
import com.library.widget.listview.PinnedSectionListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:作者详情Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/2.
 */

public class AuthorAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    public static final int TYPE_VIEWPOINT = 1;
    public static final int TYPE_ARTICLE = 2;

    public static final int TYPE_TITLE = 0;

    private Context mContext;
    private List<ViewpointJson> viewpoints;
    private List<AuthorNewsJson> news;
    private int type = 0;

    public AuthorAdapter(Context context, List<ViewpointJson> viewpoints, List<AuthorNewsJson> news, int type) {
        this.mContext = context;
        this.viewpoints = viewpoints;
        this.news = news;
        this.type = type;
    }

    @Override
    public int getCount() {
        switch (type) {
            case TYPE_ARTICLE:
                return news == null ? 1 : news.size() + 1;
            case TYPE_VIEWPOINT:
                return viewpoints == null ? 1 : viewpoints.size() + 1;
        }
        return 1;
    }

    @Override
    public Object getItem(int position) {
        try {
            switch (type) {
                case TYPE_ARTICLE:
                    return news == null ? null : news.get(position);
                case TYPE_VIEWPOINT:
                    return viewpoints == null ? null : viewpoints.get(position);
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
        if (convertView == null) {
            switch (viewType) {
                case TYPE_TITLE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_viewpoint_function_nav, parent, false);
                    titleViewHolder = new TitleViewHolder(convertView);
                    convertView.setTag(titleViewHolder);
                    break;
                case TYPE_ARTICLE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
                    articleViewHolder = new ArticleViewHolder(convertView);
                    convertView.setTag(articleViewHolder);
                    break;
                case TYPE_VIEWPOINT:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_viewpoint, parent, false);
                    viewpointViewHolder = new ViewpointViewHolder(convertView);
                    convertView.setTag(viewpointViewHolder);
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
            }
        }
        try {
            switch (viewType) {
                case TYPE_TITLE:
                    titleViewHolder.ntlTitleView.setData(R.array.nav_trading);
                    titleViewHolder.ntlTitleView.setIndicatorStyle(NavigationTabLayout.STYLE_TRIANGLE);

                    if (type == TYPE_VIEWPOINT) {
                        titleViewHolder.ntlTitleView.setCurrentTab(0);
                    } else {
                        titleViewHolder.ntlTitleView.setCurrentTab(1);
                    }
                    titleViewHolder.ntlTitleView.setOnTabSelectListener(new NavigationTabLayout.OnTabSelectListener() {
                        @Override
                        public void onTabSelect(int position, int clickId) {
                            if (position == 0) {
                                type = TYPE_VIEWPOINT;
                            } else {
                                type = TYPE_ARTICLE;
                            }
                            notifyDataSetChanged();
                        }
                    });

                    break;
                case TYPE_ARTICLE:
                    if (type == TYPE_ARTICLE) {
                        AuthorNewsJson news = this.news.get(position - 1);
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
                            if (!RegexValidateUtil.isEmpty(author))
                                author = "文/" + author;
                            else
                                author = "";
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
                        ViewpointJson viewpoint = viewpoints.get(position - 1);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        } else {
            Object item = getItem(position);
            if (item instanceof ViewpointJson) {
                return TYPE_VIEWPOINT;
            } else {
                return TYPE_ARTICLE;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_TITLE;
    }

    public void setViewpointData(List<ViewpointJson> viewpointData) {
        if (viewpoints == null) {
            viewpoints = viewpointData;
        } else {
            viewpoints.clear();
            viewpoints.addAll(viewpointData);
        }
        notifyDataSetChanged();
    }

    public void setArticleData(List<AuthorNewsJson> articleData) {
        if (news == null) {
            news = articleData;
        } else {
            news.clear();
            news.addAll(articleData);
        }
        notifyDataSetChanged();
    }

    public void addViewPointData(List<ViewpointJson> viewpointData) {
        viewpoints.clear();
        viewpoints.addAll(viewpointData);
        notifyDataSetChanged();
    }

    public void addArticleData(List<AuthorNewsJson> news) {
        this.news.clear();
        this.news.addAll(news);
        notifyDataSetChanged();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
//        if (RegexValidateUtil.isEmpty(searchKey)) {
        if (browered) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        }
//        } else {
//            if (content.contains(searchKey)) {
//                int searchKeyIndex = content.indexOf(searchKey);
//                String before = content.substring(0, searchKeyIndex);
//                String end = content.substring(searchKeyIndex + searchKey.length());
//
//                String defalutColor = "";
//                String keyColor = "";
//                String browerColor = "";
//
//                switch (ThemeUtil.getAlertTheme(mContext)) {
//                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
//                        defalutColor = "#909090";
//                        keyColor = "#136AA4";
//                        browerColor = "#4D4D4D";
//                        break;
//                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
//                        defalutColor = "#2E3239";
//                        keyColor = "#EA492A";
//                        browerColor = "#A1ABB2";
//                        break;
//                }
//
//                String textColor;
//                if (browered) {
//                    textColor = browerColor;
//                } else
//                    textColor = defalutColor;
//
//                content = "<font color='" + textColor + "'>" + before + "</font><font color='" + keyColor + "'>" + searchKey +
//                        "</font><font " +
//                        "color='" + textColor +
//                        "'>" + end + "</font>";
//            }
//        }
        holder.tvTitle.setText(Html.fromHtml(content));
        holder.tvAuthor.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
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

        TitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ViewpointViewHolder {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.iv_more) ImageView ivMore;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.iv_rootView) GridLayout ivRootView;
        @BindView(R.id.tv_thumb) ThumbView2 tvThumb;
        @BindView(R.id.ll_dianzan) LinearLayout llDianzan;
        @BindView(R.id.tv_pinglun) TextView tvPinglun;
        @BindView(R.id.ll_pinglun) LinearLayout llPinglun;
        @BindView(R.id.ll_share) LinearLayout llShare;

        ViewpointViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
