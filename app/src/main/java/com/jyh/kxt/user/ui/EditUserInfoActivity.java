package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.user.presenter.EditUserInfoPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.window.ToastView;

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

    @BindView(R.id.pl_rootView)
    public PageLoadLayout plRootView;
    @BindView(R.id.iv_bar_break)
    ImageView btnBreak;
    @BindView(R.id.tv_bar_title)
    TextView tvTitle;
    @BindView(R.id.iv_bar_function)
    TextView btnSubmit;
    @BindView(R.id.iv_photo)
    RoundImageView ivPhoto;
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
    @BindView(R.id.fl_picker)
    public FrameLayout fl_picker;

    public static final String VIEW_NAME_IMG = "view_name_img";
    public static final String VIEW_NAME_TITLE = "view_name_title";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edituserinfo, StatusBarColor.THEME1);

        editUserInfoPresenter = new EditUserInfoPresenter(this);

        editUserInfoPresenter.initData();

        editUserInfoPresenter.loadCitis();

//        UserJson userInfo = LoginUtils.getUserInfo(this);
//        tvNickname.setText(userInfo.getNickname());
//        Glide.with(this).load(userInfo.getPicture()).override(50, 50).error(R.mipmap.icon_user_def_photo).error(R.mipmap.icon_user_def_photo).into(ivPhoto);

        ViewCompat.setTransitionName(ivPhoto, VIEW_NAME_IMG);
        ViewCompat.setTransitionName(tvNickname, VIEW_NAME_TITLE);

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
            .id.rl_changePwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                //退出
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                //提交更改
                showWaitDialog(null);
                editUserInfoPresenter.postChangedInfo();
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
                if (editUserInfoPresenter.isLoadCityInfoError) {
                    //加载失败
                    if (editUserInfoPresenter.isLoadCityInfoOver)
                        editUserInfoPresenter.loadCitis();//重新加载
                    else
                        ToastView.makeText3(this, "信息加载中");//已重新加载中,等待加载完毕
                }
                if (editUserInfoPresenter.isLoadCityInfoOver)//加载完毕,直接显示选择器
                    editUserInfoPresenter.showPickerCitisView();
                else
                    ToastView.makeText3(this, "信息加载中");//加载中,等待加载完毕
                break;
            case R.id.rl_work:
                //修改工作
                break;
            case R.id.rl_changePwd:
                //修改密码
                break;
        }
    }

}
