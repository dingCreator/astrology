package com.dingCreator.astrology.util;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.util.function.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author ding
 * @date 2024/8/3
 */
public class LockUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LockUtil.class);

    private static final Set<String> LOCK_SET = new ConcurrentHashSet<>();

    private static final Object MONITOR = new Object();

    public static <T> T execute(String lockKey, Supplier<T> supplier) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= Constants.GET_LOCK_TIME_OUT) {
            if (!LOCK_SET.contains(lockKey)) {
                synchronized (MONITOR) {
                    if (!LOCK_SET.contains(lockKey)) {
                        LOCK_SET.add(lockKey);
                        try {
                            LOG.info("线程【" + Thread.currentThread().getName() + "】获取key为【" + lockKey + "】的锁成功");
                            return supplier.get();
                        } finally {
                            LOCK_SET.remove(lockKey);
                            LOG.info("线程【" + Thread.currentThread().getName() + "】释放key为【" + lockKey + "】的锁");
                        }
                    }
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ite) {
                LOG.info("线程【" + Thread.currentThread().getName() + "】中止获取key为【" + lockKey + "】的锁");
                break;
            }
        }
        throw new RuntimeException("获取key为【" + lockKey + "】的锁失败");
    }

    public static void execute(String lockKey, Executor executor) {
        execute(lockKey, () -> {
            executor.execute();
            return null;
        });
    }
}
