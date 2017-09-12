package com.jyh.kxt.datum.bean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/31.
 */

public class HistoryCftcListBean {

    /**
     * data : [{"bear":23508,"bull":54832,"change":-2951,"only":31324,"open":"85136","openchange":7086,
     * "time":1495468800,"tradingbear":55785,"tradingbull":18483},{"bear":17220,"bull":51495,"change":-1474,
     * "only":34275,"open":"78050","openchange":3246,"time":1494864000,"tradingbear":55803,"tradingbull":15403},
     * {"bear":12969,"bull":48718,"change":-4271,"only":35749,"open":"74804","openchange":936,"time":1494259200,
     * "tradingbear":55790,"tradingbull":13821},{"bear":10437,"bull":50457,"change":-3521,"only":40020,
     * "open":"73868","openchange":3659,"time":1493654400,"tradingbear":58262,"tradingbull":12770},{"bear":6036,
     * "bull":49577,"change":104,"only":43541,"open":"70209","openchange":3551,"time":1493049600,"tradingbear":59406,
     * "tradingbull":10385},{"bear":3329,"bull":46766,"change":-898,"only":43437,"open":"66658","openchange":-4837,
     * "time":1492444800,"tradingbear":58731,"tradingbull":9926},{"bear":4760,"bull":49095,"change":1376,
     * "only":44335,"open":"71495","openchange":2156,"time":1491840000,"tradingbear":59832,"tradingbull":9746},
     * {"bear":5857,"bull":48816,"change":-1254,"only":42959,"open":"69339","openchange":-5311,"time":1491235200,
     * "tradingbear":59014,"tradingbull":10643},{"bear":8636,"bull":52849,"change":-2052,"only":44213,"open":"74650",
     * "openchange":1897,"time":1490630400,"tradingbear":61841,"tradingbull":11989},{"bear":6522,"bull":52787,
     * "change":-1478,"only":46265,"open":"72753","openchange":-1699,"time":1490025600,"tradingbear":62288,
     * "tradingbull":9276},{"bear":6961,"bull":54704,"change":-3308,"only":47743,"open":"74452","openchange":-9059,
     * "time":1489420800,"tradingbear":63678,"tradingbull":9364},{"bear":6489,"bull":57540,"change":6241,
     * "only":51051,"open":"83511","openchange":8602,"time":1488816000,"tradingbear":72011,"tradingbull":13525},
     * {"bear":7821,"bull":52631,"change":-1004,"only":44810,"open":"74909","openchange":107,"time":1488211200,
     * "tradingbear":61315,"tradingbull":10116},{"bear":6842,"bull":52656,"change":-47,"only":45814,"open":"74802",
     * "openchange":592,"time":1487606400,"tradingbear":61773,"tradingbull":9987},{"bear":7254,"bull":53115,
     * "change":-872,"only":45861,"open":"74210","openchange":235,"time":1487001600,"tradingbear":61421,
     * "tradingbull":8509},{"bear":7602,"bull":54335,"change":161,"only":46733,"open":"73975","openchange":-1626,
     * "time":1486396800,"tradingbear":60553,"tradingbull":7217},{"bear":8229,"bull":54801,"change":-1850,
     * "only":46572,"open":"75601","openchange":763,"time":1485792000,"tradingbear":62118,"tradingbull":6822},
     * {"bear":6676,"bull":55098,"change":-700,"only":48422,"open":"74838","openchange":-2881,"time":1485187200,
     * "tradingbear":63451,"tradingbull":6938},{"bear":8467,"bull":57589,"change":-4006,"only":49122,"open":"77719",
     * "openchange":-2899,"time":1484582400,"tradingbear":64074,"tradingbull":6916},{"bear":8090,"bull":61218,
     * "change":-1143,"only":53128,"open":"80618","openchange":-4323,"time":1484006400,"tradingbear":67379,
     * "tradingbull":7496}]
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
         * bear : 23508
         * bull : 54832
         * change : -2951
         * only : 31324
         * open : 85136
         * openchange : 7086
         * time : 1495468800
         * tradingbear : 55785
         * tradingbull : 18483
         */

        private String bear;
        private String bull;
        private double change;
        private String only;
        private String open;
        private String openchange;
        private String time;
        private String tradingbear;
        private String tradingbull;

        public String getBear() {
            return bear;
        }

        public void setBear(String bear) {
            this.bear = bear;
        }

        public String getBull() {
            return bull;
        }

        public void setBull(String bull) {
            this.bull = bull;
        }

        public double getChange() {
            return change;
        }

        public void setChange(double change) {
            this.change = change;
        }

        public String getOnly() {
            return only;
        }

        public void setOnly(String only) {
            this.only = only;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getOpenchange() {
            return openchange;
        }

        public void setOpenchange(String openchange) {
            this.openchange = openchange;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTradingbear() {
            return tradingbear;
        }

        public void setTradingbear(String tradingbear) {
            this.tradingbear = tradingbear;
        }

        public String getTradingbull() {
            return tradingbull;
        }

        public void setTradingbull(String tradingbull) {
            this.tradingbull = tradingbull;
        }
    }
}
