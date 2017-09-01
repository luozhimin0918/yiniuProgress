package com.jyh.kxt.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.BlockActivity;
import com.jyh.kxt.chat.json.BlockJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class BlockAdapter extends BaseListAdapter<BlockJson> {
    private Context mContext;
    private VolleyRequest request;

    public BlockAdapter(Context mContext, List<BlockJson> dataList) {
        super(dataList);
        this.mContext = mContext;
        request = new VolleyRequest(mContext, ((BlockActivity) mContext).getQueue());
        request.setTag(BlockActivity.class.getName());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_letter_ban, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
        holder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));
        final BlockJson bean = dataList.get(position);

        holder.tvName.setText(bean.getNickname());
        final ViewHolder finalHolder = holder;
        Glide.with(mContext).load(bean.getAvatar()).asBitmap().
                error(R.mipmap.icon_user_def_photo)
                .placeholder(R.mipmap.icon_user_def_photo)
                .into(new ImageViewTarget<Bitmap>(finalHolder.rivAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        finalHolder.rivAvatar.setImageBitmap(resource);
                    }
                });

        if (bean.isBan()) {
            holder.tvBan.setTextColor(ContextCompat.getColor(mContext, R.color.font_color_ban));
            holder.tvBan.setText("解除");
        } else {
            holder.tvBan.setTextColor(ContextCompat.getColor(mContext, R.color.font_color_unban));
            holder.tvBan.setText("屏蔽");
        }

        holder.tvBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonParam = request.getJsonParam();
                jsonParam.put(VarConstant.HTTP_SENDER, LoginUtils.getUserInfo(mContext).getUid());
                jsonParam.put(VarConstant.HTTP_RECEIVER,bean.getReceiver());
                jsonParam.put(VarConstant.HTTP_IS_BANNED, bean.isBan()?"0":"1");
                request.doGet(HttpConstant.MSG_USER_BAN, jsonParam, new HttpListener<Object>() {
                    @Override
                    protected void onResponse(Object o) {
                        bean.setBan(!bean.isBan());
                        notifyDataSetChanged();
                    }

                    @Override
                    protected void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ToastView.makeText3(mContext, bean.isBan() ? "解除屏蔽失败" : "添加屏蔽失败");
                    }
                });
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_ban) TextView tvBan;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
