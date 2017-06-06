package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:要闻收藏删除Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/11.
 */

public class AttentionArticleAdapter extends BaseListAdapter<NewsJson> {

    private Context context;
    private boolean isEdit = false;
    private Set<String> delIds = new HashSet<>();

    public AttentionArticleAdapter(List<NewsJson> dataList, Context context) {
        super(dataList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_collect_news, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isEdit) {
            holder.flDel.setVisibility(View.VISIBLE);
        } else {
            holder.flDel.setVisibility(View.GONE);
        }

        final NewsJson news = dataList.get(position);
        setTheme(holder, news);
        holder.ivDel.setSelected(news.isSel());

        Glide.with(context).load(news.getPicture()).placeholder(R.mipmap.icon_def_news).error(R.mipmap.icon_def_news)
                .into
                        (holder.ivPhoto);

        holder.tvTitle.setText(news.getTitle());
        String author = news.getAuthor();
        if (author != null)
            author = "文/" + author;
        holder.tvAuthor.setText(author);
        try {
            holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(news.getDatetime()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvTime.setText("00:00");
        }

        final ViewHolder finalHolder = holder;
        holder.flDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加或移除选中状态
                if (finalHolder.ivDel.isSelected()) {
                    finalHolder.ivDel.setSelected(false);
                    try {
                        delIds.remove(news.getO_id());
                        news.setSel(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finalHolder.ivDel.setSelected(true);
                    try {
                        delIds.add(news.getO_id());
                        news.setSel(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (observerData != null)
                    observerData.delItem(delIds.size());
            }
        });

        return convertView;
    }

    private void setTheme(ViewHolder holder, NewsJson news) {
        holder.ivDel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.sel_collect_item));
        holder.tvAuthor.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color5));

        if (BrowerHistoryUtils.isBrowered(context, news)) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color5));
        }

    }

    public void setData(List<NewsJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<NewsJson> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public Set<String> getDelIds() {
        return delIds;
    }

    public void setDelIds(Set<String> delIds) {
        this.delIds = delIds;
    }

    private DelNumListener observerData;

    public void setSelListener(DelNumListener selListener) {
        this.observerData = selListener;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    /**
     * 删除
     *
     * @param ids
     */
    public void removeById(String ids) {
        if (RegexValidateUtil.isEmpty(ids))
            return;
        List<String> list = Arrays.asList(ids.split(","));

        Iterator<NewsJson> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            NewsJson next = iterator.next();
            for (String id : list) {
                if (id.equals(next.getO_id())) {
                    iterator.remove();
                }
            }
        }

        notifyDataSetChanged();
    }

    public List<NewsJson> getData() {
        return dataList;
    }

    static class ViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_author) TextView tvAuthor;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.iv_del) ImageView ivDel;
        @BindView(R.id.fl_del) FrameLayout flDel;
        @BindView(R.id.rl_contact) RelativeLayout rlContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
