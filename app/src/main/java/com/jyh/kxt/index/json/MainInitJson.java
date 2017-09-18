package com.jyh.kxt.index.json;

import com.jyh.kxt.base.json.JumpJson;
import com.library.base.http.VarConstant;

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

    private IndexAdBean flash_ad;//快讯广告
    private IndexAdBean index_ad;
    private TextAdBean index_top_ad;

    private LoadAdBean load_ad;
    private String quotes_chart_url;
    private String url_contact;
    private String url_feedback;
    private String url_kx_share;
    private String url_video_share;
    private String url_quotes_share;
    private String download_QR_code;
    private String message_socket_addr;

    public TextAdBean getIndex_top_ad() {
        return index_top_ad;
    }

    public void setIndex_top_ad(TextAdBean index_top_ad) {
        this.index_top_ad = index_top_ad;
    }

    public String getUrl_quotes_share() {
        return url_quotes_share;
    }

    public void setUrl_quotes_share(String url_quotes_share) {
        this.url_quotes_share = url_quotes_share;
    }

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

    public IndexAdBean getFlash_ad() {
        return flash_ad;
    }

    public void setFlash_ad(IndexAdBean flash_ad) {
        this.flash_ad = flash_ad;
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

    public String getDownload_QR_code() {
        return download_QR_code;
    }

    public void setDownload_QR_code(String download_QR_code) {
        this.download_QR_code = download_QR_code;
    }

    public String getMessage_socket_addr() {
        return message_socket_addr;
    }

    public void setMessage_socket_addr(String message_socket_addr) {
        this.message_socket_addr = message_socket_addr;
    }

    public static class TextAdBean extends JumpJson{

        /**
         * day_color : #1c9cf2
         * day_icon : http://img.kxt.com/Uploads/Picture/2017-07-24/5975707c9b7ea.png
         * font_family : PingFang
         * font_size : 26px
         * href : http://www.kxt.com/apiaction/ad_jump?system=app&code=SY-YW-T-T&href=aHR0cDovLzUyMWJtLmNvbS82MDAx
         * night_color : #136aa4
         * night_icon : http://img.kxt.com/Uploads/Picture/2017-07-24/5975706fdf3cd.png
         * o_id : 23
         * position : 1
         * title : • 要闻顶部ad
         */

        private String day_color;
        private String day_icon;
        private String font_family;
        private String font_size;
        private String href;
        private String night_color;
        private String night_icon;
        private int position;
        private String title;

        public String getDay_color() {
            return day_color;
        }

        public void setDay_color(String day_color) {
            this.day_color = day_color;
        }

        public String getDay_icon() {
            return day_icon;
        }

        public void setDay_icon(String day_icon) {
            this.day_icon = day_icon;
        }

        public String getFont_family() {
            return font_family;
        }

        public void setFont_family(String font_family) {
            this.font_family = font_family;
        }

        public String getFont_size() {
            return font_size;
        }

        public void setFont_size(String font_size) {
            this.font_size = font_size;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getNight_color() {
            return night_color;
        }

        public void setNight_color(String night_color) {
            this.night_color = night_color;
        }

        public String getNight_icon() {
            return night_icon;
        }

        public void setNight_icon(String night_icon) {
            this.night_icon = night_icon;
        }


        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class IndexAdBean extends JumpJson {
        private String href;
        private String picture;
        private int showTime;
        private String title;
        private String type;
        private String icon;
        private int left_screen_size;
        private int show;//0不主动显示   1 主动显示


        public String getHref() {
            String connector = "?";
            if (href.contains("?"))
                connector = "&";
            href = href + connector + VarConstant.HTTP_VERSION + "=" + VarConstant.HTTP_VERSION_VALUE
                    + "&" + VarConstant.HTTP_SYSTEM + "=" + VarConstant.HTTP_SYSTEM_VALUE;
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

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getLeft_screen_size() {
            return left_screen_size;
        }

        public void setLeft_screen_size(int left_screen_size) {
            this.left_screen_size = left_screen_size;
        }

        public int getShow() {
            return show;
        }

        public void setShow(int show) {
            this.show = show;
        }
    }
    public static class LoadAdBean extends JumpJson {
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
