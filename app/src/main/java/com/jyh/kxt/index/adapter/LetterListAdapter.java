package com.jyh.kxt.index.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
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

    private float deleteViewWidth;
    private float chartContentWidth;


    public LetterListAdapter(List<LetterListJson> dataList, Context mContext, ListView listView) {
        super(dataList);
        this.mContext = mContext;
        this.listView = listView;

        deleteViewWidth = SystemUtil.dp2px(mContext, 60);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(mContext);
        chartContentWidth = screenDisplay.widthPixels + deleteViewWidth;
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
