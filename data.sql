-- smazání všech záznamů z tabulek
CREATE or replace FUNCTION clean_tables() RETURNS void AS $$
declare
    l_stmt text;
begin
    select 'truncate ' || string_agg(format('%I.%I', schemaname, tablename) , ',')
    into l_stmt
    from pg_tables
    where schemaname in ('public');

    if l_stmt is not null then
        execute l_stmt || ' cascade';
    end if;
end;
$$ LANGUAGE plpgsql;
select clean_tables();

-- reset sekvenci
CREATE or replace FUNCTION restart_sequences() RETURNS void AS $$
DECLARE
    i TEXT;
BEGIN
    FOR i IN (SELECT column_default FROM information_schema.columns WHERE column_default SIMILAR TO 'nextval%')
        LOOP
            EXECUTE 'ALTER SEQUENCE'||' ' || substring(substring(i from '''[a-z_]*')from '[a-z_]+') || ' '||' RESTART 1;';
        END LOOP;
END $$ LANGUAGE plpgsql;
select restart_sequences();

INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (1, 0.03, 'Dragon Lore', 500, 1500.00, 'Legendary', 'Factory New');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (2, 0.10, 'Hyper Beast', 250, 300.00, 'Mythical', 'Minimal Wear');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (3, 0.35, 'Asiimov', 750, 200.00, 'Rare', 'Field-Tested');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (4, 0.40, 'Redline', 100, 100.00, 'Uncommon', 'Well-Worn');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (5, 0.06, 'Fire Serpent', 850, 800.00, 'Ancient', 'Factory New');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (6, 0.15, 'Crimson Web', 300, 120.00, 'Common', 'Minimal Wear');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (7, 0.07, 'Vulcan', 400, 250.00, 'Immortal', 'Factory New');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (8, 0.20, 'Golden Coil', 600, 350.00, 'Legendary', 'Field-Tested');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (9, 0.05, 'Oni Taiji', 950, 500.00, 'Mythical', 'Factory New');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (10, 0.30, 'Neon Revolution', 700, 150.00, 'Rare', 'Field-Tested');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (11, 0.45, 'Desolate Space', 200, 80.00, 'Uncommon', 'Well-Worn');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (12, 0.06, 'Mecha Industries', 800, 400.00, 'Ancient', 'Factory New');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (13, 0.14, 'Chatterbox', 350, 220.00, 'Common', 'Minimal Wear');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (14, 0.08, 'The Empress', 450, 300.00, 'Immortal', 'Factory New');
INSERT INTO skin (id_skin, float, name_skin, paint_seed, price, rarity, exterior) VALUES (15, 0.25, 'Bloodsport', 650, 280.00, 'Legendary', 'Field-Tested');
select setval('skin_seq', max(id_skin)) from skin;


INSERT INTO csgo_case (id_case, name_case, price) VALUES (1, 'Operation Bravo Case', 2.5);
INSERT INTO csgo_case (id_case, name_case, price) VALUES (2, 'Operation Phoenix Weapon Case', 1.0);
INSERT INTO csgo_case (id_case, name_case, price) VALUES (3, 'CS:GO Weapon Case', 1.5);
INSERT INTO csgo_case (id_case, name_case, price) VALUES (4, 'Chroma Case', 0.8);
INSERT INTO csgo_case (id_case, name_case, price) VALUES (5, 'Spectrum Case', 0.6);
select setval('csgo_case_seq', max(id_case)) from csgo_case;


INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (1, 1);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (2, 1);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (3, 1);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (4, 2);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (5, 2);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (6, 2);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (7, 3);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (8, 3);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (9, 3);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (10, 4);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (11, 4);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (12, 4);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (13, 5);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (14, 5);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (15, 5);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (1, 2);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (2, 3);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (3, 4);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (4, 5);
INSERT INTO skin_csgo_case (id_skin, id_case) VALUES (5, 1);


INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (1, 'AK-47', '', 'Rifle', 4);
INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (2, 'AWP', 'The Beast', 'Sniper Rifle', 1);
INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (3, 'M4A4', '', 'Rifle', 3);
INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (4, 'Desert Eagle', 'The Spider', 'Pistol', 6);
INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (5, 'P90', 'The Serpent', 'SMG', 5);
INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (6, 'Glock-18', '', 'Pistol', 7);
INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (7, 'USP-S', 'The Revolution', 'Pistol', 10);
INSERT INTO weapon (id_weapon, name_weapon, tag, type_weapon, id_skin) VALUES (8, 'P2000', 'The Demon', 'Pistol', 9);
select setval('weapon_seq', max(id_weapon)) from weapon;