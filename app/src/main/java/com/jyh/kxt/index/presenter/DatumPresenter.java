package com.jyh.kxt.index.presenter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.custom.DiscolorTextView;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.jyh.kxt.datum.ui.fragment.CalendarItemFragment;
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.library.widget.datetimepicker.fourmob.datetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class DatumPresenter extends BasePresenter implements DatePickerDialog.OnDateSetListener {

    @BindObject DatumFragment datumFragment;


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

//    private RotateAnimation mRotateAnimation;
//    private boolean isRotateAnimationIng = false;

    public void registerFiltrateAgency(View filtrateView,
                                       final CalendarFragment calendarFragment,
                                       final CalendarItemFragment mCalendarItemFragment) {
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

        HashMap<CalendarItemFragment, HashSet<String>> cityOptionMap = calendarFragment.cityOptionMap;
        HashSet<String> cityDefaultData = cityOptionMap.get(mCalendarItemFragment);
        List myCityList = new ArrayList(cityDefaultData);
        myCityList.add(0, "全部");

        HashMap<CalendarItemFragment, HashSet<String>> citySelectMap = calendarFragment.citySelectMap;
        HashSet<String> citySelectData = citySelectMap.get(mCalendarItemFragment);

        olArea.setMinSelectCount(1);
        olArea.generateCheckBox(myCityList);
        olArea.setMaxSelectCount(myCityList.size());
        olArea.setSelectMode(OptionLayout.SelectMode.CheckMode);

        if (citySelectData.size() == 0) {
            olArea.setSelectItemIndex(0);
        } else {
            olArea.setSelectItemIndex(citySelectData);
        }

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
                datumFragment.getCalendarFragment().stateSet = olState.getSelectedMap();
                datumFragment.getCalendarFragment().importanceSet = olImportance.getSelectedMap();
                datumFragment.getCalendarFragment().judgeSet = olJudge.getMYSelectedMap();
//                SPUtils.save(mContext, SpConstant.DATUM_STATE, olState.getSelectedMap());
//                SPUtils.save(mContext, SpConstant.DATUM_IMPORTANCE, olImportance.getSelectedMap());
//                SPUtils.save(mContext, SpConstant.DATUM_JUDGE, olJudge.getSelectedMap());
//                SPUtils.save(mContext, SpConstant.DATUM_AREA, olArea.getSelectedMap());

                calendarFragment.updateSelectedCityDataFromFragment(mCalendarItemFragment, olArea.getSelectedMap());

                datumFragment.filtratePopup.dismiss();
//                datumFragment.getCalendarFragment().initializeFiltrationSet();
                datumFragment.getCalendarFragment().updateFiltration();
            }
        });

        /*if (mAreaList.size() == 0) {
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
        }*/
    }


    /**
     * 日历选择器
     *
     * @param currentTabDate
     * @return
     */
    public void openCalendar(String currentTabDate) {
        Calendar calendar = Calendar.getInstance();

        String[] splitDate = currentTabDate.split("-");
        int year = Integer.parseInt(splitDate[0]);
        int month = Integer.parseInt(splitDate[1]) - 1;
        int dayOfMonth = Integer.parseInt(splitDate[2]);

        DatePickerDialog datePickerDialog;
        datePickerDialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                false);

        datePickerDialog.setYearRange(1985, 2028);
        datePickerDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
        datePickerDialog.onDayOfMonthSelected(year, month, dayOfMonth);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        GregorianCalendar mGregorianCalendar = new GregorianCalendar(year, month - 1, day); //选择的时间
        datumFragment.gotoCorrespondItem(mGregorianCalendar.getTimeInMillis());

    }

    public void requestAreaInfo(final boolean isAlreadyDisplay) {
        /*mAreaList.clear();
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
        });*/
    }


}
