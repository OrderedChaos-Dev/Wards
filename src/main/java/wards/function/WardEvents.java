package wards.function;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wards.Wards;
import wards.WardsRegistryManager;

public class WardEvents {
	
	@SubscribeEvent
	public void tickingExplosion(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		EffectInstance eff = entity.getActivePotionEffect(WardsRegistryManager.tickingExplosion);
		if(eff != null) {
			int amp = eff.getAmplifier();
			if(entity.getEntityWorld().getGameTime() % 20 == 0 && entity.getEntityWorld().isRemote) {
				for(int i = 0; i < (amp + 1) * 25; i++) {
					double x = entity.getPosXRandom(0.3F);
					double y = entity.getPosYRandom();
					double z = entity.getPosZRandom(0.3F);
					entity.getEntityWorld().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void frenzy(LivingDeathEvent event) {
		DamageSource ds = event.getSource();
		if(ds.getTrueSource() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)ds.getTrueSource();
			EffectInstance eff = player.getActivePotionEffect(WardsRegistryManager.slayer);
			if(eff != null) {
				if(!player.getEntityWorld().isRemote) {
					if(player.getEntityWorld().getRandom().nextBoolean()) {
						int amp = eff.getAmplifier();
						player.addPotionEffect(new EffectInstance(Effects.HEALTH_BOOST, 80, amp));
						player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 80, amp));
						player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 80, amp));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void editLootPools(LootTableLoadEvent event) {
		String name = event.getName().toString();
		if(name.equals("minecraft:chests/buried_treasure")
				|| name.equals("minecraft:chests/shipwreck_treasure")
				|| name.equals("minecraft:chests/shipwreck_supply")) {
			Wards.LOGGER.info("Adding ward cores to loot table: " + name);
			LootPool wardCorePool = new LootPool.Builder()
					.addEntry(TableLootEntry.builder(new ResourceLocation(Wards.MOD_ID, "chests/ward_core"))).build();
			
			LootTable table = event.getTable();
			table.addPool(wardCorePool);
		}
	}
}
