package com.jyh.kxt.user.presenter;

import android.graphics.Color;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindActivity;
import com.jyh.kxt.base.utils.GetJsonDataUtil;
import com.jyh.kxt.user.json.CityBean;
import com.jyh.kxt.user.json.ProvinceJson;
import com.jyh.kxt.user.ui.EditUserInfoActivity;
import com.library.widget.pickerview.OptionsPickerView;
import com.library.widget.pickerview.TimePickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/6.
 */

public class EditUserInfoPresenter extends BasePresenter {

    @BindActivity
    EditUserInfoActivity activity;

    private OptionsPickerView cityPicker, genderPicker;
    private TimePickerView birthdayPicker;

    private ArrayList<ProvinceJson> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//省-市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//省-市-县

    private int pickerTextSize = 20;//选择器文字大小

    public EditUserInfoPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 显示地区选择界面
     */
    public void showPickerCitisView() {
        if (cityPicker == null) {
            cityPicker = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    activity.setAddress(options1Items.get(options1).getPickerViewText(), options2Items.get(options1).get(options2),
                            options3Items.get(options1).get(options2).get(options3));
                }
            })

                    .setTitleText("选择居住地")
                    .setDividerColor(Color.BLACK)
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setContentTextSize(pickerTextSize)
                    .setOutSideCancelable(true)// default is true
                    .setDecorView(activity.fl_picker)
                    .build();
            cityPicker.setPicker(options1Items, options2Items, options3Items);//三级选择器
        }
        if (cityPicker.isShowing())
            return;
        cityPicker.show();
    }

    /**
     * 显示性别选择器
     */
    public void showPickerGenderView() {
        if (genderPicker == null) {
            final ArrayList<String> genders = new ArrayList<>();
            genders.add("男");
            genders.add("女");
            genders.add("保密");
            genderPicker = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    activity.setGender(genders.get(options1));
                }
            })

                    .setTitleText("选择性别")
                    .setDividerColor(Color.BLACK)
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setContentTextSize(pickerTextSize)
                    .setOutSideCancelable(true)// default is true
                    .setDecorView(activity.fl_picker)
                    .build();

            genderPicker.setPicker(genders);//一级选择器
        }
        if (genderPicker.isShowing())
            return;
        genderPicker.show();
    }

    /**
     * 显示日期选择器
     */
    public void showPickerBirthdayView() {
        if (birthdayPicker == null) {
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            startDate.set(1900, 0, 1);
            Calendar endDate = selectedDate;
            birthdayPicker = new TimePickerView.Builder(activity, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    activity.setBirthday(date);
                }
            })
                    .setTitleText("选择生日")
                    .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                    .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                    .setDividerColor(Color.DKGRAY)
                    .setContentSize(pickerTextSize)
                    .setDate(selectedDate)
                    .setRangDate(startDate, endDate)
                    .setDecorView(activity.fl_picker)
                    .build();
        }
        if (birthdayPicker.isShowing())
            return;
        birthdayPicker.show();
    }

    /**
     * 加载省市县信息
     */
    public void loadCitis() {
        if (!thread.isAlive())
            thread.start();
    }

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            //解析数据
            /**
             * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
             * 关键逻辑在于循环体
             * */
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
            }
        }
    });

    public ArrayList<ProvinceJson> parseData(String result) {
        ArrayList<ProvinceJson> detail = new ArrayList<>();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

}
