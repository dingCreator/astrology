package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.MailBoxDTO;
import com.dingCreator.astrology.entity.MailBox;
import com.dingCreator.astrology.mapper.MailBoxMapper;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.util.PageUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2025/5/22
 */
public class MailBoxService {

    public void sendMail(List<MailBoxDTO> mailList) {
        DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
            MailBoxMapper mapper = sqlSession.getMapper(MailBoxMapper.class);
            mailList.forEach(mail -> LockUtil.execute(Constants.MAIL_LOCK_PREFIX + mail.getPlayerId(),
                        () -> mapper.insert(mail.convert2Entity())
            ));
        });
    }

    public int countUnreceived(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(MailBoxMapper.class)
                .selectCount(constructQueryUnreceived(playerId)));
    }

    public PageResponse<MailBoxDTO> getUnreceivedPage(Long playerId, int pageIndex, int pageSize) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            List<MailBox> mailBoxList = sqlSession.getMapper(MailBoxMapper.class)
                    .selectList(constructQueryUnreceived(playerId)
                            .last(" limit " + (pageIndex - 1) * pageSize + "," + pageSize));
            List<MailBoxDTO> dtoList = mailBoxList.stream().map(MailBox::convert2Dto).collect(Collectors.toList());
            return PageUtil.addPageDesc(dtoList, pageIndex, pageSize, countUnreceived(playerId));
        });
    }

    public List<MailBoxDTO> doReceive(Long playerId) {
        return DatabaseProvider.getInstance().batchTransactionExecuteReturn(sqlSession ->
                LockUtil.execute(Constants.MAIL_LOCK_PREFIX + playerId, () -> {
                    MailBoxMapper mapper = sqlSession.getMapper(MailBoxMapper.class);
                    List<MailBox> mailBoxList = mapper.selectList(constructQueryUnreceived(playerId).last("limit 50"));
                    return mailBoxList.stream().peek(mailBox -> {
                                mailBox.setReceived(true);
                                mapper.updateById(mailBox);
                            }).map(MailBox::convert2Dto).peek(dto -> dto.getItem().changeCnt(playerId, dto.getItemCnt()))
                            .collect(Collectors.toList());
                })
        );
    }

    private QueryWrapper<MailBox> constructQueryUnreceived(Long playerId) {
        return new QueryWrapper<MailBox>().eq(MailBox.PLAYER_ID, playerId)
                .ge(MailBox.EXPIRE_IN, LocalDateTime.now()).eq(MailBox.RECEIVED, false);
    }


    public static MailBoxService getInstance() {
        return Holder.SERVICE;
    }

    private MailBoxService() {

    }

    private static class Holder {
        public static final MailBoxService SERVICE = new MailBoxService();
    }
}
