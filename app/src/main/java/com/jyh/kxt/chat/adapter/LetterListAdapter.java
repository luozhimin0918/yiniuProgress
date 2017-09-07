package com.jyh.kxt.chat.adapter;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.dao.ChatRoomJsonDao;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.util.emoje.EmoticonReplaceTextView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.util.emoje.EmoticonTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.ToastSnack;
import com.jyh.kxt.chat.LetterActivity;
import com.jyh.kxt.chat.json.ChatPreviewJson;
import com.jyh.kxt.chat.json.LetterListJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.DateUtils;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

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

    private LetterActivity mActivity;
    private ListView listView;

    private float deleteViewWidth;
    private float chartContentWidth;
    private VolleyRequest request;


    private boolean isShowSystemMessageRed = false;

    public LetterListAdapter(List<LetterListJson> dataList, LetterActivity mActivity, ListView listView) {
        super(dataList);
        this.mActivity = mActivity;
        this.listView = listView;

        request = new VolleyRequest(mActivity, mActivity.presenter.mQueue);
        request.setTag(mActivity.presenter.getClass().getName());

        deleteViewWidth = SystemUtil.dp2px(mActivity, 70);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(mActivity);
        chartContentWidth = screenDisplay.widthPixels + deleteViewWidth;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderSys viewHolderSys = null;
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == TYPE_SYS) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_letter_sys, parent, false);
                viewHolderSys = new ViewHolderSys(convertView);
                convertView.setTag(viewHolderSys);
            } else {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_letter, parent, false);
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
            Glide.with(mActivity).load(R.mipmap.icon_msg_sys).asBitmap().centerCrop().into(new ImageViewTarget<Bitmap>(finalViewHolderSys.rivAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    finalViewHolderSys.rivAvatar.setImageBitmap(resource);
                }
            });
            viewHolderSys.vPoint.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_point_red));
            viewHolderSys.tvContent.setTextColor(ContextCompat.getColor(mActivity, R.color.font_color64));
            viewHolderSys.ivBreak.setImageDrawable(ContextCompat.getDrawable(mActivity, R.mipmap.icon_msg_sys_enter));
            viewHolderSys.vLine.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.line_color6));


            if (isShowSystemMessageRed) {
                viewHolderSys.vPoint.setVisibility(View.VISIBLE);
            } else {
                viewHolderSys.vPoint.setVisibility(View.GONE);
            }
        } else {
            final int index = position - 1;
            LetterListJson bean = dataList.get(index);

            chatNickNameHandle(viewHolder, bean);
            chatLastContentHandle(viewHolder, bean);
            try {
                viewHolder.tvTime.setText(DateUtils.transformTime(Long.parseLong(bean.getDatetime()) * 1000));
            } catch (Exception e) {
                e.printStackTrace();
                viewHolder.tvTime.setText("00:00");
            }
            viewHolder.tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delMsg(index);
                }
            });
            final ViewHolder finalViewHolder = viewHolder;
            Glide.with(mActivity).load(bean.getAvatar()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap
                    .icon_user_def_photo)
                    .centerCrop()
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

    private void chatNickNameHandle(ViewHolder viewHolder, LetterListJson bean) {

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(bean.getNickname());
        if ("1".equals(bean.getIs_banned())) {

            ForegroundColorSpan graySpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.font_color3));
            SpannableString errorSpannable = new SpannableString(" [已屏蔽]");
            errorSpannable.setSpan(graySpan, 0, errorSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableStringBuilder.insert(bean.getNickname().length(), errorSpannable);
        }
        viewHolder.tvName.setText(spannableStringBuilder);
    }

    /**
     * 处理最后一条内容的Handler
     *
     * @param viewHolder
     * @param bean
     */
    private void chatLastContentHandle(ViewHolder viewHolder, LetterListJson bean) {
        SpannableStringBuilder spannableStringBuilder;
        if (bean.getContentType() == 0) {
            spannableStringBuilder = new SpannableStringBuilder(bean.getLast_content());
        } else {
            spannableStringBuilder = new SpannableStringBuilder(bean.getLocal_content());
        }

        ForegroundColorSpan redSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.red2));
        switch (bean.getContentType()) {
            case 0:

                break;
            case 1:
                SpannableString errorSpannable = new SpannableString("[发送失败] ");
                errorSpannable.setSpan(redSpan, 0, errorSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.insert(0, errorSpannable);
                break;
            case 2:
                SpannableString draftSpannable = new SpannableString("[草稿] ");
                draftSpannable.setSpan(redSpan, 0, draftSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.insert(0, draftSpannable);
                break;
        }
        viewHolder.tvContent.convertEmoJeToText(spannableStringBuilder,"[表情]");
    }

    /**
     * 删除回话
     *
     * @param index
     */
    private void delMsg(final int index) {
        if (!SystemUtil.isConnected(mActivity)) {
            ToastSnack.show(mActivity, listView, "网络连接失败");
            return;
        }
        LetterListJson bean = dataList.get(index);

        if (bean.getNum_unread() != null) {
            mActivity.deleteMessage(bean);
        }

        String sender = LoginUtils.getUserInfo(mActivity).getUid();
        String receiver = bean.getReceiver();

        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_RECEIVER, receiver);
        jsonParam.put(VarConstant.HTTP_SENDER, sender);
        request.doGet(HttpConstant.MSG_DEL, jsonParam, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                downHindContentView();
                ToastView.makeText3(mActivity, "删除失败");
            }
        });
        downHindContentView();
        dataList.remove(index);
        notifyDataSetChanged();

        //删除数据库中的失败消息
        String delSql = "DELETE FROM CHAT_ROOM_BEAN WHERE  SENDER = '" + sender + "' AND RECEIVER = '" + receiver + "'";
        DBManager mDBManager = DBManager.getInstance(mActivity);
        ChatRoomJsonDao chatRoomJsonDao = mDBManager.getDaoSessionWrit().getChatRoomJsonDao();
        chatRoomJsonDao.getDatabase().execSQL(delSql);

        //删除本地SP中的草稿信息
        String chatPreviewDraft = SPUtils.getString(mActivity, SpConstant.CHAT_PREVIEW);
        if (!TextUtils.isEmpty(chatPreviewDraft)) {
            List<ChatPreviewJson> chatDraftList = JSONArray.parseArray(chatPreviewDraft, ChatPreviewJson.class);
            if (chatDraftList != null) {
                int indexOf = -1;
                for (int i = 0; i < chatDraftList.size(); i++) {
                    ChatPreviewJson chatPreviewJson = chatDraftList.get(i);
                    if (chatPreviewJson.getReceiver().equals(receiver)) {
                        indexOf = i;
                    }
                }
                if (indexOf != -1) {
                    chatDraftList.remove(indexOf);
                    SPUtils.save(mActivity, SpConstant.CHAT_PREVIEW, JSON.toJSONString(chatDraftList));
                }
            }
        }


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
        viewHolder.tvName.setTextColor(ContextCompat.getColor(mActivity, R.color.font_color64));
        viewHolder.tvTime.setTextColor(ContextCompat.getColor(mActivity, R.color.font_color6));
        viewHolder.tvContent.setTextColor(ContextCompat.getColor(mActivity, R.color.font_color3));
        viewHolder.tvNum.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        viewHolder.tvNum.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_oval_red));
        viewHolder.tvDel.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        viewHolder.tvDel.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.red2));
        viewHolder.vLine.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.line_color6));
    }

    public void setData(List<LetterListJson> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void setShowRed(boolean isShowSystemMessageRed) {
        this.isShowSystemMessageRed = isShowSystemMessageRed;
    }

    static class ViewHolder {
        @BindView(R.id.ll_rootView) LinearLayout llRootView;
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_content) EmoticonReplaceTextView tvContent;
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

    private float currentTranslationX;
    private LinearLayout contentLayout;

    public void translationContentView(int position, float distanceX) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int index = position - firstVisiblePosition;

        View contentView = listView.getChildAt(index);
        View itemView = ((ViewGroup) contentView).getChildAt(0);

        if (itemView instanceof ViewGroup) {
            contentLayout = (LinearLayout) itemView.findViewById(R.id.ll_rootView);

            if (contentLayout != null && currentTranslationX <= 0) {
                if (Math.abs(currentTranslationX) <= deleteViewWidth) {
                    currentTranslationX -= distanceX;

                    if (Math.abs(currentTranslationX) >= deleteViewWidth) {
                        currentTranslationX = -deleteViewWidth;
                    }
                    contentLayout.setTranslationX(currentTranslationX);
                }
            }
        }
    }

    public void upContentView() {
        if (contentLayout == null) {
            return;
        }
        if (Math.abs(currentTranslationX) < deleteViewWidth / 2) {
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
