package wards.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.EnchantmentTableTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import wards.block.WardTileEntity;

@OnlyIn(Dist.CLIENT)
public class WardTileEntityRenderer extends TileEntityRenderer<WardTileEntity> {
	public static final RenderMaterial TEXTURE_BOOK = EnchantmentTableTileEntityRenderer.BOOK_LOCATION;
	private final BookModel modelBook = new BookModel();

	public WardTileEntityRenderer(TileEntityRendererDispatcher te_rd) {
		super(te_rd);
	}

	@Override
	public void render(WardTileEntity ward, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		matrixStackIn.pushPose();
		if(!ward.getBook().isEmpty()) {
			matrixStackIn.translate(0.5D, 0.85D, 0.5D);
			float f = (float) ward.tickCount + partialTicks;
			matrixStackIn.translate(0.0D, (double) (0.1F + MathHelper.sin(f * 0.1F) * 0.01F), 0.0D);

			float f1;
			for (f1 = ward.bookRotation - ward.bookRotationPrev; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
				;
			}

			while (f1 < -(float) Math.PI) {
				f1 += ((float) Math.PI * 2F);
			}

			float f2 = ward.bookRotationPrev + f1 * partialTicks;
			matrixStackIn.mulPose(Vector3f.YP.rotation(-f2));
			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
			float f3 = MathHelper.lerp(partialTicks, ward.pageFlipPrev, ward.pageFlip);
			float f4 = MathHelper.frac(f3 + 0.25F) * 1.6F - 0.3F;
			float f5 = MathHelper.frac(f3 + 0.75F) * 1.6F - 0.3F;
			float f6 = MathHelper.lerp(partialTicks, ward.bookSpreadPrev, ward.bookSpread);
			this.modelBook.setupAnim(f, MathHelper.clamp(f4, 0.0F, 1.0F), MathHelper.clamp(f5, 0.0F, 1.0F), f6);
			IVertexBuilder ivertexbuilder = TEXTURE_BOOK.buffer(bufferIn, RenderType::entitySolid);
			this.modelBook.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
		}
		matrixStackIn.popPose();
	}
}