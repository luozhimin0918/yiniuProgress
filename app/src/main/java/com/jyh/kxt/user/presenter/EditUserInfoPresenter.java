package com.jyh.kxt.user.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.GetJsonDataUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.photo.PhotoTailorUtil;
import com.jyh.kxt.user.json.CityBean;
import com.jyh.kxt.user.json.ProvinceJson;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.EditUserInfoActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.BitmapUtils;
import com.library.util.DateUtils;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;
import com.library.widget.pickerview.OptionsPickerView;
import com.library.widget.pickerview.TimePickerView;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/6.
 */

public class EditUserInfoPresenter extends BasePresenter implements View.OnClickListener, PhotoTailorUtil.OnCompleteListener {

    @BindObject
    EditUserInfoActivity activity;

    private OptionsPickerView cityPicker, genderPicker, workPicker;
    private TimePickerView birthdayPicker;

    private ArrayList<ProvinceJson> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//省-市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//省-市-县

    private int pickerTextSize = 20;//选择器文字大小
    public boolean isLoadCityInfoError = false;//加载省市县信息是否失败
    public boolean isLoadCityInfoOver = false;//加载省市县信息是否完毕

    private String province;//省
    private String city;//市
    private String address;//地区
    private int sexInt;//性别 0 保密 1 男 2 女
    private String birthdayStr;//年龄 1999-11-01
    private String work;//工作

    private VolleyRequest request;

    /**
     * 图片相关
     */
    private TextView openTake;
    private TextView openDiffer;
    private TextView cancelWindow;
    private PopupUtil popupWindow;
    private PhotoTailorUtil photoTailorUtil;

    public EditUserInfoPresenter(IBaseView iBaseView) {
        super(iBaseView);
        photoTailorUtil = new PhotoTailorUtil();
        photoTailorUtil.initPath((Activity) iBaseView);
        photoTailorUtil.setOnCompleteListener(this);
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
    }

    /**
     * 显示地区选择界面
     */
    public void showPickerCitisView() {
        if (cityPicker == null) {

            int provinceSel = 0;
            int citySel = 0;

            if (!RegexValidateUtil.isEmpty(province) && !RegexValidateUtil.isEmpty(city)) {
                int size = options1Items.size();
                for (int i = 0; i < size; i++) {
                    ProvinceJson provinceJson = options1Items.get(i);
                    if (province.equals(provinceJson.getName())) {
                        provinceSel = i;
                    }
                    List<CityBean> cityList = provinceJson.getCityList();
                    int size1 = cityList.size();
                    for (int j = 0; j < size1; j++) {
                        if (city.equals(cityList.get(j).getName())) {
                            citySel = j;
                        }
                    }
                }
            }

            cityPicker = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    String province = options1Items.get(options1).getPickerViewText();
                    String city = options2Items.get(options1).get(options2);
                    activity.changeAddress(province, city);
                    address = province + "-" + city;
                }
            })

                    .setTitleText("")
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.line_color3))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.font_color5)) //设置选中项文字颜色
                    .setContentTextSize(pickerTextSize)
                    .setOutSideCancelable(true)// default is true
                    .setDecorView(activity.fl_picker)
                    .setTitleBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_center))
                    .setTextColorOut(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_out))
                    .setBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_divider))
                    .setSelectOptions(provinceSel, citySel)
                    .build();
            cityPicker.setPicker(options1Items, options2Items);
        }
        if (cityPicker.isShowing()) {
            return;
        }
        cityPicker.show();
    }

    /**
     * 显示性别选择器
     */
    public void showPickerGenderView() {
        if (genderPicker == null) {
            final ArrayList<String> genders = new ArrayList<>();
            genders.add("保密");
            genders.add("男");
            genders.add("女");
            genderPicker = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    activity.changeGender(genders.get(options1));
                    sexInt = options1;
                }
            })

                    .setTitleText("")
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.line_color3))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.font_color5)) //设置选中项文字颜色
                    .setSelectOptions(sexInt)
                    .setContentTextSize(pickerTextSize)
                    .setOutSideCancelable(true)// default is true
                    .setTitleBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_center))
                    .setTextColorOut(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_out))
                    .setBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_divider))
                    .setDecorView(activity.fl_picker)
                    .build();

            genderPicker.setPicker(genders);//一级选择器
        }
        if (genderPicker.isShowing()) {
            return;
        }
        genderPicker.show();
    }

    /**
     * 显示工作选择器
     */
    public void showWork() {
        if (workPicker == null) {
            String[] works = mContext.getResources().getStringArray(R.array.work);
            final List<String> worksList = Arrays.asList(works);

            int selPosition = 0;
            if (!RegexValidateUtil.isEmpty(work) && worksList.contains(work)) {
                selPosition = worksList.indexOf(work);
            }

            workPicker = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    work = worksList.get(options1);
                    activity.changeWork(work);
                }
            })

                    .setTitleText("")
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.line_color3))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.font_color5)) //设置选中项文字颜色
                    .setSelectOptions(selPosition)
                    .setTitleBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_center))
                    .setTextColorOut(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_out))
                    .setBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_divider))
                    .setContentTextSize(pickerTextSize)
                    .setOutSideCancelable(true)// default is true
                    .setDecorView(activity.fl_picker)
                    .build();

            workPicker.setPicker(worksList);//一级选择器
        }
        if (workPicker.isShowing()) {
            return;
        }
        workPicker.show();
    }

    /**
     * 显示日期选择器
     */
    public void showPickerBirthdayView() {
        if (birthdayPicker == null) {
            Calendar selectedDate = Calendar.getInstance();
            try {
                selectedDate = DateUtils.stringToCalendar(birthdayStr, DateUtils.TYPE_YMD);
            } catch (Exception e) {
                e.printStackTrace();
                selectedDate.set(1990, 0, 1);
            }
            Calendar startDate = Calendar.getInstance();
            startDate.set(1900, 0, 1);
            Calendar endDate = Calendar.getInstance();
            birthdayPicker = new TimePickerView.Builder(activity, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    activity.changeBirthday(date);
                    try {
                        birthdayStr = DateUtils.dateToString(date, DateUtils.TYPE_YMD);
                    } catch (Exception e) {
                        e.printStackTrace();
                        birthdayStr = null;
                    }
                }
            })
                    .setTitleText("")
                    .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                    .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示
                    .setDividerColor(Color.DKGRAY)
                    .setContentSize(pickerTextSize)
                    .setDate(selectedDate)
                    .setTitleBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setRangDate(startDate, endDate)
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_center))
                    .setTextColorOut(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_out))
                    .setBgColor(ContextCompat.getColor(mContext, R.color.theme1))
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.pickerview_wheelview_textcolor_divider))
                    .setDecorView(activity.fl_picker)
                    .build();
        }
        if (birthdayPicker.isShowing()) {
            return;
        }
        birthdayPicker.show();
    }

    /**
     * 加载省市县信息
     */
    public void loadCitis() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            //解析数据
            /**
             * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
             * 关键逻辑在于循环体
             * */
            try {
                isLoadCityInfoOver = false;
                String JsonData = new GetJsonDataUtil().getJson(mContext, "province.json");//获取assets目录下的json文件数据
                ArrayList<ProvinceJson> jsonBean = parseData(JsonData);//用Gson 转成实体
                /**
                 * 添加省份数据
                 *
                 * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
                 * PickerView会通过getPickerViewText方法获取字符串显示出来。
                 */
                options1Items = jsonBean;
                for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
                    ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

                    for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                        String CityName = jsonBean.get(i).getCityList().get(c).getName();
                        CityList.add(CityName);//添加城市

                        ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                        if (jsonBean.get(i).getCityList().get(c).getArea() == null
                                || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                            City_AreaList.add("");
                        } else {

                            for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                                String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                                City_AreaList.add(AreaName);//添加该城市所有地区数据
                            }
                        }
                        Province_AreaList.add(City_AreaList);//添加该省所有地区数据
                    }
                    /**
                     * 添加城市数据
                     */
                    options2Items.add(CityList);
                    /**
                     * 添加地区数据
                     */
                    options3Items.add(Province_AreaList);

                    isLoadCityInfoError = false;
                    isLoadCityInfoOver = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                options1Items.clear();
                options2Items.clear();
                options3Items.clear();
                isLoadCityInfoError = true;
                isLoadCityInfoOver = true;
            }
        }
    });

    public ArrayList<ProvinceJson> parseData(String result) throws JSONException {
        ArrayList<ProvinceJson> detail = new ArrayList<>();
        JSONArray array = new JSONArray(result);
        int prpvinceNum = array.length();
        for (int i = 0; i < prpvinceNum; i++) {
            ProvinceJson provinceBean = new ProvinceJson();
            List<CityBean> cityBeens = new ArrayList<>();
            JSONObject province = array.getJSONObject(i);
            provinceBean.setName(province.getString("name"));
            JSONArray cities = province.getJSONArray("city");
            int cityNum = cities.length();
            for (int j = 0; j < cityNum; j++) {
                //得到城市
                CityBean cityBean = new CityBean();
                JSONObject cityJson = cities.getJSONObject(j);
                cityBean.setName(cityJson.getString("name"));
                cityBean.setArea(JSON.parseArray(cityJson.getJSONArray("area").toString(), String.class));
                cityBeens.add(cityBean);
            }
            provinceBean.setCityList(cityBeens);
            detail.add(provinceBean);
        }
        return detail;
    }

    public void initData() {
        try {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            //初始化地址
            String address = userInfo.getAddress();
            if (address != null) {
                String[] split = address.split("-");
                if (split != null && split.length >= 2) {
                    province = split[0];
                    city = split[1];
                }
            } else {
                activity.restoreAddress("上海-浦东新区");
            }
            //初始化性别
            sexInt = userInfo.getSex();
            switch (sexInt) {
                case 0:
                    activity.restoreGender("保密");
                    break;
                case 1:
                    activity.restoreGender("男");
                    break;
                case 2:
                    activity.restoreGender("女");
                    break;
                default:
                    activity.restoreGender("保密");
                    break;
            }
            //初始化年龄
            birthdayStr = userInfo.getBirthday();
            birthdayStr = RegexValidateUtil.isEmpty(birthdayStr) ? "1980-01-01" : birthdayStr;
            //初始化工作
            work = userInfo.getWork();
            work = RegexValidateUtil.isEmpty(work) ? "金融" : work;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TSnackbar snackBar;

    public void postChangedInfo(final String newValue, final String oldValue, final String type) {
        snackBar = TSnackbar.make(activity.plRootView, "信息更改中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN) ;
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
        snackBar.show();

        request.doPost(HttpConstant.USER_CHANEINFO, getPostInfo(request, newValue, type), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                try {
                    UserJson oldUser = LoginUtils.getUserInfo(mContext);
                    UserJson newUser = oldUser;
                    switch (type) {
                        case VarConstant.HTTP_NICKNAME:
                            newUser.setNickname(newValue);
                            break;
                        case VarConstant.HTTP_SEX:
                            newUser.setSex(sexInt);
                            break;
                        case VarConstant.HTTP_BIRTHDAY:
                            newUser.setBirthday(birthdayStr);
                            break;
                        case VarConstant.HTTP_ADDRESS:
                            newUser.setAddress(address);
                            break;
                        case VarConstant.HTTP_WORK:
                            newUser.setWork(work);
                            break;
                    }
                    LoginUtils.changeUserInfo(mContext, newUser);
                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_CHANGEUSERINFO, newUser));
                    snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("信息更改成功").setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    postError(type, oldValue);
                    snackBar.setPromptThemBackground(Prompt.ERROR).setText("信息更改失败").setDuration(TSnackbar.LENGTH_LONG)
                            .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                postError(type, oldValue);
                snackBar.setPromptThemBackground(Prompt.ERROR).setText("信息更改失败")
                        .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).setDuration(TSnackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * 提交失败
     *
     * @param type
     */
    private void postError(String type, String oldValue) {
        ToastView.makeText3(mContext, "信息提交失败");
        switch (type) {
            case VarConstant.HTTP_NICKNAME:
                activity.restoreNickName(oldValue);
                break;
            case VarConstant.HTTP_SEX:
                activity.restoreGender(oldValue);
                break;
            case VarConstant.HTTP_BIRTHDAY:
                activity.restoreBirthday(oldValue);
                break;
            case VarConstant.HTTP_ADDRESS:
                activity.restoreAddress(oldValue);
                break;
            case VarConstant.HTTP_WORK:
                activity.restoreWork(oldValue);
                break;
        }
    }

    /**
     * 获取提交信息
     *
     * @param request
     * @param newValue
     * @param type
     * @return
     */
    private Map<String, String> getPostInfo(VolleyRequest request, String newValue, String type) {
        com.alibaba.fastjson.JSONObject jsonParam = request.getJsonParam();
        UserJson userJson = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userJson.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userJson.getToken());
        jsonParam.put(type, newValue);
        Map<String, String> map = new HashMap();
        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 显示图片选择器
     *
     * @param context
     * @param rlPhoto
     */
    public void showPop(Context context, RelativeLayout rlPhoto) {
        if (popupWindow == null)
            initPhotoPop(context);
        popupWindow.showAtLocation(rlPhoto, Gravity.BOTTOM, 0, 0);

    }

    private void initPhotoPop(Context context) {

        popupWindow = new PopupUtil(activity);
        View view = popupWindow.createPopupView(R.layout.popup_selectimg_view);

        openTake = (TextView) view.findViewById(R.id.selectimg_open_take);
        openDiffer = (TextView) view.findViewById(R.id.selectimg_open_differ);
        cancelWindow = (TextView) view.findViewById(R.id.selectimg_open_cancel);
        View spaceView = view.findViewById(R.id.selectimg_open_space);
        openTake.setOnClickListener(this);
        openDiffer.setOnClickListener(this);
        cancelWindow.setOnClickListener(this);
        view.setOnClickListener(this);

        if (SystemUtil.navigationBar(context) > 10) {
            ViewGroup.LayoutParams layoutParams = spaceView.getLayoutParams();
            layoutParams.height = SystemUtil.navigationBar(context);
            spaceView.setLayoutParams(layoutParams);
        }

        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.WRAP_CONTENT;
        popupWindow.setConfig(config);
    }

    /**
     * 拍照
     */
    public void takePicture() {
        photoTailorUtil.startToCamera();
    }

    public void onActivityResult(EditUserInfoActivity editUserInfoActivity, int requestCode, int resultCode, Intent data, RoundImageView
            ivPhoto, Bitmap lastBmp, byte[] lastByte, String photoFolderAddress) {
        photoTailorUtil.onActivityResult(requestCode, resultCode, data);
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectimg_open_take://拍照
                photoTailorUtil.startToCamera();
                break;
            case R.id.selectimg_open_differ://相册选择
                photoTailorUtil.selectFromPhotos();
                break;
            case R.id.selectimg_open_cancel://取消
                dismiss();
                break;
            case R.id.selectimg_open_layout:
                dismiss();
                break;
        }
    }

    //取消popupWindow()
    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) { //不为null,并且正在显示中
            popupWindow.dismiss();
        }
    }

    @Override
    public void onPhotoComplete(Bitmap bitmap) {
        ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, mOutputStream);
        byte[] bitmapByte = mOutputStream.toByteArray();
        activity.setBitmapAndByte(bitmap, bitmapByte);
    }

    /**
     * 提交头像
     *
     * @param lastByte
     */
    public void postBitmap(byte[] lastByte) {
        final String bitmapStr = BitmapUtils.drawableToByte(lastByte);
        request.doPost(HttpConstant.USER_UPLOAD_AVATAR, getPhotoMap(bitmapStr), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                UserJson newUser = LoginUtils.getUserInfo(mContext);
                newUser.setPictureStr(bitmapStr);
                LoginUtils.changeUserInfo(mContext, newUser);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_CHANGEUSERINFO, newUser));
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    private Map<String, String> getPhotoMap(String bitmapStr) {
        Map<String, String> map = new HashMap<>();
        com.alibaba.fastjson.JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        jsonParam.put(VarConstant.HTTP_PICTURE, bitmapStr);
        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
