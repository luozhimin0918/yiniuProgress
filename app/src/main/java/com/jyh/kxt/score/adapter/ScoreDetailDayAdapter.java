package com.jyh.kxt.score.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.score.json.ScoreDetailDayJson;
import com.library.util.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:积分明细 - 本月明细
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class ScoreDetailDayAdapter extends BaseListAdapter<ScoreDetailDayJson> {

    private Context mContext;

    public ScoreDetailDayAdapter(List<ScoreDetailDayJson> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_score_history, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScoreDetailDayJson bean = dataList.get(position);
        holder.tvContent.setText(bean.getTitle());
        String award = bean.getAward();
        if (award == null || "".equals(award.trim())) {
            award = "";
        } else {
            award = "金币" + award;
        }
        holder.tvNum.setText(award);
        String time = bean.getTime();
        try {
            time = DateUtils.transformTime(Long.parseLong(time) * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            time = "00:00";
        }
        holder.tvTime.setText(time);

        holder.tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        holder.tvNum.setTextColor(ContextCompat.getColor(mContext, R.color.font_color8));
        holder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color17));
        holder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_num) TextView tvNum;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
