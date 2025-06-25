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
    map_id                    BIGINT         NOT NULL comment '所在地图ID',
    status                    VARCHAR(16)    NOT NULL comment '状态',
    status_start_time         DATETIME comment '状态开始时间',
    enabled                   BIT            NOT NULL default 1 comment '是否可用',
    PRIMARY KEY (`id`) USING BTREE,
    unique key udx_name (name)
) ENGINE = innodb comment = '玩家属性表';

CREATE TABLE astrology_monster
(
    id                        BIGINT AUTO_INCREMENT NOT NULL comment '主键',
    name                      VARCHAR(32)           NOT NULL comment '名称',
    hp                        BIGINT                NOT NULL comment '血量',
    max_hp                    BIGINT                NOT NULL comment '最大血量',
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
    skill_id     VARCHAR(256)          not null comment '按顺序排列的技能ID列表',
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
    max_rank   INTEGER               not null comment '副本最高可参与阶级',
    flush_time INTEGER               not null comment '探索副本冷却时间（秒）',
    pass_rate  DECIMAL(8,5)          not null comment '直接通过概率',
    PRIMARY KEY (`id`) using btree,
    key idx_name (name)
) engine = innodb comment = '副本表';

CREATE TABLE astrology_dungeon_config (
  id BIGINT auto_increment not null comment '主键',
  dungeon_id BIGINT NOT NULL comment '副本ID',
  floor INT NOT NULL comment '层数',
  wave INT NOT NULL comment '波数',
  monster_id BIGINT NOT NULL comment 'boss的ID',
  cnt INT NOT NULL comment '数量',
  PRIMARY KEY (`id`) using btree,
  key idx_dungeon_id (dungeon_id)
) engine = innodb comment = '副本配置表';

CREATE TABLE astrology_dungeon_record
(
    id              BIGINT auto_increment not null comment '主键',
    player_id       BIGINT               not null comment '玩家ID',
    dungeon_id      BIGINT               not null comment '副本ID',
    explore_status  VARCHAR(16)          not null comment '探索状态',
    floor           INT                  not null comment '层数',
    last_explore_time DATETIME           not null comment '上次挑战时间',
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
    belong_to_id BIGINT                NOT NULL comment '所属ID',
    equipment_id BIGINT                NOT NULL comment '装备ID',
    equipment_level        INT         NOT NULL DEFAULT 1 comment '装备等级',
    cnt          INT                   NOT NULL DEFAULT 1 comment '装备数量',
    equip        BIT                   not null default 0 comment '是否已装备',
    PRIMARY KEY (`id`) using btree,
    unique key uk_blt_blt_id_eq_id_lv (belong_to, belong_to_id, equipment_id, level)
) engine = innodb comment = '装备表';

CREATE TABLE `astrology_loot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `belong_to` varchar(32) NOT NULL COMMENT '所属类型',
  `belong_to_id` int NOT NULL COMMENT '所属ID',
  `asset` varchar(512) NOT NULL DEFAULT '{}' COMMENT '资产',
  `exp` int NOT NULL DEFAULT '0' COMMENT '经验',
  `ext_info` VARCHAR(1024) COMMENT '扩展信息',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_blt_blt_id` (`belong_to`,`belong_to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='掉落物表'

CREATE TABLE astrology_loot_item
(
    id BIGINT auto_increment not null comment '主键',
    loot_id BIGINT not null comment '所属掉落物id',
    rate DECIMAL(10,4) not null default 1 comment '掉落概率',
    article_json varchar(256) comment '具体掉落物json',
    primary key (id) using btree,
    key idx_loot_id (loot_id)
) engine = innodb comment = '掉落物详情表';

CREATE TABLE astrology_world_boss
(
    id          bigint auto_increment not null comment '主键',
    monster_id  bigint                not null comment 'boss id',
    start_time  datetime              not null comment '开始时间',
    end_time    datetime              not null comment '结束时间',
    award       varchar(4096)         comment '奖励配置',
    distribute  bit                   comment '是否已发放奖励',
    PRIMARY KEY (`id`) using btree,
    key idx_start_end_time (`start_time`,`end_time`)
) engine = innodb comment = '世界boss配置表';

CREATE TABLE astrology_world_boss_record
(
    id             bigint auto_increment not null comment '主键',
    world_boss_id  bigint                not null comment '世界boss配置ID',
    player_id      bigint                not null comment '玩家ID',
    damage         bigint                not null comment '伤害量',
    create_time    datetime              not null comment '记录创建时间',
    PRIMARY KEY (`id`) using btree,
    unique key uk_world_boss_player_id (`world_boss_id`,`player_id`),
    key idx_damage (`damage`)
) engine = innodb comment ='世界boss挑战记录表';

CREATE TABLE astrology_task_template_detail
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    task_template_id BIGINT COMMENT '任务模板ID',
    target_type      VARCHAR(255) COMMENT '任务目标',
    allow_failed     BOOLEAN COMMENT '此任务是否允许失败（即失败后是否直接导致任务失败）',
    target_id        BIGINT COMMENT '目标ID',
    target_cnt       INT COMMENT '目标数量',
    success_msg      VARCHAR(255) COMMENT '完成任务返回信息',
    fail_msg         VARCHAR(255) COMMENT '任务失败返回信息'
) ENGINE = InnoDB COMMENT ='任务详情表';

CREATE TABLE astrology_task_template
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title_id    BIGINT COMMENT '任务标题ID',
    description TEXT COMMENT '任务描述',
    priority    INT COMMENT '优先级'
) ENGINE = InnoDB COMMENT ='任务模板表';

CREATE TABLE astrology_task_template_title
(
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `name`                   VARCHAR(255) NOT NULL COMMENT '任务名称',
    description              TEXT COMMENT '任务描述',
    task_type                VARCHAR(50) COMMENT '任务类型',
    limit_map_id             VARCHAR(255) COMMENT '可完成任务的地图，逗号分隔，为空表示无限制',
    children_sort            BOOLEAN COMMENT '任务是否必须按顺序完成',
    mutual_exclusion         BOOLEAN COMMENT '是否与其他任务互斥',
    allow_team               BOOLEAN COMMENT '是否允许组队',
    allow_repeatable_receive BOOLEAN COMMENT '是否允许多次接取'
) ENGINE = InnoDB COMMENT ='任务模板标题表';

CREATE TABLE astrology_task_schedule_detail
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    player_id               BIGINT COMMENT '玩家ID',
    task_title_id           BIGINT COMMENT '任务模板标题ID',
    task_template_id        BIGINT COMMENT '任务模板ID',
    task_template_detail_id BIGINT COMMENT '任务模板详情ID',
    target                  VARCHAR(32) NOT NULL COMMENT '任务目标类型',
    target_id               BIGINT COMMENT '任务目标ID',
    target_cnt              INT COMMENT '任务目标数量',
    complete_cnt            INT COMMENT '已完成数量',
    task_schedule_type      VARCHAR(50) COMMENT '任务进度类型',
    KEY `idx_player_id` (`player_id`) USING BTREE
) ENGINE = InnoDB COMMENT ='任务调度详情表';

CREATE TABLE astrology_peak_task_template
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    job                    VARCHAR(255) COMMENT '职业',
    `rank`                 INT COMMENT '阶级',
    task_template_title_id BIGINT COMMENT '任务模板ID'
) ENGINE = InnoDB COMMENT ='巅峰任务模板表';

CREATE TABLE astrology_player_asset
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    player_id      BIGINT NOT NULL COMMENT '玩家ID',
    asset_type     varchar(64) NOT NULL DEFAULT 0 COMMENT '资产类型',
    asset_cnt      BIGINT NOT NULL DEFAULT 0 COMMENT '资产数量',
    unique key uk_player_id_asset_type(player_id, asset_type)
) ENGINE = InnoDB COMMENT ='玩家资产表';

CREATE TABLE astrology_activity
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    activity_type   VARCHAR(255) NOT NULL COMMENT '活动类型',
    activity_name   VARCHAR(255) NOT NULL COMMENT '活动名称',
    `limit`         INT          NOT NULL COMMENT '限制值',
    enabled         BIT          NOT NULL DEFAULT 1 COMMENT '是否可用',
    default_flag    BIT          NOT NULL DEFAULT 0 COMMENT '是否默认',
    start_time      DATETIME     NOT NULL comment '开始时间',
    end_time        DATETIME     NOT NULL comment '结束时间',
    cost_json       TEXT         NOT NULL comment '参与花费',
    award_rule_json TEXT         NOT NULL comment '奖品规则'
) ENGINE = InnoDB COMMENT ='活动表';

CREATE TABLE astrology_activity_record
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    activity_id BIGINT   NOT NULL COMMENT '活动ID',
    player_id   BIGINT   NOT NULL COMMENT '玩家ID',
    join_times  INT      NOT NULL DEFAULT 1 COMMENT '连续参与次数',
    create_time DATETIME NOT NULL COMMENT '参与时间',
    key idx_activity_player_id (activity_id, player_id) using btree
) COMMENT ='活动参与记录表';

CREATE TABLE astrology_activity_statics
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    player_id   BIGINT NOT NULL COMMENT '玩家ID',
    date_time   VARCHAR(255) COMMENT '统计时间节点',
    count       INT    NOT NULL COMMENT '次数',
    key idx_activity_player_id_time (activity_id, player_id, date_time) using btree
) COMMENT ='活动参与统计表';

CREATE TABLE `astrology_pill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pill_name` VARCHAR(255) NOT NULL COMMENT '丹药名称',
    `pill_rank` INT COMMENT '丹药等级',
    `quality_rank` INT COMMENT '品质等级',
    `pill_type` VARCHAR(32) COMMENT '丹药类型',
    `vigor` INT COMMENT '生机',
    `warn` INT COMMENT '温热',
    `cold` INT COMMENT '寒韵',
    `toxicity` INT COMMENT '毒性',
    `quality_start` INT COMMENT '品质',
    `quality_end` INT COMMENT '品质',
    `star_start` INT COMMENT '星辰之力',
    `star_end` INT COMMENT '星辰之力',
    `effect_json` TEXT COMMENT '效果',
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_pill_name_quality_rank (`pill_name`,`quality_rank`),
    KEY idx_prop(vigor,warn,cold,toxicity,quality_start,quality_end,star_start,star_end)
) ENGINE=InnoDB COMMENT='丹药表';

create table astrology_player_herb (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    player_id BIGINT NOT NULL COMMENT '玩家ID',
    herb_id BIGINT NOT NULL COMMENT '药材ID',
    herb_cnt INT NOT NULL DEFAULT 0 COMMENT '药材数量',
    PRIMARY KEY (id),
    UNIQUE KEY uk_player_id_herb_id (player_id, herb_id)
) ENGINE=innodb COMMENT='玩家药材表';

create table astrology_player_pill (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    player_id BIGINT NOT NULL COMMENT '玩家ID',
    pill_id BIGINT NOT NULL COMMENT '丹药ID',
    pill_cnt INT NOT NULL COMMENT '丹药数量',
    PRIMARY KEY(id),
    UNIQUE KEY uk_player_id_pill_id (player_id, pill_id)
) ENGINE=innodb COMMENT='玩家丹药表';

create table astrology_player_alchemy (
    player_id BIGINT NOT NULL COMMENT '玩家ID',
    property_exp INT NOT NULL DEFAULT 0 COMMENT '属性丹经验值',
    property_rank INT NOT NUll DEFAULT 0 COMMENT '属性丹炼制等级',
    buff_exp INT NOT NULL DEFAULT 0 COMMENT 'buff丹经验值',
    buff_rank INT NOT NULL DEFAULT 0 COMMENT 'buff丹炼制等级',
    status_exp INT NOT NULL DEFAULT 0 COMMENT '状态丹经验值',
    status_rank INT NOT NULL DEFAULT 0 COMMENT '状态丹炼制等级',
    alchemy_skill_id BIGINT COMMENT '炼丹术',
    PRIMARY KEY (player_id)
) ENGINE=innodb COMMENT='玩家炼丹术表'

create table astrology_skill_bag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    player_id BIGINT NOT NULL COMMENT '玩家ID',
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    skill_cnt INT NOT NULL DEFAULT 0 COMMENT '技能数量',
    PRIMARY KEY (id),
    UNIQUE KEY uk_player_id_skill_id (player_id, skill_id)
) ENGINE=innodb COMMENT='玩家技能背包表';

create table astrology_mail_box (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    player_id BIGINT NOT NULL COMMENT '玩家ID',
    subject varchar(64) not null comment '主题',
    item_json varchar(1024) not null comment '物品信息',
    item_cnt INT not null default 1 comment '数量',
    create_time datetime not null default current_timestamp comment '发放时间',
    expire_in datetime not null comment '过期时间',
    received bit not null default 0 comment '是否已领取',
    primary key (id),
    key idx_player_id_expire_in_received(player_id, expire_in, received),
    key idx_create_time(create_time)
) engine=innodb comment='玩家邮箱表';

CREATE TABLE `astrology_market_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `player_id` bigint NOT NULL COMMENT '玩家ID',
  `article_name` varchar(255) NOT NULL COMMENT '物品名称',
  `article_json` text NOT NULL COMMENT '物品json',
  `item_cnt` int NOT NULL COMMENT '物品数量',
  `cost_map_json` text NOT NULL COMMENT '价格（单价）',
  PRIMARY KEY (`id`),
  key idx_player_id(player_id),
  key idx_article_name(article_name)
) ENGINE=InnoDB COMMENT='市场物品表';