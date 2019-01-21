package wards;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Wards.MODID, name = Wards.NAME, type = Type.INSTANCE)
public class WardsConfig
{
	@Name("Ward Damage Multiplier")
	@Comment({
		"All damage by a ward is multiplied by this value",
		"e.g. a multiplier of 1.5 is 150% damage"
	})
	public static float damageMultiplier = 1.0F;
	
	@Mod.EventBusSubscriber
	public static class ConfigEventHandler
	{
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if(event.getModID().equals(Wards.MODID))
			{
				ConfigManager.sync(Wards.MODID, Type.INSTANCE);
			}
		}
	}
}
