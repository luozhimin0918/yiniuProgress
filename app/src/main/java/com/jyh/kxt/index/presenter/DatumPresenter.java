package com.jyh.kxt.index.presenter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.DiscolorTextView;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.datetimepicker.fourmob.datetimepicker.date.DatePickerDialog;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class DatumPresenter extends BasePresenter implements DatePickerDialog.OnDateSetListener {

    @BindObject DatumFragment datumFragment;


    public List<String> mAreaList = new ArrayList<>();

    public DatumPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 注册 筛选的 代理方法
     *
     * @param filtrateView
     */

    @BindView(R.id.ol_state) OptionLayout olState;
    @BindView(R.id.ol_importance) OptionLayout olImportance;
    @BindView(R.id.ol_area) OptionLayout olArea;
    @BindView(R.id.ol_judge) OptionLayout olJudge;
    @BindView(R.id.iv_update_area) ImageView ivUpdateArea;

    @BindView(R.id.dtv_reset) DiscolorTextView dtvReset;
    @BindView(R.id.dtv_confirm) DiscolorTextView dtvConfirm;

    private RotateAnimation mRotateAnimation;
    private boolean isRotateAnimationIng = false;

    public void registerFiltrateAgency(View filtrateView) {
        ButterKnife.bind(this, filtrateView);

        olState.setMinSelectCount(1);
        olState.setMaxSelectCount(2);
        olState.setSelectItemIndex(datumFragment.getCalendarFragment().stateSet);
        olState.setSelectMode(OptionLayout.SelectMode.RadioMode);

        olImportance.setMinSelectCount(1);
        olImportance.setMaxSelectCount(3);
        olImportance.setSelectItemIndex(datumFragment.getCalendarFragment().importanceSet);
        olImportance.setSelectMode(OptionLayout.SelectMode.CheckMode);

        olJudge.setMinSelectCount(1);
        olJudge.setSelectItemIndex(datumFragment.getCalendarFragment().judgeSet);
        olJudge.setMaxSelectCount(3);
        olJudge.setSelectMode(OptionLayout.SelectMode.CheckMode);

        //重置
        dtvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                olState.setSelectItemIndex(0);
                olImportance.setSelectItemIndex(0);
                olArea.setSelectItemIndex(0);
                olJudge.setSelectItemIndex(0);
            }
        });
        //确定
        dtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.save(mContext, SpConstant.DATUM_STATE, olState.getSelectedMap());
                SPUtils.save(mContext, SpConstant.DATUM_IMPORTANCE, olImportance.getSelectedMap());
                SPUtils.save(mContext, SpConstant.DATUM_AREA, olArea.getSelectedMap());
                SPUtils.save(mContext, SpConstant.DATUM_JUDGE, olJudge.getSelectedMap());

                datumFragment.filtratePopup.dismiss();
                datumFragment.getCalendarFragment().initializeFiltrationSet();
                datumFragment.getCalendarFragment().updateFiltration();
            }
        });


        if (mAreaList.size() == 0) {
            olArea.setVisibility(View.GONE);
            ivUpdateArea.setVisibility(View.VISIBLE);

            ivUpdateArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SystemUtil.isConnected(mContext)) {
                        ToastView.makeText(mContext, "网络异常请稍后");
                        return;
                    }

                    if (!isRotateAnimationIng) {

                        mRotateAnimation = new RotateAnimation(0f,
                                360f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f);

                        LinearInterpolator lin = new LinearInterpolator();
                        mRotateAnimation.setInterpolator(lin);
                        mRotateAnimation.setDuration(1000);//设置动画持续时间
                        mRotateAnimation.setRepeatCount(-1);//设置重复次数
                        mRotateAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                        mRotateAnimation.setStartOffset(10);//执行前的等待时间
                        v.startAnimation(mRotateAnimation);

                        requestAreaInfo(true);
                        isRotateAnimationIng = true;
                    }
                }
            });
        } else {

            olArea.setMinSelectCount(1);
            olArea.generateCheckBox(mAreaList);
            olArea.setMaxSelectCount(mAreaList.size());
            olArea.setSelectMode(OptionLayout.SelectMode.CheckMode);

            Set<String> areaSet = datumFragment.getCalendarFragment().areaSet;
            if (areaSet.size() != 0) {
                olArea.setSelectItemIndex(areaSet);
            } else {
                olArea.setSelectItemIndex(0);
            }
        }
    }


    /**
     * 日历选择器
     *
     * @return
     */
    public void openCalendar() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog;
        datePickerDialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                false);

        datePickerDialog.setYearRange(1985, 2028);

        datePickerDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        GregorianCalendar mGregorianCalendar = new GregorianCalendar(year, month - 1, day); //选择的时间
        datumFragment.gotoCorrespondItem(mGregorianCalendar.getTimeInMillis());

    }

    public void requestAreaInfo(final boolean isAlreadyDisplay) {
        mAreaList.clear();
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        volleyRequest.doPost(HttpConstant.DATA_COUNTRY, jsonParam, new HttpListener<List<String>>() {
            @Override
            protected void onResponse(List<String> mAreaResult) {

                isRotateAnimationIng = false;

                mAreaResult.add(0, "全部");
                mAreaList.addAll(mAreaResult);

                if (isAlreadyDisplay) {
                    mRotateAnimation.cancel();
                    ivUpdateArea.clearAnimation();

                    olArea.removeAllViews();
                    olArea.setMinSelectCount(1);
                    olArea.generateCheckBox(mAreaList);
                    olArea.setMaxSelectCount(mAreaList.size());
                    olArea.setSelectMode(OptionLayout.SelectMode.CheckMode);

                    Set<String> areaSet = datumFragment.getCalendarFragment().areaSet;
                    if (areaSet.size() != 0) {
                        olArea.setSelectItemIndex(areaSet);
                    } else {
                        olArea.setSelectItemIndex(0);
                    }

                    ivUpdateArea.setVisibility(View.GONE);
                    olArea.setVisibility(View.VISIBLE);
                }

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (isAlreadyDisplay) {
                    mRotateAnimation.cancel();
                    ivUpdateArea.setVisibility(View.GONE);
                }
            }
        });
    }
}
