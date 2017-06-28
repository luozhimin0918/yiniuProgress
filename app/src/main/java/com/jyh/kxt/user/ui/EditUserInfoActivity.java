package com.jyh.kxt.user.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.presenter.EditUserInfoPresenter;
import com.library.base.http.VarConstant;
import com.library.util.CommonUtil;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.window.ToastView;

import java.io.File;
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

public class EditUserInfoActivity extends BaseActivity implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {

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
    private PopupUtil popupWindow;
    private EditText edtName;

    /**
     * 图片相关
     */
    private Bitmap lastBmp;
    private byte[] lastByte;
    private String photoFolderAddress;
    private View namePopView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edituserinfo, StatusBarColor.THEME1);

        editUserInfoPresenter = new EditUserInfoPresenter(this);

        if (getIntent().getStringExtra("folderName") == null) {
            photoFolderAddress = CommonUtil.getSDPath() + File.separator + "test_photo";
        } else {
            photoFolderAddress = getIntent().getStringExtra("folderName");
        }

        editUserInfoPresenter.initData();

        editUserInfoPresenter.loadCitis();

        initView();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.CAMERA}, 1000);
    }

    private void initView() {
        UserJson userInfo = LoginUtils.getUserInfo(this);
        tvNickname.setText(userInfo.getNickname());
        tvAddress.setText(userInfo.getAddress());
        tvBirthday.setText(userInfo.getBirthday());
        tvWork.setText(userInfo.getWork());
        switch (userInfo.getSex()) {
            case 0:
                tvGender.setText("保密");
                break;
            case 1:
                tvGender.setText("男");
                break;
            case 2:
                tvGender.setText("女");
                break;
            default:
                tvGender.setText("保密");
                break;
        }

        int imgSize = (int) getResources().getDimension(R.dimen.item_height);
        Glide.with(getContext())
                .load(userInfo.getPicture())
                .asBitmap()
                .override(imgSize, imgSize)

                .error(R.mipmap.icon_user_def_photo)
                .placeholder(R.mipmap.icon_user_def_photo)

                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivPhoto.setImageBitmap(resource);
                    }
                });


//        ViewCompat.setTransitionName(ivPhoto, VIEW_NAME_IMG);
//        ViewCompat.setTransitionName(tvNickname, VIEW_NAME_TITLE);

        tvTitle.setText("个人中心");
    }

    /**
     * 设置居住地
     *
     * @param province
     * @param city
     */
    public void changeAddress(String province, String city) {
        String oldAddress = tvAddress.getText().toString();
        String address = province + "-" + city;
        tvAddress.setText(address);
        editUserInfoPresenter.postChangedInfo(address, oldAddress, VarConstant.HTTP_ADDRESS);
    }

    /**
     * 设置生日
     *
     * @param birthday
     */
    public void changeBirthday(Date birthday) {
        String oldBirthday = tvBirthday.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(birthday);
        tvBirthday.setText(dateStr);
        editUserInfoPresenter.postChangedInfo(dateStr, oldBirthday, VarConstant.HTTP_BIRTHDAY);
    }

    /**
     * 设置工作
     *
     * @param work
     */
    public void changeWork(String work) {
        String oldWork = tvWork.getText().toString();
        tvWork.setText(work);
        editUserInfoPresenter.postChangedInfo(work, oldWork, VarConstant.HTTP_WORK);
    }

    /**
     * 设置性别
     *
     * @param gender
     */
    public void changeGender(String gender) {
        String oldGender = tvGender.getText().toString();
        tvGender.setText(gender);

        String sexInt;
        switch (gender) {
            case "保密":
                sexInt="0";
                break;
            case "男":
                sexInt="1";
                break;
            case "女":
                sexInt="2";
                break;
            default:
                sexInt="0";
                break;
        }

        editUserInfoPresenter.postChangedInfo(sexInt, oldGender, VarConstant.HTTP_SEX);
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
            case R.id.rl_photo:
                //修改头像
                editUserInfoPresenter.showPop(getContext(), rlPhoto);
                break;
            case R.id.rl_nickname:
                //修改昵称
                DisplayMetrics metrics = SystemUtil.getScreenDisplay(this);
                if (popupWindow == null)
                    initNameChangePop(metrics);
                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                //让编辑框弹出来，并显示对谁进行评论
                edtName.setFocusable(true);
                edtName.setFocusableInTouchMode(true);
                edtName.requestFocus();
                //打开软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

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
                editUserInfoPresenter.showWork();
                break;
            case R.id.rl_changePwd:
                //修改密码
                Intent changPwdIntent = new Intent(this, ChangePwdActivity.class);
                startActivity(changPwdIntent);
                break;
        }
    }

    /**
     * 初始化修改昵称的popuwindow
     *
     * @param metrics
     */
    private void initNameChangePop(DisplayMetrics metrics) {
        popupWindow = new PopupUtil(this);
        namePopView = popupWindow.createPopupView(R.layout.dialog_edittext);
        edtName = (EditText) namePopView.findViewById(R.id.edt_name);
        final TextView tvNum = (TextView) namePopView.findViewById(R.id.tv_num);
        TextView tvSure = (TextView) namePopView.findViewById(R.id.tv_sure);
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvNum.setText(s.length() + "/10");
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable text = edtName.getText();
                if (text == null || text.toString().trim().equals("")) {
                    ToastView.makeText3(getContext(), "昵称不能为空");
                    return;
                }

                int textLength = text.toString().length();
                if (textLength > 10) {
                    ToastView.makeText3(getContext(), "昵称字符不能大于10");
                    return;
                }
                String name = text.toString().trim();
                String oldName = tvNickname.getText().toString();
                tvNickname.setText(name);
                editUserInfoPresenter.postChangedInfo(name, oldName, VarConstant.HTTP_NICKNAME);

                popupWindow.dismiss();
            }
        });


        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.WRAP_CONTENT;

        popupWindow.setConfig(config);

        SoftKeyBoardListener.setListener(this, this);

        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editUserInfoPresenter.takePicture();
                } else {
                    ToastView.makeText3(this, "不开启权限无法拍照喔");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        editUserInfoPresenter.onActivityResult(this, requestCode, resultCode, data, ivPhoto, lastBmp, lastByte,
                photoFolderAddress);
    }

    /**
     * 设置图片
     *
     * @param bitmap
     * @param bitmapByte
     */
    public void setBitmapAndByte(Bitmap bitmap, byte[] bitmapByte) {
        lastBmp = bitmap;
        lastByte = bitmapByte;
        ivPhoto.setImageBitmap(bitmap);
        //提交图片
        editUserInfoPresenter.postBitmap(lastByte);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(editUserInfoPresenter.getClass().getName());
    }

    /**
     * 还原昵称
     *
     * @param oldValue
     */
    public void restoreNickName(String oldValue) {
        tvNickname.setText(oldValue);
    }

    /**
     * 还原性别
     *
     * @param oldValue
     */
    public void restoreGender(String oldValue) {
        tvGender.setText(oldValue);
    }

    /**
     * 还原生日
     *
     * @param oldValue
     */
    public void restoreBirthday(String oldValue) {
        tvBirthday.setText(oldValue);
    }

    /**
     * 还原地址
     *
     * @param oldValue
     */
    public void restoreAddress(String oldValue) {
        tvAddress.setText(oldValue);
    }

    /**
     * 还原工作
     *
     * @param oldValue
     */
    public void restoreWork(String oldValue) {
        tvWork.setText(oldValue);
    }

    public void restorePhoto() {
        UserJson userInfo = LoginUtils.getUserInfo(this);
        int imgSize = (int) getResources().getDimension(R.dimen.item_height);
        Glide.with(getContext())
                .load(userInfo.getPicture())
                .asBitmap()
                .override(imgSize, imgSize)

                .error(R.mipmap.icon_user_def_photo)
                .placeholder(R.mipmap.icon_user_def_photo)

                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivPhoto.setImageBitmap(resource);
                    }
                });
    }

    @Override
    public void keyBoardShow(int height) {
//        ViewGroup.LayoutParams layoutParams = namePopView.getLayoutParams();
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height = height + namePopView.getHeight();
//        namePopView.setLayoutParams(layoutParams);
    }

    @Override
    public void keyBoardHide(int height) {
        popupWindow.dismiss();
    }

}
