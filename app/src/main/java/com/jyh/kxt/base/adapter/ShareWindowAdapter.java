package com.jyh.kxt.base.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.json.ShareItemJson;
import com.jyh.kxt.base.utils.OnPopupFunListener;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class ShareWindowAdapter extends RecyclerView.Adapter<ShareWindowAdapter.FunctionViewHolder> {

    private Context context;
    private List<ShareItemJson> shareItemList;
    private OnPopupFunListener onPopupFunListener;

    public void setOnPopupFunListener(OnPopupFunListener onPopupFunListener) {
        this.onPopupFunListener = onPopupFunListener;
    }

    public ShareWindowAdapter(List<ShareItemJson> shareItemList, Context context) {
        this.shareItemList = shareItemList;
        this.context = context;
    }

    @Override
    public FunctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FunctionViewHolder functionViewHolder = new FunctionViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_umeng_share, parent, false));
        return functionViewHolder;
    }

    @Override
    public void onBindViewHolder(final FunctionViewHolder holder, final int position) {

        final ShareItemJson shareBtnJson = shareItemList.get(position);

        holder.textView.setText(shareBtnJson.title);
        holder.textView.setTextColor(ContextCompat.getColor(context, R.color.font_color60));

        holder.imageView.setSelected(shareBtnJson.isSelectedView);
        holder.imageView.setImageResource(shareBtnJson.icon);
        //设置监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPopupFunListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    ShareItemJson shareItemJson = shareItemList.get(adapterPosition);
                    onPopupFunListener.onClickItem(v, shareItemJson, ShareWindowAdapter.this);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shareItemList == null ? 0 : shareItemList.size();
    }


    class FunctionViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        FunctionViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_btn);
            imageView = (ImageView) itemView.findViewById(R.id.iv_share_icon);
        }
    }
}
