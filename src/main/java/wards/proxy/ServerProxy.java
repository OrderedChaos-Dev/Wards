package wards.proxy;

import net.minecraft.tileentity.TileEntity;

public class ServerProxy implements ICommonProxy
{
	@Override
	public <T extends TileEntity> void registerTESRs(){}
}
