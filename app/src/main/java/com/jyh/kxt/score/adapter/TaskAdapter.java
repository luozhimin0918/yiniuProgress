package com.jyh.kxt.score.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.score.json.TaskJson;
import com.library.util.RegexValidateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:任务Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class TaskAdapter extends BaseListAdapter {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_CONTENT = 1;

    private Context mContext;

    public TaskAdapter(List dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = dataList.get(position);
        return obj instanceof TaskJson ? TYPE_CONTENT : TYPE_TITLE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        TitleHolder titleHolder = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if (type == TYPE_TITLE) {
                convertView = inflater.inflate(R.layout.item_score_title, parent, false);
                titleHolder = new TitleHolder(convertView);
                convertView.setTag(titleHolder);
            } else {
                convertView = inflater.inflate(R.layout.item_score, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
        } else {
            if (type == TYPE_TITLE) {
                titleHolder = (TitleHolder) convertView.getTag();
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        }

        if (type == TYPE_TITLE) {
            titleHolder.tvTitle.setText((String) dataList.get(position));
            titleHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
            titleHolder.vLineTop.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));
            titleHolder.vLineDown.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));
        } else {
            viewHolder.ivTask.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sel_score_done));
            viewHolder.tvCoin.setTextColor(ContextCompat.getColor(mContext, R.color.font_color17));
            viewHolder.tvScoreNum.setTextColor(ContextCompat.getColor(mContext, R.color.font_color8));
            viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
            viewHolder.tvSchedule.setTextColor(ContextCompat.getColor(mContext, R.color.font_color17));
            viewHolder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));

            final TaskJson task = (TaskJson) dataList.get(position);
            String is_finished = task.getIs_finished();
            if (RegexValidateUtil.isEmpty(is_finished)) {
                viewHolder.tvSchedule.setVisibility(View.VISIBLE);
                String task_succ_num = task.getNum_finished();
                String task_sum_num = task.getTotal_tasks();
                if (task_sum_num == null || task_sum_num.trim().equals("")) {
                    task_sum_num = "0";
                }
                if (task_succ_num == null || task_succ_num.trim().equals(""))
                    task_succ_num = "0";

                viewHolder.tvSchedule.setText("完成 " + task_succ_num + "/" + task_sum_num);
                viewHolder.ivTask.setSelected(!task_sum_num.equals("0") && task_sum_num.equals(task_succ_num));
            } else {
                viewHolder.tvSchedule.setVisibility(View.GONE);
                viewHolder.ivTask.setSelected("1".equals(is_finished));
            }

            viewHolder.tvTitle.setText(task.getDescription());
            String award = task.getAward();
            viewHolder.tvScoreNum.setText((award == null || "".equals(award.trim())) ? "" : "+" + award);

            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.ivTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalViewHolder.ivTask.isSelected()) return;
                    JumpUtils.jump((BaseActivity) mContext, task.getO_class(), task.getO_action(), task.getO_id(), null);
                }
            });

        }

        return convertView;
    }

    public void setData(List data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    static class TitleHolder {
        @BindView(R.id.v_line_top) View vLineTop;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.v_line_down) View vLineDown;

        TitleHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolder {
        @BindView(R.id.iv_task) ImageView ivTask;
        @BindView(R.id.tv_coin) TextView tvCoin;
        @BindView(R.id.tv_scoreNum) TextView tvScoreNum;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_schedule) TextView tvSchedule;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
