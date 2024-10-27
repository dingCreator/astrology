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
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T executeReturn(Function<SqlSession, T> function) {
        return executeReturn(function, false, true);
    }

    /**
     * 执行SQL
     *
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T batchExecuteReturn(Function<SqlSession, T> function) {
        return executeReturn(function, true, true);
    }

    /**
     * 执行SQL
     *
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T batchTransactionExecuteReturn(Function<SqlSession, T> function) {
        return executeReturn(function, true, false);
    }

    /**
     * 执行SQL
     *
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T executeReturn(Function<SqlSession, T> function, boolean batch, boolean autoCommit) {
        validateSqlSessionFactory();
        SqlSession sqlSession = null;
        try {
            if (batch) {
                sqlSession = DatabaseContext.getSqlSessionFactory().openSession(ExecutorType.BATCH, autoCommit);
            } else {
                sqlSession = DatabaseContext.getSqlSessionFactory().openSession(ExecutorType.SIMPLE, autoCommit);
            }
            T result = function.apply(sqlSession);
            if (!autoCommit) {
                sqlSession.commit();
            }
            return result;
        } catch (Exception e) {
            if (sqlSession != null && !autoCommit) {
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
