package wards.client;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import wards.block.TileEntityWard;

public class TileEntityWardRenderer  extends TileEntitySpecialRenderer<TileEntityWard>
{
    private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private final ModelBook modelBook = new ModelBook();
    
	@Override
    public void render(TileEntityWard ward, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        if(ward.getBook() != ItemStack.EMPTY)
        {
            GlStateManager.translate((float)x + 0.5F, (float)y + 0.85F, (float)z + 0.5F);
            float f = (float)ward.tickCount + partialTicks;
            GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);
            float f1;

            for (f1 = ward.bookRotation - ward.bookRotationPrev; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F))
            {
                ;
            }

            while (f1 < -(float)Math.PI)
            {
                f1 += ((float)Math.PI * 2F);
            }

            float f2 = ward.bookRotationPrev + f1 * partialTicks;
            GlStateManager.rotate(-f2 * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
            
            this.bindTexture(TEXTURE_BOOK);
            float f3 = ward.pageFlipPrev + (ward.pageFlip - ward.pageFlipPrev) * partialTicks + 0.25F;
            float f4 = ward.pageFlipPrev + (ward.pageFlip - ward.pageFlipPrev) * partialTicks + 0.75F;
            f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
            f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;

            if (f3 < 0.0F)
            {
                f3 = 0.0F;
            }

            if (f4 < 0.0F)
            {
                f4 = 0.0F;
            }

            if (f3 > 1.0F)
            {
                f3 = 1.0F;
            }

            if (f4 > 1.0F)
            {
                f4 = 1.0F;
            }

            float f5 = ward.bookSpreadPrev + (ward.bookSpread - ward.bookSpreadPrev) * partialTicks;
            GlStateManager.enableCull();
            this.modelBook.render((Entity)null, f, f3, f4, f5, 0.0F, 0.0625F);
        }
        GlStateManager.popMatrix();
    }
}
