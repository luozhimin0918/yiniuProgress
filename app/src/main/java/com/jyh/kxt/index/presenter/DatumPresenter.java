package com.jyh.kxt.index.presenter;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.library.widget.datetimepicker.fourmob.datetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public void registerFiltrateAgency(View filtrateView) {
        ButterKnife.bind(this, filtrateView);

        olState.simpleInitConfig();
        olState.setSelectMode(1);

        olImportance.simpleInitConfig();
        olImportance.setSelectMode(1);

        olArea.simpleInitConfig();
        olArea.setSelectMode(1);

        olJudge.simpleInitConfig();
        olJudge.setMaxSelectCount(2);


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
}
