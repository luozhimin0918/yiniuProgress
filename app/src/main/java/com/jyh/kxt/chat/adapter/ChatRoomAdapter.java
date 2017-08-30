package com.jyh.kxt.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.chat.json.ChatRoomJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class ChatRoomAdapter extends BaseListAdapter<ChatRoomJson> {
    private Context mContext;
    private LayoutInflater mInflater;

    public ChatRoomAdapter(Context mContext, List<ChatRoomJson> dataList) {
        super(dataList);
        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder0 viewHolder0;
        ViewHolder1 viewHolder1;

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

                break;
            case 1:

                break;
        }

        return convertView;
    }


    class ViewHolder0 {
        @BindView(R.id.chat_room_time) TextView chatRoomTime;
        @BindView(R.id.chat_room_portrait) RoundImageView chatRoomPortrait;
        @BindView(R.id.chat_room_content) TextView chatRoomContent;
        @BindView(R.id.chat_room_tip) ImageView chatRoomTip;

        ViewHolder0(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder1 {
        @BindView(R.id.chat_room_time) TextView chatRoomTime;
        @BindView(R.id.chat_room_portrait) RoundImageView chatRoomPortrait;
        @BindView(R.id.chat_room_content) TextView chatRoomContent;
        @BindView(R.id.chat_room_tip) ImageView chatRoomTip;

        ViewHolder1(View view) {
            ButterKnife.bind(this, view);
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

}
