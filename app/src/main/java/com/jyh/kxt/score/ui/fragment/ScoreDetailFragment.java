package com.jyh.kxt.score.ui.fragment;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.presenter.PullListViewPresenter;
import com.jyh.kxt.score.adapter.ScoreDetailDayAdapter;
import com.jyh.kxt.score.adapter.ScoreDetailMonthAdapter;
import com.jyh.kxt.score.json.ScoreDetailDayJson;
import com.jyh.kxt.score.json.ScoreDetailMonthJson;
import com.library.base.http.VarConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 项目名:KxtProfessional
 * 类描述:金币明细
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class ScoreDetailFragment extends BaseFragment {

    public static final String TYPE = "type";
    public static final String TYPE_DAY = "1";
    public static final String TYPE_MONTH = "2";

    @BindView(R.id.pl_rootView) public FrameLayout plRootView;

    private String type;
    private PullListViewPresenter presenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_score_detail);
        type = getArguments().getString(TYPE);
        presenter = new PullListViewPresenter(this);
        presenter.createView(plRootView);
        presenter.setLoadMode(PullListViewPresenter.LoadMode.PAGE_LOAD);
        JSONObject parameterJson = new JSONObject();
        parameterJson.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(getContext()).getUid());

        if (TYPE_DAY.equals(type)) {
            List<ScoreDetailDayJson> scoreDetailDayJsons = new ArrayList<>();
            ScoreDetailDayAdapter scoreDetailDayAdapter = new ScoreDetailDayAdapter(scoreDetailDayJsons, getContext());
            presenter.setRequestInfo(HttpConstant.CREDITS_DETAIL, parameterJson, ScoreDetailDayJson.class);
            presenter.setAdapter(scoreDetailDayAdapter);
        } else {
            List<ScoreDetailMonthJson> scoreDetailMonthJsons = new ArrayList<>();
            ScoreDetailMonthAdapter scoreDetailMonthAdapter = new ScoreDetailMonthAdapter(scoreDetailMonthJsons, getContext());
            presenter.setRequestInfo(HttpConstant.CREDITS_MON_SUM, parameterJson, ScoreDetailMonthJson.class);
            presenter.setAdapter(scoreDetailMonthAdapter);
        }
        presenter.startRequest();
        presenter.setOnLoadMoreListener(new PullListViewPresenter.OnLoadMoreListener() {
            @Override
            public void beforeParameter(List dataList, JSONObject jsonObject) {
                Object obj = dataList.get(dataList.size() - 1);
                if (obj instanceof ScoreDetailDayJson) {
                    jsonObject.put(VarConstant.HTTP_LASTID, "");
                } else if (obj instanceof ScoreDetailMonthJson) {
                    jsonObject.put(VarConstant.HTTP_LASTID, "");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}