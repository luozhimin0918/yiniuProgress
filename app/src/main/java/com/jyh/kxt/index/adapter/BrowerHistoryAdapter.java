package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:浏览记录Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/4.
 */

public class BrowerHistoryAdapter extends BaseListAdapter implements FastInfoPinnedListView.PinnedSectionListAdapter {

    private static final int TYPE_TIME = 0;
    private static final int TYPE_NEWS = 1;

    private Context context;

    public BrowerHistoryAdapter(Context context, List dataList) {
        super(dataList);
        this.context = context;
        inspiritDateInfo(this.dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TimeHolder timeHolder = null;
        NewsHolder newsHolder = null;

        int itemViewType = getItemViewType(position);
        if (convertView == null) {
            switch (itemViewType) {
                case TYPE_TIME:
                    convertView = LayoutInflater.from(context).inflate(R.layout.layout_brower_time_bar, parent, false);
                    timeHolder = new TimeHolder(convertView);
                    convertView.setTag(timeHolder);
                    break;
                case TYPE_NEWS:
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
                    newsHolder = new NewsHolder(convertView);
                    convertView.setTag(newsHolder);
                    break;
            }
        } else {
            switch (itemViewType) {
                case TYPE_TIME:
                    timeHolder = (TimeHolder) convertView.getTag();
                    break;
                case TYPE_NEWS:
                    newsHolder = (NewsHolder) convertView.getTag();
                    break;
            }
        }

        switch (itemViewType) {
            case TYPE_TIME:
                timeHolder.tvTime.setText(dataList.get(position).toString());
                setTimeTheme(timeHolder);
                break;
            case TYPE_NEWS:
                NewsJson newsBean = (NewsJson) dataList.get(position);

                String picture = newsBean.getPicture();
                if (!picture.contains("http://")) {
                    picture = HttpConstant.IMG_URL + picture;
                }

                Glide.with(context).load(picture).error(R.mipmap.icon_def_news).placeholder(R.mipmap
                        .icon_def_news).into
                        (newsHolder.ivPhoto);

                newsHolder.tv1.setVisibility(View.GONE);
                newsHolder.tvTitle.setText(newsBean.getTitle());

                String author;
                String type = newsBean.getType();
                if ("ad".equals(type)) {
                    author = "广告";
                } else {
                    author = newsBean.getAuthor();
                    if (!RegexValidateUtil.isEmpty(author))
                        author = "文/" + author;
                    else
                        author = "";
                }

                newsHolder.tvAuthor.setText(author);

                String time = "00:00";
                try {
                    time = DateUtils.transformTime(Long.parseLong(newsBean.getDatetime()) * 1000, DateUtils.TYPE_MDHM);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newsHolder.tvTime.setText(time);

                setContentTheme(newsHolder);
                break;
        }

        return convertView;
    }

    private void setContentTheme(NewsHolder newsHolder) {
        newsHolder.tv1.setTextColor(ContextCompat.getColor(context, R.color.font_color60));
        int paddingVal = SystemUtil.dp2px(context, 2);
        newsHolder.tv1.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(newsHolder.tv1, R.mipmap.icon_video_line, 0, 0, 0);

        newsHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color5));
        newsHolder.tvAuthor.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        newsHolder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
    }

    private void setTimeTheme(TimeHolder timeHolder) {
        timeHolder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color3));
        timeHolder.vLine.setBackground(ContextCompat.getDrawable(context, R.color.line_color3));
    }

    @Override
    public int getItemViewType(int position) {
        Object itemObj = dataList.get(position);
        if (itemObj instanceof NewsJson) {
            return TYPE_NEWS;
        } else {
            return TYPE_TIME;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_TIME;
    }

    public void setData(List<NewsJson> data) {
        if (data == null) {
            dataList.clear();
        } else {
            dataList.clear();
            dataList.addAll(data);
            inspiritDateInfo(dataList);
        }
        notifyDataSetChanged();
    }

    public void addData(List<NewsJson> data) {
        inspiritDateInfo2(data);
        dataList.add(data);
        notifyDataSetChanged();
    }

    private Map<String, Integer> timeMap = new HashMap<>();

    /**
     * 插入时间(清除之前所有时间)
     *
     * @param newsJsons
     */
    public void inspiritDateInfo(List newsJsons) {
        timeMap.clear();
        if (newsJsons == null) return;
        int size = newsJsons.size();

        for (int i = 0; i < size; i++) {
            Object obj = newsJsons.get(i);
            if (obj instanceof String)
                newsJsons.remove(obj);
        }

        size = newsJsons.size();
        for (int i = 0; i < size; i++) {
            NewsJson flashJson = (NewsJson) newsJsons.get(i);
            String time = null;
            time = getTime(flashJson);
            if (!TextUtils.isEmpty(time)) {

                if (!timeMap.containsKey(time)) {
                    timeMap.put(time, i);
                    if (i < size)
                        newsJsons.add(i, time);
                    else
                        newsJsons.add(time);
                }
            }
        }

    }

    /**
     * 获取时间横条所显示的时间
     *
     * @param flashJson
     * @return
     */
    private String getTime(NewsJson flashJson) {
        String time;
        try {
            time = DateUtils.transformTime(Long.parseLong(flashJson.getDatetime()) * 1000, DateUtils.TYPE_MD);
        } catch (Exception e) {
            time = "01-01";
        }

        String today = "";
        try {
            today = DateUtils.getTodayString(DateUtils.TYPE_MD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (today.equals(time)) {
            time = "今天";
        }

        return time;
    }

    /**
     * 插入时间
     *
     * @param flashJsons
     */
    public void inspiritDateInfo2(List flashJsons) {
        if (flashJsons == null) return;
        int size = flashJsons.size();
        for (int i = 0; i < size; i++) {
            NewsJson flashJson = (NewsJson) flashJsons.get(i);
            String time = "";
            time = getTime(flashJson);
            if (!TextUtils.isEmpty(time)) {
                if (!timeMap.containsKey(time)) {
                    timeMap.put(time, i);
                    if (i < size)
                        flashJsons.add(i, time);
                    else
                        flashJsons.add(time);
                }
            }
        }
    }

    public List<NewsJson> getNewsData() {
        List data = new ArrayList(dataList);
        for (Object o : data) {
            if (o instanceof String) {
                data.remove(o);
            }
        }
        return new ArrayList<>(data);
    }


    static class TimeHolder {
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.v_line) View vLine;

        TimeHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class NewsHolder {
        @BindView(R.id.tv1) TextView tv1;
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_author) TextView tvAuthor;
        @BindView(R.id.tv_time) TextView tvTime;

        NewsHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
