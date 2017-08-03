package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.support.v4.widget.Space;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.library.util.SystemUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class CommentDetailAdapter extends BaseListAdapter<CommentDetailBean> {

    private Context mContext;
    private LayoutInflater mInflater;

    public CommentDetailAdapter(Context mContext, List<CommentDetailBean> dataList) {
        super(dataList);
        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_point_detail_comment2, parent, false);

            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        ViewGroup.LayoutParams layoutParams = mViewHolder.mCommentSpace.getLayoutParams();
        if (position == 0) {
            layoutParams.width = 0;
        } else {
            layoutParams.width = SystemUtil.dp2px(mContext, 40);
        }

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.point_comment_space) Space mCommentSpace;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
