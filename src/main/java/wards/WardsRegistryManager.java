package wards;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import wards.block.WardBlock;
import wards.block.WardTileEntity;
import wards.function.TickingExplosionEffect;

@EventBusSubscriber(modid = Wards.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class WardsRegistryManager {

	public static Block ward;
	public static TileEntityType<WardTileEntity> ward_te;
	
	public static Effect tickingExplosion;
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ward = new WardBlock(Block.Properties.create(Material.ROCK).lightValue(10).hardnessAndResistance(5.0F, 12.0F));
		ward.setRegistryName(new ResourceLocation(Wards.MOD_ID, "ward"));
		event.getRegistry().register(ward);
		
		BlockItem item = new BlockItem(ward, new Item.Properties().group(ItemGroup.DECORATIONS));
		item.setRegistryName(new ResourceLocation(Wards.MOD_ID, "ward"));
		ForgeRegistries.ITEMS.register(item);
	}
	
	@SubscribeEvent
	public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
		ward_te = TileEntityType.Builder.create(WardTileEntity::new, ward).build(null);
		ward_te.setRegistryName(new ResourceLocation(Wards.MOD_ID, "ward"));
		event.getRegistry().register(ward_te);
	}
	
	@SubscribeEvent
	public static void registerEffects(RegistryEvent.Register<Effect> event) {
		tickingExplosion = new TickingExplosionEffect(EffectType.HARMFUL, 0);
		tickingExplosion.setRegistryName(Wards.MOD_ID, "ticking_explosion");
		event.getRegistry().register(tickingExplosion);
	}
}
