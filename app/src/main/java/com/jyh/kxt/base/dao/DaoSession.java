package com.jyh.kxt.base.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.main.json.NewsJson;

import com.jyh.kxt.base.dao.EmojeBeanDao;
import com.jyh.kxt.base.dao.NewsJsonDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig emojeBeanDaoConfig;
    private final DaoConfig newsJsonDaoConfig;

    private final EmojeBeanDao emojeBeanDao;
    private final NewsJsonDao newsJsonDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        emojeBeanDaoConfig = daoConfigMap.get(EmojeBeanDao.class).clone();
        emojeBeanDaoConfig.initIdentityScope(type);

        newsJsonDaoConfig = daoConfigMap.get(NewsJsonDao.class).clone();
        newsJsonDaoConfig.initIdentityScope(type);

        emojeBeanDao = new EmojeBeanDao(emojeBeanDaoConfig, this);
        newsJsonDao = new NewsJsonDao(newsJsonDaoConfig, this);

        registerDao(EmojeBean.class, emojeBeanDao);
        registerDao(NewsJson.class, newsJsonDao);
    }
    
    public void clear() {
        emojeBeanDaoConfig.clearIdentityScope();
        newsJsonDaoConfig.clearIdentityScope();
    }

    public EmojeBeanDao getEmojeBeanDao() {
        return emojeBeanDao;
    }

    public NewsJsonDao getNewsJsonDao() {
        return newsJsonDao;
    }

}