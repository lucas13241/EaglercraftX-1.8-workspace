package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.BlockVertexIDs;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DeferredStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class BlockFluidRenderer {
	private EaglerTextureAtlasSprite[] atlasSpritesLava = new EaglerTextureAtlasSprite[2];
	private EaglerTextureAtlasSprite[] atlasSpritesWater = new EaglerTextureAtlasSprite[2];

	public BlockFluidRenderer() {
		this.initAtlasSprites();
	}

	protected void initAtlasSprites() {
		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
		this.atlasSpritesLava[0] = texturemap.getAtlasSprite("minecraft:blocks/lava_still");
		this.atlasSpritesLava[1] = texturemap.getAtlasSprite("minecraft:blocks/lava_flow");
		this.atlasSpritesWater[0] = texturemap.getAtlasSprite("minecraft:blocks/water_still");
		this.atlasSpritesWater[1] = texturemap.getAtlasSprite("minecraft:blocks/water_flow");
	}

	public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn,
			WorldRenderer worldRendererIn) {
		BlockPos tmp = new BlockPos(0, 0, 0);
		boolean deferred = DeferredStateManager.isDeferredRenderer();
		boolean isDynamicLights = deferred;// || DynamicLightsStateManager.isDynamicLightsRender();
		BlockLiquid blockliquid = (BlockLiquid) blockStateIn.getBlock();
		boolean lava = blockliquid.getMaterial() == Material.lava;
		boolean realistic = !lava && DeferredStateManager.isRenderingRealisticWater();
		blockliquid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
		EaglerTextureAtlasSprite[] atextureatlassprite = lava ? this.atlasSpritesLava : this.atlasSpritesWater;
		int i = blockliquid.colorMultiplier(blockAccess, blockPosIn);
		float f = (float) (i >> 16 & 255) / 255.0F;
		float f1 = (float) (i >> 8 & 255) / 255.0F;
		float f2 = (float) (i & 255) / 255.0F;
		boolean flag = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.up(tmp), EnumFacing.UP);
		if (realistic && blockStateIn.getValue(BlockLiquid.LEVEL).intValue() == 0) {
			Block blockUp = blockAccess.getBlockState(blockPosIn.up(tmp)).getBlock();
			flag &= !blockUp.isFullCube() || !blockUp.isBlockSolid(blockAccess, blockPosIn.up(tmp), EnumFacing.DOWN);
		}
		boolean flag1 = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.down(tmp), EnumFacing.DOWN);
		boolean[] aboolean = new boolean[] {
				blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.north(tmp), EnumFacing.NORTH),
				blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.south(tmp), EnumFacing.SOUTH),
				blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.west(tmp), EnumFacing.WEST),
				blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.east(tmp), EnumFacing.EAST) };
		if (!flag && !flag1 && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3]) {
			return false;
		} else {
			boolean flag2 = false;
			float f3 = 0.5F;
			float f4 = 1.0F;
			float f5 = 0.8F;
			float f6 = 0.6F;
			Material material = blockliquid.getMaterial();
			float f7 = this.getFluidHeight(blockAccess, blockPosIn, material);
			float f8 = this.getFluidHeight(blockAccess, blockPosIn.south(tmp), material);
			float f9 = this.getFluidHeight(blockAccess, blockPosIn.east(tmp).south(tmp), material);
			float f10 = this.getFluidHeight(blockAccess, blockPosIn.east(tmp), material);
			double d0 = (double) blockPosIn.x;
			double d1 = (double) blockPosIn.y;
			double d2 = (double) blockPosIn.z;
			float f11 = 0.001F;
			if (flag) {
				flag2 = true;
				EaglerTextureAtlasSprite textureatlassprite = atextureatlassprite[0];
				float f12 = (float) BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material);
				if (f12 > -999.0F) {
					textureatlassprite = atextureatlassprite[1];
				}

				f7 -= f11;
				f8 -= f11;
				f9 -= f11;
				f10 -= f11;
				float f13;
				float f14;
				float f15;
				float f16;
				float f17;
				float f18;
				float f19;
				float f20;
				if (realistic || f12 < -999.0F) {
					f13 = realistic ? (f12 < -999.0F ? 0.0f : MathHelper.sin(f12))
							: textureatlassprite.getInterpolatedU(0.0D);
					f17 = realistic ? (f12 < -999.0F ? 0.0f : -MathHelper.cos(f12))
							: textureatlassprite.getInterpolatedV(0.0D);
					f14 = f13;
					f18 = realistic ? f17 : textureatlassprite.getInterpolatedV(16.0D);
					f15 = realistic ? f13 : textureatlassprite.getInterpolatedU(16.0D);
					f19 = f18;
					f16 = f15;
					f20 = f17;
				} else {
					float f21 = MathHelper.sin(f12) * 0.25F;
					float f22 = MathHelper.cos(f12) * 0.25F;
					float f23 = 8.0F;
					f13 = textureatlassprite.getInterpolatedU((double) (8.0F + (-f22 - f21) * 16.0F));
					f17 = textureatlassprite.getInterpolatedV((double) (8.0F + (-f22 + f21) * 16.0F));
					f14 = textureatlassprite.getInterpolatedU((double) (8.0F + (-f22 + f21) * 16.0F));
					f18 = textureatlassprite.getInterpolatedV((double) (8.0F + (f22 + f21) * 16.0F));
					f15 = textureatlassprite.getInterpolatedU((double) (8.0F + (f22 + f21) * 16.0F));
					f19 = textureatlassprite.getInterpolatedV((double) (8.0F + (f22 - f21) * 16.0F));
					f16 = textureatlassprite.getInterpolatedU((double) (8.0F + (f22 - f21) * 16.0F));
					f20 = textureatlassprite.getInterpolatedV((double) (8.0F + (-f22 - f21) * 16.0F));
				}

				int k2 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn);
				int l2 = k2 >> 16 & '\uffff';
				int i3 = k2 & '\uffff';
				float f24 = f4 * f;
				float f25 = f4 * f1;
				float f26 = f4 * f2;
				worldRendererIn.pos(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D).color(f24, f25, f26, 1.0F)
						.tex((double) f13, (double) f17).lightmap(l2, i3).endVertex();
				worldRendererIn.pos(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D).color(f24, f25, f26, 1.0F)
						.tex((double) f14, (double) f18).lightmap(l2, i3).endVertex();
				worldRendererIn.pos(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D).color(f24, f25, f26, 1.0F)
						.tex((double) f15, (double) f19).lightmap(l2, i3).endVertex();
				worldRendererIn.pos(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D).color(f24, f25, f26, 1.0F)
						.tex((double) f16, (double) f20).lightmap(l2, i3).endVertex();
				if (isDynamicLights)
					worldRendererIn.genNormals(true, f12 <= -999.0F ? BlockVertexIDs.builtin_water_still_vertex_id
							: BlockVertexIDs.builtin_water_flow_vertex_id);

				if (blockliquid.func_176364_g(blockAccess, blockPosIn.up(tmp))) {
					worldRendererIn.pos(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D).color(f24, f25, f26, 1.0F)
							.tex((double) f13, (double) f17).lightmap(l2, i3).endVertex();
					worldRendererIn.pos(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D).color(f24, f25, f26, 1.0F)
							.tex((double) f16, (double) f20).lightmap(l2, i3).endVertex();
					worldRendererIn.pos(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D).color(f24, f25, f26, 1.0F)
							.tex((double) f15, (double) f19).lightmap(l2, i3).endVertex();
					worldRendererIn.pos(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D).color(f24, f25, f26, 1.0F)
							.tex((double) f14, (double) f18).lightmap(l2, i3).endVertex();
					if (isDynamicLights)
						worldRendererIn.genNormals(true, f12 <= -999.0F ? BlockVertexIDs.builtin_water_still_vertex_id
								: BlockVertexIDs.builtin_water_flow_vertex_id);
				}
			}

			if (flag1) {
				float f35 = realistic ? 0.0f : atextureatlassprite[0].getMinU();
				float f36 = realistic ? 0.0f : atextureatlassprite[0].getMaxU();
				float f37 = realistic ? 0.0f : atextureatlassprite[0].getMinV();
				float f38 = realistic ? 0.0f : atextureatlassprite[0].getMaxV();
				int l1 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down(tmp));
				int i2 = l1 >> 16 & '\uffff';
				int j2 = l1 & '\uffff';
				worldRendererIn.pos(d0, d1, d2 + 1.0D).color(f3, f3, f3, 1.0F).tex((double) f35, (double) f38)
						.lightmap(i2, j2).endVertex();
				worldRendererIn.pos(d0, d1, d2).color(f3, f3, f3, 1.0F).tex((double) f35, (double) f37).lightmap(i2, j2)
						.endVertex();
				worldRendererIn.pos(d0 + 1.0D, d1, d2).color(f3, f3, f3, 1.0F).tex((double) f36, (double) f37)
						.lightmap(i2, j2).endVertex();
				worldRendererIn.pos(d0 + 1.0D, d1, d2 + 1.0D).color(f3, f3, f3, 1.0F).tex((double) f36, (double) f38)
						.lightmap(i2, j2).endVertex();
				if (isDynamicLights)
					worldRendererIn.putNormal(0.0f, -1.0f, 0.0f, BlockVertexIDs.builtin_water_still_vertex_id);
				flag2 = true;
			}

			for (int i1 = 0; i1 < 4; ++i1) {
				int j1 = 0;
				int k1 = 0;
				if (i1 == 0) {
					--k1;
				}

				if (i1 == 1) {
					++k1;
				}

				if (i1 == 2) {
					--j1;
				}

				if (i1 == 3) {
					++j1;
				}

				BlockPos blockpos = blockPosIn.add(j1, 0, k1);
				EaglerTextureAtlasSprite textureatlassprite1 = atextureatlassprite[1];
				if (aboolean[i1]) {
					float f39;
					float f40;
					double d3;
					double d4;
					double d5;
					double d6;
					if (i1 == 0) {
						f39 = f7;
						f40 = f10;
						d3 = d0;
						d5 = d0 + 1.0D;
						d4 = d2 + (double) f11;
						d6 = d2 + (double) f11;
					} else if (i1 == 1) {
						f39 = f9;
						f40 = f8;
						d3 = d0 + 1.0D;
						d5 = d0;
						d4 = d2 + 1.0D - (double) f11;
						d6 = d2 + 1.0D - (double) f11;
					} else if (i1 == 2) {
						f39 = f8;
						f40 = f7;
						d3 = d0 + (double) f11;
						d5 = d0 + (double) f11;
						d4 = d2 + 1.0D;
						d6 = d2;
					} else {
						f39 = f10;
						f40 = f9;
						d3 = d0 + 1.0D - (double) f11;
						d5 = d0 + 1.0D - (double) f11;
						d4 = d2;
						d6 = d2 + 1.0D;
					}

					flag2 = true;
					float f41 = realistic ? 1.0f : textureatlassprite1.getInterpolatedU(0.0D);
					float f27 = realistic ? 1.0f : textureatlassprite1.getInterpolatedU(8.0D);
					float f28 = realistic ? 0.0f
							: textureatlassprite1.getInterpolatedV((double) ((1.0F - f39) * 16.0F * 0.5F));
					float f29 = realistic ? 0.0f
							: textureatlassprite1.getInterpolatedV((double) ((1.0F - f40) * 16.0F * 0.5F));
					float f30 = realistic ? 0.0f : textureatlassprite1.getInterpolatedV(8.0D);
					int j = blockliquid.getMixedBrightnessForBlock(blockAccess, blockpos);
					int k = j >> 16 & '\uffff';
					int l = j & '\uffff';
					float f31 = i1 < 2 ? f5 : f6;
					float f32 = f4 * f31 * f;
					float f33 = f4 * f31 * f1;
					float f34 = f4 * f31 * f2;
					worldRendererIn.pos(d3, d1 + (double) f39, d4).color(f32, f33, f34, 1.0F)
							.tex((double) f41, (double) f28).lightmap(k, l).endVertex();
					worldRendererIn.pos(d5, d1 + (double) f40, d6).color(f32, f33, f34, 1.0F)
							.tex((double) f27, (double) f29).lightmap(k, l).endVertex();
					worldRendererIn.pos(d5, d1 + 0.0D, d6).color(f32, f33, f34, 1.0F).tex((double) f27, (double) f30)
							.lightmap(k, l).endVertex();
					worldRendererIn.pos(d3, d1 + 0.0D, d4).color(f32, f33, f34, 1.0F).tex((double) f41, (double) f30)
							.lightmap(k, l).endVertex();
					if (isDynamicLights)
						worldRendererIn.putNormal(j1, 0.0f, k1, BlockVertexIDs.builtin_water_flow_vertex_id);
					if (!realistic) {
						worldRendererIn.pos(d3, d1 + 0.0D, d4).color(f32, f33, f34, 1.0F)
								.tex((double) f41, (double) f30).lightmap(k, l).endVertex();
						worldRendererIn.pos(d5, d1 + 0.0D, d6).color(f32, f33, f34, 1.0F)
								.tex((double) f27, (double) f30).lightmap(k, l).endVertex();
						worldRendererIn.pos(d5, d1 + (double) f40, d6).color(f32, f33, f34, 1.0F)
								.tex((double) f27, (double) f29).lightmap(k, l).endVertex();
						worldRendererIn.pos(d3, d1 + (double) f39, d4).color(f32, f33, f34, 1.0F)
								.tex((double) f41, (double) f28).lightmap(k, l).endVertex();
						if (isDynamicLights)
							worldRendererIn.putNormal(-j1, 0.0f, -k1, BlockVertexIDs.builtin_water_flow_vertex_id);
					}
				}
			}

			return flag2;
		}
	}

	private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; ++j) {
			BlockPos blockpos = blockPosIn.add(-(j & 1), 0, -(j >> 1 & 1));
			if (blockAccess.getBlockState(blockpos.up()).getBlock().getMaterial() == blockMaterial) {
				return 1.0F;
			}

			IBlockState iblockstate = blockAccess.getBlockState(blockpos);
			Material material = iblockstate.getBlock().getMaterial();
			if (material != blockMaterial) {
				if (!material.isSolid()) {
					++f;
					++i;
				}
			} else {
				int k = ((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue();
				if (k >= 8 || k == 0) {
					f += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
					i += 10;
				}

				f += BlockLiquid.getLiquidHeightPercent(k);
				++i;
			}
		}

		return 1.0F - f / (float) i;
	}
}