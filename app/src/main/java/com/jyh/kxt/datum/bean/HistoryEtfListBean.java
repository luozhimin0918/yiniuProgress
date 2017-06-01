package com.jyh.kxt.datum.bean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/31.
 */

public class HistoryEtfListBean {

    /**
     * data : [{"change":"0.00","time":1496160000,"total":"5886327972","totalounce":"340976507.30",
     * "totaltonne":"10605.56"},{"change":"-88.29","time":1496073600,"total":"5886327972","totalounce":"340976507
     * .30","totaltonne":"10605.56"},{"change":"0.00","time":1495987200,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495900800,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495814400,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495728000,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495641600,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495555200,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495468800,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495382400,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495296000,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495209600,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1495123200,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"44.15","time":1495036800,"total":"5778155369","totalounce":"343815117
     * .30","totaltonne":"10693.85"},{"change":"0.00","time":1494950400,"total":"5723636814","totalounce":"342395688
     * .30","totaltonne":"10649.70"},{"change":"117.74","time":1494864000,"total":"5723636814",
     * "totalounce":"342395688.30","totaltonne":"10649.70"},{"change":"0.00","time":1494777600,"total":"5515200566",
     * "totalounce":"338610336.30","totaltonne":"10531.96"},{"change":"0.00","time":1494691200,"total":"5515200566",
     * "totalounce":"338610336.30","totaltonne":"10531.96"},{"change":"0.00","time":1494604800,"total":"5515200566",
     * "totalounce":"338610336.30","totaltonne":"10531.96"},{"change":"0.00","time":1494518400,"total":"5515200566",
     * "totalounce":"338610336.30","totaltonne":"10531.96"}]
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
         * change : 0.00
         * time : 1496160000
         * total : 5886327972
         * totalounce : 340976507.30
         * totaltonne : 10605.56
         */

        private String change;
        private String time;
        private String total;
        private String totalounce;
        private String totaltonne;

        public String getChange() {
            return change;
        }

        public void setChange(String change) {
            this.change = change;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getTotalounce() {
            return totalounce;
        }

        public void setTotalounce(String totalounce) {
            this.totalounce = totalounce;
        }

        public String getTotaltonne() {
            return totaltonne;
        }

        public void setTotaltonne(String totaltonne) {
            this.totaltonne = totaltonne;
        }
    }
}
