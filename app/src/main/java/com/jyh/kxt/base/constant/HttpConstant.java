package com.jyh.kxt.base.constant;

import com.library.base.http.VarConstant;
import com.library.util.EncryptionUtils;

import org.json.JSONObject;

/**
 * Created by Mr'Dai on 2017/3/1.
 */

public class HttpConstant {

    private static String JWT;

    static {
        try {
            JSONObject object = new JSONObject();
            object.put(VarConstant.HTTP_VERSION, VarConstant
                    .HTTP_VERSION_VALUE);
            object.put(VarConstant.HTTP_SYSTEM, VarConstant
                    .HTTP_SYSTEM_VALUE);
            JWT = VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY,
                    object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            JWT = "";
        }

    }

    public static final String OFFICIAL = "http://www.yn.com";//官网
    public static final String APP_DOWNLOAD = "http://appapi.yn.com/app/share.html";//推荐给好友下载

    //    public static final String BASE_URL = "https://kxtadi.kuaixun56.com/";
//    public static final String BASE_URL = "http://pre-kxtadi.kuaixun56.com/";
//    public static final String BASE_URL = "http://test.kxtadi.kuaixun56.com/";

        public static final String BASE_URL = "https://ynadi.kuaixun56.com/";
//        public static final String BASE_URL = "http://dev.ynadi.kuaixun56.com/";
//    public static final String BASE_URL = "http://pre.ynadi.kuaixun56.com/";

    public static final String IMG_URL = "";
    public static final String FLAG_URL = "http://res.kxt.com/static/home/images/flag/circle/%s.png";

    public static final String DOWN_PATCH = BASE_URL + "patch/find";//请求下载补丁
    public static final String DOWNLOAD_NUM = BASE_URL + "patch/download_num";//增加数量

    public static final String CONTACT_US_URL = BASE_URL + "statement/contact_us";//联系我们
    public static final String FEEDBACK_URL = BASE_URL + "statement/feedback";//意见反馈

    public static final String CONFIG = BASE_URL + "app/config" + JWT;//配置信息
    public static final String VERSION = BASE_URL + "version/android" + JWT;//版本信息

    public static final String INDEX_MAIN = BASE_URL + "index/main" + JWT;//首页

    /**
     * 交易圈
     */
    public static final String TRADE_MAIN = BASE_URL + "trade/main?content=";
    public static final String VIEW_POINT_DETAIL = BASE_URL + "viewpoint/detail?content=";
    public static final String TRADE_FAVORSTATUS = BASE_URL + "trade/favorstatus?content=";
    public static final String VIEW_POINT_ADDGOOD = BASE_URL + "viewpoint/addGood?content=";
    public static final String VP_COMMENT_ADDGOOD = BASE_URL + "vpcomment/addGood?content=";
    //是否关注
    public static final String VIEW_POINT_IS_FOLLOW = BASE_URL + "viewpoint/is_follow?content=";
    //置顶观点
    public static final String VIEW_POINT_TOP = BASE_URL + "viewpoint/stickTop?content=";
    //删除观点
    public static final String VIEW_POINT_DEL = BASE_URL + "viewpoint/delPoint?content=";
    //举报
    public static final String VIEW_POINT_REPORT = BASE_URL + "viewpoint/report?content=";
    public static final String VP_COMMENT_PUBLISH = BASE_URL + "vpcomment/publish?content=";
    public static final String VP_COMMENT_DETAIL = BASE_URL + "vpcomment/detail?content=";
    public static final String VP_COMMENT_LIST = BASE_URL + "vpcomment/list?content=";

    /**
     * 要闻点评
     */
    public static final String NEWS_LIST = BASE_URL + "news/list" + VarConstant.HTTP_CONTENT;//要闻列表
    public static final String NEWS_CONTENT = BASE_URL + "news/detail" + VarConstant.HTTP_CONTENT;//要闻详情
    public static final String DP_CODE = BASE_URL + "news/dp_nav" + JWT;//点评CODE
    public static final String DP_LIST = BASE_URL + "news/dianping";//点评列表 ?code={}&last_id={}

    /**
     * 视听
     */
    public static final String VIDEO_NAV = BASE_URL + "video/nav" + JWT;//视听导航
    public static final String VIDEO_LIST = BASE_URL + "video/list";//视听列表
    public static final String VIDEO_MOST_PLAY = BASE_URL + "video/most_play";//视听排行 最多播放
    public static final String VIDEO_MOST_COMMENT = BASE_URL + "video/most_comment";//视听排行 最多评论
    public static final String VIDEO_MOST_COLLECT = BASE_URL + "video/most_collect";//视听排行 最多收藏

    /**
     * 探索
     */
    public static final String EXPLORE = BASE_URL + "discover/index" + JWT;//探索
    public static final String EXPLORE_LOAD_MORE = BASE_URL + "discover/load_more";//探索首页加载更多
    public static final String EXPLORE_AUTHOR = BASE_URL + "discover/list";//作者专栏 ?id={writer_id}&last_id={0}
    public static final String EXPLORE_BLOG_INDEX = BASE_URL + "blog/index" + JWT;//作者专栏首页
    public static final String EXPLORE_BLOG_LIST = BASE_URL + "blog/list";//专栏列表 list_type={writer_id | recommend
    // }&last_id={0}
    public static final String EXPLORE_BLOG_PROFILE = BASE_URL + "blog/profile";//专栏作者 ?id={writer_id}
    public static final String EXPLORE_BLOG_DELETEFAVORARTICLE = BASE_URL + "blog/deleteFavorArticle" + VarConstant
            .HTTP_CONTENT;//取消关注文章
    public static final String EXPLORE_BLOG_ADDFAVOR = BASE_URL + "blog/addFavor" + VarConstant.HTTP_CONTENT;//关注作者
    // ?id={writer_id}
    public static final String EXPLORE_BLOG_DELETEFAVOR = BASE_URL + "blog/deleteFavor" + VarConstant.HTTP_CONTENT;
    //取消关注作者 ?id={writer_id}
    public static final String EXPLORE_BLOG_ADDFAVORARTICLE = BASE_URL + "blog/addFavorArticle" + VarConstant
            .HTTP_CONTENT;//关注文章
    // ?id={writer_id}
    public static final String EXPLORE_BLOG_CONTENT = BASE_URL + "/blog/detail" + VarConstant.HTTP_CONTENT;//blog详情
    // ?id={}&last_id={0}&uid={0}&accessToken={0}
    public static final String EXPLORE_TOPIC = BASE_URL + "topic/list";//专题
    public static final String EXPLORE_ACTIVITY = BASE_URL + "activity/list";//活动
    public static final String EXPLORE_BLOG_WRITER = BASE_URL + "blog/writer" + VarConstant.HTTP_CONTENT;//专栏作者列表
    public static final String EXPLORE_BLOG_WRITER_LIST = BASE_URL + "blog/writer_list" + VarConstant.HTTP_CONTENT;
    //专栏作者列表
    public static final String EXPLORE_BLOG_NAV = BASE_URL + "blog/blog_nav" + VarConstant.HTTP_CONTENT;//专栏作者列表

    /**
     * socket
     */
    public static final String SOCKET_TOKEN_KX = BASE_URL + "kuaixun/ws" + VarConstant.HTTP_CONTENT;//快讯socketToken
    public static final String SOCKET_TOKEN_HQ = BASE_URL + "quotes/ws";//行情socketToken

    /**
     * 快讯详情
     */
    public static final String FLASH_INFO = BASE_URL + "kuaixun/detail";//id
    public static final String FLASH_AD = BASE_URL + "kuaixun/ad" + VarConstant.HTTP_CONTENT;//快讯广告

    public static final String CJRL = BASE_URL + "cjrl/data" + JWT;

    public static final String RILI = BASE_URL + "data/rili" + VarConstant.HTTP_CONTENT;

    public static final String MARKET_NAV = BASE_URL + "quotes/nav" + VarConstant.HTTP_CONTENT;
    public static final String MARKET_INDEX = BASE_URL + "quotes/index" + VarConstant.HTTP_CONTENT;
    public static final String MARKET_LIST = BASE_URL + "quotes/list" + VarConstant.HTTP_CONTENT;
    public static final String MARKET_DETAIL = BASE_URL + "quotes/detail" + VarConstant.HTTP_CONTENT;

    public static final String VIDEO_DETAIL = BASE_URL + "video/detail" + VarConstant.HTTP_CONTENT;
    public static final String COMMENT_LIST = BASE_URL + "comment/list" + VarConstant.HTTP_CONTENT;


    public static final String COMMENT_PUBLISH = BASE_URL + "comment/publish" + VarConstant.HTTP_CONTENT;
    /**
     * 用户接口
     */
    public static final String USER_LOGIN = BASE_URL + "user/login_from";//第三方登录
    public static final String USER_LOGIN2 = BASE_URL + "user/login";//登录
    public static final String USER_REGISTER = BASE_URL + "user/register";//注册
    public static final String USER_FORGET = BASE_URL + "user/find_password";//忘记密码
    public static final String USER_CHANEPWD = BASE_URL + "user/login_from";//修改密码
    public static final String USER_CHANEINFO = BASE_URL + "member/modify_profile";//修改资料
    // picture、uid、token、address、sex、work、nickname、birthday
    public static final String USER_UPLOAD_AVATAR = BASE_URL + "member/upload_avatar";//上传头像
    // uid={uid}&accessToken={accessToken}&picture={picture}
    public static final String USER_FAVOR_WRITER = BASE_URL + "member/favor_writer";//我的关注 作者
    public static final String USER_FAVOR_BLOGARTICLE = BASE_URL + "member/favor_blogArticle";//我的关注 文章

    public static final String USER_CODE_REQUEST = BASE_URL + "user/get_code";//请求验证码
    public static final String USER_CODE_VERIFY = BASE_URL + "user/verify_code";//验证验证码
    public static final String USER_SET_PASSWORD = BASE_URL + "user/set_password";//设置密码，修改密码，忘记密码
    public static final String USER_SET_BIND_WITH_PWD = BASE_URL + "user/bind_with_password";//绑定并设置密码(无密码绑定最后一步)
    public static final String USER_BIND_PHONE_EMAIL = BASE_URL + "user/bind_phone_email";//有密码时 手机或邮箱绑定

    /**
     * 收藏
     */
    public static final String COLLECT_NEWS = BASE_URL + "member/favor_article";//收藏列表-文章
    // ?uid={uid}&token={token}&lastid={lastid}
    public static final String COLLECT_VIDEO = BASE_URL + "member/favor_video";//收藏列表-视听
    public static final String COLLECT_POINT = BASE_URL + "member/favor_point" + VarConstant.HTTP_CONTENT;//收藏列表-观点
    public static final String COLLECT_LIST_DEL = BASE_URL + "member/favor_delete";//收藏列表-删除
    // ?uid={uid}&token={token}&type={type:1,
    // 2}&id={1,2,3,4}
    public static final String COLLECT_ADD = BASE_URL + "favor/addFavor";//收藏-添加 ?uid={}?id={}?type={} type: video |
    // article
    public static final String COLLECT_ADDS = BASE_URL + "favor/batchAddFavor";//收藏-批量添加 ?uid={}?id={}?type={}

    public static final String COLLECT_DEL = BASE_URL + "favor/deleteFavor";//收藏-删除 ?uid={}?id={}?type={} type: video
    // | article
    public static final String COLLECT_DELS = BASE_URL + "member/favor_delete";
    public static final String COLLECT_DELS_POINT = BASE_URL + "favor/batchDelPoint";//观点批量取消收藏

    /**
     * 点评
     */
    public static final String GOOD_NEWS = BASE_URL + "news/addGood" + VarConstant.HTTP_CONTENT;//点赞-文章 ?id={}
    public static final String GOOD_BLOG = BASE_URL + "blog/addGood" + VarConstant.HTTP_CONTENT;//点赞-文章 ?id={}
    public static final String GOOD_VIDEO = BASE_URL + "video/addGood" + VarConstant.HTTP_CONTENT;//点赞-视听 ?id={}
    public static final String GOOD_COMMENT = BASE_URL + "comment/addGood" + VarConstant.HTTP_CONTENT;//点赞-评论
    // ?id={}&type


    public static final String DATA_HOT = BASE_URL + "data/hot" + VarConstant.HTTP_CONTENT;
    public static final String DATA_GROUP = BASE_URL + "data/group" + VarConstant.HTTP_CONTENT;
    public static final String DATA_LIST = BASE_URL + "data/list" + VarConstant.HTTP_CONTENT;

    public static final String DATA_COUNTRY = BASE_URL + "data/country";

    /**
     * 搜索
     */
    public static final String SEARCH_ARTICLE = BASE_URL + "search/article";//?word={}?last_id={}
    public static final String SEARCH_VIDEO = BASE_URL + "search/video";//?word={}?last_id={}
    public static final String SEARCH_MARKET_HOT = BASE_URL + "quotes/hotQuotes" + JWT;//行情热门搜索
    public static final String SEARCH_MARKET = BASE_URL + "quotes/search";//?word={}

    public static final String SEARCH_LIST = BASE_URL + "search/list" + VarConstant.HTTP_CONTENT;
    public static final String SEARCH_NAV = BASE_URL + "search/nav" + VarConstant.HTTP_CONTENT;


    public static final String MEMBER_FAVOR_WRITER = BASE_URL + "member/favor_writer";

    public static final String MEMBER_COMMENT_REPLY = BASE_URL + "member/comment_reply";
    public static final String MEMBER_COMMENT_MY = BASE_URL + "member/comment_my";
    public static final String POINT_COMMENT_MY = BASE_URL + "vpcomment/comment_my";

    public static final String VERSION_VERSION = BASE_URL + "version/version";

    public static final String DATA_FINANCE = BASE_URL + "data/finance";
    public static final String DATA_ETF = BASE_URL + "data/etf";
    public static final String DATA_CFTC = BASE_URL + "data/cftc";

    public static final String MORE_DATA = BASE_URL + "data/more_data";


    /**
     * 行情相关
     */
    public static final String QUOTES_FAVOR = BASE_URL + "quotes/favor";
    public static final String QUOTES_DELFAVOR = BASE_URL + "quotes/delFavor";
    public static final String QUOTES_ADDFAVOR = BASE_URL + "quotes/addFavor";
    public static final String QUOTES_SORT = BASE_URL + "quotes/sort";
    public static final String QUOTES_CHART = BASE_URL + "quotes/chart";

    /**
     * 交易圈
     */
    public static final String TRADING_COLUMNIST_NAV = BASE_URL + "writer/nav" + VarConstant.HTTP_CONTENT;//全部专栏-标题
    public static final String TRADING_COLUMNIST_LIST = BASE_URL + "writer/list";//全部专栏-列表
    public static final String TRADING_COLUMNIST_PROFILE = BASE_URL + "writer/profile";//专栏详情
    public static final String TRADING_TRADE_ARTICLE = BASE_URL + "trade/article/nav";//文章列表

    public static final String VIEWPOINT_PUBLISH = BASE_URL + "viewpoint/publish";//发布观点

    /**
     * 专栏入驻
     */
    public static final String ZLRZ_URL = BASE_URL + "blogapply/apply";
    public static final String MEMBER_INFO = BASE_URL + "member/user_info";//获取用户信息

    /**
     * 私信
     */
    public static final String MSG_USERCENTER = BASE_URL + "member/my_message" + VarConstant.HTTP_CONTENT;//个人中心我的私信
    public static final String MSG_SYS_LIST = BASE_URL + "message/sys_msg" + VarConstant.HTTP_CONTENT;//系统消息列表
    public static final String MSG_USER_BAN = BASE_URL + "message/handle_banned" + VarConstant.HTTP_CONTENT;//添加|解除屏蔽
    public static final String MSG_DEL = BASE_URL + "message/del_talk" + VarConstant.HTTP_CONTENT;//删除会话
    public static final String MSG_BANNED_LIST = BASE_URL + "message/banned_list" + VarConstant.HTTP_CONTENT;//屏蔽的会话列表
    public static final String MSG_USER_SET = BASE_URL + "message/set_msg" + VarConstant.HTTP_CONTENT;//消息设置

    public static final String MESSAGE_HISTORY = BASE_URL + "message/history";//历史消息
    public static final String MESSAGE_SEND_MSG = BASE_URL + "message/send_msg";//发送消息

    /**
     * 积分
     */
    public static final String CREDITS_MAIN = BASE_URL + "coins/main" + VarConstant.HTTP_CONTENT;//我的金币
    public static final String CREDITS_DETAIL = BASE_URL + "coins/month_detail" + VarConstant.HTTP_CONTENT;//本月明细
    public static final String CREDITS_MON_SUM = BASE_URL + "coins/month_summary" + VarConstant.HTTP_CONTENT;//月度汇总
    public static final String CREDITS_PUNCH_CARD = BASE_URL + "coins/punch_card" + VarConstant.HTTP_CONTENT;//签到


    public static final String MESSAGE_CLEAR_UNREAD = BASE_URL + "message/clear_unread";//清空未读消息

    public static final String COINS_ADD = BASE_URL + "coins/add";//增加金币分享
    public static final String COINS_SIGN = BASE_URL + "coins/sign_info";//签到状态

    /**
     * web分享
     */
    public static final String SHARE_WEB = BASE_URL + "webview/share" + VarConstant.HTTP_CONTENT;


    public static final String VERSION_IMEI = BASE_URL + "version/imei";
}
