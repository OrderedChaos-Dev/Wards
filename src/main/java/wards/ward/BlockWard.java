package wards.ward;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWard extends Block
{
	public BlockWard()
	{
		super(Material.ROCK);
		this.setHardness(5.0F);
		this.setResistance(12.0F);
		this.setSoundType(SoundType.STONE);
	}
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(world.isRemote)
		{
			return true;
		}
		else
		{
			TileEntityWard ward = (TileEntityWard)world.getTileEntity(pos);
			ItemStack stack = player.getHeldItem(hand);
			if(stack == ItemStack.EMPTY)
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ward.getBook());
				ward.setBook(ItemStack.EMPTY);
				
				return true;
			}
			else if(stack.getItem() instanceof ItemEnchantedBook)
			{
				if(ward.getBook() != ItemStack.EMPTY)
				{
					InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ward.getBook());
				}
				ward.setBook(stack);
				
				if(!player.isCreative())
				{
					stack.shrink(1);
				}
				
				return true;
			}
		}
		
        return false;
    }
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
		TileEntityWard ward = (TileEntityWard)world.getTileEntity(pos);
		if(ward.getBook() != ItemStack.EMPTY)
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ward.getBook());
		
		super.breakBlock(world, pos, state);
    }
	
	@Override
    public boolean isFullCube(IBlockState state)
    {
        return true;
    }
	
	@Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

	@Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
    	return new TileEntityWard();
    }
}
