package wards.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
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
import wards.Wards;
import wards.WardsConfig;

public class WardBlock extends ContainerBlock {
	
	private static final VoxelShape BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	private static final VoxelShape RAISED_BASE = Block.box(3.0D, 2.0D, 3.0D, 13.0D, 4.0D, 13.0D);
	private static final VoxelShape PILLAR = Block.box(5.0D, 2.0D, 5.0D, 11.0D, 13.0D, 11.0D);
	private static final VoxelShape SHAPE = VoxelShapes.or(BASE, RAISED_BASE, PILLAR);
	
	public static final BooleanProperty ADMIN_MODE = BooleanProperty.create("admin_mode");
	
	private Map<String, Integer> powerSources = new HashMap<String, Integer>();
	
	public WardBlock(Block.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ADMIN_MODE, false));
		
		for(String s : WardsConfig.powerSources.get()) {
			String[] data = s.split("-");
			try {
				if(data.length == 2) {
					powerSources.put(data[0], Integer.valueOf(data[1]));
				} else {
					Wards.LOGGER.warn("Warning: invalid token in powerSources config option: " + s);
				}
			} catch(Exception e) {
				Wards.LOGGER.warn("Warning: invalid token in powerSources config option: " + s);
			}

		}
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytraceresult) {
		if(!world.isClientSide()) {
			TileEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof WardTileEntity) {
				ItemStack item = player.getItemInHand(hand);
				String registryName = item.getItem().getRegistryName().toString();
				
				if(!state.getValue(ADMIN_MODE) || player.canUseGameMasterBlocks()) {
					if(powerSources.containsKey(registryName)) {
						((WardTileEntity)tileentity).addFuel(powerSources.get(registryName), true);
						if(!player.isCreative())
							item.shrink(1);
					} else if(WardsConfig.acceptedItems.get().contains(item.getItem().getRegistryName().toString())) {
						ItemStack newBook = item.copy();
						newBook.setCount(1);
						if(((WardTileEntity)tileentity).replaceBook(newBook, pos)) {
							if(!player.isCreative())
								item.shrink(1);
						}
					} else if(item.getItem() == Items.COMMAND_BLOCK) {
						world.setBlock(pos, this.defaultBlockState().setValue(ADMIN_MODE, !state.getValue(ADMIN_MODE)), 2);
					} else {
						((WardTileEntity)tileentity).dropBook();
					}
				}
			}
		}

		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof WardTileEntity) {
			if(!((WardTileEntity)te).getBook().isEmpty()) {
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((WardTileEntity) te).getBook());
			}
		}
		WardTileEntity.WARD_CACHE.remove(pos);
		super.onRemove(state, world, pos, newState, isMoving);
	}
	
	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		if(state.getValue(ADMIN_MODE))
			return -1.0F;
		else
			return super.getDestroyProgress(state, player, worldIn, pos);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
        return true;
    }
	
	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new WardTileEntity();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ADMIN_MODE);
	}
}
