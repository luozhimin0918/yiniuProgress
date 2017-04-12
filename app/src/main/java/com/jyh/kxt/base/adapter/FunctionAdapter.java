package com.jyh.kxt.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {

    private List<String> list;
    private Context context;
    private OnClickListener onClickListener;

    public FunctionAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public FunctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FunctionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_umeng_share,
                parent, false));
    }

    @Override
    public void onBindViewHolder(FunctionViewHolder holder, final int position) {
        holder.textView.setText(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null)
                    onClickListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class FunctionViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public FunctionViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_btn);
            imageView = (ImageView) itemView.findViewById(R.id.iv_btn);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(View view, int position);
    }
}
