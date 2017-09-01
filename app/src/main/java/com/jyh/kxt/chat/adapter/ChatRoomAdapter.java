package com.jyh.kxt.chat.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.chat.json.ChatRoomJson;
import com.jyh.kxt.chat.presenter.ChatRoomPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class ChatRoomAdapter extends BaseListAdapter<ChatRoomJson> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ChatRoomPresenter chatRoomPresenter;

    public ChatRoomAdapter(Context mContext, List<ChatRoomJson> dataList, ChatRoomPresenter chatRoomPresenter) {
        super(dataList);
        this.mContext = mContext;
        this.chatRoomPresenter = chatRoomPresenter;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder0 viewHolder0 = null;
        ViewHolder1 viewHolder1 = null;

        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            switch (itemViewType) {
                case 0:
                    convertView = mInflater.inflate(R.layout.item_chat_view1, parent, false);
                    viewHolder0 = new ViewHolder0(convertView);
                    convertView.setTag(viewHolder0);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.item_chat_view2, parent, false);
                    viewHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(viewHolder1);
                    break;
            }
        } else {
            switch (itemViewType) {
                case 0:
                    viewHolder0 = (ViewHolder0) convertView.getTag();
                    break;
                case 1:
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
            }
        }

        switch (itemViewType) {
            case 0:
                parseHolderData(position, viewHolder0);
                break;
            case 1:
                parseHolderData(position, viewHolder1);
                break;
        }
        return convertView;
    }

    /**
     * 解析参数
     *
     * @param position
     * @param baseViewHolder
     */
    private void parseHolderData(int position, final BaseViewHolder baseViewHolder) {
        final ChatRoomJson chatRoomJson = dataList.get(position);

        long partitionTime = chatRoomJson.getPartitionTime();
        if (partitionTime != 0) {
            baseViewHolder.chatRoomTime.setVisibility(View.VISIBLE);

            String partitionLabel = DateFormat.format("HH:mm", partitionTime).toString();
            baseViewHolder.chatRoomTime.setText(partitionLabel);
        } else {
            baseViewHolder.chatRoomTime.setVisibility(View.GONE);
        }

        /**
         * 消息的状态
         */

        baseViewHolder.progressTip.setVisibility(View.GONE);
        baseViewHolder.chatRoomTip.setVisibility(View.GONE);
        baseViewHolder.chatRoomTip.setOnClickListener(null);

        baseViewHolder.chatRoomShield.setVisibility(View.GONE);

        if (chatRoomJson.getMsgSendStatus() == 1) {
            baseViewHolder.progressTip.setVisibility(View.VISIBLE);

        } else if (chatRoomJson.getMsgSendStatus() == 2) {
            baseViewHolder.chatRoomTip.setVisibility(View.VISIBLE);

            Glide.with(mContext).load(R.mipmap.icon_msg_error).into(baseViewHolder.chatRoomTip);

            if (chatRoomJson.getIs_banned_for_receiver() == 1) {
                baseViewHolder.chatRoomShield.setVisibility(View.VISIBLE);
            } else {
                baseViewHolder.chatRoomTip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertResendDialog(chatRoomJson);
                    }
                });
            }
        }

        /**
         * 内容
         */
        baseViewHolder.chatRoomContent.convertToGif(chatRoomJson.getContent());
        /**
         * 头像
         */
        Glide.with(mContext)
                .load(chatRoomJson.getAvatar())
                .asBitmap()
                .placeholder(R.mipmap.icon_user_def_photo)
                .override(100, 100)
                .error(R.mipmap.icon_user_def_photo)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        baseViewHolder.chatRoomPortrait.setImageBitmap(resource);
                    }
                });
    }

    class BaseViewHolder {

        @BindView(R.id.chat_room_time) TextView chatRoomTime;
        @BindView(R.id.chat_room_portrait) RoundImageView chatRoomPortrait;
        @BindView(R.id.chat_room_content) EmoticonSimpleTextView chatRoomContent;
        @BindView(R.id.chat_room_tip) ImageView chatRoomTip;
        @BindView(R.id.chat_room_loading) ProgressBar progressTip;
        @BindView(R.id.chat_room_shield_tip) TextView chatRoomShield;

        BaseViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder0 extends BaseViewHolder {
        ViewHolder0(View view) {
            super(view);
        }
    }

    class ViewHolder1 extends BaseViewHolder {
        ViewHolder1(View view) {
            super(view);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getViewType();
    }


    class AlertResendDialog {
        public AlertResendDialog(final ChatRoomJson chatRoomJson) {
            new AlertDialog.Builder(mContext)
                    .setMessage("是否重新发送")
                    .setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String randomUnique = chatRoomJson.getId();
                                    chatRoomPresenter.requestSendChitChat(chatRoomJson, randomUnique);
                                }
                            }).setNegativeButton("否", null).show();
        }
    }
}
