package wards.ward;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import wards.effect.WardEffect;

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
	
	public TileEntityWard()
	{
		book = ItemStack.EMPTY;
	}
	
	public void setBook(ItemStack stack)
	{
		this.book = stack;
		
		if(stack == null)
		{
			this.book = ItemStack.EMPTY;
		}
		
		updateTE();
	}
	
	public ItemStack getBook()
	{
		return this.book;
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
							world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1, true);
							InventoryHelper.spawnItemStack(getWorld(), pos.getX(), pos.getY(), pos.getZ(), this.book);
							this.setBook(ItemStack.EMPTY);
						}
						else
						{
							wardArea(primaryEnchant, false);
							wardArea(secondaryEnchant, true);	
						}
					}
					else
					{
						wardArea(primaryEnchant, false);
					}
				}
			}
			else
			{
				if(world.isRemote)
				{
    	        	if(world.rand.nextBoolean())
    	        	{
    					double x = pos.getX() + 0.5;
    					double y = pos.getY() + 0.8;
    					double z = pos.getZ() + 0.5;
    					
        	        	double xPos = x + (0.5 * this.world.rand.nextDouble()) - (0.5 * this.world.rand.nextDouble());
        	        	double zPos = z + (0.5 * this.world.rand.nextDouble()) - (0.5 * this.world.rand.nextDouble());
    	        		world.spawnParticle(EnumParticleTypes.REDSTONE, xPos, y, zPos, 0, 0, 0);
    	        	}
				}
			}
		}
	}
	
	public void wardArea(EnchantmentData enchantData, boolean isSecondary)
	{
		Enchantment enchant = enchantData.enchantment;
		int lvl = isSecondary ? 1 : enchantData.enchantmentLevel;
		int range = 5 + (lvl * 2);
		
		if(lvl > 5)
			range = 15;
		
		BlockPos pos1 = this.getPos().add(-range, -range, -range);
		BlockPos pos2 = this.getPos().add(range, range, range);
		
		AxisAlignedBB wardArea = new AxisAlignedBB(pos1, pos2);
		
		for(EntityPlayer player : this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, wardArea))
		{
			player.addPotionEffect(new PotionEffect(WardEffect.byEnchant(enchant), 20, lvl - 1));
		}
	}
	
	public boolean canWard()
	{
		boolean flag = true;
		int range = 15;
		BlockPos pos1 = this.getPos().add(-range, -range, -range);
		BlockPos pos2 = this.getPos().add(range, range, range);
		
		for(BlockPos pos : BlockPos.getAllInBoxMutable(pos1, pos2))
		{
			if(!Objects.equal(pos, this.getPos()))
			{
				if(world.getBlockState(pos).getBlock() instanceof BlockWard)
				{
					if(((TileEntityWard)world.getTileEntity(pos)).getBook() != ItemStack.EMPTY)
					{
						flag = false;
						
						//pulses every 2 seconds to show where the interfering ward is
						if(world.isRemote && world.getTotalWorldTime() % 40 == 0)
						{
							double xDiff = pos.getX() - this.getPos().getX();
							double yDiff = pos.getY() - this.getPos().getY();
							double zDiff = pos.getZ() - this.getPos().getZ();
							
							for(int i = 1; i < 9; i++)
							{
								double x = this.getPos().getX() + (xDiff / i) + 0.5;
								double y = this.getPos().getY() + (yDiff / i) + 0.5;
								double z = this.getPos().getZ() + (zDiff / i) + 0.5;
								world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0, 0, 0);
							}
						}
					}
				}
			}
		}
		
		return flag;
	}
	
	//taken from TileEntityEnchantmentTable
	public void rotateBook()
	{
		Random rand = this.getWorld().rand;
		
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        this.tRot += 0.02F;
        
        if(canWard())
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
        this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
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
        	this.book = new ItemStack(Item.getByNameOrId(compound.getString("Item")), 1, compound.getInteger("Data"));
        }
        else
        {
        	this.book = ItemStack.EMPTY;
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
            compound.setInteger("Data", book.getMetadata());
        }
		
		return compound;
	}
	
	@Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 7, this.writeToNBT(new NBTTagCompound()));
    }
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
		this.readFromNBT(pkt.getNbtCompound());
    }
	
	@Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }
	
	public void updateTE()
	{
		IBlockState state = world.getBlockState(this.getPos());
		this.world.notifyBlockUpdate(this.getPos(), state, state, 3);
		this.world.markBlockRangeForRenderUpdate(pos, pos);
		this.world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
		this.markDirty();
	}
}
