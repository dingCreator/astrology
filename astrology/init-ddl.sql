CREATE TABLE astrology_player
(
    id                      INTEGER PRIMARY KEY autoincrement,
    name                    VARCHAR(32) NOT NULL,
    hp                      INTEGER     NOT NULL,
    maxHp                   INTEGER     NOT NULL,
    mp                      INTEGER     NOT NULL,
    maxMp                   INTEGER     NOT NULL,
    atk                     INTEGER     NOT NULL,
    magicAtk                INTEGER     NOT NULL,
    def                     INTEGER     NOT NULL,
    magicDef                INTEGER     NOT NULL,
    penetrate               REAL        NOT NULL,
    magicPenetrate          REAL        NOT NULL,
    criticalRate            REAL        NOT NULL,
    criticalDamageReduction REAL        NOT NULL,
    criticalDamage          REAL        NOT NULL,
    criticalReductionRate   REAL        NOT NULL,
    behaviorSpeed           INTEGER     NOT NULL,
    hit                     INTEGER     NOT NULL,
    dodge                   INTEGER     NOT NULL,
    rank                    INTEGER     NOT NULL,
    level                   INTEGER     NOT NULL,
    exp                     INTEGER     NOT NULL,
    job                     VARCHAR(32) NOT NULL,
    mapId                   INTEGER     NOT NULL,
    status                  VARCHAR(16) NOT NULL,
    statusStartTime         DATE,
    enabled                 INTEGER     NOT NULL
);

CREATE TABLE astrology_monster
(
    id                      INTEGER PRIMARY KEY autoincrement,
    name                    VARCHAR(32)   NOT NULL,
    hp                      INTEGER       NOT NULL,
    maxHp                   INTEGER       NOT NULL,
    mp                      INTEGER       NOT NULL,
    maxMp                   INTEGER       NOT NULL,
    atk                     INTEGER       NOT NULL,
    magicAtk                INTEGER       NOT NULL,
    def                     INTEGER       NOT NULL,
    magicDef                INTEGER       NOT NULL,
    penetrate               REAL          NOT NULL,
    magicPenetrate          REAL          NOT NULL,
    criticalRate            REAL          NOT NULL,
    criticalDamageReduction REAL          NOT NULL,
    criticalDamage          REAL          NOT NULL,
    criticalReductionRate   REAL          NOT NULL,
    behaviorSpeed           INTEGER       NOT NULL,
    hit                     INTEGER       NOT NULL,
    dodge                   INTEGER       NOT NULL,
    lifeStealing            REAL          NOT NULL,
    rank                    INTEGER       NOT NULL,
    level                   INTEGER       NOT NULL,
    description             VARCHAR(1024) NULL
);

CREATE TABLE astrology_area_boss
(
    id   INTEGER PRIMARY KEY autoincrement,
    name VARCHAR(64) NOT NULL,
    xPos INTEGER     NOT NULL,
    yPos INTEGER     NOT NULL
);

CREATE TABLE astrology_area_boss_record
(
    id       INTEGER PRIMARY KEY autoincrement,
    playerId INTEGER,
    mapId    INTEGER
);

CREATE TABLE astrology_skill_bar_item
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    belongTo   VARCHAR(16),
    belongToId INTEGER,
    skillId    VARCHAR(128)
);

CREATE TABLE astrology_skill_belong_to
(
    id         INTEGER PRIMARY KEY autoincrement,
    belongTo   VARCHAR,
    belongToId INTEGER,
    skillId    INTEGER
);

CREATE TABLE astrology_rank_up_boss
(
    id        INTEGER PRIMARY KEY autoincrement,
    job       VARCHAR,
    rank      INTEGER,
    monsterId INTEGER
);