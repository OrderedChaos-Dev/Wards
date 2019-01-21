package wards.proxy;

import net.minecraft.tileentity.TileEntity;

public interface ICommonProxy
{
	public <T extends TileEntity> void registerTESRs();
}
