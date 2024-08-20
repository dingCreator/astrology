package com.dingCreator.astrology.util;

import com.dingCreator.astrology.util.function.FunctionExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author ding
 * @date 2024/7/24
 */
public class ThreadPoolUtil {
    /**
     * 线程池
     */
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(8, 16,
            5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), (ThreadFactory) Thread::new);

    /**
     * 线程池执行consumer
     */
    public static void execute(FunctionExecutor functionExecutor) {
        EXECUTOR.execute(functionExecutor::execute);
    }

    /**
     * 线程池执行consumer
     */
    public static <T> void executeConsumer(Consumer<T> consumer, T t) {
        EXECUTOR.execute(() -> consumer.accept(t));
    }

    /**
     * 线程池执行BiConsumer
     */
    public static <T, U> void executeBiConsumer(BiConsumer<T, U> biConsumer, T t, U u) {
        EXECUTOR.execute(() -> biConsumer.accept(t, u));
    }
}
