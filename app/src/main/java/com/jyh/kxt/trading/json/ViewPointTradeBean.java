package com.jyh.kxt.trading.json;

import com.jyh.kxt.trading.util.TradeHandlerUtil;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/1.
 */

public class ViewPointTradeBean extends TradeHandlerUtil.TradeHandlerBean {

    /**
     * 自定义的  格式发生改变  时不动
     */

    private int itemViewType = 1;

    public int getItemViewType() {
        return itemViewType;
    }

    public void setItemViewType(int itemViewType) {
        this.itemViewType = itemViewType;
    }

    /**
     * 实体类
     * {
     * content:行情简述$超链接$  //内包含超链接 超链接内容用#&<  >&# 包含，格式数据包含 title href,o_class,o_action,o_id    示例格式 #&<
     * title=文字&href=&o_class=&o_action=&o_id=  >&#
     * num_good:200,
     * num_commit:200,
     * author_name:快讯通,
     * author_id:108108,
     * author_img:头像url
     * time:159039321,
     * href:"",
     * o_class:trading,
     * o_action:detail,
     * o_id:108108,
     * report:["垃圾营销","内容抄袭","违法信息","低俗信息","虚假",],
     * //转发内容
     * forward:{
     * //格式参考观点的格式[图片],[图片] },
     * picture：[ img1url,img2url,img3url ],
     * shareDict:{url:http://www.baidu.com,
     * img:http://www.baidu.com/icon.png,
     * title:行情描述,
     * descript:行情描述 },
     * <p>
     * }
     */
    public String content;
    public int num_good;
    public int num_comment;
    public String author_name;
    public String author_id;
    public String author_img;
    public long time;
    public String href;
    public ShareDict shareDict;

    public String o_class;
    public String o_action;
    public String o_id;
    public String is_top;

    public List<String> report;
    public List<String> picture;

    public ViewPointTradeBean forward;

    public class ShareDict {
        public String url;
        public String img;
        public String title;
        public String descript;
        public String descript_sina;
    }
}
