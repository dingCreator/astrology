package com.dingCreator.astrology.database;

import lombok.Getter;
import org.apache.ibatis.session.SqlSession;

/**
 * @author ding
 * @date 2024/2/20
 */
@Getter
public class DatabaseProvider {

    private PlayerDataMapper playerDataMapper;
    private MapDataMapper mapDataMapper;

    private DatabaseProvider() {
        this.playerDataMapper = DatabaseContext.getSqlSession().getMapper(PlayerDataMapper.class);
        this.mapDataMapper = DatabaseContext.getSqlSession().getMapper(MapDataMapper.class);
    }

    private static class Holder {
        private static final DatabaseProvider PROVIDER = new DatabaseProvider();
    }

    public static DatabaseProvider getInstance() {
        return Holder.PROVIDER;
    }
}
