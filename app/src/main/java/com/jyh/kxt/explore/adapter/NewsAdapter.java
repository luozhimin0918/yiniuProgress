package com.jyh.kxt.explore.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.VarConstant;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class NewsAdapter extends BaseListAdapter<AuthorNewsJson> {

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

    public NewsAdapter(Context context, List<AuthorNewsJson> dataList) {
        super(dataList);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_author);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv = (TextView) convertView.findViewById(R.id.tv1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isShowTitle)
            if (position == 0) holder.tv.setVisibility(View.VISIBLE);
            else holder.tv.setVisibility(View.GONE);

        final AuthorNewsJson news = dataList.get(position);

        setContentColor(holder, news);

        String picture = news.getPicture();

        Glide.with(mContext).load(picture)
                .placeholder(R.mipmap.icon_def_news)
                .override(100, 100)
                .error(R.mipmap.icon_def_news)
                .into(holder.ivPhoto);

        String author = news.getName();
        if (author != null)
            author = "文/" + author;
        holder.tvAuthor.setText(author);
        try {
            holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(news.getCreate_time()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvTime.setText("00:00");
        }

        return convertView;
    }

    /**
     * 设置内容字体颜色
     *
     * @param holder
     * @param newsJson
     */
    private void setContentColor(ViewHolder holder, AuthorNewsJson newsJson) {
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
        if (RegexValidateUtil.isEmpty(searchKey)) {
            if (browered) {
                holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
            } else {
                holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
            }
        } else {
            if (content.contains(searchKey)) {
                int searchKeyIndex = content.indexOf(searchKey);
                String before = content.substring(0, searchKeyIndex);
                String end = content.substring(searchKeyIndex + searchKey.length());

                String defalutColor = "";
                String keyColor = "";
                String browerColor = "";

                switch (ThemeUtil.getAlertTheme(mContext)) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        defalutColor = "#909090";
                        keyColor = "#136AA4";
                        browerColor = "#4D4D4D";
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        defalutColor = "#2E3239";
                        keyColor = "#EA492A";
                        browerColor = "#A1ABB2";
                        break;
                }

                String textColor;
                if (browered) {
                    textColor = browerColor;
                } else
                    textColor = defalutColor;

                content = "<font color='" + textColor + "'>" + before + "</font><font color='" + keyColor + "'>" + searchKey +
                        "</font><font " +
                        "color='" + textColor +
                        "'>" + end + "</font>";
            }
        }
        holder.tvTitle.setText(Html.fromHtml(content));
    }

    public void addData(List<AuthorNewsJson> newsJsons) {
        dataList.addAll(newsJsons);
        notifyDataSetChanged();
    }

    public void setData(List<AuthorNewsJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public String getLastId() {
        return dataList.get(dataList.size() - 1).getO_id();
    }

    public List<AuthorNewsJson> getData() {
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

    class ViewHolder {
        public TextView tv, tvTitle, tvAuthor, tvTime;
        public ImageView ivPhoto;
    }
}
