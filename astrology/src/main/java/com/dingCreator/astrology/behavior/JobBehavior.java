package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.response.BaseResponse;

import java.util.Arrays;
import java.util.List;
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
    public BaseResponse<List<String>> getJob() {
        BaseResponse<List<String>> response = new BaseResponse<>();
        response.setContent(Arrays.stream(JobEnum.values()).map(JobEnum::getJobName).collect(Collectors.toList()));
        return response;
    }

    public BaseResponse<?> getJobDetail(String jobName) {
        return null;
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
