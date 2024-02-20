package com.dingCreator.astrology.database;

import lombok.Data;
import org.apache.ibatis.session.SqlSession;

/**
 * @author ding
 * @date 2024/2/20
 */
public class DatabaseContext {

    private static SqlSession sqlSession;

    public static SqlSession getSqlSession() {
        return sqlSession;
    }

    public static void setSqlSession(SqlSession sqlSession) {
        DatabaseContext.sqlSession = sqlSession;
    }
}
