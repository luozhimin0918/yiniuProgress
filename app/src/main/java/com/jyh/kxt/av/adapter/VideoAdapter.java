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
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.base.utils.GoodUtils;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.library.base.http.VarConstant;
import com.library.util.DateUtils;
import com.library.widget.window.ToastView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 项目名:Kxt
 * 类描述:视听Adapter
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
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
        boolean collect = CollectUtils.isCollect(mContext, VarConstant.COLLECT_TYPE_VIDEO, video);
        video.setCollect(collect);
        boolean good = GoodUtils.isGood(mContext, video.getId(), VarConstant.GOOD_TYPE_VIDEO);
        video.setGood(good);

        Glide.with(mContext)
                .load(HttpConstant.IMG_URL + video.getPicture())
                .error(R.mipmap.ico_def_load)
                .thumbnail(0.6f)
                .placeholder(R.mipmap.ico_def_load)
                .into(holder.iv);
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
                UmengShareTool.initUmengLayout((BaseActivity) mContext, new ShareJson(video.getTitle(), "http://www" +
                                ".baidu.com",
                                "http://www.baidu.com", null, null, UmengShareTool.TYPE_VIDEO,
                                video.getId(), VarConstant.COLLECT_TYPE_VIDEO, VarConstant.GOOD_TYPE_VIDEO, video
                                .isGood(), video
                                .isCollect()),
                        video, holder.ivMore, new
                                ObserverData<Map<String,
                                        Boolean>>() {
                                    @Override
                                    public void callback(Map<String, Boolean> o) {
                                        Set<String> set = o.keySet();
                                        Iterator<String> iterator = set.iterator();
                                        while (iterator.hasNext()) {
                                            String key = iterator.next();
                                            Boolean b = o.get(key);
                                            switch (key) {
                                                case VarConstant.FUNCTION_TYPE_COLLECT:
                                                    //更改video 收藏状态
                                                    video.setCollect(b);
                                                    break;
                                                case VarConstant.FUNCTION_TYPE_GOOD:
                                                    //更改video 点赞状态
                                                    video.setGood(b);
                                                    break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        //失败
                                        if (e != null && e.getMessage() != null) {
                                            ToastView.makeText3(mContext, e.getMessage());
                                        }
                                    }
                                });
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
        list.clear();
        list.addAll(videoListJsons);
        list = videoListJsons;
        notifyDataSetChanged();
    }

    public void addData(List<VideoListJson> videoListJsons) {
        list.addAll(videoListJsons);
        notifyDataSetChanged();
    }

    class ViewHolder {
        public ImageView iv;
        public TextView tvTitle, tvTime, tvPlayCount, tvCommentCount;
        public ImageView ivMore;
    }
}
