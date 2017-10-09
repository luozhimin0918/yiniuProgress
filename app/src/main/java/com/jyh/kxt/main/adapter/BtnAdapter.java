package com.jyh.kxt.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.json.SlideJson;
import com.library.util.SystemUtil;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:首页按钮
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class BtnAdapter extends RecyclerView.Adapter<BtnAdapter.BtnViewHolder> {

    private List<SlideJson> slideJsons;
    private Context context;

    private int width;

    public BtnAdapter(List<SlideJson> slideJsons, Context context) {
        this.slideJsons = slideJsons;
        this.context = context;
        width = SystemUtil.getScreenDisplay(context).widthPixels / 4;
    }


    @Override
    public BtnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        BtnViewHolder viewHolder = new BtnViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_header_btn, parent, false));

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        params.width = width;
        if (getItemCount() > 4)
            params.height = parent.getHeight() / 2;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BtnViewHolder holder, int position) {
        final SlideJson slideJson = slideJsons.get(position);
        holder.textView.setText(slideJson.getName());
        Glide.with(context).load(HttpConstant.IMG_URL + slideJson.getPicture()).error(R.mipmap.icon_def_btn).placeholder(R.mipmap
                .icon_def_btn).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump((MainActivity) context, slideJson);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (slideJsons == null)
            return 0;
        return slideJsons.size() > 8 ? 8 : slideJsons.size();
    }

    class BtnViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public BtnViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv);
            imageView = (ImageView) itemView.findViewById(R.id.iv);
        }
    }

    /**
     * 跳转
     *
     * @param slideJson
     */
    public static void jump(final MainActivity context, SlideJson slideJson) {
        if (slideJson == null || context == null) return;
        String o_class = slideJson.getO_class();
        String o_action = slideJson.getO_action();
        String o_id = slideJson.getO_id();
        String href = slideJson.getHref();
        JumpUtils.jump(context, o_class, o_action, o_id, href);
    }

}
