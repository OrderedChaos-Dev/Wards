package wards.effect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;

public class WardEffect extends Potion
{
	private Enchantment enchant;
	
	public WardEffect(Enchantment enchant)
	{
		super(false, 8171462);
		this.setBeneficial();
		
		this.enchant = enchant;
	}
	
	public static WardEffect byEnchant(Enchantment enchant)
	{
		for(Potion effect : WardEffectManager.EFFECTS)
		{
			if(((WardEffect)effect).getEnchant() == enchant)
				return (WardEffect)effect;
		}
		
		return null;
	}
	
	public Enchantment getEnchant()
	{
		return this.enchant;
	}
}
