package com.jyh.kxt.index.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.index.ui.fragment.ExploreFragment;
import com.jyh.kxt.index.json.HomeHeaderJson;
import com.jyh.kxt.explore.json.ActivityJson;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.explore.json.TopicJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/26.
 */

public class ExplorePresenter extends BasePresenter {

    @BindObject ExploreFragment exploreFragment;
    private VolleyRequest request;

    private String lastId = "";
    private boolean isMore;//是否拥有更多数据

    public ExplorePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init() {
        if (request == null)
            request = new VolleyRequest(mContext, mQueue);
        request.doGet(HttpConstant.EXPLORE, new HttpListener<List<HomeHeaderJson>>() {
            @Override
            protected void onResponse(List<HomeHeaderJson> newsExplore) {

                List<SlideJson> slides = new ArrayList<>();
                List<SlideJson> shortcuts = new ArrayList<>();
                List<TopicJson> topics = new ArrayList<>();
                List<ActivityJson> activitys = new ArrayList<>();
                List<AuthorJson> authors = new ArrayList<>();
                List<NewsJson> articles = new ArrayList<>();

                exploreFragment.initHeadView();

                for (HomeHeaderJson homeHeaderJson : newsExplore) {
                    switch (homeHeaderJson.getType()) {
                        case VarConstant.EXPLORE_SLIDE:
                            try {
                                JSONArray slidesJson = (JSONArray) homeHeaderJson.getData();
                                slides = JSON.parseArray(slidesJson.toString(), SlideJson.class);
                                if (slides.size() != 0)
                                    exploreFragment.addSlide(slides);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_SHORTCUT:
                            try {
                                JSONArray shortcutJson = (JSONArray) homeHeaderJson.getData();
                                shortcuts = JSON.parseArray(shortcutJson.toString(), SlideJson.class);
                                if (shortcuts.size() != 0)
                                    exploreFragment.addShortcut(shortcuts);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_TOPIC:
                            try {
                                JSONArray topicJson = (JSONArray) homeHeaderJson.getData();
                                topics = JSON.parseArray(topicJson.toString(), TopicJson.class);
                                if (topics.size() != 0)
                                    exploreFragment.addTopic(topics);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_ACTIVITY:
                            try {
                                JSONArray activityJson = (JSONArray) homeHeaderJson.getData();
                                activitys = JSON.parseArray(activityJson.toString(), ActivityJson.class);
                                if (activitys.size() != 0)
                                    exploreFragment.addActivity(activitys);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_BLOG_WRITER:
                            try {
                                JSONArray writerJson = (JSONArray) homeHeaderJson.getData();
                                authors = JSON.parseArray(writerJson.toString(), AuthorJson.class);
                                if (authors.size() != 0)
                                    exploreFragment.addAuthor(authors);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_BLOG_ARTICLE:
                            try {
                                JSONArray article = (JSONArray) homeHeaderJson.getData();
                                articles = JSON.parseArray(article.toString(), NewsJson.class);
                                if (articles.size() != 0) {
                                    exploreFragment.addArticle(articles);
                                    getLastId(articles);
                                } else {
                                    getLastId(null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
        });
    }

    /**
     * 刷新
     */
    public void refresh() {
        init();
    }

    /**
     * 加载更多
     */
    public void loadMore() {

        if (isMore) {
            if (request == null)
                request = new VolleyRequest(mContext, mQueue);
            request.doGet(getLoadMoreUrl(request), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    exploreFragment.loadMore(newsJsons);
                    getLastId(newsJsons);
                }
            });
        }else{
            exploreFragment.noMoreData();
        }
    }

    private String getLoadMoreUrl(VolleyRequest request) {
        String url = HttpConstant.EXPLORE_LOAD_MORE;
        JSONObject jsonParam = request.getJsonParam();
        if (!RegexValidateUtil.isEmpty(lastId))
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        try {
            url += VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 获取lastId
     *
     * @param article
     * @return
     */
    public String getLastId(List<NewsJson> article) {

        if (article == null)
            return lastId = "";
        try {
            int size = article.size();
            if (size > VarConstant.LIST_MAX_SIZE) {
                isMore = true;
            } else {
                isMore = false;
            }
            lastId = article.get(size - 1).getO_id();
            return lastId;
        } catch (Exception e) {
            e.printStackTrace();
            return lastId = "";
        }
    }

}
