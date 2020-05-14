package wards;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class WardsConfig {
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec COMMON_CONFIG;
	
	public static ForgeConfigSpec.ConfigValue<List<String>> acceptedItems;
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
		acceptedItems = COMMON_BUILDER.comment("Accepted Items").define("acceptedItems",
				Arrays.asList("minecraft:enchanted_book"));
		
		COMMON_BUILDER.pop();
		COMMON_BUILDER.comment("Enchantments").push("Enchantments");
		combatEnchantments = COMMON_BUILDER.comment("Combat Enchantments").define("combatEnchantments", 
				Arrays.asList("minecraft:sharpness", "minecraft:knockback", "minecraft:power", "minecraft:punch", "minecraft:riptide"));
		
		fortitudeEnchantments = COMMON_BUILDER.comment("Fortitude Enchantments").define("fortitudeEnchantments",
				Arrays.asList("minecraft:protection", "minecraft:projectile_protection", "minecraft:thorns", "minecraft:unbreaking", "minecraft:feather_falling"));
		
		fireEnchantments = COMMON_BUILDER.comment("Fire Enchantments").define("fireEnchantments",
				Arrays.asList("minecraft:fire_protection", "minecraft:fire_aspect", "minecraft:flame"));
		
		waterEnchantments = COMMON_BUILDER.comment("Water Enchantments").define("waterEnchantments",
				Arrays.asList("minecraft:respiration", "minecraft:aqua_affinity", "minecraft:depth_strider", "minecraft:frost_walker"));
		
		luckEnchantments = COMMON_BUILDER.comment("Luck Enchantments").define("luckEnchantments",
				Arrays.asList("minecraft:looting", "minecraft:silk_touch", "minecraft:fortune", "minecraft:luck_of_the_sea", "minecraft:lure"));
		
		destructionEnchantments = COMMON_BUILDER.comment("Destruction Enchantments").define("destructionEnchantments",
				Arrays.asList("minecraft:blast_protection", "minecraft:multishot", "minecraft:sweeping", "minecraft:channeling"));
		
		slayerEnchantments = COMMON_BUILDER.comment("Slayer Enchantments").define("slayerEnchantments",
				Arrays.asList("minecraft:smite", "minecraft:bane_of_arthropods", "minecraft:impaling", "minecraft:piercing"));
		
		hasteEnchantments = COMMON_BUILDER.comment("Haste Enchantments").define("hasteEnchantments",
				Arrays.asList("minecraft:efficiency", "minecraft:quick_charge"));
		
		knowledgeEnchantments = COMMON_BUILDER.comment("Knowledge Enchantments").define("knowledgeEnchantments",
				Arrays.asList("minecraft:infinity", "minecraft:mending", "minecraft:loyalty"));
		
		curseEnchantments = COMMON_BUILDER.comment("Curse Enchantments").define("curseEnchantments",
				Arrays.asList("minecraft:binding_curse", "minecraft:vanishing_curse"));
		
		COMMON_BUILDER.pop(2);
		COMMON_CONFIG = COMMON_BUILDER.build();
	}
	
	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		
		configData.load();
		spec.setConfig(configData);
	}
}
