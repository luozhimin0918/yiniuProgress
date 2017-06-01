package com.jyh.kxt.datum.bean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/31.
 */

public class HistoryListBean {

    /**
     * data : [{"before":"9.8","effecttype":0,"forecast":"19","reality":"21.1","time":"1493913600"},{"before":"23.5",
     * "effecttype":1,"forecast":"18","reality":"9.8","time":"1491494400"},{"before":"22.7","effecttype":0,
     * "forecast":"20","reality":"23.5","time":"1489075200"},{"before":"15.6","effecttype":0,"forecast":"18",
     * "reality":"22.7","time":"1486051200"},{"before":"17.8","effecttype":1,"forecast":"17.5","reality":"15.6",
     * "time":"1483632000"},{"before":"16.1","effecttype":1,"forecast":"18","reality":"17.8","time":"1480608000"},
     * {"before":"15.6","effecttype":1,"forecast":"17.3","reality":"16.1","time":"1478188800"},{"before":"15.1",
     * "effecttype":1,"forecast":"17.2","reality":"15.6","time":"1475769600"},{"before":"25.5","effecttype":1,
     * "forecast":"18","reality":"15.1","time":"1472745600"},{"before":"28.7","effecttype":0,"forecast":"18",
     * "reality":"25.5","time":"1470326400"},{"before":"3.8","effecttype":0,"forecast":"18","reality":"28.7",
     * "time":"1467907200"},{"before":"16","effecttype":1,"forecast":"16","reality":"3.8","time":"1464883200"},
     * {"before":"21.5","effecttype":1,"forecast":"20","reality":"16","time":"1462464000"},{"before":"24.2",
     * "effecttype":0,"forecast":"20.5","reality":"21.5","time":"1459440000"},{"before":"15.1","effecttype":0,
     * "forecast":"19.5","reality":"24.2","time":"1457020800"},{"before":"29.2","effecttype":1,"forecast":"19",
     * "reality":"15.1","time":"1454601600"},{"before":"21.1","effecttype":0,"forecast":"20","reality":"29.2",
     * "time":"1452182400"},{"before":"27.1","effecttype":0,"forecast":"20","reality":"21.1","time":"1449158400"},
     * {"before":"14.2","effecttype":0,"forecast":"18.5","reality":"27.1","time":"1446739200"},{"before":"17.3",
     * "effecttype":1,"forecast":"20","reality":"14.2","time":"1443715200"}]
     * type : history
     */

    private String type;
    private List<DataBean> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * before : 9.8
         * effecttype : 0
         * forecast : 19
         * reality : 21.1
         * time : 1493913600
         */

        private String before;
        private int effecttype;
        private String forecast;
        private String reality;
        private String time;
        private int listAdapterType = 1;

        public String getBefore() {
            return before;
        }

        public void setBefore(String before) {
            this.before = before;
        }

        public int getEffecttype() {
            return effecttype;
        }

        public void setEffecttype(int effecttype) {
            this.effecttype = effecttype;
        }

        public String getForecast() {
            return forecast;
        }

        public void setForecast(String forecast) {
            this.forecast = forecast;
        }

        public String getReality() {
            return reality;
        }

        public void setReality(String reality) {
            this.reality = reality;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getListAdapterType() {
            return listAdapterType;
        }

        public void setListAdapterType(int listAdapterType) {
            this.listAdapterType = listAdapterType;
        }
    }
}
