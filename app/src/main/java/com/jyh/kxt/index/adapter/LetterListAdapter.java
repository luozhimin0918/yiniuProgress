package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.index.json.LetterListJson;
import com.library.util.SystemUtil;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class LetterListAdapter extends BaseListAdapter<LetterListJson> {

    private final int TYPE_SYS = 0;
    private final int TYPE_LETTER = 1;

    private Context mContext;
    private ListView listView;
    private float maxDx;

    public int delPosition = -1;

    public LetterListAdapter(List<LetterListJson> dataList, Context mContext, ListView listView) {
        super(dataList);
        this.mContext = mContext;
        this.listView = listView;
        maxDx = SystemUtil.dp2px(mContext, 60);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        ViewHolderSys viewHolderSys = null;
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
            }
        } else {
            if (type == TYPE_SYS) {
                viewHolderSys = (ViewHolderSys) convertView.getTag();
            } else
                viewHolder = (ViewHolder) convertView.getTag();
        }

        if (type == TYPE_SYS) {

        } else {
            LetterListJson bean = dataList.get(position);
            viewHolder.tvName.setText(bean.getName());
            viewHolder.tvContent.setText(bean.getName());
            viewHolder.tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataList.remove(position);
                    notifyDataSetChanged();
                }
            });

            setTheme(viewHolder);

            if (bean.isShowDelBtn()) {
                showDel(viewHolder.llRootView);
            } else {
                hideDel(viewHolder.llRootView);
            }
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_SYS : TYPE_LETTER;
    }

    private void setTheme(ViewHolder viewHolder) {

    }

    public void showDel(int position, float dx, int type) {
        try {
            if (dx < -maxDx) dx = -maxDx;
            if (dx > 0) return;
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            if (delPosition != -1) {
                int delIndex = delPosition + 1 - firstVisiblePosition;
                dataList.get(delPosition).setShowDelBtn(false);
                if (delIndex >= 0) {
                    ViewGroup delBtnItem = (ViewGroup) ((ViewGroup) listView.getChildAt(delIndex)).getChildAt(0);
                    hideDel(delBtnItem);
                }
            }
            if (position == 0) return;
            int index = position - firstVisiblePosition;
            ViewGroup itemView = (ViewGroup) ((ViewGroup) (listView.getChildAt(index))).getChildAt(0);
            ViewHelper.setTranslationX(itemView.getChildAt(0), dx);
            ViewHelper.setTranslationX(itemView.getChildAt(1), dx);
            ViewHelper.setTranslationX(itemView.getChildAt(2), dx);

            if (type == 1) {
                LetterListJson letterListJson = dataList.get(position - 1);
                if (dx == -maxDx) {
                    letterListJson.setShowDelBtn(true);
                    delPosition = position - 1;
                    notifyDataSetChanged();
                } else if (dx == 0) {
                    letterListJson.setShowDelBtn(false);
                    notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifyDataSetChanged();
        }
    }

    public void hideDel(int position, float dx, int type) {
        try {
            if (dx > maxDx) dx = maxDx;
            if (dx < 0) return;
            int index = position - listView.getFirstVisiblePosition();
            ViewGroup itemView = (ViewGroup) ((ViewGroup) (listView.getChildAt(index))).getChildAt(0);
            ViewHelper.setTranslationX(itemView.getChildAt(0), -maxDx + dx);
            ViewHelper.setTranslationX(itemView.getChildAt(1), -maxDx + dx);
            ViewHelper.setTranslationX(itemView.getChildAt(2), -maxDx + dx);

            if (type == 1) {
                LetterListJson letterListJson = dataList.get(position - 1);
                if (dx == maxDx) {
                    letterListJson.setShowDelBtn(false);
                    notifyDataSetChanged();
                } else if (dx == 0) {
                    letterListJson.setShowDelBtn(true);
                    notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifyDataSetChanged();
        }
    }

    private void showDel(ViewGroup rootView) {
        ViewHelper.setTranslationX(rootView.getChildAt(0), -maxDx);
        ViewHelper.setTranslationX(rootView.getChildAt(1), -maxDx);
        ViewHelper.setTranslationX(rootView.getChildAt(2), -maxDx);
    }

    public void hideDel(ViewGroup rootView) {
        ViewHelper.setTranslationX(rootView.getChildAt(0), 0);
        ViewHelper.setTranslationX(rootView.getChildAt(1), 0);
        ViewHelper.setTranslationX(rootView.getChildAt(2), 0);
    }


    static class ViewHolder {
        @BindView(R.id.ll_rootView) LinearLayout llRootView;
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_content) TextView tvContent;
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
}
