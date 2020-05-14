package wards.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class WardBlock extends ContainerBlock {
	
	private static final VoxelShape BASE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	private static final VoxelShape RAISED_BASE = Block.makeCuboidShape(3.0D, 2.0D, 3.0D, 13.0D, 4.0D, 13.0D);
	private static final VoxelShape PILLAR = Block.makeCuboidShape(5.0D, 2.0D, 5.0D, 11.0D, 13.0D, 11.0D);
	private static final VoxelShape SHAPE = VoxelShapes.or(BASE, RAISED_BASE, PILLAR);
	
	public WardBlock(Block.Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytraceresult) {
		if(!world.isRemote) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof WardTileEntity) {
				ItemStack item = player.getHeldItem(hand);
				
				if(item.getItem() == Items.LAPIS_LAZULI) {
					((WardTileEntity)tileentity).addFuel(12000, true);
					if(!player.isCreative())
						item.shrink(1);
				} else if(((WardTileEntity)tileentity).replaceBook(item.copy(), pos)) {
					if(!player.isCreative())
						item.shrink(1);
				}
			}
		}

		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
        return true;
    }
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new WardTileEntity();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
