package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.user.presenter.EditUserInfoPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述: 修改个人信息
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/6.
 */

public class EditUserInfoActivity extends BaseActivity {

    private EditUserInfoPresenter editUserInfoPresenter;

    @BindView(R.id.iv_bar_break)
    ImageView btnBreak;
    @BindView(R.id.tv_bar_title)
    TextView tvTitle;
    @BindView(R.id.iv_bar_function)
    TextView btnSubmit;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.rl_photo)
    RelativeLayout rlPhoto;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.rl_nickname)
    RelativeLayout rlNickname;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.rl_gender)
    RelativeLayout rlGender;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.rl_birthday)
    RelativeLayout rlBirthday;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_address)
    RelativeLayout rlAddress;
    @BindView(R.id.tv_work)
    TextView tvWork;
    @BindView(R.id.rl_work)
    RelativeLayout rlWork;
    @BindView(R.id.rl_changePwd)
    RelativeLayout rlChangePwd;
    @BindView(R.id.btn_exit_login)
    DiscolorButton btnExitLogin;
    @BindView(R.id.fl_picker)
    public FrameLayout fl_picker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edituserinfo, StatusBarColor.THEME1);

        editUserInfoPresenter = new EditUserInfoPresenter(this);

        editUserInfoPresenter.loadCitis();

        tvTitle.setText("个人中心");
        btnSubmit.setText("完成");
    }

    /**
     * 设置居住地
     *
     * @param province
     * @param city
     * @param area
     */
    public void setAddress(String province, String city, String area) {
        tvAddress.setText(province + "-" + city + "-" + area);
    }

    /**
     * 设置生日
     *
     * @param birthday
     */
    public void setBirthday(Date birthday) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(birthday);

        tvBirthday.setText(dateStr);
    }

    /**
     * 设置性别
     *
     * @param gender
     */
    public void setGender(String gender) {
        tvGender.setText(gender);
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function, R.id.rl_photo, R.id.rl_nickname, R.id.rl_gender, R.id.rl_birthday, R.id
            .rl_address, R.id.rl_work, R
            .id.rl_changePwd, R.id.btn_exit_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                //退出
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                //提交更改
                break;
            case R.id.rl_photo:
                //修改头像
                break;
            case R.id.rl_nickname:
                //修改昵称
                break;
            case R.id.rl_gender:
                //修改性别
                editUserInfoPresenter.showPickerGenderView();
                break;
            case R.id.rl_birthday:
                //修改生日
                editUserInfoPresenter.showPickerBirthdayView();
                break;
            case R.id.rl_address:
                //修改地址
                editUserInfoPresenter.showPickerCitisView();
                break;
            case R.id.rl_work:
                //修改工作
                break;
            case R.id.rl_changePwd:
                //修改密码
                break;
            case R.id.btn_exit_login:
                //退出登录
                break;
        }
    }
}
