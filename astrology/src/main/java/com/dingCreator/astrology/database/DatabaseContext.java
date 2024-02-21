package com.dingCreator.astrology.database;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author ding
 * @date 2024/2/20
 */
public class DatabaseContext {

    private static SqlSessionFactory sqlSessionFactory;

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public static void setSqlSession(SqlSessionFactory sqlSessionFactory) {
        DatabaseContext.sqlSessionFactory = sqlSessionFactory;
    }
}
