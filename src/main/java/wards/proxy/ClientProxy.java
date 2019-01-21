package wards.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import wards.block.TileEntityWard;
import wards.client.TileEntityWardRenderer;

public class ClientProxy implements ICommonProxy
{
	@Override
	public <T extends TileEntity> void registerTESRs()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWard.class, new TileEntityWardRenderer());
	}
	
}
