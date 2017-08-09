package com.jyh.kxt.search.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class QuoteJson {

    /**
     * type : quotes
     * data : [{"id":188,"name":"美燃油11","code":"HONX"},{"id":210,"name":"布伦特原油11","code":"OILX"},{"id":396,"name":"美精铜11","code":"CHCX"}]
     * is_more : 1
     */

    private String type;
    private String is_more;
    private List<QuoteItemJson> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIs_more() {
        return is_more;
    }

    public void setIs_more(String is_more) {
        this.is_more = is_more;
    }

    public List<QuoteItemJson> getData() {
        return data;
    }

    public void setData(List<QuoteItemJson> data) {
        this.data = data;
    }
    
}
