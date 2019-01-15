package wards.effect;

import java.util.ArrayList;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import wards.Wards;

public class WardEffectManager
{
	public static final ArrayList<Potion> EFFECTS = new ArrayList<Potion>();
	
	public static void initEffects()
	{
		for(Enchantment enchant : ForgeRegistries.ENCHANTMENTS.getValuesCollection())
		{
			String name = enchant.getName().replace("enchantment.", "");
			Potion effect = new WardEffect(enchant).setPotionName("effect." + name);
			effect.setRegistryName(new ResourceLocation(Wards.MODID, "effect_" + name));
			EFFECTS.add(effect);
			Wards.logger.debug("loading " + effect.getRegistryName());
		}
	}
}
