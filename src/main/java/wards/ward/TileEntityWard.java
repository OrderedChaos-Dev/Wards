package wards.ward;

import javax.annotation.Nullable;

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
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import wards.effect.WardEffect;

public class TileEntityWard extends TileEntity implements ITickable
{
	private ItemStack book;
	
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
		if(this.book.getItem() instanceof ItemEnchantedBook)
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
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
        if(compound.hasKey("Book"))
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
}
