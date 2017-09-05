package com.jyh.kxt.score.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.score.json.ScoreDetailMonthJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class ScoreDetailMonthAdapter extends BaseListAdapter<ScoreDetailMonthJson> {

    private Context mContext;

    public ScoreDetailMonthAdapter(List<ScoreDetailMonthJson> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_score_history2, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScoreDetailMonthJson bean = dataList.get(position);
        holder.tvIn.setText(bean.getTask_award_in());
        holder.tvOut.setText(bean.getTask_award_out());
        holder.tvTime.setText(bean.getTask_title());

        holder.tvIn.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        holder.tvOut.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        holder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        holder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_in) TextView tvIn;
        @BindView(R.id.tv_out) TextView tvOut;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
