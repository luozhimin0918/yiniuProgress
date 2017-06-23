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

    public static final String OFFICIAL = "http://www.kxt.com";//官网

    public static final String BASE_URL = "https://kxtadi.kuaixun56.com/";
    //    public static final String IMG_URL = "http://img.kxt.com/";
    public static final String IMG_URL = "";
    public static final String FLAG_URL = "http://res.kxt.com/static/home/images/flag/circle/%s.png";

    public static final String CONTACT_US_URL = BASE_URL + "statement/contact_us";//联系我们
    public static final String FEEDBACK_URL = BASE_URL + "statement/feedback";//意见反馈

    public static final String CONFIG = BASE_URL + "app/config" + JWT;//配置信息
    public static final String VERSION = BASE_URL + "version/android" + JWT;//版本信息

    public static final String INDEX_MAIN = BASE_URL + "index/main" + JWT;//首页

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
    public static final String VIDEO_MOST_PLAY = BASE_URL + "video/list";//视听排行 最多播放
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
    public static final String EXPLORE_BLOG_DELETEFAVORARTICLE = BASE_URL + "blog/deleteFavorArticle" + VarConstant.HTTP_CONTENT;//取消关注文章
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
    public static final String EXPLORE_BLOG_WRITER_LIST = BASE_URL + "blog/writer_list" + VarConstant.HTTP_CONTENT;//专栏作者列表
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

    public static final String CJRL = BASE_URL + "cjrl/data" + JWT;

    public static final String RILI = BASE_URL + "data/rili" + VarConstant.HTTP_CONTENT;

    public static final String MARKET_NAV = BASE_URL + "quotes/nav" + VarConstant.HTTP_CONTENT;
    public static final String MARKET_INDEX = BASE_URL + "quotes/index" + VarConstant.HTTP_CONTENT;
    public static final String MARKET_LIST = BASE_URL + "quotes/list" + VarConstant.HTTP_CONTENT;

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

    /**
     * 收藏
     */
    public static final String COLLECT_NEWS = BASE_URL + "member/favor_article";//收藏列表-文章
    // ?uid={uid}&token={token}&lastid={lastid}
    public static final String COLLECT_VIDEO = BASE_URL + "member/favor_video";//收藏列表-视听
    public static final String COLLECT_LIST_DEL = BASE_URL + "member/favor_delete";//收藏列表-删除
    // ?uid={uid}&token={token}&type={type:1,
    // 2}&id={1,2,3,4}
    public static final String COLLECT_ADD = BASE_URL + "favor/addFavor";//收藏-添加 ?uid={}?id={}?type={} type: video |
    // article
    public static final String COLLECT_ADDS = BASE_URL + "favor/batchAddFavor";//收藏-批量添加 ?uid={}?id={}?type={}

    public static final String COLLECT_DEL = BASE_URL + "favor/deleteFavor";//收藏-删除 ?uid={}?id={}?type={} type: video
    // | article
    public static final String COLLECT_DELS = BASE_URL + "member/favor_delete";

    /**
     * 点评
     */
    public static final String GOOD_NEWS = BASE_URL + "news/addGood" + VarConstant.HTTP_CONTENT;//点赞-文章 ?id={}
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

    public static final String MEMBER_FAVOR_WRITER = BASE_URL + "member/favor_writer";

    public static final String MEMBER_COMMENT_REPLY = BASE_URL + "member/comment_reply";
    public static final String MEMBER_COMMENT_MY = BASE_URL + "member/comment_my";

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

}
