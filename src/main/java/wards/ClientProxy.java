package wards;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import wards.ward.TileEntityWard;
import wards.ward.TileEntityWardRenderer;

public class ClientProxy implements ICommonProxy
{
	@Override
	public <T extends TileEntity> void registerTESRs()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWard.class, new TileEntityWardRenderer());
	}
	
}
