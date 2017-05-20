package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.main.json.NewsJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:搜索界面 浏览记录Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class SearchBrowerAdapter extends RecyclerView.Adapter<SearchBrowerAdapter.ViewHolder> {

    private Context context;
    private List<NewsJson> newsJsons;
    private OnItemClickListener onItemClickListener;

    public SearchBrowerAdapter(Context context, List<NewsJson> newsJsons) {
        this.context = context;
        this.newsJsons = newsJsons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_brower, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        NewsJson news = newsJsons.get(position);
        holder.tvContent.setText(news.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsJsons == null ? 0 : newsJsons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_type) TextView tvType;
        @BindView(R.id.tv_content) TextView tvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
