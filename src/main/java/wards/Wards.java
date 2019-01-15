package wards;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import wards.effect.WardEffectManager;
import wards.ward.BlockWard;
import wards.ward.TileEntityWard;
import wards.ward.WardFunctionEvent;

@Mod(modid = Wards.MODID, name = Wards.NAME, version = Wards.VERSION)
public class Wards
{
    public static final String MODID = "wards";
    public static final String NAME = "Wards";
    public static final String VERSION = "1.0";
    
    @SidedProxy(clientSide = "wards.ClientProxy", serverSide = "wards.ServerProxy")
    public static ICommonProxy proxy;

    public static Logger logger;

    public static Block ward;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        
        logger.info("pre init!!!!!!!!!!!!!!!!!!!!!!");
        ward = new BlockWard().setUnlocalizedName("ward").setRegistryName(new ResourceLocation(MODID, "ward"));
        ward.setCreativeTab(CreativeTabs.MISC);
        GameRegistry.registerTileEntity(TileEntityWard.class, new ResourceLocation(MODID, "ward"));
        WardEffectManager.initEffects();
        
    	MinecraftForge.EVENT_BUS.register(new WardRegistryEvents());
    	MinecraftForge.EVENT_BUS.register(new WardFunctionEvent());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.registerTESRs();
    }
}
