package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.enums.JobEnum;
import com.dingCreator.astrology.vo.BaseVO;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/22
 */
public class JobBehavior {

    /**
     * 获取职业信息
     *
     * @return 职业信息
     */
    public BaseVO<String> getJob() {
        BaseVO<String> baseVO = new BaseVO<>();
        final AtomicInteger index = new AtomicInteger(1);
        baseVO.setMsg(Arrays.stream(JobEnum.values()).map(job ->
                index.getAndAdd(1) + "." + job.getJobName()
        ).collect(Collectors.toList()));
        return baseVO;
    }

    private static class Holder {
        private static final JobBehavior BEHAVIOR = new JobBehavior();
    }

    private JobBehavior() {

    }

    public static JobBehavior getInstance() {
        return JobBehavior.Holder.BEHAVIOR;
    }
}
