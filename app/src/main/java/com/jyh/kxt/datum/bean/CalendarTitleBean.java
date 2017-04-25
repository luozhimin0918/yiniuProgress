package com.jyh.kxt.datum.bean;

/**
 * Created by Mr'Dai on 2017/4/21.
 *  标题的Bean
 */
public class CalendarTitleBean extends CalendarType{
    private String name;
    private int spaceType;//填充类型  0不填充  1需要填充  2已经填充

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(int spaceType) {
        this.spaceType = spaceType;
    }
}
