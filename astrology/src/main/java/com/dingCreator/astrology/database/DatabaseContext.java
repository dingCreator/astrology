package com.dingCreator.astrology.database;

import lombok.Getter;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author ding
 * @date 2024/2/20
 */
public class DatabaseContext {

    @Getter
    private static SqlSessionFactory sqlSessionFactory;

    public static void setSqlSession(SqlSessionFactory sqlSessionFactory) {
        DatabaseContext.sqlSessionFactory = sqlSessionFactory;
    }
}
