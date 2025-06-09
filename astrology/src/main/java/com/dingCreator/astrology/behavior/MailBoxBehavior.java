package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.dto.MailBoxDTO;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.MailBoxService;

import java.util.List;

/**
 * @author ding
 * @date 2025/5/22
 */
public class MailBoxBehavior {

    /**
     * 发送邮件
     *
     * @param dtoList 邮件内容
     */
    public void sendMail(List<MailBoxDTO> dtoList) {
        MailBoxService.getInstance().sendMail(dtoList);
    }

    /**
     * 获取邮箱未领取记录
     *
     * @param playerId  玩家id
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 未领取物品
     */
    public PageResponse<MailBoxDTO> getUnreceivedPage(Long playerId, int pageIndex, int pageSize) {
        return MailBoxService.getInstance().getUnreceivedPage(playerId, pageIndex, pageSize);
    }

    /**
     * 领取邮件
     *
     * @param playerId 玩家ID
     * @return 领取结果
     */
    public List<MailBoxDTO> doReceive(Long playerId) {
        return MailBoxService.getInstance().doReceive(playerId);
    }

    private MailBoxBehavior() {

    }

    public static MailBoxBehavior getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final MailBoxBehavior INSTANCE = new MailBoxBehavior();
    }
}
