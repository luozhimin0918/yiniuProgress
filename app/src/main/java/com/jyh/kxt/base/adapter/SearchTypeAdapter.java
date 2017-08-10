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
        holder.setSearchType(searchTypes.get(position), position);
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

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private View vLine;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_type);
            vLine = itemView.findViewById(R.id.v_line);
        }

        public void setSearchType(SearchType searchType, int position) {
            title.setText(searchType.getName());
            title.setTextColor(ContextCompat.getColor(mContext, R.color.font_color8));
            vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color3));

            if (position == getItemCount() - 1 || position == 2) {
                vLine.setVisibility(View.GONE);
            } else {
                vLine.setVisibility(View.VISIBLE);
            }

        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
