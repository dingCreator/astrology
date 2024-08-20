CREATE TABLE astrology_player
(
    id                        BIGINT         not null comment '用户ID',
    name                      VARCHAR(32)    NOT null comment '昵称',
    hp                        INTEGER        NOT NULL comment '血量',
    max_hp                    INTEGER        NOT NULL comment '最大血量',
    mp                        INTEGER        NOT NULL comment '蓝量',
    max_mp                    INTEGER        NOT NULL comment '最大蓝量',
    atk                       INTEGER        NOT NULL comment '攻击',
    magic_atk                 INTEGER        NOT NULL comment '法强',
    def                       INTEGER        NOT NULL comment '防御',
    magic_def                 INTEGER        NOT NULL comment '法抗',
    penetrate                 DECIMAL(10, 2) NOT NULL comment '穿甲',
    magic_penetrate           DECIMAL(10, 2) NOT NULL comment '法穿',
    critical_rate             DECIMAL(10, 2) NOT NULL comment '暴击',
    critical_damage_reduction DECIMAL(10, 2) NOT NULL comment '抗暴',
    critical_damage           DECIMAL(10, 2) NOT NULL comment '暴伤',
    critical_reduction_rate   DECIMAL(10, 2) NOT NULL comment '暴免',
    behavior_speed            INTEGER        NOT NULL comment '速度',
    hit                       INTEGER        NOT NULL comment '命中',
    dodge                     INTEGER        NOT NULL comment '闪避',
    life_stealing             DECIMAL(10, 2) NOT NULL comment '吸血',
    `rank`                    INTEGER        NOT NULL comment '阶级',
    `level`                   INTEGER        NOT NULL comment '等级',
    `exp`                     INTEGER        NOT NULL comment '经验',
    job                       VARCHAR(32)    NOT NULL comment '职业',
    map_id                    INTEGER        NOT NULL comment '所在地图ID',
    status                    VARCHAR(16)    NOT NULL comment '状态',
    status_start_time         DATE comment '状态开始时间',
    enabled                   BIT            NOT NULL default 1 comment '是否可用',
    PRIMARY KEY (`id`) USING BTREE,
    unique key udx_name (name)
) ENGINE = innodb comment = '玩家属性表';

CREATE TABLE astrology_monster
(
    id                        BIGINT AUTO_INCREMENT NOT NULL comment '主键',
    name                      VARCHAR(32)           NOT NULL comment '名称',
    hp                        INTEGER               NOT NULL comment '血量',
    max_hp                    INTEGER               NOT NULL comment '最大血量',
    mp                        INTEGER               NOT NULL comment '蓝量',
    max_mp                    INTEGER               NOT NULL comment '最大蓝量',
    atk                       INTEGER               NOT NULL comment '攻击',
    magic_atk                 INTEGER               NOT NULL comment '法强',
    def                       INTEGER               NOT NULL comment '防御',
    magic_def                 INTEGER               NOT NULL comment '法抗',
    penetrate                 DECIMAL(10, 2)        NOT NULL comment '穿甲',
    magic_penetrate           DECIMAL(10, 2)        NOT NULL comment '法穿',
    critical_rate             DECIMAL(10, 2)        NOT NULL comment '暴击',
    critical_damage_reduction DECIMAL(10, 2)        NOT NULL comment '抗暴',
    critical_damage           DECIMAL(10, 2)        NOT NULL comment '暴伤',
    critical_reduction_rate   DECIMAL(10, 2)        NOT NULL comment '暴免',
    behavior_speed            INTEGER               NOT NULL comment '速度',
    hit                       INTEGER               NOT NULL comment '命中',
    dodge                     INTEGER               NOT NULL comment '闪避',
    life_stealing             DECIMAL(10, 2)        NOT NULL comment '吸血',
    `rank`                    INTEGER               NOT NULL comment '阶级',
    `level`                   INTEGER               NOT NULL comment '等级',
    description               VARCHAR(1024)         NULL comment '描述',
    PRIMARY KEY (`id`) using BTREE
) ENGINE = innodb comment = '怪物属性表';

# CREATE TABLE astrology_area_boss
# (
#     id   INTEGER,
#     name VARCHAR(64) NOT NULL,
#     xPos INTEGER     NOT NULL,
#     yPos INTEGER     NOT NULL,
#     PRIMARY KEY (`id`) using BTREE
# ) ENGINE = innodb comment = '区域boss';
#
# CREATE TABLE astrology_area_boss_record
# (
#     id       BIGINT AUTO_INCREMENT comment '主键',
#     player_id INTEGER comment '玩家ID',
#     mapId    INTEGER comment '地图ID',
#     PRIMARY KEY (`id`) using BTREE,
#     KEY idx_pid (player_id)
# ) ;

CREATE TABLE astrology_skill_bar_item
(
    id           BIGINT AUTO_INCREMENT not null comment '主键',
    belong_to    VARCHAR(16)           not null comment '所属类型',
    belong_to_id BIGINT                not null comment '所属ID',
    skill_id     VARCHAR(128)          not null comment '按顺序排列的技能ID列表',
    PRIMARY KEY (`id`) using BTREE,
    KEY idx_blt_blt_id (belong_to, belong_to_id)
) engine = innodb comment = '技能栏';

CREATE TABLE astrology_skill_belong_to
(
    id           BIGINT auto_increment not null comment '主键',
    belong_to    VARCHAR(16)           not null comment '所属类型',
    belong_to_id BIGINT                not null comment '所属ID',
    skill_id     INTEGER               not null comment '技能ID',
    PRIMARY KEY (`id`) using BTREE,
    KEY idx_blt_blt_id (belong_to, belong_to_id)
) engine = innodb comment = '技能列表';

CREATE TABLE astrology_rank_up_boss
(
    id         BIGINT auto_increment not null comment '主键',
    job        VARCHAR(32)           not null comment '职业',
    `rank`     INTEGER               not null comment '阶级',
    monster_id INTEGER               not null comment '怪物ID',
    PRIMARY KEY (`id`) using btree
) engine = innodb comment = '突破boss';

CREATE TABLE astrology_dungeon
(
    id         BIGINT auto_increment not null comment '主键',
    name       VARCHAR(32)           not null comment '副本名称',
    map_id     INTEGER               not null comment '副本所在地图ID',
    max_rank   INTEGER               not null comment '副本最高可参与阶级',
    flush_time INTEGER               not null comment '探索副本冷却时间（秒）',
    loot       VARCHAR(512)          not null comment '掉落物',
    PRIMARY KEY (`id`) using btree,
    key idx_map_id (map_id)
) engine = innodb comment = '副本表';

CREATE TABLE astrology_dungeon_boss
(
    id         BIGINT       NOT NULL comment '主键',
    dungeon_id INTEGER      NOT NULL comment '副本ID',
    monster_id INTEGER      NOT NULL comment '怪物ID',
    loot       VARCHAR(512) NOT NULL comment '掉落物',
    PRIMARY KEY (`id`) using btree,
    key idx_dg_id (dungeon_id)
) engine = innodb comment = '副本boss';

CREATE TABLE astrology_dungeon_record
(
    id              BIGINT auto_increment not null comment '主键',
    player_id       INTEGER               not null comment '玩家ID',
    dungeon_id      INTEGER               not null comment '副本ID',
    lastExploreTime DATE                  not null comment '上次挑战时间',
    PRIMARY KEY (`id`) using btree,
    key idx_pl_id (`player_id`)
) engine = innodb comment = '副本挑战记录';

CREATE TABLE astrology_map
(
    id    BIGINT      NOT NULL comment '主键',
    name  VARCHAR(32) NOT NULL comment '地图名称',
    x_pos INTEGER     NOT NULL comment 'x坐标',
    y_pos INTEGER     NOT NULL comment 'y坐标',
    PRIMARY KEY (`id`) using btree
) engine = innodb comment = '地图表';

CREATE TABLE astrology_equipment_belong_to
(
    id           BIGINT auto_increment not null comment '主键',
    belong_to    VARCHAR(32)           NOT NULL comment '所属类型',
    belong_to_id INTEGER               NOT NULL comment '所属ID',
    equipment_id INTEGER               NOT NULL comment '装备ID',
    level        INTEGER               NOT NULL DEFAULT 0 comment '装备等级',
    equip        BIT                   not null default 0 comment '是否已装备',
    PRIMARY KEY (`id`) using btree,
    key idx_blt_blt_id (belong_to, belong_to_id)
) engine = innodb comment = '装备表';

CREATE TABLE astrology_loot
(
    id             BIGINT auto_increment not null comment '主键',
    belong_to      VARCHAR(32)           NOT NULL comment '所属类型',
    belong_to_id   INTEGER               NOT NULL comment '所属ID',
    money          INTEGER               NOT NULL DEFAULT 0 comment '钱',
    exp            INTEGER               NOT NULL DEFAULT 0 comment '经验',
    loot_item_list VARCHAR(2048) comment '掉落物json',
    PRIMARY KEY (`id`) using btree,
    key idx_blt_blt_id (belong_to, belong_to_id)
) engine = innodb comment = '掉落物表';

CREATE TABLE astrology_world_boss
(
    id          bigint auto_increment not null comment '主键',
    monster_id  bigint                not null comment 'boss id',
    appear_date date                  not null comment '出现日期',
    start_time  datetime              not null comment '开始时间',
    end_time    datetime              not null comment '结束时间',
    PRIMARY KEY (`id`) using btree,
    key idx_appear_date (`appear_date`)
) engine = innodb comment = '世界boss配置表';

CREATE TABLE astrology_world_boss_record
(
    id             bigint auto_increment not null comment '主键',
    player_id_list varchar(64)           not null comment '玩家ID',
    damage         bigint                not null comment '伤害量',
    create_time    datetime              not null comment '记录创建时间',
    PRIMARY KEY (`id`) using btree,
    key idx_player_id_list (`player_id_list`)
) engine = innodb comment ='世界boss挑战记录表';