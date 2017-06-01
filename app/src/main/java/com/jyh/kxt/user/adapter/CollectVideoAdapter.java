package com.jyh.kxt.user.adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;
import com.nineoldandroids.animation.AnimatorSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:收藏视听
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/11.
 */

public class CollectVideoAdapter extends BaseListAdapter<VideoListJson> {

    private int widthPixels;
    private Context context;
    private boolean isEdit = false;
    private Set<String> delIds = new HashSet<>();

    public CollectVideoAdapter(List<VideoListJson> dataList, Context context) {
        super(dataList);
        this.context = context;
        widthPixels = (int) (SystemUtil.getScreenDisplay(context).widthPixels - context.getResources().getDimension(R.dimen
                .newsContentPadding3) * 2);
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

        changeTheme(viewHolder);

        if (isEdit) {
            viewHolder.flDel.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = viewHolder.rlContent.getLayoutParams();
            layoutParams.width = widthPixels;
            viewHolder.rlContent.setLayoutParams(layoutParams);
        } else {
            viewHolder.flDel.setVisibility(View.GONE);


        }

        final VideoListJson videoBean = dataList.get(position);
        viewHolder.ivDel.setSelected(videoBean.isSel());

        Glide.with(context).load(HttpConstant.IMG_URL + videoBean.getPicture()).error(R.mipmap.icon_def_news).placeholder(R.mipmap
                .icon_def_news).into(viewHolder
                .ivPhoto);

        viewHolder.tvTitle.setText(videoBean.getTitle());
        viewHolder.tvPlayCount.setText(videoBean.getNum_play());

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.flDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加或移除选中状态
                if (finalViewHolder.ivDel.isSelected()) {
                    finalViewHolder.ivDel.setSelected(false);
                    try {
                        delIds.remove(videoBean.getId());
                        videoBean.setSel(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finalViewHolder.ivDel.setSelected(true);
                    try {
                        delIds.add(videoBean.getId());
                        videoBean.setSel(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (observerData != null)
                    observerData.delItem(delIds.size());
            }
        });

        try {
            viewHolder.tvTime.setText(DateUtils.transformTime(Long.parseLong(videoBean.getCreate_time()) * 1000, DateUtils.TYPE_YMD));
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.tvTime.setText("2017-1-1");
        }

        return convertView;
    }

    private void changeTheme(ViewHolder holder) {
        holder.ivDel.setBackground(ContextCompat.getDrawable(context, R.drawable.sel_collect_item));
        holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color9));
        holder.tvPlayCount.setTextColor(ContextCompat.getColor(context, R.color.font_color9));
        holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color5));

        holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color9));
        int paddingVal = SystemUtil.dp2px(context, 4);
        holder.tvTime.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(holder.tvTime, R.mipmap.icon_explore_time, 0, 0, 0);

        holder.tvPlayCount.setTextColor(ContextCompat.getColor(context, R.color.font_color9));
        paddingVal = SystemUtil.dp2px(context, 4);
        holder.tvPlayCount.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(holder.tvPlayCount, R.mipmap.icon_collect_play_smail, 0, 0, 0);

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

    /**
     * 获取删除id
     *
     * @return
     */
    public Set<String> getDelIds() {
        return delIds;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public List<VideoListJson> getData() {
        return dataList;
    }

    private DelNumListener observerData;

    public void setSelListener(DelNumListener observerData) {
        this.observerData = observerData;
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

        Iterator<VideoListJson> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            VideoListJson next = iterator.next();
            for (String id : list) {
                if (id.equals(next.getId())) {
                    iterator.remove();
                }
            }
        }

        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.iv_photo) ImageView ivPhoto;
        @BindView(R.id.fl_photo) FrameLayout flPhoto;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_playCount) TextView tvPlayCount;
        @BindView(R.id.iv_del) ImageView ivDel;
        @BindView(R.id.fl_del) FrameLayout flDel;
        @BindView(R.id.rl_content) RelativeLayout rlContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
