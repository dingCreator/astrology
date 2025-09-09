delete from bot.astrology_player where id=1;
delete from bot.astrology_player_asset where player_id=1;
delete from bot.astrology_skill_bar_item where belong_to ='Player' and belong_to_id =1;
delete from bot.astrology_skill_belong_to where belong_to ='Player' and belong_to_id =1;
update bot.astrology_equipment_belong_to set equip=0 where belong_to='Player' and belong_to_id=1 and equip=1;

delete from bot.astrology_player;
delete from bot.astrology_player_asset;
DELETE from bot.astrology_skill_bag ;
delete from bot.astrology_skill_bar_item WHERE belong_to ='Player';
delete from bot.astrology_skill_belong_to WHERE belong_to ='Player';
delete from bot.astrology_equipment_belong_to;
delete from bot.astrology_activity_statics ;
DELETE from bot.astrology_activity_record ;
DELETE from bot.astrology_mail_box ;
DELETE from bot.astrology_market_item ;
DELETE from bot.astrology_player_alchemy ;
DELETE from bot.astrology_player_herb ;
DELETE from bot.astrology_player_pill ;
DELETE from bot.astrology_world_boss_record ;

SELECT
	case activity_id
		when 4 then '普池'
		else '精选池'
	end as 卡池类型,
	player_id as 玩家ID,
	sum(count) as 抽卡次数
from bot.astrology_activity_statics aas
WHERE activity_id in(4,7)
and player_id in (11)
group by activity_id
,player_id
ORDER by sum(count) desc;

SELECT belong_to_id , equipment_id ,sum(total_cnt) cnt from bot.astrology_equipment_belong_to
where equipment_id >600
group by belong_to_id ,equipment_id
HAVING cnt>0