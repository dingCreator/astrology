package com.dingCreator.astrology.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.MarketItemDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.MarketItem;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.exception.MarketExceptionEnum;
import com.dingCreator.astrology.mapper.MarketItemMapper;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.vo.MarketResultVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2025/6/19
 */
public class MarketService {

    public MarketResultVO buy(long playerId, long itemId, int cnt) {
        PlayerInfoDTO info = PlayerCache.getPlayerById(playerId);
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            MarketItem marketItem = sqlSession.getMapper(MarketItemMapper.class).selectById(itemId);
            if (Objects.isNull(marketItem) || marketItem.getItemCnt() <= 0) {
                throw MarketExceptionEnum.ITEM_NOT_EXIST.getException();
            }
            if (marketItem.getPlayerId().equals(playerId)) {
                throw MarketExceptionEnum.CANT_BUY_SELF.getException();
            }
            int buyCnt = cnt > 0 ? cnt : marketItem.getItemCnt();
            if (buyCnt > marketItem.getItemCnt()) {
                buyCnt = marketItem.getItemCnt();
            }
            MarketItemDTO itemDTO = marketItem.convert();
            Map<AssetTypeEnum, Long> costMap = itemDTO.getCostMap();
            final int finalBuyCnt = buyCnt;
            costMap.entrySet().forEach(entry -> entry.setValue(entry.getValue() * finalBuyCnt));
            // 买方扣钱
            List<PlayerAssetDTO> buyerAssetDTOList = costMap.entrySet().stream()
                    .map(entry -> PlayerAssetDTO.builder().playerId(playerId)
                            .assetType(entry.getKey().getCode())
                            .assetCnt(-entry.getValue()).build()
                    ).collect(Collectors.toList());
            PlayerService.getInstance().changeAsset(info, buyerAssetDTOList);
            // 卖方加钱
            List<PlayerAssetDTO> assetDTOList = costMap.entrySet().stream()
                    .map(entry -> PlayerAssetDTO.builder().playerId(marketItem.getPlayerId())
                            .assetType(entry.getKey().getCode())
                            .assetCnt(entry.getValue()).build()
                    ).collect(Collectors.toList());
            PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(marketItem.getPlayerId()), assetDTOList);
            // 更新数量
            marketItem.setItemCnt(marketItem.getItemCnt() - finalBuyCnt);
            if (marketItem.getItemCnt() <= 0) {
                sqlSession.getMapper(MarketItemMapper.class).deleteById(itemId);
            } else {
                sqlSession.getMapper(MarketItemMapper.class).updateById(marketItem);
            }
            // 买方收货
            itemDTO.getArticleItem().changeCnt(playerId, finalBuyCnt);
            return MarketResultVO.builder().itemName(marketItem.getArticleName()).itemCnt(finalBuyCnt).build();
        });
    }

    public Long shelve(long playerId, ArticleItemDTO item, int itemCnt, Map<String, Long> assetMap) {
        validAssetMap(assetMap);
        PlayerCache.getPlayerById(playerId);
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession ->
                LockUtil.execute(Constants.SHOP_LOCK_PREFIX + playerId, () -> {
                    item.changeCnt(playerId, -itemCnt);
                    MarketItem marketItem = MarketItem.builder().playerId(playerId)
                            .articleName(item.view().getName())
                            .articleJson(JSONObject.toJSONString(item)).itemCnt(itemCnt)
                            .costMapJson(JSONObject.toJSONString(assetMap)).build();
                    sqlSession.getMapper(MarketItemMapper.class).insert(marketItem);
                    return marketItem.getId();
                })
        );
    }

    public PageResponse<MarketItemDTO> pageMarketItem(int pageIndex, int pageSize, String itemName) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            MarketItemMapper mapper = sqlSession.getMapper(MarketItemMapper.class);
            QueryWrapper<MarketItem> wrapper = new QueryWrapper<MarketItem>()
                    .like(StringUtils.isNotBlank(itemName), MarketItem.ARTICLE_NAME, "%" + itemName + "%")
                    .gt(MarketItem.ITEM_CNT, 0);
            int count = mapper.selectCount(wrapper);
            wrapper.last(" limit " + (pageIndex - 1) * pageSize + "," + pageSize);
            List<MarketItem> itemList = mapper.selectList(wrapper);
            PageResponse<MarketItemDTO> pageResponse = new PageResponse<>();
            pageResponse.setData(itemList.stream().map(MarketItem::convert).collect(Collectors.toList()));
            pageResponse.setPageIndex(pageIndex);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(count);
            return pageResponse;
        });
    }

    public MarketResultVO offShelve(long playerId, long shelveId, int cnt) {
        PlayerCache.getPlayerById(playerId);
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession ->
                LockUtil.execute(Constants.SHOP_LOCK_PREFIX + playerId, () -> {
                    MarketItem marketItem = sqlSession.getMapper(MarketItemMapper.class).selectById(shelveId);
                    if (Objects.isNull(marketItem) || marketItem.getItemCnt() <= 0) {
                        throw MarketExceptionEnum.ITEM_NOT_EXIST.getException();
                    }
                    if (!marketItem.getPlayerId().equals(playerId)) {
                        throw MarketExceptionEnum.NOT_YOUR_ITEM.getException();
                    }
                    int finalCnt = cnt > marketItem.getItemCnt() || cnt < 0 ?
                            marketItem.getItemCnt() : cnt;
                    marketItem.setItemCnt(marketItem.getItemCnt() - finalCnt);
                    marketItem.convert().getArticleItem().changeCnt(marketItem.getPlayerId(), finalCnt);
                    if (marketItem.getItemCnt() <= 0) {
                        sqlSession.getMapper(MarketItemMapper.class).deleteById(shelveId);
                    } else {
                        sqlSession.getMapper(MarketItemMapper.class).updateById(marketItem);
                    }
                    return MarketResultVO.builder().itemName(marketItem.getArticleName()).itemCnt(finalCnt).build();
                })
        );
    }

    public void changeCost(long playerId, long itemId, Map<String, Long> assetMap) {
        validAssetMap(assetMap);
        DatabaseProvider.getInstance().transactionExecute(sqlSession -> {
            MarketItem marketItem = sqlSession.getMapper(MarketItemMapper.class).selectById(itemId);
            if (Objects.isNull(marketItem) || marketItem.getItemCnt() <= 0) {
                throw MarketExceptionEnum.ITEM_NOT_EXIST.getException();
            }
            if (!marketItem.getPlayerId().equals(playerId)) {
                throw MarketExceptionEnum.NOT_YOUR_ITEM.getException();
            }
            marketItem.setCostMapJson(JSONObject.toJSONString(assetMap));
            sqlSession.getMapper(MarketItemMapper.class).updateById(marketItem);
        });
    }

    private void validAssetMap(Map<String, Long> assetMap) {
        assetMap.forEach((assetType, val) -> {
            if (!AssetTypeEnum.getByCode(assetType).getDealAsset()) {
               throw MarketExceptionEnum.NOT_DEAL_ASSET.getException();
            }
            if (val <= 0) {
                throw MarketExceptionEnum.INVALID_COST.getException();
            }
        });
    }

    private MarketService() {

    }

    public static MarketService getInstance() {
        return Holder.SERVICE;
    }

    private static class Holder {
        public static final MarketService SERVICE = new MarketService();
    }
}
