package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.jyh.kxt.trading.ui.CommentDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class ViewPointDetailAdapter extends BaseListAdapter<CommentDetailBean> {

    private Context mContext;
    private LayoutInflater mInflater;

    public ViewPointDetailAdapter(Context mContext, List<CommentDetailBean> dataList) {
        super(dataList);
        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_point_detail_comment, parent, false);

            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.tvReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CommentDetailActivity.class));
            }
        });
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_reply_count) TextView tvReplyCount;

        ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
