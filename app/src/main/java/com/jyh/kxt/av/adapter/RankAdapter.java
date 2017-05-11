package com.jyh.kxt.av.adapter;

import android.content.Context;
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
import com.library.util.DateUtils;

import org.w3c.dom.ls.LSInput;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:排行Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class RankAdapter extends BaseListAdapter<VideoListJson> {

    private Context context;

    public RankAdapter(Context context, List<VideoListJson> dataList) {
        super(dataList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_rank, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VideoListJson videoBean = dataList.get(position);
        Glide.with(context).load(HttpConstant.IMG_URL + videoBean.getPicture()).error(R.mipmap.ico_def_load).placeholder(R.mipmap
                .ico_def_load).into(viewHolder.ivPhoto);
        viewHolder.tvTitle.setText(videoBean.getTitle());

        try {
            viewHolder.tvTime.setText(DateUtils.transformTime(Long.parseLong(videoBean.getCreate_time()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.tvTime.setText("00:00");
        }

        if (position < 10) {
            viewHolder.ivRank.setVisibility(View.VISIBLE);
            viewHolder.ivRank.setImageResource(context.getResources().getIdentifier("icon_rank" + (position + 1), "mipmap", context
                    .getPackageName()));
        } else {
            viewHolder.ivRank.setVisibility(View.GONE);
        }

        return convertView;
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

    static class ViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.iv_rank) ImageView ivRank;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
