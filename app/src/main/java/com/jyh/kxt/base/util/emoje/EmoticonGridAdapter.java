package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.dao.EmojeBean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/5.
 */

public class EmoticonGridAdapter extends BaseAdapter {

    private Context mContext;
    private int itemSize;
    private LayoutInflater mInflater;
    private List<EmojeBean> emoJeBeanList;
    private EmoticonViewPager emoticonViewPager;

    public EmoticonGridAdapter(Context mContext, int itemSize, List<EmojeBean> emoJeBeanList, EmoticonViewPager
            emoticonViewPager) {
        this.mContext = mContext;
        this.itemSize = itemSize;
        this.emoJeBeanList = emoJeBeanList;
        this.emoticonViewPager = emoticonViewPager;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return emoJeBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return emoJeBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_emoticon_grid, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.ivEmoticon = (ImageView) convertView.findViewById(R.id.iv_emoticon);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        final EmojeBean emojeBean = emoJeBeanList.get(position);

        convertView.setBackgroundResource(R.drawable.iv_face);

        Glide
                .with(mContext)
                .load(Uri.parse(emojeBean.getPngUrl()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mViewHolder.ivEmoticon.setImageBitmap(resource);
                    }
                });

        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        layoutParams.width = itemSize;
        layoutParams.height = itemSize;
        convertView.setLayoutParams(layoutParams);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticonViewPager.itemEmoJeClick(emojeBean);
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView ivEmoticon;
    }
}
