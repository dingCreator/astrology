package com.dingCreator.astrology.database;

import lombok.Getter;
import org.apache.ibatis.session.SqlSession;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author ding
 * @date 2024/2/20
 */
@Getter
public class DatabaseProvider {

    private static class Holder {
        private static final DatabaseProvider PROVIDER = new DatabaseProvider();
    }

    private DatabaseProvider() {

    }

    public static DatabaseProvider getInstance() {
        return Holder.PROVIDER;
    }

    /**
     * 执行SQL
     *
     * @param function 执行SQL后的处理方法
     * @return 处理结果
     */
    public Object doExecute(Function<SqlSession, ?> function) {
        if (Objects.isNull(DatabaseContext.getSqlSessionFactory())) {
            throw new NullPointerException("sql session factory may not initial");
        }
        try (SqlSession sqlSession = DatabaseContext.getSqlSessionFactory().openSession()) {
            return function.apply(sqlSession);
        }
    }
}
