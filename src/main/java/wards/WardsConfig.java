package wards;

import java.nio.file.Path;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class WardsConfig {
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec COMMON_CONFIG;
	
	public static ForgeConfigSpec.ConfigValue<List<String>> acceptedItems;
	public static ForgeConfigSpec.ConfigValue<List<String>> powerSources;
	
	public static ForgeConfigSpec.ConfigValue<List<String>> combatEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> fortitudeEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> fireEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> waterEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> airEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> luckEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> destructionEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> slayerEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> hasteEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> knowledgeEnchantments;
	public static ForgeConfigSpec.ConfigValue<List<String>> curseEnchantments;
	
	static {		
		COMMON_BUILDER.comment("Wards Settings").push("Wards");
		COMMON_BUILDER.comment("Items").push("Items");

		acceptedItems = COMMON_BUILDER.comment("Accepted Items").define("acceptedItems",WardsConfigDefaults.ACCEPTED_ITEMS);
		powerSources = COMMON_BUILDER.comment("Power Sources").define("powerSources",WardsConfigDefaults.POWER_SOURCES);
		
		COMMON_BUILDER.pop();
		COMMON_BUILDER.comment("Enchantments").push("Enchantments");
		combatEnchantments = COMMON_BUILDER.comment("Combat Enchantments").define("combatEnchantments", WardsConfigDefaults.COMBAT_ENCHANTMENTS);
		fortitudeEnchantments = COMMON_BUILDER.comment("Fortitude Enchantments").define("fortitudeEnchantments", WardsConfigDefaults.FORTITUDE_ENCHANTMENTS);
		fireEnchantments = COMMON_BUILDER.comment("Fire Enchantments").define("fireEnchantments", WardsConfigDefaults.FIRE_ENCHANTMENTS);
		waterEnchantments = COMMON_BUILDER.comment("Water Enchantments").define("waterEnchantments", WardsConfigDefaults.WATER_ENCHANTMENTS);
		luckEnchantments = COMMON_BUILDER.comment("Luck Enchantments").define("luckEnchantments", WardsConfigDefaults.LUCK_ENCHANTMENTS);
		destructionEnchantments = COMMON_BUILDER.comment("Destruction Enchantments").define("destructionEnchantments", WardsConfigDefaults.DESTRUCTION_ENCHANTMENTS);
		slayerEnchantments = COMMON_BUILDER.comment("Slayer Enchantments").define("slayerEnchantments", WardsConfigDefaults.SLAYER_ENCHANTMENTS);
		hasteEnchantments = COMMON_BUILDER.comment("Haste Enchantments").define("hasteEnchantments", WardsConfigDefaults.HASTE_ENCHANTMENTS);
		knowledgeEnchantments = COMMON_BUILDER.comment("Knowledge Enchantments").define("knowledgeEnchantments", WardsConfigDefaults.KNOWLEDGE_ENCHANTMENTS);
		curseEnchantments = COMMON_BUILDER.comment("Curse Enchantments").define("curseEnchantments", WardsConfigDefaults.CURSE_ENCHANTMENTS);

		COMMON_BUILDER.pop(2);
		COMMON_CONFIG = COMMON_BUILDER.build();
	}
	
	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		
		configData.load();
		spec.setConfig(configData);
	}
}
