package wards.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import wards.effect.WardEffect;
import wards.function.EnchantmentTypeHelper;
import wards.function.EnchantmentTypeHelper.EnchantmentType;

public class TileEntityWard extends TileEntity implements ITickable
{
	private ItemStack book;
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
    
    private int power;
    private int maxPower;
    
    private boolean disableAttacks;
    private boolean disableWard;
    
    private boolean isDisplayMode;
    
    private NBTTagList list;
	
	public TileEntityWard()
	{
		book = ItemStack.EMPTY;
		list = new NBTTagList();
		power = 0;
		maxPower = 120000;
		
	    disableAttacks = false;
	    disableWard = false;
	    isDisplayMode = false;
	}
	
	public void setBook(ItemStack stack)
	{
		this.book = stack;
		setNBTTagList(ItemEnchantedBook.getEnchantments(stack));
		
		if(stack == ItemStack.EMPTY)
		{
			this.list = new NBTTagList();
		}
		
		updateTE();
	}
	
	private void setNBTTagList(NBTTagList list)
	{
		this.list = list.copy();
	}
	
	public ItemStack getBook()
	{
		return this.book;
	}
	
	public boolean fuelWard()
	{
		updateTE();
		if(power + 24000 > maxPower)
		{
			return false;
		}
		else
		{
			power += 24000;
			this.spawnParticles(EnumParticleTypes.ENCHANTMENT_TABLE, 16);
			return true;
		}
	}
	
	public int getPower()
	{
		return this.power;
	}
	
	public int getMaxPower()
	{
		return this.maxPower;
	}
	
	public void consumePower()
	{
		if(this.power > 0)
		{
			boolean hasBook = false;
			
			if(this.book.getItem() instanceof ItemEnchantedBook)
			{
				hasBook = true;
			}
			else
			{
				if(this.getWorld().getTotalWorldTime() % (hasBook ? 1 : 10) == 0)
				{
					power--;
				}
			}
		}
		updateTE();
	}
	
	public void disableAttacks(boolean bool)
	{
		this.disableAttacks = bool;
		updateTE();
		if(bool)
		{
			this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY, 15);
		}
	}
	
	public void disableWard(boolean bool)
	{
		this.disableWard = bool;
		updateTE();
		if(bool)
		{
			if(bool)
			{
				this.spawnParticles(EnumParticleTypes.CRIT, 15);
			}
		}
	}
	
	public void setDisplayMode(boolean bool)
	{
		this.isDisplayMode = bool;
		updateTE();
		if(bool)
		{
			this.spawnParticles(EnumParticleTypes.ENCHANTMENT_TABLE, 15);
		}
	}
	
	public boolean isDisplayMode()
	{
		return this.isDisplayMode;
	}
	
	public EnchantmentData[] getEnchantments()
	{
		if(this.book.getItem() instanceof ItemEnchantedBook)
		{
			NBTTagList list = ItemEnchantedBook.getEnchantments(this.book);
			EnchantmentData[] enchants = new EnchantmentData[list.tagCount()];
			
			for(int index = 0; index < list.tagCount(); index++)
			{
	            NBTTagCompound nbttagcompound = list.getCompoundTagAt(index);
	            int id = nbttagcompound.getShort("id");
	            int lvl = nbttagcompound.getShort("lvl");
	            
	            Enchantment enchant = Enchantment.getEnchantmentByID(id);
	            
	            enchants[index] = new EnchantmentData(enchant, lvl);
			}
			
			return enchants;
		}
		
		return new EnchantmentData[0];
	}

	@Override
	public void update()
	{
		this.rotateBook();
        
		if(this.book.getItem() instanceof ItemEnchantedBook)
		{
			if(this.canWard())
			{
				if(this.getWorld().getTotalWorldTime() % 100 == 0)
				{
					EnchantmentData[] enchants = getEnchantments();
					if(enchants.length > 0)
					{
						EnchantmentData primaryEnchant = null;
						EnchantmentData secondaryEnchant = null;
						
						if(enchants.length == 1)
							primaryEnchant = enchants[0];
						else
						{
							primaryEnchant = enchants[0];
							secondaryEnchant = enchants[1];
							
							//grabs the two strongest enchantments
							for(int i = 1; i < enchants.length; i++)
							{
								if(primaryEnchant.enchantmentLevel < enchants[i].enchantmentLevel)
								{
									secondaryEnchant = primaryEnchant;
									primaryEnchant = enchants[i];
								}
								else if(secondaryEnchant.enchantmentLevel < enchants[i].enchantmentLevel)
								{
									secondaryEnchant = enchants[i];
								}
							}
						}
						
						if(secondaryEnchant != null)
						{
							if((primaryEnchant.enchantment == Enchantments.FORTUNE && secondaryEnchant.enchantment == Enchantments.SILK_TOUCH)
									|| (secondaryEnchant.enchantment == Enchantments.FORTUNE && primaryEnchant.enchantment == Enchantments.SILK_TOUCH))
							{
								BlockPos pos = this.getPos();
								this.getWorld().createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1, true);
								InventoryHelper.spawnItemStack(getWorld(), pos.getX(), pos.getY(), pos.getZ(), this.book);
								this.setBook(ItemStack.EMPTY);
							}
						}
						
						this.wardArea(primaryEnchant, secondaryEnchant);
					}
				}
			}
			else
			{
				if(this.getWorld().isRemote && !this.isDisplayMode)
				{
    	        	if(this.getWorld().rand.nextBoolean())
    	        	{
    	        		this.spawnParticles(EnumParticleTypes.REDSTONE, 1);
    	        	}
				}
			}
		}
		consumePower();
	}
	
	public void wardArea(EnchantmentData ed1, EnchantmentData ed2)
	{
		Enchantment enchant1 = ed1.enchantment;
		int lvl = ed1.enchantmentLevel;
		
		int range = 5 + (lvl * 2);
		
		if(lvl > 5)
			range = 15;
		
		BlockPos pos1 = this.getPos().add(-range, -range, -range);
		BlockPos pos2 = this.getPos().add(range, range, range);
		
		AxisAlignedBB wardArea = new AxisAlignedBB(pos1, pos2);
		for(EntityPlayer player : this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, wardArea))
		{
			if(!this.getWorld().isRemote)
			{
				player.addPotionEffect(new PotionEffect(WardEffect.byEnchant(enchant1), 100, lvl - 1, false, false));
				if(ed2 != null)
				{
					player.addPotionEffect(new PotionEffect(WardEffect.byEnchant(ed2.enchantment), 100, 0, false, false));
				}
				if(EnchantmentTypeHelper.getEnchantmentType(enchant1) == EnchantmentType.FORTITUDE)
				{
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("absorption"), 100, 0));
				}
			}
			
			if(this.getWorld().isRemote)
			{
				double xDiff = (player.posX - this.getPos().getX()) / 16;
				double yDiff = (player.posY + 0.5 - this.getPos().getY()) / 16;
				double zDiff = (player.posZ - this.getPos().getZ()) / 16;
				
				Random rand = this.getWorld().rand;
				
				for(double i = 1.0; i <= 16.0; i++)
				{
					double x = this.getPos().getX() + (xDiff * i) + 0.5 + (0.25 * rand.nextDouble() - 0.25 * rand.nextDouble());
					double y = this.getPos().getY() + (yDiff * i) + 0.5;
					double z = this.getPos().getZ() + (zDiff * i) + 0.5 + (0.25 * rand.nextDouble() - 0.25 * rand.nextDouble());
					this.getWorld().spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, x, y, z, 0, 0, 0);
				}
			}
		}
		
		this.handleSpecial(enchant1, range);
		
		if(!this.disableAttacks)
		{
			List<EntityMob> nearbyMobs = this.getWorld().getEntitiesWithinAABB(EntityMob.class, wardArea);
			
			if(nearbyMobs.size() > 0)
			{
				power -= 10;
				float damage = lvl;
				
				if(ed2 != null)
				{
					damage += 0.5F;
				}
				
				for(EntityMob mob : nearbyMobs)
				{
					mob.attackEntityFrom(DamageSource.MAGIC, damage);
					this.handleEnchantAttack(mob, enchant1, lvl);
					
					if(this.getWorld().isRemote)
					{
						double xDiff = (mob.posX - (this.getPos().getX() + 0.5)) / 14.0;
						double yDiff = (mob.posY + 0.5 - (this.getPos().getY() + 0.8)) / 14.0;
						double zDiff = (mob.posZ - (this.getPos().getZ() + 0.5)) / 14.0;
						EnumParticleTypes[] particles = EnchantmentTypeHelper.getParticles(enchant1);
						
						for(double zapCount = 1.0; zapCount <= 14.0; zapCount++) //zap!
						{
							double xCoord = this.getPos().getX() + (xDiff * zapCount);
							double yCoord = this.getPos().getY() + (yDiff * zapCount);
							double zCoord = this.getPos().getZ() + (zDiff * zapCount);
							
							this.getWorld().spawnParticle(particles[0], xCoord, yCoord, zCoord, 0, 0, 0);
							this.getWorld().spawnParticle(particles[1], xCoord, yCoord, zCoord, 0, 0, 0);
						}
					}
				}
			}
		}
	}
	
	public void handleEnchantAttack(EntityMob mob, Enchantment enchant, int level)
	{
		EnchantmentType type = EnchantmentTypeHelper.getEnchantmentType(enchant);
		double x = mob.posX;
		double y = mob.posY;
		double z = mob.posZ;
		
		if(type == EnchantmentType.GENERIC)
		{
			mob.attackEntityFrom(DamageSource.MAGIC, Math.min(0.5F * level, 2.0F));
		}
		if(type == EnchantmentType.FIRE)
		{
			mob.setFire(3 * level);
		}
		if(type == EnchantmentType.WATER)
		{
			if(mob.isInWater() || this.getWorld().getBlockState(mob.getPosition()).getMaterial() == Material.WATER)
				mob.attackEntityFrom(DamageSource.MAGIC, Math.min(1.0F * level, 3.0F));
		}
		if(type == EnchantmentType.FROST)
		{
			mob.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), 100, Math.min(level - 1, 2)));
		}
		if(type == EnchantmentType.EXPLOSION)
		{
			if(this.getWorld().rand.nextInt(15) == 0)
			{
				this.getWorld().createExplosion(mob, x, y + 0.3, z, Math.min(1.0F * level, 3.0F), false);
			}
		}
		if(type == EnchantmentType.KNOCKUP)
		{
			mob.motionY += 1.0;
		}
		if(type == EnchantmentType.OOF)
		{
			BlockPos pos = this.getPos();
			Vec3d mobVec = new Vec3d(x, y, z);
			Vec3d wardVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
			double distance = wardVec.distanceTo(mobVec);
			
			Vec3d vec = new Vec3d(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
			
			mob.motionX += (-vec.x / 2.5);
			mob.motionY += (-vec.y / 2.0) / distance;
			mob.motionZ += (-vec.z / 2.5);
		}
		if(type == EnchantmentType.SMITE)
		{
			if(mob.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
			{
				if(this.getWorld().rand.nextInt(100) == 0)
				{
					this.getWorld().spawnEntity(new EntityLightningBolt(this.getWorld(), x, y, z, false));
				}
				else
				{
					mob.attackEntityFrom(DamageSource.MAGIC, Math.min(1.0F * level, 3.0F));
				}
			}
		}
		if(type == EnchantmentType.ARTHROPODS)
		{
			if(mob.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD)
			{
				mob.attackEntityFrom(DamageSource.MAGIC, Math.min(1.0F * level, 3.0F));
			}
		}
		if(type == EnchantmentType.SWEEPING)
		{
			AxisAlignedBB bounds = new AxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
			for(Entity entity : world.getEntitiesWithinAABBExcludingEntity(mob, bounds))
			{
				if(entity instanceof EntityMob)
				{
					((EntityMob)entity).attackEntityFrom(DamageSource.MAGIC, Math.min(0.5F * level, 2.0F));
				}
			}
		}
		if(type == EnchantmentType.EXPERIENCE)
		{
			if(!this.getWorld().isRemote && this.getWorld().rand.nextInt(7) == 0)
			{
				EntityXPOrb orb = new EntityXPOrb(this.getWorld());
				orb.xpValue = 1;
				orb.setPositionAndRotation(x, y, z, mob.rotationYawHead, mob.rotationPitch);
				this.getWorld().spawnEntity(orb);
			}
		}
		if(type == EnchantmentType.CURSE)
		{
			mob.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("wither"), 100, 0));
		}
		if(type == EnchantmentType.LUCK)
		{
			if(this.getWorld().rand.nextInt(100) == 0)
			{
				mob.attackEntityFrom(DamageSource.MAGIC, 50.0F);
			}
		}
	}
	
	public void handleSpecial(Enchantment enchant, int range)
	{
		EnchantmentType type = EnchantmentTypeHelper.getEnchantmentType(enchant);
		Random rand = this.getWorld().rand;
		
		if(type == EnchantmentType.WATER)
		{
			BlockPos pos1 = this.getPos().add(-range, -range, -range);
			BlockPos pos2 = this.getPos().add(range, range, range);
			
			for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos1, pos2))
			{
				IBlockState state = this.getWorld().getBlockState(blockpos);
				Block block = state.getBlock();
				
				if(rand.nextInt(100) == 0)
				{
					double x = blockpos.getX() + 0.5;
					double y = blockpos.getY() + 0.5;
					double z = blockpos.getZ() + 0.5;
					
					if(block instanceof BlockCrops)
					{
						if(!((BlockCrops)block).isMaxAge(state))
						{
							this.getWorld().setBlockState(blockpos, ((BlockCrops)block).withAge(((BlockCrops)block).getMetaFromState(state) + 1));
							
							if(this.getWorld().isRemote)
							{
								for(int i = 0; i < 5; i++)
								{
			        	        	double xPos = x + (0.5 * this.getWorld().rand.nextDouble()) - (0.5 * this.getWorld().rand.nextDouble());
			        	        	double zPos = z + (0.5 * this.getWorld().rand.nextDouble()) - (0.5 * this.getWorld().rand.nextDouble());
			        	        	this.getWorld().spawnParticle(EnumParticleTypes.WATER_SPLASH, xPos, y, zPos, 0, 0, 0);
								}
							}
						}
					}
					if((block instanceof BlockStem))
					{
						if(state.getValue(BlockStem.AGE) < 7)
						{
							this.getWorld().setBlockState(blockpos, state.withProperty(BlockStem.AGE, state.getValue(BlockStem.AGE) + 1));
							
							if(this.getWorld().isRemote)
							{
								for(int i = 0; i < 5; i++)
								{
			        	        	double xPos = x + (0.5 * this.getWorld().rand.nextDouble()) - (0.5 * this.getWorld().rand.nextDouble());
			        	        	double zPos = z + (0.5 * this.getWorld().rand.nextDouble()) - (0.5 * this.getWorld().rand.nextDouble());
			        	        	this.getWorld().spawnParticle(EnumParticleTypes.WATER_SPLASH, xPos, y, zPos, 0, 0, 0);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean canWard()
	{
		if(this.disableWard || this.power <= 0 || this.isDisplayMode)
		{
			return false;
		}
		
		boolean flag = true;
		int range = 15;
		BlockPos pos1 = this.getPos().add(-range, -range, -range);
		BlockPos pos2 = this.getPos().add(range, range, range);

		for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos1, pos2))
		{
			if(!Objects.equal(blockpos, this.getPos()))
			{
				if(this.getWorld().getBlockState(blockpos).getBlock() instanceof BlockWard)
				{
					if(((TileEntityWard)this.getWorld().getTileEntity(blockpos)).getBook().getItem() instanceof ItemEnchantedBook)
					{
						flag = false;
						
						//pulses every 2 seconds to show where the interfering ward is
						if(!flag && this.getWorld().isRemote && this.getWorld().getTotalWorldTime() % 40 == 0)
						{
							double xDiff = (blockpos.getX() - this.getPos().getX()) / 8.0;
							double yDiff = (blockpos.getY() - this.getPos().getY()) / 8.0;
							double zDiff = (blockpos.getZ() - this.getPos().getZ()) / 8.0;
							
							for(double i = 1.0; i <= 8.0; i++)
							{
								double x = this.getPos().getX() + (xDiff * i) + 0.5;
								double y = this.getPos().getY() + (yDiff * i) + 0.5;
								double z = this.getPos().getZ() + (zDiff * i) + 0.5;
								this.getWorld().spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0, 0, 0);
							}
						}
					}
				}
			}
		}
		
		return flag;
	}
	
	public void spawnParticles(EnumParticleTypes particle, int amount)
	{
		double x = this.getPos().getX() + 0.5;
		double y = this.getPos().getY() + 0.8;
		double z = this.getPos().getZ() + 0.5;
		
		for(int i = 0; i < amount; i++)
		{
        	double xPos = x + (0.5 * this.getWorld().rand.nextDouble()) - (0.5 * this.getWorld().rand.nextDouble());
        	double zPos = z + (0.5 * this.getWorld().rand.nextDouble()) - (0.5 * this.getWorld().rand.nextDouble());
        	this.getWorld().spawnParticle(particle, xPos, y, zPos, 0, 0, 0);
		}
	}
	
	//taken from TileEntityEnchantmentTable (and modified)
	public void rotateBook()
	{
		Random rand = this.getWorld().rand;
		
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        this.tRot += 0.02F;
        
        if(canWard() || this.isDisplayMode)
        	this.bookSpread += 0.1F;
        else
        	this.bookSpread -= 0.1F;

        if (this.bookSpread < 0.5F || rand.nextInt(40) == 0)
        {
            float f1 = this.flipT;

            while (true)
            {
                this.flipT += (float)(rand.nextInt(4) - rand.nextInt(4));

                if (f1 != this.flipT)
                {
                    break;
                }
            }
        }

        while (this.bookRotation >= (float)Math.PI)
        {
            this.bookRotation -= ((float)Math.PI * 2F);
        }

        while (this.bookRotation < -(float)Math.PI)
        {
            this.bookRotation += ((float)Math.PI * 2F);
        }

        while (this.tRot >= (float)Math.PI)
        {
            this.tRot -= ((float)Math.PI * 2F);
        }

        while (this.tRot < -(float)Math.PI)
        {
            this.tRot += ((float)Math.PI * 2F);
        }

        float f2;

        for (f2 = this.tRot - this.bookRotation; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (f2 < -(float)Math.PI)
        {
            f2 += ((float)Math.PI * 2F);
        }

        this.bookRotation += f2 * 0.4F;
        this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F * (float)((double)this.power / this.maxPower));
        ++this.tickCount;
        this.pageFlipPrev = this.pageFlip;
        float f = (this.flipT - this.pageFlip) * 0.4F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.flipA += (f - this.flipA) * 0.9F;
        this.pageFlip += this.flipA;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
        if(compound.hasKey("Item"))
        {
        	this.book = new ItemStack(Item.getByNameOrId(compound.getString("Item")), 1);
        	this.list = (NBTTagList) compound.getTag("Enchantments");
        	
            for (int i = 0; i < list.tagCount(); ++i)
            {
	            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
	            int id = nbttagcompound.getShort("id");
	            int lvl = nbttagcompound.getShort("lvl");
	            
	            Enchantment enchant = Enchantment.getEnchantmentByID(id);
	            EnchantmentData data = new EnchantmentData(enchant, lvl);
	            
	            ItemEnchantedBook.addEnchantment(book, data);
            }
        }
        else
        {
        	this.book = ItemStack.EMPTY;
        }
        
        if(compound.hasKey("Power"))
        {
        	this.power = compound.getInteger("Power");
        }
        if(compound.hasKey("DisableAttacks"))
        {
        	this.disableAttacks = compound.getBoolean("DisableAttacks");
        }
        if(compound.hasKey("DisableWard"))
        {
        	this.disableWard = compound.getBoolean("DisableWard");
        }
        if(compound.hasKey("Display"))
        {
        	this.isDisplayMode = compound.getBoolean("Display");
        }
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
        if(book != ItemStack.EMPTY)
        {
            ResourceLocation resource = Item.REGISTRY.getNameForObject(book.getItem());
            compound.setString("Item", resource == null ? "" : resource.toString());
            compound.setTag("Enchantments", list);
        }
        compound.setInteger("Power", power);
        compound.setBoolean("DisableAttacks", this.disableAttacks);
        compound.setBoolean("DisableWard", this.disableWard);
        compound.setBoolean("Display", this.isDisplayMode);
		
		return compound;
	}
	
	@Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.getPos(), 7, this.writeToNBT(new NBTTagCompound()));
    }
	
	@Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
		this.readFromNBT(pkt.getNbtCompound());
    }
	
	public void updateTE()
	{
		IBlockState state = this.getWorld().getBlockState(this.getPos());
		this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
		this.getWorld().markBlockRangeForRenderUpdate(this.getPos(), this.getPos());
		this.getWorld().scheduleBlockUpdate(this.getPos(), this.getBlockType(), 0, 0);
		this.markDirty();
	}
}
