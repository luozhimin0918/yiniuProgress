package com.jyh.kxt.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.library.util.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:收藏视听
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/11.
 */

public class CollectVideoAdapter extends BaseListAdapter<VideoListJson> {

    private Context context;

    public CollectVideoAdapter(List<VideoListJson> dataList, Context context) {
        super(dataList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_collect_video, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VideoListJson videoBean = dataList.get(position);

        Glide.with(context).load(HttpConstant.IMG_URL + videoBean.getPicture()).error(R.mipmap.ico_def_load).placeholder(R.mipmap
                .ico_def_load).into(viewHolder
                .ivPhoto);

        viewHolder.tvTitle.setText(videoBean.getTitle());
        viewHolder.tvPlayCount.setText(videoBean.getNum_play());

        try {
            viewHolder.tvTime.setText(DateUtils.transformTime(Long.parseLong(videoBean.getCreate_time()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.tvTime.setText("00:00");
        }

        return convertView;
    }

    public void setData(List<VideoListJson> videos) {
        dataList.clear();
        dataList.addAll(videos);
        notifyDataSetChanged();
    }

    public void addData(List<VideoListJson> videoMore) {
        dataList.addAll(videoMore);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public List<VideoListJson> getData() {
        return dataList;
    }

    static class ViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.fl_photo) FrameLayout flPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_playCount) TextView tvPlayCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
