package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.EncryptionUtils;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:关注文章Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/17.
 */

public class AttentionAuthorAdapter extends BaseListAdapter<AuthorDetailsJson> {

    private Context context;
    private VolleyRequest request;

    public AttentionAuthorAdapter(Context context, List dataList) {
        super(dataList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_attention_author, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AuthorDetailsJson author = dataList.get(position);
        final ViewHolder finalViewHolder = viewHolder;
        Glide.with(context).load(author.getPicture()).asBitmap().error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                finalViewHolder.ivPhoto.setImageBitmap(resource);
            }
        });

        viewHolder.ivAttention.setSelected(true);

        viewHolder.ivAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消关注
                if (request != null) {
                    request.doPost(HttpConstant.EXPLORE_BLOG_DELETEFAVOR, getFollowMap(author.getId()), new HttpListener<Object>() {
                        @Override
                        protected void onResponse(Object o) {
                            dataList.remove(author);
                            notifyDataSetChanged();
                            EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_ATTENTION_AUTHOR_DEL,null));
                        }

                        @Override
                        protected void onErrorResponse(VolleyError error) {
                            super.onErrorResponse(error);
                            ToastView.makeText3(context, "取消关注失败");
                        }
                    });
                }
            }
        });

        viewHolder.tvName.setText(author.getName());
        viewHolder.tvContent.setText(author.getIntroduce());

        return convertView;
    }

    public void setData(List<AuthorDetailsJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<AuthorDetailsJson> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public List<AuthorDetailsJson> getData() {
        return dataList;
    }

    public VolleyRequest getRequest() {
        return request;
    }

    public void setRequest(VolleyRequest request) {
        this.request = request;
    }


    public Map<String, String> getFollowMap(String authorId) {
        JSONObject jsonParam = request.getJsonParam();
        if (LoginUtils.isLogined(context)) {
            UserJson userInfo = LoginUtils.getUserInfo(context);
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        }
        jsonParam.put(VarConstant.HTTP_ID, authorId);
        Map<String, String> map = new HashMap();
        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    static class ViewHolder {
        @BindView(R.id.iv_photo) RoundImageView ivPhoto;
        @BindView(R.id.iv_attention) ImageView ivAttention;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_content) TextView tvContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
