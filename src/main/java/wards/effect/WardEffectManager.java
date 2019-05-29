package wards.effect;

import java.util.ArrayList;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import wards.Wards;

public class WardEffectManager
{
	public static final ArrayList<Potion> EFFECTS = new ArrayList<Potion>();
	
	public static final String depth_strider_UUID = "792F4B83-88DD-4B61-BC60-E6799B92B0D4";
	
	public static void initEffects()
	{
		for(Enchantment enchant : ForgeRegistries.ENCHANTMENTS.getValuesCollection())
		{
			int count = 0;
			String name = enchant.getRegistryName().getResourcePath();
		
			Potion effect = new WardEffect(enchant).setPotionName("effect.ward_" + name);
			for(Potion potion : EFFECTS)
			{
				//in case of other mods adding enchantments that share names with existing potion effects
				if(potion.getName().equals(effect.getName()))
				{
					count++;
					effect.setPotionName("effect.ward_" + name + count);
				}
			}
			
			if(enchant == Enchantments.DEPTH_STRIDER)
			{
				effect.registerPotionAttributeModifier(EntityPlayer.SWIM_SPEED, depth_strider_UUID, 0.7, 2);
			}
			
			effect.setRegistryName(new ResourceLocation(Wards.MODID, "effect_" + name + (count > 0 ? count : "")));
			EFFECTS.add(effect);
			Wards.logger.debug("loading " + effect.getRegistryName());
		}
	}
}
