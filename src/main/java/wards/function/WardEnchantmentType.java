package wards.function;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion.Mode;
import wards.WardsConfig;
import wards.WardsRegistryManager;
import wards.block.WardTileEntity;

public class WardEnchantmentType {
	
	private IParticleData[] particles;
	private String name;
	private int interval;
	
	public WardEnchantmentType(String name, int interval, IParticleData... particles) {
		this.name = name;
		this.particles = particles;
		this.interval = interval;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getInterval() {
		return this.interval;
	}
	
	public IParticleData[] getParticles() {
		return this.particles;
	}
	
	public void expelMagic(WardTileEntity ward, LivingEntity entity, int level) {
		if(!entity.getEntityWorld().isRemote) {
			if(this == COMBAT) {
				entity.attackEntityFrom(DamageSource.MAGIC, 1.0F + (0.5F * (level - 1)));
			} else if(this == FORTITUDE) {
				entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 25 * level, Math.min(level, 3)));
			} else if(this == FIRE) {
				entity.setFire(level * 2);
			} else if(this == WATER) {
				entity.attackEntityFrom(DamageSource.MAGIC, 0.5F + (0.25F * (level - 1)));
				if(entity.isInWaterRainOrBubbleColumn()) {
					entity.attackEntityFrom(DamageSource.MAGIC, 1.0F + (0.5F * (level - 1)));
				}
			} else if(this == LUCK) {
				entity.attackEntityFrom(DamageSource.MAGIC, 0.5F + (0.25F * (level - 1)));
				if(entity.getEntityWorld().getRandom().nextInt(5) == 0) {
					int exp = entity.getEntityWorld().getRandom().nextInt(4) + 1;
					ExperienceOrbEntity orb = new ExperienceOrbEntity(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), exp);
					entity.getEntityWorld().addEntity(orb);
				}
			} else if(this == DESTRUCTION) {
				EffectInstance eff = entity.getActivePotionEffect(WardsRegistryManager.tickingExplosion);
				if(eff != null) {
					int amp = eff.getAmplifier();
					if(amp == 1) {
						entity.getEntityWorld().createExplosion(null, DamageSource.MAGIC, null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), 0.5F + (0.4F * level), false, Mode.NONE);
						entity.attackEntityFrom(DamageSource.MAGIC, 1.0F + (0.75F * (level - 1)));
						entity.removePotionEffect(WardsRegistryManager.tickingExplosion);
					} else {
						entity.addPotionEffect(new EffectInstance(WardsRegistryManager.tickingExplosion, 200, amp + 1));		
					}
				} else {
					entity.addPotionEffect(new EffectInstance(WardsRegistryManager.tickingExplosion, 200, 0));
				}
				entity.attackEntityFrom(DamageSource.MAGIC, 0.5F + (0.5F * (level - 1)));
			} else if(this == SLAYER) {
				entity.attackEntityFrom(DamageSource.MAGIC, 0.5F + (0.5F * (level - 1)));
				if(entity.getCreatureAttribute() == CreatureAttribute.ARTHROPOD || entity.getCreatureAttribute() == CreatureAttribute.UNDEAD) {
					entity.attackEntityFrom(DamageSource.MAGIC, 1.0F + (0.5F * (level - 1)));
				}
			} else if(this == HASTE) {
				entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 25 * level, Math.min(level, 3)));
			} else if(this == KNOWLEDGE) {
				entity.addPotionEffect(new EffectInstance(Effects.GLOWING, 100, Math.min(level, 3)));
			} else if(this == CURSE) {
				entity.addPotionEffect(new EffectInstance(Effects.WITHER, 60, Math.min(level, 3)));
			}
		}
	}
	
	public void empowerPlayer(WardTileEntity ward, PlayerEntity entity, int level) {
		if(!entity.getEntityWorld().isRemote) {
			if(this == COMBAT) {
				entity.addPotionEffect(new EffectInstance(Effects.STRENGTH, this.getInterval(), Math.min(level, 3)));
			} else if(this == FORTITUDE) {
				entity.addPotionEffect(new EffectInstance(Effects.RESISTANCE, this.getInterval(), Math.min(level, 3)));
			} else if(this == FIRE) {
				entity.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, this.getInterval(), Math.min(level, 3)));
			} else if(this == WATER) {
				entity.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, this.getInterval(), Math.min(level, 3)));
			} else if(this == LUCK) {
				entity.addPotionEffect(new EffectInstance(Effects.LUCK, this.getInterval(), Math.min(level, 3)));
			} else if(this == DESTRUCTION) {
				entity.addPotionEffect(new EffectInstance(Effects.ABSORPTION, this.getInterval(), Math.min(level, 3)));
			} else if(this == SLAYER) {
				entity.addPotionEffect(new EffectInstance(WardsRegistryManager.slayer, this.getInterval(), Math.min(level, 3)));
			} else if(this == HASTE) {
				entity.addPotionEffect(new EffectInstance(Effects.SPEED, this.getInterval(), Math.min(level, 3)));
			} else if(this == KNOWLEDGE) {
				entity.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, this.getInterval(), Math.min(level, 3)));
				entity.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, this.getInterval(), Math.min(level, 3)));
			} else if(this == CURSE) {
				entity.addPotionEffect(new EffectInstance(Effects.POISON, this.getInterval(), Math.min(level, 3)));
			}
		}
	}
	
	public static WardEnchantmentType fromEnchant(Enchantment enchant) {
		String registryStr = enchant.getRegistryName().toString();
		if(WardsConfig.combatEnchantments.get().contains(registryStr)) {
			return COMBAT;
		} else if(WardsConfig.fortitudeEnchantments.get().contains(registryStr)) {
			return FORTITUDE;
		} else if(WardsConfig.fireEnchantments.get().contains(registryStr)) {
			return FIRE;
		} else if(WardsConfig.waterEnchantments.get().contains(registryStr)) {
			return WATER;
		} else if(WardsConfig.luckEnchantments.get().contains(registryStr)) {
			return LUCK;
		} else if(WardsConfig.destructionEnchantments.get().contains(registryStr)) {
			return DESTRUCTION;
		} else if(WardsConfig.slayerEnchantments.get().contains(registryStr)) {
			return SLAYER;
		} else if(WardsConfig.hasteEnchantments.get().contains(registryStr)) {
			return HASTE;
		} else if(WardsConfig.knowledgeEnchantments.get().contains(registryStr)) {
			return KNOWLEDGE;
		} else if(WardsConfig.curseEnchantments.get().contains(registryStr)) {
			return CURSE;
		}
		return COMBAT;
	}
	
	public static final WardEnchantmentType COMBAT = new WardEnchantmentType("Combat", 80, ParticleTypes.CRIT, ParticleTypes.INSTANT_EFFECT);
	public static final WardEnchantmentType FORTITUDE = new WardEnchantmentType("Fortitude", 80, ParticleTypes.ENCHANT, ParticleTypes.INSTANT_EFFECT);
	public static final WardEnchantmentType FIRE = new WardEnchantmentType("Fire", 80, ParticleTypes.FLAME, ParticleTypes.DRIPPING_LAVA, ParticleTypes.FALLING_LAVA);
	public static final WardEnchantmentType WATER = new WardEnchantmentType("Water", 80, ParticleTypes.DRIPPING_WATER, ParticleTypes.FALLING_WATER, ParticleTypes.SPLASH);
	public static final WardEnchantmentType LUCK = new WardEnchantmentType("Luck", 80, ParticleTypes.HAPPY_VILLAGER, ParticleTypes.ENCHANT);
	public static final WardEnchantmentType DESTRUCTION = new WardEnchantmentType("Destruction", 140, ParticleTypes.ANGRY_VILLAGER, ParticleTypes.CLOUD);
	public static final WardEnchantmentType SLAYER = new WardEnchantmentType("Slayer", 120, ParticleTypes.CRIT, ParticleTypes.ENCHANTED_HIT);
	public static final WardEnchantmentType HASTE = new WardEnchantmentType("Haste", 60, ParticleTypes.EFFECT, ParticleTypes.POOF);
	public static final WardEnchantmentType KNOWLEDGE = new WardEnchantmentType("Knowledge", 100, ParticleTypes.ENCHANT, ParticleTypes.ENCHANT);
	public static final WardEnchantmentType CURSE = new WardEnchantmentType("Curse", 100, ParticleTypes.MYCELIUM, ParticleTypes.UNDERWATER, ParticleTypes.PORTAL);
}
