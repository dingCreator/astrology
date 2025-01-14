package com.dingCreator.astrology.database;

import lombok.Getter;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

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
        sqlSessionThreadLocal = new ThreadLocal<>();
    }

    public static DatabaseProvider getInstance() {
        return Holder.PROVIDER;
    }

    private void validateSqlSessionFactory() {
        if (Objects.isNull(DatabaseContext.getSqlSessionFactory())) {
            throw new NullPointerException("sql session factory may not initial");
        }
    }

    private final ThreadLocal<SqlSession> sqlSessionThreadLocal;


    public <T> T transactionExecuteReturn(Function<SqlSession, T> function) {
        validateSqlSessionFactory();
        SqlSession sqlSession;
        // 若无事务，创建一个事务
        if (Objects.isNull(sqlSession = sqlSessionThreadLocal.get())) {
            sqlSession = DatabaseContext.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            try {
                T result = function.apply(sqlSession);
                sqlSession.commit();
                return result;
            } catch (Throwable t) {
                if (sqlSession != null) {
                    sqlSession.rollback();
                }
                throw t;
            } finally {
                if (sqlSession != null) {
                    sqlSession.close();
                }
                sqlSessionThreadLocal.remove();
            }
        }
        // 若有事务，加入已存在事务
        try {
            return function.apply(sqlSession);
        } catch (Throwable t) {
            sqlSessionThreadLocal.remove();
            sqlSession.rollback();
            sqlSession.close();
            throw t;
        }
    }


    /**
     * 执行SQL
     *
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T executeReturn(Function<SqlSession, T> function, boolean autoCommit) {
        validateSqlSessionFactory();
        SqlSession sqlSession = null;
        try {
            sqlSession = DatabaseContext.getSqlSessionFactory().openSession(ExecutorType.SIMPLE, autoCommit);
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
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T executeReturn(Function<SqlSession, T> function) {
        return executeReturn(function, true);
    }

    /**
     * 执行SQL
     *
     * @param consumer 执行SQL
     */
    public void batchExecute(Consumer<SqlSession> consumer) {
        executeReturn(sqlSession -> {
            consumer.accept(sqlSession);
            return null;
        }, true);
    }

    /**
     * 执行SQL
     *
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T batchExecuteReturn(Function<SqlSession, T> function) {
        return transactionExecuteReturn(function);
    }

    /**
     * 执行SQL
     *
     * @param function 执行SQL
     * @return 处理结果
     */
    public <T> T batchTransactionExecuteReturn(Function<SqlSession, T> function) {
        return transactionExecuteReturn(function);
    }

    /**
     * 执行SQL
     *
     * @param consumer 执行SQL
     */
    public void batchTransactionExecute(Consumer<SqlSession> consumer) {
        transactionExecuteReturn(sqlSession -> {
            consumer.accept(sqlSession);
            return null;
        });
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
