package wards.function;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.util.EnumParticleTypes;

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
	
	public static EnumParticleTypes[] getParticles(Enchantment enchant)
	{
		EnchantmentType type = getEnchantmentType(enchant);
		EnumParticleTypes[] particles = new EnumParticleTypes[] {EnumParticleTypes.CRIT, EnumParticleTypes.CRIT_MAGIC};
		
		if(type == EnchantmentType.GENERIC)
		{
			particles[0] = EnumParticleTypes.CRIT;
			particles[1] = EnumParticleTypes.CRIT_MAGIC;
		}
		if(type == EnchantmentType.FIRE)
		{
			particles[0] = EnumParticleTypes.FLAME;
			particles[1] = EnumParticleTypes.DRIP_LAVA;
		}
		if(type == EnchantmentType.WATER)
		{
			particles[0] = EnumParticleTypes.WATER_SPLASH;
			particles[1] = EnumParticleTypes.DRIP_WATER;
		}
		if(type == EnchantmentType.FROST)
		{
			particles[0] = EnumParticleTypes.SNOWBALL;
			particles[1] = EnumParticleTypes.SNOW_SHOVEL;
		}
		if(type == EnchantmentType.EXPLOSION)
		{
			particles[0] = EnumParticleTypes.SMOKE_NORMAL;
			particles[1] = EnumParticleTypes.CLOUD;
		}
		if(type == EnchantmentType.KNOCKUP)
		{
			particles[0] = EnumParticleTypes.CLOUD;
			particles[1] = EnumParticleTypes.SNOWBALL;
		}
		if(type == EnchantmentType.OOF)
		{
			particles[0] = EnumParticleTypes.DRAGON_BREATH;
			particles[1] = EnumParticleTypes.CRIT;
		}
		if(type == EnchantmentType.SMITE)
		{
			particles[0] = EnumParticleTypes.END_ROD;
			particles[1] = EnumParticleTypes.CRIT_MAGIC;
		}
		if(type == EnchantmentType.ARTHROPODS)
		{
			particles[0] = EnumParticleTypes.REDSTONE;
			particles[1] = EnumParticleTypes.CRIT_MAGIC;
		}
		if(type == EnchantmentType.SWEEPING)
		{
			particles[0] = EnumParticleTypes.SPELL_INSTANT;
			particles[1] = EnumParticleTypes.CRIT_MAGIC;
		}
		if(type == EnchantmentType.EXPERIENCE)
		{
			particles[0] = EnumParticleTypes.VILLAGER_HAPPY;
			particles[1] = EnumParticleTypes.ENCHANTMENT_TABLE;
		}
		if(type == EnchantmentType.CURSE)
		{
			particles[0] = EnumParticleTypes.SUSPENDED;
			particles[1] = EnumParticleTypes.SUSPENDED_DEPTH;
		}
		if(type == EnchantmentType.LUCK)
		{
			particles[0] = EnumParticleTypes.TOTEM;
			particles[1] = EnumParticleTypes.CRIT;
		}
		
		return particles;
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
