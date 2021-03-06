package com.jyh.kxt.index.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.explore.json.ActivityJson;
import com.jyh.kxt.explore.json.AuthorJson;
import com.jyh.kxt.explore.json.AuthorNewsJson;
import com.jyh.kxt.explore.json.TopicJson;
import com.jyh.kxt.index.json.TypeDataJson;
import com.jyh.kxt.index.ui.fragment.ExploreFragment;
import com.jyh.kxt.main.json.SlideJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;

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
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
        request.doGet(HttpConstant.EXPLORE, new HttpListener<List<TypeDataJson>>() {
            @Override
            protected void onResponse(List<TypeDataJson> newsExplore) {

                List<SlideJson> slides;
                List<SlideJson> shortcuts;
                List<TopicJson> topics;
                List<ActivityJson> activitys;
                List<AuthorJson> authors;
                List<AuthorNewsJson> articles;

                exploreFragment.initHeadView();

                for (TypeDataJson typeDataJson : newsExplore) {
                    switch (typeDataJson.getType()) {
                        case VarConstant.EXPLORE_SLIDE:
                            try {
                                JSONArray slidesJson = (JSONArray) typeDataJson.getData();
                                slides = JSON.parseArray(slidesJson.toString(), SlideJson.class);
                                if (slides.size() != 0) {
                                    exploreFragment.addSlide(slides);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_SHORTCUT:
                            try {
                                JSONArray shortcutJson = (JSONArray) typeDataJson.getData();
                                shortcuts = JSON.parseArray(shortcutJson.toString(), SlideJson.class);
                                if (shortcuts.size() != 0) {
                                    exploreFragment.addShortcut(shortcuts);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_TOPIC:
                            try {
                                JSONArray topicJson = (JSONArray) typeDataJson.getData();
                                topics = JSON.parseArray(topicJson.toString(), TopicJson.class);
                                if (topics.size() != 0) {
                                    exploreFragment.addTopic(topics, typeDataJson.getTitle());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_ACTIVITY:
                            try {
                                JSONArray activityJson = (JSONArray) typeDataJson.getData();
                                activitys = JSON.parseArray(activityJson.toString(), ActivityJson.class);
                                if (activitys.size() != 0) {
                                    exploreFragment.addActivity(activitys);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_BLOG_WRITER:
                            try {
                                JSONArray writerJson = (JSONArray) typeDataJson.getData();
                                authors = JSON.parseArray(writerJson.toString(), AuthorJson.class);
                                if (authors.size() != 0) {
                                    exploreFragment.addAuthor(authors);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.EXPLORE_BLOG_ARTICLE:
                            try {
                                JSONArray article = (JSONArray) typeDataJson.getData();
                                articles = JSON.parseArray(article.toString(), AuthorNewsJson.class);
                                if (articles.size() != 0) {
                                    exploreFragment.addArticle(articles);
                                    getLastId(articles);
                                } else {
                                    getLastId(null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                exploreFragment.plRootView.loadError();
                            }
                            break;
                    }
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                exploreFragment.plRootView.loadError();
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
            if (request == null) {
                request = new VolleyRequest(mContext, mQueue);
                request.setTag(getClass().getName());
            }
            JSONObject jsonParam = request.getJsonParam();
            if (!RegexValidateUtil.isEmpty(lastId)) {
                jsonParam.put(VarConstant.HTTP_LASTID, lastId);
            }
            request.doPost(HttpConstant.EXPLORE_BLOG_LIST, jsonParam, new HttpListener<List<AuthorNewsJson>>() {
                @Override
                protected void onResponse(List<AuthorNewsJson> newsJsons) {
                    exploreFragment.loadMore(newsJsons);
                    getLastId(newsJsons);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    exploreFragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exploreFragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        } else {
            exploreFragment.noMoreData();
        }
    }

    private String getLoadMoreUrl(VolleyRequest request) {
        String url = HttpConstant.EXPLORE_BLOG_LIST;
        JSONObject jsonParam = request.getJsonParam();
        if (!RegexValidateUtil.isEmpty(lastId)) {
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        }
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
    public String getLastId(List<AuthorNewsJson> article) {

        if (article == null) {
            return lastId = "";
        }
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
