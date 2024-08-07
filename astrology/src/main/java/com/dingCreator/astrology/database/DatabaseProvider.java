package com.dingCreator.astrology.database;

import lombok.Getter;
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

    /**
     * 执行SQL
     *
     * @param function 执行SQL后的处理方法
     * @return 处理结果
     */
    public <T> T executeReturn(Function<SqlSession, T> function) {
        if (Objects.isNull(DatabaseContext.getSqlSessionFactory())) {
            throw new NullPointerException("sql session factory may not initial");
        }
        try (SqlSession sqlSession = DatabaseContext.getSqlSessionFactory().openSession()) {
            return function.apply(sqlSession);
        }
    }

    /**
     * 执行SQL
     * @param consumer 执行操作
     */
    public void execute(Consumer<SqlSession> consumer) {
        if (Objects.isNull(DatabaseContext.getSqlSessionFactory())) {
            throw new NullPointerException("sql session factory may not initial");
        }
        try (SqlSession sqlSession = DatabaseContext.getSqlSessionFactory().openSession()) {
            consumer.accept(sqlSession);
        }
    }

    public <T> T doExecuteTransaction(Function<SqlSession, T> function) {
        if (Objects.isNull(DatabaseContext.getSqlSessionFactory())) {
            throw new NullPointerException("sql session factory may not initial");
        }

        try (SqlSession sqlSession = DatabaseContext.getSqlSessionFactory().openSession()) {
            sqlSession.getConnection().setAutoCommit(false);
            try {
                return function.apply(sqlSession);
            } catch (Exception e) {
                sqlSession.rollback();
                throw e;
            } finally {
                sqlSession.getConnection().setAutoCommit(true);
            }
        } catch (Exception e) {

        }
        return null;
    }
}
