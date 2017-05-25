package com.jyh.kxt.index.json;

import com.jyh.kxt.base.json.JumpJson;

/**
 * Created by Mr'Dai on 2017/5/24.
 */

public class MainInitJson {


//    {
//        "icon": "",
//            "index_ad": {
//        "href": "http://www.kxt.com/topic",
//                "o_action": "",
//                "o_class": "",
//                "o_id": 2,
//                "picture": "/Uploads/Picture/2017-05-18/591d0b79ae970.jpg",
//                "showTime": 3137160,
//                "title": "webview模式测试数据",
//                "type": "webview"
//    },
//        "load_ad": {
//        "href": "http://appapi.kxt.com/Topic/index/id/3.html",
//                "o_action": "",
//                "o_class": "",
//                "o_id": 4,
//                "picture": "/Uploads/Picture/2017-05-04/590b033c1b940.png",
//                "showTime": 631620,
//                "title": "normal页面测试数据",
//                "type": "normal"
//    },
//        "quotes_chart_url": "http://appapi.kxt.com/Quotes/newchart2?code={code}&system={system}&version={version}",
//            "url_contact": "http://test.kxtadi.kuaixun56.com/Statement/contact",
//            "url_feedback": "http://test.kxtadi.kuaixun56.com/Statement/feedback",
//            "url_kx_share": "http://m.kxt.com/kuaixun/detail/id/{id}",
//            "url_video_share": "http://m.kxt.com/videoArt/{id}.html"
//    }

    private String icon;
    private IndexAdBean index_ad;
    private LoadAdBean load_ad;
    private String quotes_chart_url;
    private String url_contact;
    private String url_feedback;
    private String url_kx_share;
    private String url_video_share;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public IndexAdBean getIndex_ad() {
        return index_ad;
    }

    public void setIndex_ad(IndexAdBean index_ad) {
        this.index_ad = index_ad;
    }

    public LoadAdBean getLoad_ad() {
        return load_ad;
    }

    public void setLoad_ad(LoadAdBean load_ad) {
        this.load_ad = load_ad;
    }

    public String getQuotes_chart_url() {
        return quotes_chart_url;
    }

    public void setQuotes_chart_url(String quotes_chart_url) {
        this.quotes_chart_url = quotes_chart_url;
    }

    public String getUrl_contact() {
        return url_contact;
    }

    public void setUrl_contact(String url_contact) {
        this.url_contact = url_contact;
    }

    public String getUrl_feedback() {
        return url_feedback;
    }

    public void setUrl_feedback(String url_feedback) {
        this.url_feedback = url_feedback;
    }

    public String getUrl_kx_share() {
        return url_kx_share;
    }

    public void setUrl_kx_share(String url_kx_share) {
        this.url_kx_share = url_kx_share;
    }

    public String getUrl_video_share() {
        return url_video_share;
    }

    public void setUrl_video_share(String url_video_share) {
        this.url_video_share = url_video_share;
    }

    public static class IndexAdBean extends JumpJson {
        /**
         * href : http://www.kxt.com/topic
         * o_action :
         * o_class :
         * o_id : 2
         * picture : /Uploads/Picture/2017-05-18/591d0b79ae970.jpg
         * showTime : 3137160
         * title : webview模式测试数据
         * type : webview
         */

        private String href;
        private String picture;
        private int showTime;
        private String title;
        private String type;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }


        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public int getShowTime() {
            return showTime;
        }

        public void setShowTime(int showTime) {
            this.showTime = showTime;
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
    }

    public static class LoadAdBean extends JumpJson{
        /**
         * href : http://appapi.kxt.com/Topic/index/id/3.html
         * o_action :
         * o_class :
         * o_id : 4
         * picture : /Uploads/Picture/2017-05-04/590b033c1b940.png
         * showTime : 631620
         * title : normal页面测试数据
         * type : normal
         */

        private String href;
        private String picture;
        private int showTime;
        private String title;
        private String type;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public int getShowTime() {
            return showTime;
        }

        public void setShowTime(int showTime) {
            this.showTime = showTime;
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
    }
}
