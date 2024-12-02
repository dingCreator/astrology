package com.dingCreator.astrology.util;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.enums.exception.SysExceptionEnum;
import com.dingCreator.astrology.util.function.FunctionExecutor;
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
        // 检查是否有锁，有锁等待50-150ms再次检查是否有锁
        // 超时仍未获取到锁的，直接失败
        while (System.currentTimeMillis() - startTime <= Constants.GET_LOCK_TIME_OUT) {
            synchronized (MONITOR) {
                if (LOCK_SET.contains(lockKey)) {
                    try {
                        Thread.sleep(RandomUtil.rangeIntRandom(50, 150));
                    } catch (InterruptedException ite) {
                        if (LOG.isDebugEnabled()) {
                            LOG.info("线程【{}】中止获取key为【{}】的锁", Thread.currentThread().getName(), lockKey);
                        }
                        break;
                    }
                    continue;
                }
                LOCK_SET.add(lockKey);
            }
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.info("线程【{}】获取key为【{}】的锁成功", Thread.currentThread().getName(), lockKey);
                }
                return supplier.get();
            } catch (Throwable e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error("线程【{}】出现错误", Thread.currentThread().getName(), e);
                }
                throw e;
            } finally {
                LOCK_SET.remove(lockKey);
                if (LOG.isDebugEnabled()) {
                    LOG.info("线程【{}】释放key为【{}】的锁", Thread.currentThread().getName(), lockKey);
                }
            }
        }
        LOG.error("线程【{}】获取key为【{}】的锁失败", Thread.currentThread().getName(), lockKey);
        throw SysExceptionEnum.SYS_BUSY.getException();
    }

    public static void execute(String lockKey, FunctionExecutor functionExecutor) {
        execute(lockKey, () -> {
            functionExecutor.execute();
            return null;
        });
    }
}
