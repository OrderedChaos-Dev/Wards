package wards;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import wards.client.WardTileEntityRenderer;
import wards.function.WardEvents;

@Mod(Wards.MOD_ID)
public class Wards {
	
	public static final String MOD_ID = "wards";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	public Wards() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WardsConfig.COMMON_CONFIG);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		WardsConfig.loadConfig(WardsConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("wards-common.toml"));
	}
	
	private void commonSetup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new WardEvents());
	}
	
	private void clientSetup(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(WardsRegistryManager.ward, RenderType.getCutoutMipped());
		ClientRegistry.bindTileEntityRenderer(WardsRegistryManager.ward_te, WardTileEntityRenderer::new);
	}
	
	private void loadComplete(FMLLoadCompleteEvent event) {
		
	}
}
