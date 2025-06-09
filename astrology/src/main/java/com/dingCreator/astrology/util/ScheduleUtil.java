package com.dingCreator.astrology.util;

import com.dingCreator.astrology.util.function.FunctionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ding
 * @date 2025/5/21
 */
public class ScheduleUtil {

    private static final Timer timer = new Timer();

    public static void startTimer(FunctionExecutor executor, long delay, long period) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                executor.execute();
            }
        }, delay, period);
    }

    public static void startTimer(FunctionExecutor executor, Date firstTime, long period) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                executor.execute();
            }
        }, firstTime, period);
    }
}
