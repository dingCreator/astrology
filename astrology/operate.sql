delete from bot.astrology_player where id=1;
delete from bot.astrology_player_asset where player_id=1;
delete from bot.astrology_skill_bar_item where belong_to ='Player' and belong_to_id =1;
delete from bot.astrology_skill_belong_to where belong_to ='Player' and belong_to_id =1;
update bot.astrology_equipment_belong_to set equip=0 where belong_to='Player' and belong_to_id=1 and equip=1;