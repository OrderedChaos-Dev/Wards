package wards.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWard extends Block
{
	private final AxisAlignedBB BASE_BOX = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
	private final AxisAlignedBB RAISED_BASE_BOX = new AxisAlignedBB(0.1875, 0.125, 0.1875, 0.8125, 0.250, 0.8125);
	private final AxisAlignedBB PILLAR_BOX = new AxisAlignedBB(0.3125, 0.125, 0.3125, 0.6875, 0.8125, 0.6875);
	
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
		TileEntityWard ward = (TileEntityWard)world.getTileEntity(pos);
		ItemStack stack = player.getHeldItem(hand);
		
		if(ward.isAdminMode())
		{
			if(!player.canUseCommand(2, ""))
			{
				return false;
			}
		}
		
		if(stack.getItem() instanceof ItemEnchantedBook)
		{
			if(ward.getBook() != ItemStack.EMPTY)
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ward.getBook());
			}
			ward.setBook(stack.copy());
			
			if(!player.isCreative())
			{
				stack.shrink(1);
			}
			
			return true;
		}
		else if(stack.getItem() == Items.DYE)
		{
			if(EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.BLUE)
			{
				if(ward.fuelWard())
				{
					if(!player.isCreative())
					{
						stack.shrink(1);
					}
				}
			}
			return true;
		}
		else if(stack.getItem() == Items.STICK)
		{
			ward.setDisplayMode(!ward.isDisplayMode());
			return true;
		}
		else if(stack.getItem() == Item.getItemFromBlock(Blocks.COMMAND_BLOCK) && player.canUseCommand(2, ""))
		{
			ward.setAdminMode(!ward.isAdminMode());
		}
		else if(ward.getBook() != ItemStack.EMPTY)
		{
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ward.getBook());
			ward.setBook(ItemStack.EMPTY);
			return true;
		}
		
        return false;
    }
	
	@Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		TileEntityWard ward = (TileEntityWard)world.getTileEntity(pos);
		int power = world.getStrongPower(fromPos);
		ward.disableAttacks(power > 1);
		ward.disableWard(power > 12);
    }
	
	@Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_BOX);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, RAISED_BASE_BOX);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, PILLAR_BOX);
    }
	
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
		return PILLAR_BOX.grow(0.125);
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
        return false;
    }
	
	@Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
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
