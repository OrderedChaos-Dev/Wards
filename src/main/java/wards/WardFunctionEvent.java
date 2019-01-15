package wards;

import java.util.Random;

import com.google.common.collect.Iterables;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import wards.effect.WardEffect;

public class WardFunctionEvent
{
	@SubscribeEvent
	public void onPlayerHurt(LivingHurtEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			float damage = event.getAmount();
			
			for(PotionEffect effect : entity.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();
				int level = effect.getAmplifier();
				DamageSource source = event.getSource();
				
				if(potion == WardEffect.byEnchant(Enchantments.PROTECTION))
				{
					damage -= (double)MathHelper.floor(damage * (double)((float)level * 0.10F));
				}
				if(potion == WardEffect.byEnchant(Enchantments.BLAST_PROTECTION) && source.isExplosion())
				{
					damage -= (double)MathHelper.floor(damage * (double)((float)level * 0.10F));
				}
				if(potion == WardEffect.byEnchant(Enchantments.FIRE_PROTECTION) && source.isFireDamage())
				{
					damage -= (double)MathHelper.floor(damage * (double)((float)level * 0.10F));
				}
				if(potion == WardEffect.byEnchant(Enchantments.PROJECTILE_PROTECTION) && source.isProjectile())
				{
					damage -= (double)MathHelper.floor(damage * (double)((float)level * 0.10F));
				}
				if(potion == WardEffect.byEnchant(Enchantments.FEATHER_FALLING) && source == DamageSource.FALL)
				{
					damage -= (double)MathHelper.floor(damage * (double)((float)level * 0.10F));
				}
				if(potion == WardEffect.byEnchant(Enchantments.THORNS) && event.getSource().getTrueSource() != null)
				{
					if(player.getEntityWorld().rand.nextInt(6) < 1 + level)
						event.getSource().getTrueSource().attackEntityFrom(DamageSource.MAGIC, 0.5F * level);
				}
				if(potion == WardEffect.byEnchant(Enchantments.UNBREAKING))
				{
					for(ItemStack item : player.getArmorInventoryList())
					{
						if(item.getItem() instanceof ItemArmor)
						{
							if(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, item) < level)
							{
								if(!item.isEmpty() && item.isItemDamaged())
								{
									if(EnchantmentDurability.negateDamage(item, level, player.getRNG()))
									{
										if(player.getRNG().nextBoolean())
											item.setItemDamage(item.getItemDamage() - 1);
									}
								}
							}
						}
					}

				}
			}
			
			event.setAmount(damage);
		}
	}
	
	@SubscribeEvent
	public void onPlayerAttack(AttackEntityEvent event)
	{
		if(event.getTarget() instanceof EntityLivingBase)
		{
			EntityLivingBase target = (EntityLivingBase)event.getTarget();
			EntityPlayer player = event.getEntityPlayer();
			
			for(PotionEffect effect : player.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();
				int level = effect.getAmplifier();
				EnumCreatureAttribute creatureAttr = target.getCreatureAttribute();
				
				if(potion == WardEffect.byEnchant(Enchantments.SHARPNESS))
				{
					target.attackEntityFrom(DamageSource.MAGIC, (float) (1.0 * level));
				}
				if(potion == WardEffect.byEnchant(Enchantments.BANE_OF_ARTHROPODS) && creatureAttr == EnumCreatureAttribute.ARTHROPOD)
				{
					target.attackEntityFrom(DamageSource.MAGIC, (float) (1.0 * level));
				}
				if(potion == WardEffect.byEnchant(Enchantments.SMITE) && creatureAttr == EnumCreatureAttribute.UNDEAD)
				{
					target.attackEntityFrom(DamageSource.MAGIC, (float) (1.0 * level));
				}
				if(potion == WardEffect.byEnchant(Enchantments.KNOCKBACK))
				{
					double xRatio = (double)MathHelper.sin(player.rotationYaw * 0.017453292F);
					double zRatio = (double)-MathHelper.cos(player.rotationYaw * 0.017453292F);
					target.knockBack(player, 0.5F * level, xRatio, zRatio);
				}
				if(potion == WardEffect.byEnchant(Enchantments.FIRE_ASPECT))
				{
					target.setFire(level * 3);
				}
				if(potion == WardEffect.byEnchant(Enchantments.SWEEPING))
				{
					if(player.getEntityWorld().rand.nextInt(10) < 1 + level)
					{
						World world = player.getEntityWorld();
                        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D)))
                        {
                            if (entity != player && entity != target && !player.isOnSameTeam(entity) && player.getDistanceSq(entity) < 9.0D)
                            {
            					double xRatio = (double)MathHelper.sin(player.rotationYaw * 0.017453292F);
            					double zRatio = (double)-MathHelper.cos(player.rotationYaw * 0.017453292F);
                                entity.knockBack(player, 0.4F, xRatio, zRatio);
                                entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F + (0.5F *level));
                            }
                        }
                        double x = player.posX;
                        double y = player.posY;
                        double z = player.posZ;
                        world.playSound((EntityPlayer)null, x, y, z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                        player.spawnSweepParticles();
					}
				}
				if(potion == WardEffect.byEnchant(Enchantments.UNBREAKING))
				{
					Random rand = player.getRNG();
					ItemStack weapon = player.getHeldItemMainhand();
					
					if(!weapon.isEmpty() && weapon.isItemDamaged())
					{
						if(EnchantmentDurability.negateDamage(weapon, level, rand))
						{
							if(rand.nextBoolean())
								weapon.setItemDamage(weapon.getItemDamage() - 1);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBreakingBlock(PlayerEvent.BreakSpeed event)
	{
		float speed = event.getOriginalSpeed();
		EntityPlayer player = event.getEntityPlayer();
		for(PotionEffect effect : player.getActivePotionEffects())
		{
			Potion potion = effect.getPotion();
			int level = effect.getAmplifier();
			
			if(potion == WardEffect.byEnchant(Enchantments.AQUA_AFFINITY) && player.isInsideOfMaterial(Material.WATER))
			{
				if(!EnchantmentHelper.getAquaAffinityModifier(player))
				{
					speed *= 3.0;
				}
			}
			if(potion == WardEffect.byEnchant(Enchantments.EFFICIENCY))
			{
				speed += level * 1.0;
			}
		}
		
		event.setNewSpeed(speed);
	}
	
	@SubscribeEvent
	public void onCastFishingRod(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityFishHook)
		{
			EntityFishHook hook = (EntityFishHook)event.getEntity();
			if(hook.getAngler() != null)
			{
				EntityPlayer player = hook.getAngler();
				ItemStack rod = player.getHeldItemMainhand();
				
				if(rod.getItem() instanceof ItemFishingRod)
				{
					for(PotionEffect effect : player.getActivePotionEffects())
					{
						Potion potion = effect.getPotion();
						int level = effect.getAmplifier();
						
						if(potion == WardEffect.byEnchant(Enchantments.LUCK_OF_THE_SEA))
						{
							if(EnchantmentHelper.getFishingLuckBonus(rod) <= 0)
							{
								hook.setLuck(level);
							}
						}
						if(potion == WardEffect.byEnchant(Enchantments.LURE))
						{
							if(EnchantmentHelper.getFishingSpeedBonus(rod) <= 0)
							{
								hook.setLureSpeed(level);
							}
						}
					}
				}
				
			}
		}
	}
	
	@SubscribeEvent
	public void onFishItem(ItemFishedEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		for(PotionEffect effect : player.getActivePotionEffects())
		{
			Potion potion = effect.getPotion();
			int level = effect.getAmplifier();
			
			if(potion == WardEffect.byEnchant(Enchantments.UNBREAKING))
			{
				Random rand = player.getRNG();
				if(EnchantmentDurability.negateDamage(player.getHeldItemMainhand(), level, rand))
				{
					event.damageRodBy(0);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPickupXP(PlayerPickupXpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		for(PotionEffect effect : player.getActivePotionEffects())
		{
			Potion potion = effect.getPotion();
			Random rand = player.getEntityWorld().rand;
			if(potion == WardEffect.byEnchant(Enchantments.MENDING) && rand.nextBoolean())
			{
				ItemStack[] stack = Iterables.toArray(player.getEquipmentAndArmor(), ItemStack.class);
				int slot = rand.nextInt(stack.length);
				
				int count = 0;
				
				while(!stack[slot].isItemDamaged())
				{
					slot = rand.nextInt(stack.length);
					if(count == 10)
						break;
					
					count++;
				}

                if (!stack[slot].isEmpty() && stack[slot].isItemDamaged())
                {
                	EntityXPOrb orb = event.getOrb();
                    int i = Math.min(orb.getXpValue() * 2, stack[slot].getItemDamage());
                    orb.xpValue -= i / 2;
                    stack[slot].setItemDamage(stack[slot].getItemDamage() - i);
                }

			}
		}
	}
	
	@SubscribeEvent
	public void onShootArrow(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityArrow)
		{
			EntityArrow arrow = (EntityArrow)event.getEntity();
			if(arrow.shootingEntity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
				for(PotionEffect effect : player.getActivePotionEffects())
				{
					Potion potion = effect.getPotion();
					int level = effect.getAmplifier();
					if(potion == WardEffect.byEnchant(Enchantments.POWER))
					{
						if(EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, player.getHeldItemMainhand()) < level)
						{
							double power = (0.5 * level);
							arrow.setDamage(arrow.getDamage() + power);
						}
					}
					if(potion == WardEffect.byEnchant(Enchantments.PUNCH))
					{
						if(EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, player.getHeldItemMainhand()) < level)
						{
							arrow.setKnockbackStrength(level);
						}
					}
					if(potion == WardEffect.byEnchant(Enchantments.FLAME))
					{
						arrow.setFire(100);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onShootArrow(ArrowLooseEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		for(PotionEffect effect : player.getActivePotionEffects())
		{
			Potion potion = effect.getPotion();
			int level = effect.getAmplifier();
			
			if(potion == WardEffect.byEnchant(Enchantments.UNBREAKING))
			{
				Random rand = player.getRNG();
				ItemStack bow = event.getBow();
				if(EnchantmentDurability.negateDamage(bow, level, rand))
				{
					if(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, bow) < level)
					{
						if(rand.nextBoolean() && bow.isItemDamaged())
						{
							bow.setItemDamage(bow.getItemDamage() - 1);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
			Random rand = player.getEntityWorld().rand;
			
			for(PotionEffect effect : player.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();
				int level = effect.getAmplifier();
				if(potion == WardEffect.byEnchant(Enchantments.RESPIRATION))
				{
					if(player.getAir() < 300 && rand.nextInt(3) == 0)
					{
						if(rand.nextInt(level + 1) > 0)
						{
							player.setAir(player.getAir() + 1);
						}
					}
				}
				if(potion == WardEffect.byEnchant(Enchantments.DEPTH_STRIDER) && player.isInWater())
				{
					if(EnchantmentHelper.getDepthStriderModifier(player) < level)
					{
						if(level > 3)
							level = 3;
						
						float f1 = 0.54600006F * level / 3.0F;
						
						player.motionX *= f1;
						player.motionZ *= f1;
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerBreakBlock(BlockEvent.HarvestDropsEvent event)
	{
		if(event.getHarvester() != null)
		{
			EntityPlayer player = event.getHarvester();
			IBlockState state = event.getState();
			Block block = state.getBlock();
			
			for(PotionEffect effect : player.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();
				int level = effect.getAmplifier();
				
				if(potion == WardEffect.byEnchant(Enchantments.SILK_TOUCH))
				{
					if(EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) <= 0)
					{
						if(block.canSilkHarvest(event.getWorld(), event.getPos(), state, player))
						{
							ItemStack item = new ItemStack(Item.getItemFromBlock(block), 1, block.getMetaFromState(state));
							Block.spawnAsEntity(event.getWorld(), event.getPos(), item);
							event.getDrops().clear();
						}
					}
				}
				if(potion == WardEffect.byEnchant(Enchantments.FORTUNE))
				{
					if(event.getFortuneLevel() < level)
					{
						block.dropBlockAsItem(event.getWorld(), event.getPos(), state, level);
						event.getDrops().clear();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerBreakBlock(BlockEvent.BreakEvent event)
	{
		if(event.getPlayer() != null)
		{
			EntityPlayer player = event.getPlayer();
			IBlockState state = event.getState();
			Block block = state.getBlock();
			
			for(PotionEffect effect : player.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();
				int level = effect.getAmplifier();
				
				if(potion == WardEffect.byEnchant(Enchantments.SILK_TOUCH))
				{
					if(EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) <= 0)
					{
						event.setExpToDrop(0);
					}
				}
				if(potion == WardEffect.byEnchant(Enchantments.FORTUNE))
				{
					if(EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()) < level)
					{
						int xp = block.getExpDrop(state, event.getWorld(), event.getPos(), level);
						event.setExpToDrop(xp);
					}
				}
				if(potion == WardEffect.byEnchant(Enchantments.UNBREAKING))
				{
					ItemStack item = player.getHeldItemMainhand();
					if(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, item) < level)
					{
						if(!item.isEmpty() && item.isItemDamaged())
						{
							Random rand = player.getRNG();
							if(EnchantmentDurability.negateDamage(item, level, rand))
							{
								if(rand.nextBoolean() && item.isItemDamaged())
									item.setItemDamage(item.getItemDamage() - 1);
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerKillMob(LootingLevelEvent event)
	{
		DamageSource source = event.getDamageSource();
		if(source.getTrueSource() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)source.getTrueSource();
			
			for(PotionEffect effect : player.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();
				int level = effect.getAmplifier();
				
				if(potion == WardEffect.byEnchant(Enchantments.LOOTING) && event.getLootingLevel() < level)
				{
					event.setLootingLevel(level);
				}
			}
		}
	}
}
