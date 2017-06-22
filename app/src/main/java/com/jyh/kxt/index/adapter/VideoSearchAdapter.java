package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.TextViewCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;

import java.util.BitSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:搜索视听Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class VideoSearchAdapter extends BaseListAdapter<VideoListJson> {

    private Context context;
    private String searchKey;

    public VideoSearchAdapter(Context context, List<VideoListJson> dataList) {
        super(dataList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_video, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VideoListJson videoBean = dataList.get(position);
        Glide.with(context).load(HttpConstant.IMG_URL + videoBean.getPicture()).error(R.mipmap.icon_def_news).placeholder(R.mipmap
                .icon_def_news).into(viewHolder.ivPhoto);
        setContent(viewHolder, videoBean);

        try {
            viewHolder.tvTime.setText(DateUtils.transformTime(Long.parseLong(videoBean.getCreate_time()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.tvTime.setText("00:00");
        }

        viewHolder.tvPlayCount.setText(videoBean.getNum_play());

        return convertView;
    }

    private void setContent(ViewHolder viewHolder, VideoListJson videoBean) {
        String content = videoBean.getTitle();
        if (RegexValidateUtil.isEmpty(searchKey)) {
        } else {
            if (content.contains(searchKey)) {
                int searchKeyIndex = content.indexOf(searchKey);
                String before = content.substring(0, searchKeyIndex);
                String end = content.substring(searchKeyIndex + searchKey.length());

                String defalutColor = "";
                String keyColor = "";

                switch (ThemeUtil.getAlertTheme(context)) {
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                        defalutColor = "#909090";
                        keyColor = "#115E91";
                        break;
                    case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                        defalutColor = "#2E3239";
                        keyColor = "#009AFF";
                        break;
                }

                content = "<font color='" + defalutColor + "'>" + before + "</font><font color='" + keyColor + "'>" + searchKey +
                        "</font><font " +
                        "color='" + defalutColor +
                        "'>" + end + "</font>";
            }
        }
        viewHolder.tvTitle.setText(Html.fromHtml(content));

        viewHolder.ivPlay.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.icon_video_play_big));

        viewHolder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color3));
        int paddingVal = SystemUtil.dp2px(context, 4);
        viewHolder.tvTime.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(viewHolder.tvTime, R.mipmap.icon_video_time, 0, 0, 0);

        viewHolder.tvPlayCount.setTextColor(ContextCompat.getColor(context, R.color.font_color3));
        paddingVal = SystemUtil.dp2px(context, 4);
        viewHolder.tvPlayCount.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(viewHolder.tvPlayCount, R.mipmap.icon_video_play_small, 0, 0, 0);
    }

    public void setData(List<VideoListJson> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(List<VideoListJson> list) {
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public List<VideoListJson> getData() {
        return dataList;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_playCount) TextView tvPlayCount;
        @BindView(R.id.iv_play) ImageView ivPlay;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
