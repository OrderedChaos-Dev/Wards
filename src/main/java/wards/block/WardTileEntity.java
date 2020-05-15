package wards.block;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import wards.WardsConfig;
import wards.WardsRegistryManager;
import wards.function.WardEnchantmentType;

public class WardTileEntity extends TileEntity implements ITickableTileEntity {

	private ItemStack book;
	private int power;
	private int maxPower = 100000;
	
	private boolean canWard;
	
	public int tickCount;
	public float pageFlip;
	public float pageFlipPrev;
	public float flipT;
	public float flipA;
	public float bookSpread;
	public float bookSpreadPrev;
	public float bookRotation;
	public float bookRotationPrev;
	public float tRot;
	
	public WardTileEntity() {
		super(WardsRegistryManager.ward_te);
		this.book = ItemStack.EMPTY;
		this.power = 0;
		this.canWard = true;
	}

	@Override
	public void tick() {
		Random rand = this.getWorld().getRandom();
		this.updateBookRotation();
		
		boolean adminMode = this.getBlockState().get(WardBlock.ADMIN_MODE);
		if(adminMode)
			this.setFuel(this.maxPower);
		
		if(!this.getBook().isEmpty() && this.power > 0) {
			Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(this.getBook());
			Enchantment primaryEnchant = null;
			Enchantment secondaryEnchant = null;
			int primaryEnchantLevel = 0;
			
			for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
				if(primaryEnchant == null) {
					primaryEnchant = entry.getKey();
					primaryEnchantLevel = entry.getValue();
				} else if(secondaryEnchant == null) {
					secondaryEnchant = entry.getKey();
				}
				if(entry.getValue() > primaryEnchantLevel) {
					secondaryEnchant = primaryEnchant;
					primaryEnchant = entry.getKey();
					primaryEnchantLevel = entry.getValue();
				}
			}
			
			int range = Math.min(5 + (3 * Math.min(4, primaryEnchantLevel)), 15);
			BlockPos pos1 = this.getPos().add(-range, -range, -range);
			BlockPos pos2 = this.getPos().add(range, range, range);
			AxisAlignedBB wardArea = new AxisAlignedBB(pos1, pos2);
			
			this.canWard = true;
			int redstoneStrength = this.getWorld().getStrongPower(this.getPos());
			
			if(!adminMode) {
				if(redstoneStrength >= 12) {
					this.canWard = false;
				} else {
					for(BlockPos pos : BlockPos.getAllInBoxMutable(pos1, pos2)) {
						if (!pos.equals(this.getPos())) {
							if (this.getWorld().getBlockState(pos) == WardsRegistryManager.ward.getDefaultState()) {
								if(this.getWorld().getTileEntity(pos) instanceof WardTileEntity) {
									//disable if other powered wards nearby
									if(((WardTileEntity)this.getWorld().getTileEntity(pos)).power > 0
											&& !((WardTileEntity)this.getWorld().getTileEntity(pos)).getBook().isEmpty()) {
										this.canWard = false;
										if(this.getWorld().getGameTime() % 20 == 0 && this.getWorld().isRemote) {
											double xDiff = (pos.getX() - this.getPos().getX()) / 14.0;
											double yDiff = (pos.getY() - this.getPos().getY()) / 14.0;
											double zDiff = (pos.getZ() - this.getPos().getZ()) / 14.0;
											for (int i = 0; i <= 14.0; i++) {
												double xCoord = this.getPos().getX() + (xDiff * i) + 0.5;
												double yCoord = this.getPos().getY() + (yDiff * i) + 0.5;
												double zCoord = this.getPos().getZ() + (zDiff * i) + 0.5;

												this.getWorld().addParticle(RedstoneParticleData.REDSTONE_DUST, true, xCoord, yCoord, zCoord, 0, 0, 0);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(this.canWard) {
				if(primaryEnchant != null) {
					WardEnchantmentType wardType = WardEnchantmentType.fromEnchant(primaryEnchant);
					WardEnchantmentType wardType2 = null;
					if(secondaryEnchant != null) {
						wardType2 = WardEnchantmentType.fromEnchant(secondaryEnchant);
					}
					
					//primary enchantment
					if(this.getWorld().getGameTime() % wardType.getInterval() == 0) {
						List<LivingEntity> entities = this.getWorld().getEntitiesWithinAABB(LivingEntity.class, wardArea);
						for(LivingEntity target : entities) {
							if(canSeeTarget(this, target)) {
								if(this.getWorld().isRemote) {
									double xDiff = (target.getPosX() - this.getPos().getX() - 0.5) / 14.0; 
									double yDiff = ((target.getPosY() + (target.getHeight() / 2)) - this.getPos().getY() + 0.5) / 14.0; 
									double zDiff = (target.getPosZ() - this.getPos().getZ() - 0.5) / 14.0; 
									for(int i = 0; i <= 14.0; i ++) {
										double xCoord = this.getPos().getX() + (xDiff * i) + 0.5;
										double yCoord = this.getPos().getY() + (yDiff * i) + 0.5;
										double zCoord = this.getPos().getZ() + (zDiff * i) + 0.5;
										
										if(target instanceof IMob && redstoneStrength == 0) {
											for(IParticleData particle : wardType.getParticles())  {
												this.getWorld().addParticle(particle, true, xCoord, yCoord, zCoord, 0, 0, 0);
												this.getWorld().addParticle(particle, true, xCoord, yCoord, zCoord, 0, 0, 0);
											}
											
										} else if(target instanceof PlayerEntity) {
											this.getWorld().addParticle(ParticleTypes.ENCHANT, true, xCoord, yCoord, zCoord, 0, 0, 0);
											this.getWorld().addParticle(ParticleTypes.ENCHANT, true, xCoord, yCoord, zCoord, 0, 0, 0);
										}
									}
								}
								if(target instanceof IMob && redstoneStrength == 0) {
									wardType.expelMagic(this, target, primaryEnchantLevel);
									this.subtractFuel(20);
								} else if(target instanceof PlayerEntity) {
									wardType.empowerPlayer(this, (PlayerEntity)target, primaryEnchantLevel);
									this.subtractFuel(20);
								}
							}
						}
					}
					
					if(this.getWorld().getGameTime() % 40 == 0 && rand.nextBoolean() && this.getWorld().isRemote) {
						for(int i = 0; i < 5 * primaryEnchantLevel; i++) {
							for(IParticleData particle : wardType.getParticles())  {
								double xCoord = this.getPos().getX() + 0.5 + 0.25 * (rand.nextDouble() - rand.nextDouble());
								double zCoord = this.getPos().getZ() + 0.5 + 0.25 * (rand.nextDouble() - rand.nextDouble());
								double yCoord = this.getPos().getY() + 0.85;
								this.getWorld().addParticle(particle, true, xCoord, yCoord, zCoord, 0, 0, 0);
								this.getWorld().addParticle(particle, true, xCoord, yCoord, zCoord, 0, 0, 0);
							}
						}
					}
					
					//secondary enchantment
					if(wardType2 != null) {
						if (this.getWorld().getGameTime() % (wardType2.getInterval() * 1.5) == 0) {
							List<LivingEntity> entities = this.getWorld().getEntitiesWithinAABB(LivingEntity.class, wardArea);
							for (LivingEntity target : entities) {
								if(canSeeTarget(this, target)) {
									if(this.getWorld().isRemote) {
										double xDiff = (target.getPosX() - this.getPos().getX() - 0.5D) / 14.0D;
										double yDiff = ((target.getPosY() + (target.getHeight() / 2)) - (this.getPos().getY() + 0.5D)) / 14.0D;
										double zDiff = (target.getPosZ() - this.getPos().getZ() - 0.5D) / 14.0D;
										for (int i = 0; i <= 14.0; i++) {
											double xCoord = this.getPos().getX() + (xDiff * i) + 0.5;
											double yCoord = this.getPos().getY() + (yDiff * i) + 0.5;
											double zCoord = this.getPos().getZ() + (zDiff * i) + 0.5;

											if (target instanceof IMob && redstoneStrength == 0) {
												for (IParticleData particle : wardType2.getParticles()) {
													this.getWorld().addParticle(particle, true, xCoord, yCoord, zCoord, 0, 0, 0);
													this.getWorld().addParticle(particle, true, xCoord, yCoord, zCoord, 0, 0, 0);
												}
											} else if (target instanceof PlayerEntity) {
												this.getWorld().addParticle(ParticleTypes.ENCHANT, true, xCoord, yCoord, zCoord, 0,0, 0);
												this.getWorld().addParticle(ParticleTypes.ENCHANT, true, xCoord, yCoord, zCoord, 0,0, 0);
											}
										}
									}
								}
								
								if(target instanceof IMob && redstoneStrength == 0) {
									wardType.expelMagic(this, target, primaryEnchantLevel);				
								} else if(target instanceof PlayerEntity) {
									wardType.empowerPlayer(this, (PlayerEntity)target, primaryEnchantLevel);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean canSeeTarget(WardTileEntity ward, Entity entity) {
		Vec3d vec3d = new Vec3d(ward.getPos().getX() + 0.5D, ward.getPos().getY() + 0.9D, ward.getPos().getZ() + 0.5D);
		Vec3d vec3d1 = new Vec3d(entity.getPosX(), entity.getPosYEye(), entity.getPosZ());
		Vec3d vec3d2 = new Vec3d(entity.getPosX(), entity.getPosY(), entity.getPosZ());
		return
			ward.getWorld().rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS
			|| ward.getWorld().rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS;
	}
	
	public void updateBookRotation() {
		Random rand = this.getWorld().getRandom();
		this.bookSpreadPrev = this.bookSpread;
		this.bookRotationPrev = this.bookRotation;
		this.tRot += 0.02F;
		
		if(this.canWard || this.getBook().getItem() == Items.BOOK) {
			bookSpread += 0.1F;
		} else {
			bookSpread -= 0.1F;
		}
        if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
            float f1 = this.flipT;
            while (true) {
                this.flipT += (float)(rand.nextInt(4) - rand.nextInt(4));
                if (f1 != this.flipT) {
                    break;
                }
            }
        }
		while (this.bookRotation >= (float) Math.PI) {
			this.bookRotation -= ((float) Math.PI * 2F);
		}
		while (this.bookRotation < -(float) Math.PI) {
			this.bookRotation += ((float) Math.PI * 2F);
		}
		while (this.tRot >= (float) Math.PI) {
			this.tRot -= ((float) Math.PI * 2F);
		}
		while (this.tRot < -(float) Math.PI) {
			this.tRot += ((float) Math.PI * 2F);
		}
		float f2;
		for (f2 = this.tRot - this.bookRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
			;
		}
		while (f2 < -(float) Math.PI) {
			f2 += ((float) Math.PI * 2F);
		}
		this.bookRotation += f2 * 0.4F;
		
		if(this.getBook().getItem() != Items.BOOK)
			this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F * (float)((double)this.power / (double)this.maxPower));
		else
			this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
		this.tickCount++;
		this.pageFlipPrev = this.pageFlip;
		float f = (this.flipT - this.pageFlip) * 0.4F;
		f = MathHelper.clamp(f, -0.2F, 0.2F);
		this.flipA += (f - this.flipA) * 0.9F;
		this.pageFlip += this.flipA;
	}
	
	public ItemStack getBook() {
		return this.book;
	}
	
	private void setBook(ItemStack book) {
		this.book = book;
		this.markDirty();
		this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
		this.getWorld().markBlockRangeForRenderUpdate(this.getPos(), this.getBlockState(), this.getBlockState());
	}
	
	public boolean replaceBook(ItemStack stack, BlockPos pos) {
		if(this.getBook().isEmpty()) {
			if(WardsConfig.acceptedItems.get().contains(stack.getItem().getRegistryName().toString())) {
				this.setBook(stack);
				return true;
			}
		} else {
			this.dropBook();
		}
		return false;
	}
	
	public void dropBook() {
		if(!this.getBook().isEmpty()) {
			InventoryHelper.spawnItemStack(this.getWorld(), pos.getX(), pos.getY(), pos.getZ(), this.book);
			this.setBook(ItemStack.EMPTY);
		}
	}
	
	public int getFuel() {
		return this.power;
	}
	
	public boolean setFuel(int value) {
		if(value <= maxPower) {
			this.power = value;
			return true;
		}
		return false;
	}
	
	public void subtractFuel(int value) {
		this.setFuel(Math.max(0, this.getFuel() - value));
	}
	
	public boolean addFuel(int value, boolean fillToMax) {
		int temp = power + value;
		if(temp <= maxPower) {
			power = temp;
			this.markDirty();
			this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
			this.getWorld().markBlockRangeForRenderUpdate(this.getPos(), this.getBlockState(), this.getBlockState());
			return true;
		} else {
			if(fillToMax) {
				power = maxPower;
				this.markDirty();
				this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
				this.getWorld().markBlockRangeForRenderUpdate(this.getPos(), this.getBlockState(), this.getBlockState());
				return true;
			}
			return false;
		}
	}
	
	public boolean canWard() {
		return this.canWard;
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		if(compound.contains("power"))
			this.power = compound.getInt("power");
		if(compound.contains("canWard"))
			this.canWard = compound.getBoolean("canWard");
		if(compound.contains("book"))
			this.book = ItemStack.read(compound.getCompound("book"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("power", this.power);
		compound.putBoolean("canWard", this.canWard);
		compound.put("book", this.getBook().write(new CompoundNBT()));
		
		return compound;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket(){
	    return new SUpdateTileEntityPacket(this.getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
	    this.read(pkt.getNbtCompound());
		this.markDirty();
		this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
		this.getWorld().markBlockRangeForRenderUpdate(this.getPos(), this.getBlockState(), this.getBlockState());
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
}
