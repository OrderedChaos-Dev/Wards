package wards.function;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wards.WardsRegistryManager;

public class WardEvents {
	
	@SubscribeEvent
	public void tickingExplosion(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		EffectInstance eff = entity.getActivePotionEffect(WardsRegistryManager.tickingExplosion);
		if(eff != null) {
			int amp = eff.getAmplifier();
			if(entity.getEntityWorld().getGameTime() % 20 == 0) {
				for(int i = 0; i < (amp + 1) * 25; i++) {
					double x = entity.getPosXRandom(0.3F);
					double y = entity.getPosYRandom();
					double z = entity.getPosZRandom(0.3F);
					entity.getEntityWorld().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
				}
			}
		}
	}
}
