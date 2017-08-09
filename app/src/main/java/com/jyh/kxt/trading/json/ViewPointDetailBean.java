package com.jyh.kxt.trading.json;

/**
 * Created by Mr'Dai on 2017/8/4.
 */

import java.util.List;

/**
 * {
 * content:行情简述$超链接$  //内包含超链接 超链接内容用#&<  >&# 包含，格式数据包含 title href,o_class,o_action,o_id
 * //示例格式 #&< title=文字&href=&o_class=&o_action=&o_id=  >&#
 * is_follow:"" // 1:Y | 0:N 关注状态
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
 * picture：[ img1url,img2url,img3url ],
 * shareDict:{url:http://www.baidu.com,
 * img:http://www.baidu.com/icon.png,
 * title:行情描述,
 * descript:行情描述
 * descript_sina:新浪分享描述},
 * //转发内容
 * forward:{
 * //格式参考观点的格式[图片],[图片]  },
 * comment:[{comment1},{comment2}], //评论列表，格式参考评论  评论如果有多条二级评论 需返回二级评论的数量
 * }
 */
public class ViewPointDetailBean {
    public String content;
    public String is_follow;
    public int num_good;
    public int num_comment;
    public int author_id;

    public String author_name;
    public String author_img;

    public long time;
    public String href;
    public String o_class;
    public String o_action;
    public String o_id;
    public List<String> report;
    public List<String> picture;

    public ViewPointTradeBean forward;

    public List<CommentDetailBean> comment;

    class ShareDict {
        public String url;
        public String img;
        public String title;
        public String descript;
    }
}
