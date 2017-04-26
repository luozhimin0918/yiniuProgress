package com.jyh.kxt.av.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.library.util.DateUtils;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/19.
 */

public class VideoAdapter extends BaseListAdapter<VideoListJson> {

    private Context mContext;
    private List<VideoListJson> list;

    public VideoAdapter(Context context, List<VideoListJson> list) {
        super(list);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_video, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.iv_more);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvCommentCount = (TextView) convertView.findViewById(R.id.tv_commentCount);
            holder.tvPlayCount = (TextView) convertView.findViewById(R.id.tv_playCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final VideoListJson video = list.get(position);
        Glide.with(mContext).load(HttpConstant.IMG_URL + video.getPicture()).error(R.mipmap.ico_def_load).placeholder
                (R.mipmap.ico_def_load).into
                (holder.iv);
        holder.tvTitle.setText(video.getTitle());
        try {
            holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(video.getCreate_time()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvTime.setText("00:00");
        }
        holder.tvCommentCount.setText(video.getNum_comment());
        holder.tvPlayCount.setText(video.getNum_play());

        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengShareTool.initUmengLayout((BaseActivity) mContext, video.getTitle(), "http://www.baidu.com",
                        "http://www.baidu" +
                                ".com",
                        null, null, holder.ivMore, null);
            }
        });
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoDetailActivity.class);
                intent.putExtra(IntentConstant.ID, video.getId());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public void setData(List<VideoListJson> videoListJsons) {
        list = videoListJsons;
    }

    public void addData(List<VideoListJson> videoListJsons) {
        list.addAll(videoListJsons);
    }

    class ViewHolder {
        public ImageView iv;
        public TextView tvTitle, tvTime, tvPlayCount, tvCommentCount;
        public ImageView ivMore;
    }
}
