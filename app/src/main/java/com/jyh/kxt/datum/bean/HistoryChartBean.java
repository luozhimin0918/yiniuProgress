package com.jyh.kxt.datum.bean;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/31.
 */

public class HistoryChartBean {

    /**
     * Y_axis : {"max":50,"min":0,"unit":"万"}
     * data : [{"time":"1443715200","value":14.2},{"time":"1446739200","value":27.1},{"time":"1449158400","value":21
     * .1},{"time":"1452182400","value":29.2},{"time":"1454601600","value":15.1},{"time":"1457020800","value":24.2},
     * {"time":"1459440000","value":21.5},{"time":"1462464000","value":16},{"time":"1464883200","value":3.8},
     * {"time":"1467907200","value":28.7},{"time":"1470326400","value":25.5},{"time":"1472745600","value":15.1},
     * {"time":"1475769600","value":15.6},{"time":"1478188800","value":16.1},{"time":"1480608000","value":17.8},
     * {"time":"1483632000","value":15.6},{"time":"1486051200","value":22.7},{"time":"1489075200","value":23.5},
     * {"time":"1491494400","value":9.8},{"time":"1493913600","value":21.1}]
     * type : chart
     */

    private YAxisBean Y_axis;
    private String type;
    private List<DataBean> data;

    public YAxisBean getY_axis() {
        return Y_axis;
    }

    public void setY_axis(YAxisBean Y_axis) {
        this.Y_axis = Y_axis;
    }

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

    public static class YAxisBean {
        /**
         * max : 50
         * min : 0
         * unit : 万
         */

        private int max;
        private int min;
        private String unit;

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    public static class DataBean {
        /**
         * time : 1443715200
         * value : 14.2
         */
        private String time;
        private double value;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getValue() {
            BigDecimal b = new BigDecimal(value);
            return b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
