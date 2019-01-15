package wards;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import wards.effect.WardEffectManager;

public class WardRegistryEvents
{
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		Wards.ward.setRegistryName(new ResourceLocation(Wards.MODID, "ward"));
		event.getRegistry().register(Wards.ward);
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		ItemBlock item = new ItemBlock(Wards.ward);
		item.setRegistryName(new ResourceLocation(Wards.MODID, "ward"));
		event.getRegistry().register(item);
	}
	
	@SubscribeEvent
	public void registerPotions(RegistryEvent.Register<Potion> event)
	{
		IForgeRegistry<Potion> registry = event.getRegistry();
		for(Potion potion : WardEffectManager.EFFECTS)
		{
			registry.register(potion);
		}
	}
	
	@SubscribeEvent
	public void registerItemModels(ModelRegistryEvent event)
	{
		Item item = Item.getItemFromBlock(Wards.ward);
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
