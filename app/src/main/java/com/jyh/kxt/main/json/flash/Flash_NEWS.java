package com.jyh.kxt.main.json.flash;

/**
 * 项目名:Kxt
 * 类描述:快讯-日历
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

public class Flash_NEWS {

    private String importance;//低,
    private String time;//2017-03-18 13:11:23,
    private String title;//【彭博调查：过半交易员看涨下周油价走势】,
    private String autoid;//199248
    private String image;//http:\\\/\\\/res.kuaixun360.com\\\/test\\\/20170318\\\/636254394625535805.jpg,
    private String image_pos;//l,
    private String description;//彭博社周五（3月17日）公布的调查结果显示，原油交易员与分析师转而看涨下周美国原油价格走势。在接受调查的39位交易员与分析师中，23人（59%）看涨，9人（23%）看跌，7人（18%）看平。,
    private Jump url;


    @Override
    public String toString() {
        return "Flash_NEWS{" +
                "importance='" + importance + '\'' +
                ", time='" + time + '\'' +
                ", image='" + image + '\'' +
                ", image_pos='" + image_pos + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url=" + url +
                ", autoid='" + autoid + '\'' +
                '}';
    }

    public Flash_NEWS() {
    }

    public Flash_NEWS(String importance, String time, String image, String image_pos, String title, String description, Jump url, String
            autoid) {

        this.importance = importance;
        this.time = time;
        this.image = image;
        this.image_pos = image_pos;
        this.title = title;
        this.description = description;
        this.url = url;
        this.autoid = autoid;
    }

    public String getImportance() {

        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_pos() {
        return image_pos;
    }

    public void setImage_pos(String image_pos) {
        this.image_pos = image_pos;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Jump getUrl() {
        return url;
    }

    public void setUrl(Jump url) {
        this.url = url;
    }

    public String getAutoid() {
        return autoid;
    }

    public void setAutoid(String autoid) {
        this.autoid = autoid;
    }

    class Jump {
        private String c;//news,
        private String i;//26074,
        private String u;

        @Override
        public String toString() {
            return "Jump{" +
                    "c='" + c + '\'' +
                    ", i='" + i + '\'' +
                    ", u='" + u + '\'' +
                    '}';
        }

        public Jump() {
        }

        public Jump(String c, String i, String u) {

            this.c = c;
            this.i = i;
            this.u = u;
        }

        public String getC() {

            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getI() {
            return i;
        }

        public void setI(String i) {
            this.i = i;
        }

        public String getU() {
            return u;
        }

        public void setU(String u) {
            this.u = u;
        }
    }
}
