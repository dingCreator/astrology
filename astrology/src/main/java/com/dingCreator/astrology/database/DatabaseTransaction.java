package com.dingCreator.astrology.database;

import com.dingCreator.astrology.util.function.FunctionExecutor;

/**
 * @author ding
 * @date 2025/5/21
 */
public class DatabaseTransaction {

    public static void doTransaction(FunctionExecutor executor) {
        try {
            DatabaseProvider.getInstance().getSqlSessionThreadLocal()
                    .set(DatabaseContext.getSqlSessionFactory().openSession());
            executor.execute();
        } finally {
            DatabaseProvider.getInstance().getSqlSessionThreadLocal().get().close();
        }
    }
}
