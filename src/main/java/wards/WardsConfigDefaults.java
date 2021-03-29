package wards;

import java.util.ArrayList;
import java.util.List;

public class WardsConfigDefaults {
	
	public static final int TARGET_CAP = 3;
	public static final int DAMAGE_COST = 35;
	public static final int BUFF_COST = 35;
	public static final int MAX_POWER = 100000;
	
	public static final List<String> ACCEPTED_ITEMS = new ArrayList<String>();
	public static final List<String> POWER_SOURCES = new ArrayList<String>();
	public static final List<String> COMBAT_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> FORTITUDE_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> FIRE_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> WATER_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> LUCK_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> DESTRUCTION_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> SLAYER_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> HASTE_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> KNOWLEDGE_ENCHANTMENTS = new ArrayList<String>();
	public static final List<String> CURSE_ENCHANTMENTS = new ArrayList<String>();
	
	static {
		addTo(ACCEPTED_ITEMS, "minecraft:enchanted_book", "minecraft:book");
		addTo(POWER_SOURCES, "minecraft:lapis_lazuli-10000", "wards:enchanted_paper-15000");
		addTo(COMBAT_ENCHANTMENTS, "minecraft:sharpness", "minecraft:knockback", "minecraft:power", "minecraft:punch", "minecraft:riptide");
		addTo(FORTITUDE_ENCHANTMENTS, "minecraft:protection", "minecraft:projectile_protection", "minecraft:thorns", "minecraft:unbreaking", "minecraft:feather_falling");
		addTo(FIRE_ENCHANTMENTS, "minecraft:fire_protection", "minecraft:fire_aspect", "minecraft:flame");
		addTo(WATER_ENCHANTMENTS, "minecraft:respiration", "minecraft:aqua_affinity", "minecraft:depth_strider", "minecraft:frost_walker");
		addTo(LUCK_ENCHANTMENTS, "minecraft:looting", "minecraft:silk_touch", "minecraft:fortune", "minecraft:luck_of_the_sea", "minecraft:lure");
		addTo(DESTRUCTION_ENCHANTMENTS, "minecraft:blast_protection", "minecraft:multishot", "minecraft:sweeping", "minecraft:channeling");
		addTo(SLAYER_ENCHANTMENTS, "minecraft:smite", "minecraft:bane_of_arthropods", "minecraft:impaling", "minecraft:piercing");
		addTo(HASTE_ENCHANTMENTS, "minecraft:efficiency", "minecraft:quick_charge");
		addTo(KNOWLEDGE_ENCHANTMENTS, "minecraft:infinity", "minecraft:mending", "minecraft:loyalty");
		addTo(CURSE_ENCHANTMENTS, "minecraft:binding_curse", "minecraft:vanishing_curse");
		
		/* MOD INTEGRATION */
		//QUARK
		addTo(ACCEPTED_ITEMS, "quark:ancient_tome");
		
		//CHARM
		addTo(FORTITUDE_ENCHANTMENTS, "charm:curse_break");
		addTo(CURSE_ENCHANTMENTS, "charm:leeching_curse");
		addTo(KNOWLEDGE_ENCHANTMENTS, "charm:homing", "charm:magnetic", "charm:salvage");
		
		//MA ENCHANTS
		addTo(COMBAT_ENCHANTMENTS, "ma-enchants:true_shot", "ma-enchants:floating", "ma-enchants:paralysis", "ma-enchants:combo");
		addTo(FIRE_ENCHANTMENTS, "ma-enchants:blazing_walker");
		addTo(DESTRUCTION_ENCHANTMENTS, "ma-enchants:detonation");
		addTo(SLAYER_ENCHANTMENTS, "ma-enchants:butchering", "ma-enchants:lifesteal");
		addTo(HASTE_ENCHANTMENTS, "ma-enchants:reinforced_tip", "ma-enchants:momentum", "ma-enchants:quick_draw",
				"ma-enchants:faster_attack", "ma-enchants:ice_aspect");
		addTo(KNOWLEDGE_ENCHANTMENTS, "ma-enchants:timeless", "ma-enchants:stone_mending", "ma-enchants:lumberjack",
				"ma-enchants:wisdom", "ma-enchants:step_assist", "ma-enchants:night_vision", "ma-enchants:timeless", "ma-enchants:multi_jump");
		addTo(CURSE_ENCHANTMENTS, "ma-enchants:curse_breaking", "ma-enchants:curse_butterfingers", "ma-enchants:curse_aquaphobia", "ma-enchants:curse_death");
		
		//ENSORCELLATION
		addTo(COMBAT_ENCHANTMENTS, "ensorcellation:cavalier", "ensorcellation:magic_edge", "ensorcellation:instigating", "ensorcellation:vorpal", "ensorcellation:trueshot", "ensorcellation:volley", "ensorcellation:bulwark", "ensorcellation:air_affinity");
		addTo(FORTITUDE_ENCHANTMENTS, "ensorcellation:magic_protection", "ensorcellation:vitality");
		addTo(FIRE_ENCHANTMENTS, "ensorcellation:fire_rebuke");
		addTo(WATER_ENCHANTMENTS, "ensorcellation:frost_rebuke", "ensorcellation:frost_aspect");
		addTo(LUCK_ENCHANTMENTS, "ensorcellation:hunter", "ensorcellation:angler");
		addTo(SLAYER_ENCHANTMENTS, "ensorcellation:damage_ender", "ensorcellation:damage_illager", "ensorcellation:damage_villager", "ensorcellation:leech");
		addTo(HASTE_ENCHANTMENTS, "ensorcellation:excavating", "ensorcellation:fire_rebuke", "ensorcellation:quick_draw", "ensorcellation:phalanx", "ensorcellation:tilling", "ensorcellation:weeding", "ensorcellation:furrowing");
		addTo(KNOWLEDGE_ENCHANTMENTS, "ensorcellation:exp_boost", "ensorcellation:reach");
		addTo(CURSE_ENCHANTMENTS, "ensorcellation:curse_fool", "ensorcellation:curse_mercy");
	}
	
	@SafeVarargs
	public static <T> void addTo(List<T> list, T... objects) {
		for(T obj : objects) {
			list.add(obj);
		}
	}
}
