package wards;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
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
	public static Item enchanted_paper;
	public static Item broken_ward_core, ward_core;
	
	public static Effect tickingExplosion, slayer;
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ward = new WardBlock(Block.Properties.of(Material.STONE).lightLevel((state) -> 10).strength(5.0F, 40.0F));
		ward.setRegistryName(new ResourceLocation(Wards.MOD_ID, "ward"));
		event.getRegistry().register(ward);
		
		BlockItem ward_item = new BlockItem(ward, new Item.Properties().tab(WardsItemGroup.INSTANCE));
		ward_item.setRegistryName(new ResourceLocation(Wards.MOD_ID, "ward"));
		ForgeRegistries.ITEMS.register(ward_item);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		enchanted_paper = new Item(new Item.Properties().tab(WardsItemGroup.INSTANCE).rarity(Rarity.UNCOMMON)) {
			@Override
			public boolean isFoil(ItemStack stack) {
				return true;
			}
		};
		enchanted_paper.setRegistryName(Wards.MOD_ID, "enchanted_paper");
		event.getRegistry().register(enchanted_paper);
		
		broken_ward_core = new Item(new Item.Properties().tab(WardsItemGroup.INSTANCE).rarity(Rarity.UNCOMMON));
		broken_ward_core.setRegistryName(Wards.MOD_ID, "broken_ward_core");
		event.getRegistry().register(broken_ward_core);
		
		ward_core = new Item(new Item.Properties().tab(WardsItemGroup.INSTANCE).rarity(Rarity.UNCOMMON));
		ward_core.setRegistryName(Wards.MOD_ID, "ward_core");
		event.getRegistry().register(ward_core);
	}
	
	@SubscribeEvent
	public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
		ward_te = TileEntityType.Builder.of(WardTileEntity::new, ward).build(null);
		ward_te.setRegistryName(new ResourceLocation(Wards.MOD_ID, "ward"));
		event.getRegistry().register(ward_te);
	}
	
	@SubscribeEvent
	public static void registerEffects(RegistryEvent.Register<Effect> event) {
		tickingExplosion = new TickingExplosionEffect(EffectType.HARMFUL, 0);
		tickingExplosion.setRegistryName(Wards.MOD_ID, "ticking_explosion");
		event.getRegistry().register(tickingExplosion);
		
		slayer = new TickingExplosionEffect(EffectType.BENEFICIAL, 0);
		slayer.setRegistryName(Wards.MOD_ID, "slayer");
		event.getRegistry().register(slayer);
	}
	
	public static class WardsItemGroup extends ItemGroup {
		
		public static final WardsItemGroup INSTANCE = new WardsItemGroup("wards");
		
		public WardsItemGroup(String label) {
			super(label);
		}

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ward);
		}
	}
}
