package com.jyh.kxt.explore.json;

/**
 * 项目名:Kxt
 * 类描述:专题
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class TopicJson {

//    "background":"意大利将于12月4日举行修宪全面公投，公投主要围绕意大利总理伦齐提出的政治体系改革，将参议员数量从315名减少到100
// 名、限制参议院解散政府的权力等。为促成公投，伦齐甚至不惜将自己的政治生涯绑定在公投结果上，一旦公投失败将会请辞。意大利公投受到民众情绪和政治动荡的深刻影响，这使得结果难以预料。如果总理伦齐不能如愿以偿，那么意大利可能将提前举行大选，反欧盟政党或借此上台并发起脱欧公投，欧盟将面临崩塌危险。全球市场将这次修宪公投定义为“意大利隐性脱欧公投”，其结果可能比英国脱欧公投更有影响力。",
//            "title":"意大利修宪公投",
//            "url":"http://img.kuaixun360.com/Uploads/Picture/2016-12-07/5847a91a947d7.png"

    private String background;
    private String title;
    private String url;

    @Override
    public String toString() {
        return "TopicJson{" +
                "background='" + background + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public TopicJson() {
    }

    public TopicJson(String background, String title, String url) {

        this.background = background;
        this.title = title;
        this.url = url;
    }

    public String getBackground() {

        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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
