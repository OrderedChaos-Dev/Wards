package wards;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Wards.MODID, name = Wards.NAME, version = Wards.VERSION)
public class Wards
{
    public static final String MODID = "wards";
    public static final String NAME = "Wards";
    public static final String VERSION = "1.0";

    public static Logger logger;

    public static Block ward;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        
        logger.info("pre init!!!!!!!!!!!!!!!!!!!!!!");
        ward = new BlockWard();
        GameRegistry.registerTileEntity(TileEntityWard.class, new ResourceLocation(MODID, "ward"));
        WardEffectManager.initEffects();
        
    	MinecraftForge.EVENT_BUS.register(new WardRegistryEvents());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }
}
