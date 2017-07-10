package com.jyh.kxt.market.bean;

/**
 * Created by Mr'Dai on 2017/7/10.
 */

public class MarketDetailBean {
    private MarketItemBean data;
    private String quotes_chart_url;
    private ShareBean share;

    public MarketItemBean getData() {
        return data;
    }

    public void setData(MarketItemBean data) {
        this.data = data;
    }

    public String getQuotes_chart_url() {
        return quotes_chart_url;
    }

    public void setQuotes_chart_url(String quotes_chart_url) {
        this.quotes_chart_url = quotes_chart_url;
    }

    public ShareBean getShare() {
        return share;
    }

    public void setShare(ShareBean share) {
        this.share = share;
    }

    public class ShareBean {
        private String descript;
        private String title;
        private String url;

        public String getDescript() {
            return descript;
        }

        public void setDescript(String descript) {
            this.descript = descript;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
