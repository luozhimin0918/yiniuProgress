package com.jyh.kxt.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoDetailVideoBean;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.widget.AdvertLayout;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.json.AuthorBean;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.trading.ui.AuthorListActivity;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:要闻Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class NewsAdapter extends BaseListAdapter<NewsJson> {

    private final int TYPE_DEF = 0;
    private final int TYPE_NEW = 1;

    private Context mContext;
    private boolean isShowTitle = false;
    private String searchKey = "";
    private boolean isSplice = true;//是否需要拼接图片地址

    public boolean isShowTitle() {
        return isShowTitle;
    }

    public void setShowTitle(boolean showTitle) {
        isShowTitle = showTitle;
    }

    public NewsAdapter(Context context, List<NewsJson> dataList) {
        super(dataList);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        ViewHolder2 holder2=null;
        final int itemViewType = getItemViewType(position);
        if (convertView == null) {
            if (itemViewType == TYPE_DEF) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
                holder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_author);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv = (TextView) convertView.findViewById(R.id.tv1);
                convertView.setTag(holder);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news2, parent, false);
                holder2=new ViewHolder2(convertView);
                convertView.setTag(holder2);
            }
        } else {
            if (itemViewType == TYPE_DEF) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder2= (ViewHolder2) convertView.getTag();
            }
        }
        final NewsJson news = dataList.get(position);
        if (itemViewType == TYPE_DEF) {
            if (isShowTitle) {
                if (position == 0) {
                    holder.tv.setVisibility(View.VISIBLE);
                } else {
                    holder.tv.setVisibility(View.GONE);
                }
            }


            setContentColor(holder, news);

            String picture;
            String picture1 = news.getPicture();
            if (isSplice) {
                picture = HttpConstant.IMG_URL + picture1;
            } else {
                picture = picture1;
            }

            Glide.with(mContext).load(picture)
                    .placeholder(R.mipmap.icon_def_news)
                    .override(100, 100)
                    .error(R.mipmap.icon_def_news)
                    .into(holder.ivPhoto);

            String author;
            String type = news.getType();
            if ("ad".equals(type)) {
                author = "广告";
            } else {
                author = news.getAuthor();
                if (!RegexValidateUtil.isEmpty(author)) {
                    author = "文/" + author;
                } else {
                    author = "";
                }
            }


            holder.tvAuthor.setText(author);
            try {
                holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(news.getDatetime()) * 1000));
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvTime.setText("00:00");
            }

        } else {
            holder2.alAd.setAdvertData(news.getCate_name(),news.getAds().getAd(),news.getAds().getIcon());
            final String type = news.getType();
            holder2.tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if("video".equals(type)){
                        ((MainActivity)mContext).rbAudioVisual.performClick();
                    }else if("blog_writer".equals(type)){
                        Intent intent=new Intent(mContext, AuthorListActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            });
            LinearLayoutManager manager=new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder2.rvContent.setLayoutManager(manager);
            if("video".equals(type)){
                final List<VideoDetailVideoBean> list = JSON.parseArray(news.getList(), VideoDetailVideoBean.class);
                VideoAdapter adapter=new VideoAdapter(mContext, list);
                        holder2.rvContent.setAdapter(adapter);
                adapter.setClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view) {
                        Intent intent=new Intent(mContext, VideoDetailActivity.class);
                        intent.putExtra(IntentConstant.O_ID,list.get(position).getId());
                        mContext.startActivity(intent);
                    }
                });
            }else if("blog_writer".equals(type)){
                final List<AuthorBean> list = JSON.parseArray(news.getList(), AuthorBean.class);
                AuthorAdapter adapter=new AuthorAdapter(mContext, list);
                adapter.setClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view) {
                        Intent intent=new Intent(mContext, AuthorActivity.class);
                        intent.putExtra(IntentConstant.O_ID, list.get(position).getId());
                        mContext.startActivity(intent);
                    }
                });
                holder2.rvContent.setAdapter(adapter);
            }

            setTheme2(holder2);
        }
        return convertView;
    }

    private void setTheme2(ViewHolder2 holder) {
        holder.llRootView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.line_color2));
        holder.llContent.setBackgroundColor(ContextCompat.getColor(mContext,R.color.theme1));
        holder.tvMore.setTextColor(ContextCompat.getColor(mContext,R.color.font_color7));
        holder.rvContent.setBackgroundColor(ContextCompat.getColor(mContext,R.color.theme1));
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String type = dataList.get(position).getType();
        return "video".equals(type) || "blog_writer".equals(type) ? TYPE_NEW : TYPE_DEF;
    }

    /**
     * 设置内容字体颜色
     *
     * @param holder
     * @param news
     */
    private void setContentColor(ViewHolder holder, NewsJson news) {
        String content = news.getTitle();
        boolean browered = BrowerHistoryUtils.isBrowered(mContext, news);
        if (RegexValidateUtil.isEmpty(searchKey)) {
            if (browered) {
                holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
            } else {
                holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
            }
        } else {
            if (content.contains(searchKey) || content.contains(searchKey.toUpperCase())) {
                int searchKeyIndex = content.indexOf(searchKey);
                if (searchKeyIndex == -1) {
                    searchKeyIndex = content.indexOf(searchKey.toUpperCase());
                }
                String before = content.substring(0, searchKeyIndex);
                String end = content.substring(searchKeyIndex + searchKey.length());

                String defalutColor = "";
                String keyColor = "";
                String browerColor = "";

                switch (ThemeUtil.getAlertTheme(mContext)) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        defalutColor = "#909090";
                        keyColor = "#115E91";
                        browerColor = "#4D4D4D";
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        defalutColor = "#2E3239";
                        keyColor = "#009AFF";
                        browerColor = "#A1ABB2";
                        break;
                }

                String textColor;
                if (browered) {
                    textColor = browerColor;
                } else {
                    textColor = defalutColor;
                }

                content = "<font color='" + textColor + "'>" + before + "</font><font color='" + keyColor + "'>" +
                        searchKey +
                        "</font><font " +
                        "color='" + textColor +
                        "'>" + end + "</font>";
            }
        }
        holder.tvTitle.setText(Html.fromHtml(content));
        holder.tvAuthor.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
    }

    public void addData(List<NewsJson> newsJsons) {
        dataList.addAll(newsJsons);
        notifyDataSetChanged();
    }

    public void setData(List<NewsJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public String getLastId() {
        return dataList.get(dataList.size() - 1).getO_id();
    }

    public List<NewsJson> getData() {
        return dataList;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
        notifyDataSetChanged();
    }

    public void setIsSplice(boolean isSplice) {
        this.isSplice = isSplice;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView tv, tvTitle, tvAuthor, tvTime;
        public ImageView ivPhoto;
    }

    class ViewHolder2 {
        @BindView(R.id.ll_rootView) LinearLayout llRootView;
        @BindView(R.id.ll_content) LinearLayout llContent;
        @BindView(R.id.al_ad) AdvertLayout alAd;
        @BindView(R.id.tv_more) TextView tvMore;
        @BindView(R.id.rv_content) RecyclerView rvContent;

        ViewHolder2(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
