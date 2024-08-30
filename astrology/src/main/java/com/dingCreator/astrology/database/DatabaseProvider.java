package com.dingCreator.astrology.database;

import lombok.Getter;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
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

    private void validateSqlSessionFactory() {
        if (Objects.isNull(DatabaseContext.getSqlSessionFactory())) {
            throw new NullPointerException("sql session factory may not initial");
        }
    }

    /**
     * 执行SQL
     *
     * @param function 执行SQL后的处理方法
     * @return 处理结果
     */
    public <T> T executeReturn(Function<SqlSession, T> function) {
        validateSqlSessionFactory();
        SqlSession sqlSession = null;
        try {
            sqlSession = DatabaseContext.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            return function.apply(sqlSession);
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    /**
     * 执行SQL
     *
     * @param consumer 执行操作
     */
    public void execute(Consumer<SqlSession> consumer) {
        executeReturn(sqlSession -> {
            consumer.accept(sqlSession);
            return null;
        });
    }
}
