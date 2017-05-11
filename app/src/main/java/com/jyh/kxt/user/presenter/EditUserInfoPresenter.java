package com.jyh.kxt.user.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
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
import com.jyh.kxt.base.utils.GetJsonDataUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.json.CityBean;
import com.jyh.kxt.user.json.ProvinceJson;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.EditUserInfoActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.DateUtils;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;
import com.jyh.kxt.base.utils.photo.PhotoTailorUtil;
import com.library.widget.pickerview.OptionsPickerView;
import com.library.widget.pickerview.TimePickerView;
import com.library.widget.window.ToastView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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

    private OptionsPickerView cityPicker, genderPicker;
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

    private VolleyRequest request;

    /**
     * 图片相关
     */
    private TextView openTake;
    private TextView openDiffer;
    private TextView cancelWindow;
    private PopupWindow popupWindow;
    private PhotoTailorUtil photoTailorUtil;

    public EditUserInfoPresenter(IBaseView iBaseView) {
        super(iBaseView);
        photoTailorUtil = new PhotoTailorUtil();
        photoTailorUtil.initPath((Activity) iBaseView);
        photoTailorUtil.setOnCompleteListener(this);
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
                    activity.setAddress(province, city);
                    address = province + "-" + city;
                }
            })

                    .setTitleText("")
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.line_color3))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.font_color5)) //设置选中项文字颜色
                    .setContentTextSize(pickerTextSize)
                    .setOutSideCancelable(true)// default is true
                    .setDecorView(activity.fl_picker)
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
                    activity.setGender(genders.get(options1));
                    sexInt = options1;
                }
            })

                    .setTitleText("")
                    .setDividerColor(ContextCompat.getColor(mContext, R.color.line_color3))
                    .setTextColorCenter(ContextCompat.getColor(mContext, R.color.font_color5)) //设置选中项文字颜色
                    .setSelectOptions(sexInt)
                    .setContentTextSize(pickerTextSize)
                    .setOutSideCancelable(true)// default is true
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
                    activity.setBirthday(date);
                    try {
                        birthdayStr = DateUtils.dateToString(date, DateUtils.TYPE_YMD);
                    } catch (Exception e) {
                        e.printStackTrace();
                        birthdayStr = null;
                    }
                }
            })
                    .setTitleText("选择生日")
                    .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                    .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示
                    .setDividerColor(Color.DKGRAY)
                    .setContentSize(pickerTextSize)
                    .setDate(selectedDate)
                    .setRangDate(startDate, endDate)
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
            }
            //初始化性别
            sexInt = userInfo.getSex();
            switch (sexInt) {
                case 0:
                    activity.setGender("保密");
                    break;
                case 1:
                    activity.setGender("男");
                    break;
                case 2:
                    activity.setGender("女");
                    break;
            }
            //初始化年龄
            birthdayStr = userInfo.getBirthday();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postChangedInfo(String photo, String nickname, String work) {
        if (request == null)
            request = new VolleyRequest(mContext, mQueue);

        request.doPost(HttpConstant.USER_CHANEINFO, getPostInfo(request, photo, nickname, work), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                try {
                    activity.dismissWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    activity.dismissWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastView.makeText3(mContext, "信息提交失败");
            }
        });

    }

    /**
     * 获取提交信息
     *
     * @param request
     * @param photo
     * @return
     */
    private Map<String, String> getPostInfo(VolleyRequest request, String photo, String nickname, String work) {
        com.alibaba.fastjson.JSONObject jsonParam = request.getJsonParam();
        UserJson userJson = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userJson.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userJson.getToken());
        jsonParam.put(VarConstant.HTTP_PICTURE, photo);
        jsonParam.put(VarConstant.HTTP_ADDRESS, address);
        jsonParam.put(VarConstant.HTTP_SEX, sexInt);
        jsonParam.put(VarConstant.HTTP_BIRTHDAY, birthdayStr);
        jsonParam.put(VarConstant.HTTP_NICKNAME, nickname);
        jsonParam.put(VarConstant.HTTP_WORK, work);
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
        View view = LayoutInflater.from(context).inflate(R.layout.popup_selectimg_view, null, false);

        SystemUtil.closeSoftInputWindow((Activity) context);

        openTake = (TextView) view.findViewById(R.id.selectimg_open_take);
        openDiffer = (TextView) view.findViewById(R.id.selectimg_open_differ);
        cancelWindow = (TextView) view.findViewById(R.id.selectimg_open_cancel);

        View spaceView = view.findViewById(R.id.selectimg_open_space);

        openTake.setOnClickListener(this);
        openDiffer.setOnClickListener(this);
        cancelWindow.setOnClickListener(this);


        if (SystemUtil.navigationBar(context) > 10) {
            ViewGroup.LayoutParams layoutParams = spaceView.getLayoutParams();
            layoutParams.height = SystemUtil.navigationBar(context);
            spaceView.setLayoutParams(layoutParams);
        }

        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams
                .MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        popupWindow.setFocusable(true); // 可以聚焦
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(rlPhoto, Gravity.BOTTOM, 0, 0);

        view.setOnClickListener(this);
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

    public String drawableToByte(byte[] bytes) {
        if (null != bytes) {
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } else {
            return null;
        }
    }

}
