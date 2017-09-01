package com.jyh.kxt.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.chat.json.LetterListJson;
import com.library.util.SystemUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:私信对象列表
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class LetterListAdapter extends BaseListAdapter<LetterListJson> {

    private final int TYPE_SYS = 0;
    private final int TYPE_LETTER = 1;

    private Context mContext;
    private ListView listView;

    private float deleteViewWidth;
    private float chartContentWidth;


    public LetterListAdapter(List<LetterListJson> dataList, Context mContext, ListView listView) {
        super(dataList);
        this.mContext = mContext;
        this.listView = listView;

        deleteViewWidth = SystemUtil.dp2px(mContext, 70);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(mContext);
        chartContentWidth = screenDisplay.widthPixels + deleteViewWidth;
    }

    private ViewHolderSys viewHolderSys;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == TYPE_SYS) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_letter_sys, parent, false);
                viewHolderSys = new ViewHolderSys(convertView);
                convertView.setTag(viewHolderSys);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_letter, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

                //重置所有Item的宽度
                ViewGroup.LayoutParams chartContentParams = viewHolder.llRootView.getLayoutParams();
                chartContentParams.width = (int) chartContentWidth;
                viewHolder.llRootView.setLayoutParams(chartContentParams);
                viewHolder.llRootView.setTranslationX(0);
            }
        } else {
            if (type == TYPE_SYS) {
                viewHolderSys = (ViewHolderSys) convertView.getTag();
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        }

        if (type == TYPE_SYS) {
            final ViewHolderSys finalViewHolderSys = viewHolderSys;
            Glide.with(mContext).load(R.mipmap.icon_msg_sys).asBitmap().into(new ImageViewTarget<Bitmap>(finalViewHolderSys.rivAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    finalViewHolderSys.rivAvatar.setImageBitmap(resource);
                }
            });
            viewHolderSys.vPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_point_red));
            viewHolderSys.tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
            viewHolderSys.ivBreak.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_msg_sys_enter));
            viewHolderSys.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));
        } else {
            final int index = position - 1;
            LetterListJson bean = dataList.get(index);
            viewHolder.tvName.setText(bean.getNickname());
            viewHolder.tvContent.setText(bean.getLast_content());
            try {
                viewHolder.tvTime.setText("" + Long.parseLong(bean.getDatetime()) * 1000);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                viewHolder.tvTime.setText("00:00");
            }
            viewHolder.tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downHindContentView();
                    dataList.remove(index);
                    notifyDataSetChanged();
                }
            });
            final ViewHolder finalViewHolder = viewHolder;
            Glide.with(mContext).load(bean.getAvatar()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap
                    .icon_user_def_photo)
                    .into(new ImageViewTarget<Bitmap>(finalViewHolder.rivAvatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            finalViewHolder.rivAvatar.setImageBitmap(resource);
                        }
                    });

            setUnreadNum(viewHolder, bean);
            setTheme(viewHolder);
        }
        return convertView;
    }

    /**
     * 设置未读信息数
     *
     * @param viewHolder
     * @param bean
     */
    private void setUnreadNum(ViewHolder viewHolder, LetterListJson bean) {
        String num_unread = bean.getNum_unread();
        if (num_unread == null || num_unread.trim().equals("") || num_unread.trim().equals("0")) {
            viewHolder.tvNum.setText("");
            viewHolder.tvNum.setVisibility(View.GONE);
        } else {
            int numInt = Integer.parseInt(num_unread);
            if (numInt > 99) {
                num_unread = "99+";
            }
            viewHolder.tvNum.setText(num_unread);
            viewHolder.tvNum.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_SYS : TYPE_LETTER;
    }

    private void setTheme(ViewHolder viewHolder) {
        viewHolder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
        viewHolder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        viewHolder.tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
        viewHolder.tvNum.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        viewHolder.tvNum.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_oval_red));
        viewHolder.tvDel.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        viewHolder.tvDel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red2));
        viewHolder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));
    }

    public void setData(List<LetterListJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void setShowRed(boolean isShowRed) {
        if (viewHolderSys != null)
            viewHolderSys.vPoint.setVisibility(isShowRed ? View.VISIBLE : View.GONE);
    }

    static class ViewHolder {
        @BindView(R.id.ll_rootView) LinearLayout llRootView;
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.tv_num) TextView tvNum;
        @BindView(R.id.tv_del) TextView tvDel;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolderSys {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.v_point) View vPoint;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.iv_break) ImageView ivBreak;
        @BindView(R.id.v_line) View vLine;

        ViewHolderSys(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private int currentTranslationX;
    private LinearLayout contentLayout;

    public void translationContentView(int position, float distanceX) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int index = position - firstVisiblePosition;
        ViewGroup itemView = (ViewGroup) ((ViewGroup) (listView.getChildAt(index))).getChildAt(0);

        contentLayout = (LinearLayout) itemView.findViewById(R.id.ll_rootView);

        if (Math.abs(currentTranslationX) <= deleteViewWidth) {
            currentTranslationX -= distanceX;
            contentLayout.setTranslationX(currentTranslationX);
        }
    }

    public void upContentView() {
        if (contentLayout == null) {
            return;
        }
        if (Math.abs(currentTranslationX) < deleteViewWidth) {
            contentLayout.setTranslationX(0);
        } else {
            contentLayout.setTranslationX(-deleteViewWidth);
        }
    }

    public void downHindContentView() {
        if (contentLayout != null) {
            contentLayout.setTranslationX(0);
            contentLayout = null;
            currentTranslationX = 0;
        }
    }
}
