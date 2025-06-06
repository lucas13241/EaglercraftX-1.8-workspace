package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

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
public class ContainerPlayer extends Container {
	/**+
	 * The crafting matrix inventory.
	 */
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
	public IInventory craftResult = new InventoryCraftResult();
	public boolean isLocalWorld;
	private final EntityPlayer thePlayer;

	public ContainerPlayer(final InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
		this.isLocalWorld = localWorld;
		this.thePlayer = player;
		this.addSlotToContainer(
				new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));

		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18, 26 + i * 18));
			}
		}

		for (int k = 0; k < 4; ++k) {
			final int k2 = k;
			this.addSlotToContainer(
					new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - k, 8, 8 + k * 18) {
						public int getSlotStackLimit() {
							return 1;
						}

						public boolean isItemValid(ItemStack itemstack) {
							return itemstack == null ? false
									: (itemstack.getItem() instanceof ItemArmor
											? ((ItemArmor) itemstack.getItem()).armorType == k2
											: (itemstack.getItem() != Item.getItemFromBlock(Blocks.pumpkin)
													&& itemstack.getItem() != Items.skull ? false : k2 == 0));
						}

						public String getSlotTexture() {
							return ItemArmor.EMPTY_SLOT_NAMES[k2];
						}
					});
		}

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
		}
		
		this.addSlotToContainer(new Slot(playerInventory, 150, 77, 62));

		this.onCraftMatrixChanged(this.craftMatrix);
	}

	/**+
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftMatrixChanged(IInventory var1) {
		this.craftResult.setInventorySlotContents(0,
				CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
	}

	/**+
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer entityplayer) {
		super.onContainerClosed(entityplayer);

		for (int i = 0; i < 4; ++i) {
			ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);
			if (itemstack != null) {
				entityplayer.dropPlayerItemWithRandomChoice(itemstack, false);
			}
		}

		this.craftResult.setInventorySlotContents(0, (ItemStack) null);
	}

	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	/**+
	 * Take a stack from the specified inventory slot.
	 */
	public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i == 0) {
				if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (i >= 1 && i < 5) {
				if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
					return null;
				}
			} else if (i >= 5 && i < 9) {
				if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
					return null;
				}
			} else if (itemstack.getItem() instanceof ItemArmor
					&& !((Slot) this.inventorySlots.get(5 + ((ItemArmor) itemstack.getItem()).armorType))
							.getHasStack()) {
				int j = 5 + ((ItemArmor) itemstack.getItem()).armorType;
				if (!this.mergeItemStack(itemstack1, j, j + 1, false)) {
					return null;
				}
			} else if (i >= 9 && i < 36) {
				if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
					return null;
				}
			} else if (i >= 36 && i < 45) {
				if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(entityplayer, itemstack1);
		}

		return itemstack;
	}

	/**+
	 * Called to determine if the current slot is valid for the
	 * stack merging (double-click) code. The stack passed in is
	 * null for the initial slot that was double-clicked.
	 */
	public boolean canMergeSlot(ItemStack itemstack, Slot slot) {
		return slot.inventory != this.craftResult && super.canMergeSlot(itemstack, slot);
	}
}
