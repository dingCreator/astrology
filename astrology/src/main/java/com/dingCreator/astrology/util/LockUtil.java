package com.dingCreator.astrology.util;

import cn.hutool.core.lang.Assert;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.enums.exception.SysExceptionEnum;
import com.dingCreator.astrology.util.function.FunctionExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/8/3
 */
public class LockUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LockUtil.class);

    private static final Map<String, Monitor> LOCK_MAP = new ConcurrentHashMap<>();

    private static final Object MONITOR = new Object();

    public static <T> T execute(String lockKey, Supplier<T> supplier) {
        return execute(Collections.singletonList(lockKey), supplier);
    }

    public static <T> T execute(List<String> lockKeys, Supplier<T> supplier) {
        Assert.notEmpty(lockKeys, "invalid lockKeys: null or empty");
        long startTime = System.currentTimeMillis();
        // 检查是否有锁，有锁等待50-150ms再次检查是否有锁
        // 超时仍未获取到锁的，直接失败
        while (System.currentTimeMillis() - startTime <= Constants.GET_LOCK_TIME_OUT) {
            synchronized (MONITOR) {
                // 获取所有锁成功，才执行
                boolean tryAcquired = lockKeys.stream().allMatch(lockKey ->
                        !LOCK_MAP.containsKey(lockKey) || LOCK_MAP.get(lockKey).getThreadId().equals(Thread.currentThread().getId()));
                if (!tryAcquired) {
                    try {
                        Thread.sleep(RandomUtil.rangeIntRandom(50, 150));
                    } catch (InterruptedException ite) {
                        if (LOG.isDebugEnabled()) {
                            LOG.info("线程【{}】中止获取key为【{}】的锁", Thread.currentThread().getName(), lockKeys);
                        }
                        break;
                    }
                    continue;
                }
                lockKeys.forEach(lockKey -> {
                    Monitor monitor = LOCK_MAP.getOrDefault(lockKey, new Monitor(0, Thread.currentThread().getId()));
                    monitor.setCnt(monitor.getCnt() + 1);
                    LOCK_MAP.put(lockKey, monitor);
                });
            }
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.info("线程【{}】获取key为【{}】的锁成功", Thread.currentThread().getName(), lockKeys);
                }
                return supplier.get();
            } catch (Throwable e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error("线程【{}】出现错误", Thread.currentThread().getName(), e);
                }
                throw e;
            } finally {
                lockKeys.forEach(lockKey -> {
                    Monitor monitor = LOCK_MAP.get(lockKey);
                    monitor.setCnt(monitor.getCnt() - 1);
                    if (monitor.getCnt() == 0) {
                        LOCK_MAP.remove(lockKey);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.info("线程【{}】释放key为【{}】的锁", Thread.currentThread().getName(), lockKey);
                    }
                });
            }
        }
        LOG.error("线程【{}】获取key为【{}】的锁失败", Thread.currentThread().getName(), lockKeys);
        throw SysExceptionEnum.SYS_BUSY.getException();
    }

    public static void execute(String lockKey, FunctionExecutor functionExecutor) {
        execute(Collections.singletonList(lockKey), () -> {
            functionExecutor.execute();
            return null;
        });
    }

    public static void execute(String lockPrefix, List<String> lockName, FunctionExecutor functionExecutor) {
        List<String> lockKeys = lockName.stream().map(name -> lockPrefix + name).collect(Collectors.toList());
        execute(lockKeys, () -> {
            functionExecutor.execute();
            return null;
        });
    }

    @Data
    @AllArgsConstructor
    private static class Monitor {
        private Integer cnt;
        private Long threadId;
    }
}
