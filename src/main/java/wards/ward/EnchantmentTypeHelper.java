package wards.ward;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;

public class EnchantmentTypeHelper
{
	public static EnchantmentType getEnchantmentType(Enchantment enchant)
	{
		if(enchant == Enchantments.SHARPNESS || enchant == Enchantments.POWER || enchant == Enchantments.THORNS || enchant == Enchantments.EFFICIENCY)
		{
			return EnchantmentType.GENERIC;
		}
		if(enchant == Enchantments.PROTECTION || enchant == Enchantments.PROJECTILE_PROTECTION || enchant == Enchantments.UNBREAKING)
		{
			return EnchantmentType.FORTITUDE;
		}
		if(enchant == Enchantments.KNOCKBACK || enchant == Enchantments.PUNCH)
		{
			return EnchantmentType.OOF;
		}
		if(enchant == Enchantments.FLAME || enchant == Enchantments.FIRE_ASPECT || enchant == Enchantments.FIRE_PROTECTION)
		{
			return EnchantmentType.FIRE;
		}
		if(enchant == Enchantments.AQUA_AFFINITY || enchant == Enchantments.RESPIRATION || enchant == Enchantments.LURE || enchant == Enchantments.DEPTH_STRIDER)
		{
			return EnchantmentType.WATER;
		}
		if(enchant == Enchantments.FROST_WALKER)
		{
			return EnchantmentType.FROST;
		}
		if(enchant == Enchantments.BLAST_PROTECTION)
		{
			return EnchantmentType.EXPLOSION;
		}
		if(enchant == Enchantments.LUCK_OF_THE_SEA || enchant == Enchantments.SILK_TOUCH || enchant == Enchantments.FORTUNE || enchant == Enchantments.LOOTING)
		{
			return EnchantmentType.LUCK;
		}
		if(enchant == Enchantments.SMITE)
		{
			return EnchantmentType.SMITE;
		}
		if(enchant == Enchantments.BANE_OF_ARTHROPODS)
		{
			return EnchantmentType.ARTHROPODS;
		}
		if(enchant == Enchantments.SWEEPING)
		{
			return EnchantmentType.SWEEPING;
		}
		if(enchant == Enchantments.FEATHER_FALLING)
		{
			return EnchantmentType.KNOCKUP;
		}
		if(enchant == Enchantments.MENDING || enchant == Enchantments.INFINITY)
		{
			return EnchantmentType.EXPERIENCE;
		}
		if(enchant == Enchantments.BINDING_CURSE || enchant == Enchantments.VANISHING_CURSE)
		{
			return EnchantmentType.CURSE;
		}
		
		return EnchantmentType.GENERIC;
	}
	
	public enum EnchantmentType
	{
		GENERIC,
		FORTITUDE,
		FIRE,
		WATER,
		FROST,
		LUCK,
		EXPLOSION,
		SMITE,
		ARTHROPODS,
		SWEEPING,
		OOF,
		KNOCKUP,
		EXPERIENCE,
		CURSE;
	}
}
