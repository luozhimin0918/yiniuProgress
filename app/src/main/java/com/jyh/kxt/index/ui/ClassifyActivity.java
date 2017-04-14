package com.jyh.kxt.index.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.index.presenter.ClassifyPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:分类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class ClassifyActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.ol_content) OptionLayout olContent;
    private ClassifyPresenter classifyPresenter;

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_classify, StatusBarColor.THEME1);
        classifyPresenter = new ClassifyPresenter(this);

        ivBarBreak.setVisibility(View.INVISIBLE);
        ivBarFunction.setImageResource(R.mipmap.ico_break);
        tvBarTitle.setText("分类");

        index = getIntent().getIntExtra(IntentConstant.INDEX, 0);
        olContent.setSelectItemIndex(index);

        olContent.setOnItemCheckBoxClick(new OptionLayout.OnItemCheckBoxClick() {
            @Override
            public void onItemClick(int position, CheckBox mCheckBox) {
                olContent.setSelectItemIndex(position);
                Intent intent = new Intent();
                intent.putExtra(IntentConstant.INDEX, position);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        index = intent.getIntExtra(IntentConstant.INDEX, 0);
        olContent.setSelectItemIndex(index);
    }

    @OnClick(R.id.iv_bar_function)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_function:
                finish();
                break;
        }
    }
}
