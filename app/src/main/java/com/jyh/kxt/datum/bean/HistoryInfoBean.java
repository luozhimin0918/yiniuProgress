package com.jyh.kxt.datum.bean;

/**
 * Created by Mr'Dai on 2017/5/31.
 */

public class HistoryInfoBean {

    /**
     * data : {"agency":"美国劳工部","code":"NFP TCH Index","count_method":"过去一个月非农业人口就业人口的变化","country":"美国",
     * "definitions":"非农业就业人数(Changes in non-farm payrolls)
     * ：为就业报告中的一个项目，该项目主要统计从事农业生产以外的职位变化情形，该数据与失业率一同公布。由美国劳工部统计局在月第一个星期五美国东部时间8:30也就是北京时间晚上20:30前一个月的数据。",
     * "focus_reason
     * ":"非农就业人数变化反映出制造行业和服务行业的发展及其增长，数字减少便代表企业减低生产，经济步入萧条；在没有发生恶性通胀的情况下，如数字大幅增加，显示一个健康的经济状况，对美元应当有利，并可能预示着更将提高利率，也对美元有利。非农就业指数若增加，反映出经济发展的上升，反之则下降。","frequency":"每月一次（每月的第一个周五公布，遇到特殊节假日或统计周期问题除外）","id":33,"influence":"公布值＞预期值，则利好美国经济及美元，利空非美。","name":"季调后非农就业人口","sort":200,"title":"美国非农数据-美国非农数据走势图-一牛-kxt.com","type":"finance","unit":"万"}
     * type : info
     */

    private DataBean data;
    private String type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class DataBean {
        /**
         * agency : 美国劳工部
         * code : NFP TCH Index
         * count_method : 过去一个月非农业人口就业人口的变化
         * country : 美国
         * definitions : 非农业就业人数(Changes in non-farm payrolls)
         * ：为就业报告中的一个项目，该项目主要统计从事农业生产以外的职位变化情形，该数据与失业率一同公布。由美国劳工部统计局在月第一个星期五美国东部时间8:30也就是北京时间晚上20:30前一个月的数据。
         * focus_reason :
         * 非农就业人数变化反映出制造行业和服务行业的发展及其增长，数字减少便代表企业减低生产，经济步入萧条；在没有发生恶性通胀的情况下，如数字大幅增加，显示一个健康的经济状况，对美元应当有利，并可能预示着更将提高利率，也对美元有利。非农就业指数若增加，反映出经济发展的上升，反之则下降。
         * frequency : 每月一次（每月的第一个周五公布，遇到特殊节假日或统计周期问题除外）
         * id : 33
         * influence : 公布值＞预期值，则利好美国经济及美元，利空非美。
         * name : 季调后非农就业人口
         * sort : 200
         * title : 美国非农数据-美国非农数据走势图-一牛-kxt.com
         * type : finance
         * unit : 万
         */

        private String agency;
        private String code;
        private String count_method;
        private String country;
        private String definitions;
        private String focus_reason;
        private String frequency;
        private int id;
        private String influence;
        private String name;
        private int sort;
        private String title;
        private String type;
        private String unit;

        public String getAgency() {
            return agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCount_method() {
            return count_method;
        }

        public void setCount_method(String count_method) {
            this.count_method = count_method;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getDefinitions() {
            return definitions;
        }

        public void setDefinitions(String definitions) {
            this.definitions = definitions;
        }

        public String getFocus_reason() {
            return focus_reason;
        }

        public void setFocus_reason(String focus_reason) {
            this.focus_reason = focus_reason;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getInfluence() {
            return influence;
        }

        public void setInfluence(String influence) {
            this.influence = influence;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
