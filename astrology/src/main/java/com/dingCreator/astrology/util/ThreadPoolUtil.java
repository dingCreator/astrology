package com.dingCreator.astrology.util;

import com.dingCreator.astrology.util.function.FunctionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/7/24
 */
public class ThreadPoolUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolUtil.class);

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

    public static void join(List<FunctionExecutor> executorList) {
        List<CompletableFuture<?>> list = executorList.stream().map(executor ->
            CompletableFuture.supplyAsync(() -> {
                executor.execute();
                return null;
            }, EXECUTOR)
        ).collect(Collectors.toList());
        list.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                LOG.error("", e);
            }
        });
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
