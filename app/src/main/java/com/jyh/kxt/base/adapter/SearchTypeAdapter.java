package com.jyh.kxt.base.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.search.json.SearchType;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/7.
 */

public class SearchTypeAdapter extends RecyclerView.Adapter<SearchTypeAdapter.ViewHolder> {

    private Context mContext;
    private List<SearchType> searchTypes;
    private OnItemClickListener onItemClickListener;

    public SearchTypeAdapter(Context mContext, List<SearchType> searchTypes) {
        this.mContext = mContext;
        this.searchTypes = searchTypes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_type, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.width = SystemUtil.dp2px(mContext, 100);
        layoutParams.height = SystemUtil.dp2px(mContext, 50);
        viewHolder.itemView.setLayoutParams(layoutParams);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setSearchType(searchTypes.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, holder.itemView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchTypes == null ? 0 : searchTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private SearchType searchType;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView;
        }

        public void setSearchType(SearchType searchType) {
            this.searchType = searchType;
            title.setText(searchType.getName());
            title.setTextColor(ContextCompat.getColor(mContext, R.color.font_color8));
            title.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            ToastView.makeText3(mContext, searchType.getName());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
