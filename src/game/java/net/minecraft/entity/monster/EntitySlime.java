package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

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
public class EntitySlime extends EntityLiving implements IMob {
	public float squishAmount;
	public float squishFactor;
	public float prevSquishFactor;
	private boolean wasOnGround;

	public EntitySlime(World worldIn) {
		super(worldIn);
		this.moveHelper = new EntitySlime.SlimeMoveHelper(this);
		this.tasks.addTask(1, new EntitySlime.AISlimeFloat(this));
		this.tasks.addTask(2, new EntitySlime.AISlimeAttack(this));
		this.tasks.addTask(3, new EntitySlime.AISlimeFaceRandom(this));
		this.tasks.addTask(5, new EntitySlime.AISlimeHop(this));
		this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
		this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 1));
	}

	protected void setSlimeSize(int size) {
		this.dataWatcher.updateObject(16, Byte.valueOf((byte) size));
		this.setSize(0.51000005F * (float) size, 0.51000005F * (float) size);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((double) (size * size));
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setBaseValue((double) (0.2F + 0.1F * (float) size));
		this.setHealth(this.getMaxHealth());
		this.experienceValue = size;
	}

	/**+
	 * Returns the size of the slime.
	 */
	public int getSlimeSize() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	/**+
	 * (abstract) Protected helper method to write subclass entity
	 * data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("Size", this.getSlimeSize() - 1);
		nbttagcompound.setBoolean("wasOnGround", this.wasOnGround);
	}

	/**+
	 * (abstract) Protected helper method to read subclass entity
	 * data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		int i = nbttagcompound.getInteger("Size");
		if (i < 0) {
			i = 0;
		}

		this.setSlimeSize(i + 1);
		this.wasOnGround = nbttagcompound.getBoolean("wasOnGround");
	}

	protected EnumParticleTypes getParticleType() {
		return EnumParticleTypes.SLIME;
	}

	/**+
	 * Returns the name of the sound played when the slime jumps.
	 */
	protected String getJumpSound() {
		return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
	}

	/**+
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL
				&& this.getSlimeSize() > 0) {
			this.isDead = true;
		}

		this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
		this.prevSquishFactor = this.squishFactor;
		super.onUpdate();
		if (this.onGround && !this.wasOnGround) {
			int i = this.getSlimeSize();

			for (int j = 0; j < i * 8; ++j) {
				float f = this.rand.nextFloat() * 3.1415927F * 2.0F;
				float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
				float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
				float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
				World world = this.worldObj;
				EnumParticleTypes enumparticletypes = this.getParticleType();
				double d0 = this.posX + (double) f2;
				double d1 = this.posZ + (double) f3;
				world.spawnParticle(enumparticletypes, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D,
						new int[0]);
			}

			if (this.makesSoundOnLand()) {
				this.playSound(this.getJumpSound(), this.getSoundVolume(),
						((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			}

			this.squishAmount = -0.5F;
		} else if (!this.onGround && this.wasOnGround) {
			this.squishAmount = 1.0F;
		}

		this.wasOnGround = this.onGround;
		this.alterSquishAmount();
	}

	protected void alterSquishAmount() {
		this.squishAmount *= 0.6F;
	}

	/**+
	 * Gets the amount of time the slime needs to wait between
	 * jumps.
	 */
	protected int getJumpDelay() {
		return this.rand.nextInt(20) + 10;
	}

	protected EntitySlime createInstance() {
		return new EntitySlime(this.worldObj);
	}

	public void onDataWatcherUpdate(int i) {
		if (i == 16) {
			int j = this.getSlimeSize();
			this.setSize(0.51000005F * (float) j, 0.51000005F * (float) j);
			this.rotationYaw = this.rotationYawHead;
			this.renderYawOffset = this.rotationYawHead;
			if (this.isInWater() && this.rand.nextInt(20) == 0) {
				this.resetHeight();
			}
		}

		super.onDataWatcherUpdate(i);
	}

	/**+
	 * Will get destroyed next tick.
	 */
	public void setDead() {
		int i = this.getSlimeSize();
		if (!this.worldObj.isRemote && i > 1 && this.getHealth() <= 0.0F) {
			int j = 2 + this.rand.nextInt(3);

			for (int k = 0; k < j; ++k) {
				float f = ((float) (k % 2) - 0.5F) * (float) i / 4.0F;
				float f1 = ((float) (k / 2) - 0.5F) * (float) i / 4.0F;
				EntitySlime entityslime = this.createInstance();
				if (this.hasCustomName()) {
					entityslime.setCustomNameTag(this.getCustomNameTag());
				}

				if (this.isNoDespawnRequired()) {
					entityslime.enablePersistence();
				}

				entityslime.setSlimeSize(i / 2);
				entityslime.setLocationAndAngles(this.posX + (double) f, this.posY + 0.5D, this.posZ + (double) f1,
						this.rand.nextFloat() * 360.0F, 0.0F);
				this.worldObj.spawnEntityInWorld(entityslime);
			}
		}

		super.setDead();
	}

	/**+
	 * Applies a velocity to each of the entities pushing them away
	 * from each other. Args: entity
	 */
	public void applyEntityCollision(Entity entity) {
		super.applyEntityCollision(entity);
		if (entity instanceof EntityIronGolem && this.canDamagePlayer()) {
			this.func_175451_e((EntityLivingBase) entity);
		}

	}

	/**+
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer(EntityPlayer entityplayer) {
		if (this.canDamagePlayer()) {
			this.func_175451_e(entityplayer);
		}

	}

	protected void func_175451_e(EntityLivingBase parEntityLivingBase) {
		int i = this.getSlimeSize();
		if (this.canEntityBeSeen(parEntityLivingBase)
				&& this.getDistanceSqToEntity(parEntityLivingBase) < 0.6D * (double) i * 0.6D * (double) i
				&& parEntityLivingBase.attackEntityFrom(DamageSource.causeMobDamage(this),
						(float) this.getAttackStrength())) {
			this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.applyEnchantments(this, parEntityLivingBase);
		}

	}

	public float getEyeHeight() {
		return 0.625F * this.height;
	}

	/**+
	 * Indicates weather the slime is able to damage the player
	 * (based upon the slime's size)
	 */
	protected boolean canDamagePlayer() {
		return this.getSlimeSize() > 1;
	}

	/**+
	 * Gets the amount of damage dealt to the player when "attacked"
	 * by the slime.
	 */
	protected int getAttackStrength() {
		return this.getSlimeSize();
	}

	/**+
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
	}

	/**+
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
	}

	protected Item getDropItem() {
		return this.getSlimeSize() == 1 ? Items.slime_ball : null;
	}

	/**+
	 * Checks if the entity's current position is a valid location
	 * to spawn this entity.
	 */
	public boolean getCanSpawnHere() {
		BlockPos blockpos = new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ));
		Chunk chunk = this.worldObj.getChunkFromBlockCoords(blockpos);
		if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT && this.rand.nextInt(4) != 1) {
			return false;
		} else {
			if (this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL) {
				BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos);
				if (biomegenbase == BiomeGenBase.swampland && this.posY > 50.0D && this.posY < 70.0D
						&& this.rand.nextFloat() < 0.5F
						&& this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor()
						&& this.worldObj.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8)) {
					return super.getCanSpawnHere();
				}

				if (this.rand.nextInt(10) == 0 && chunk.getRandomWithSeed(987234911L).nextInt(10) == 0
						&& this.posY < 40.0D) {
					return super.getCanSpawnHere();
				}
			}

			return false;
		}
	}

	/**+
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F * (float) this.getSlimeSize();
	}

	/**+
	 * The speed it takes to move the entityliving's rotationPitch
	 * through the faceEntity method. This is only currently use in
	 * wolves.
	 */
	public int getVerticalFaceSpeed() {
		return 0;
	}

	/**+
	 * Returns true if the slime makes a sound when it jumps (based
	 * upon the slime's size)
	 */
	protected boolean makesSoundOnJump() {
		return this.getSlimeSize() > 0;
	}

	/**+
	 * Returns true if the slime makes a sound when it lands after a
	 * jump (based upon the slime's size)
	 */
	protected boolean makesSoundOnLand() {
		return this.getSlimeSize() > 2;
	}

	/**+
	 * Causes this entity to do an upwards motion (jumping).
	 */
	protected void jump() {
		this.motionY = 0.41999998688697815D;
		this.isAirBorne = true;
	}

	/**+
	 * Called only once on an entity when first time spawned, via
	 * egg, mob spawner, natural spawning etc, but not called when
	 * entity is reloaded from nbt. Mainly used for initializing
	 * attributes and inventory
	 */
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficultyinstance,
			IEntityLivingData ientitylivingdata) {
		int i = this.rand.nextInt(3);
		if (i < 2 && this.rand.nextFloat() < 0.5F * difficultyinstance.getClampedAdditionalDifficulty()) {
			++i;
		}

		int j = 1 << i;
		this.setSlimeSize(j);
		return super.onInitialSpawn(difficultyinstance, ientitylivingdata);
	}

	static class AISlimeAttack extends EntityAIBase {
		private EntitySlime slime;
		private int field_179465_b;

		public AISlimeAttack(EntitySlime parEntitySlime) {
			this.slime = parEntitySlime;
			this.setMutexBits(2);
		}

		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
			return entitylivingbase == null ? false
					: (!entitylivingbase.isEntityAlive() ? false
							: !(entitylivingbase instanceof EntityPlayer)
									|| !((EntityPlayer) entitylivingbase).capabilities.disableDamage);
		}

		public void startExecuting() {
			this.field_179465_b = 300;
			super.startExecuting();
		}

		public boolean continueExecuting() {
			EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
			return entitylivingbase == null ? false
					: (!entitylivingbase.isEntityAlive() ? false
							: (entitylivingbase instanceof EntityPlayer
									&& ((EntityPlayer) entitylivingbase).capabilities.disableDamage ? false
											: --this.field_179465_b > 0));
		}

		public void updateTask() {
			this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
			((EntitySlime.SlimeMoveHelper) this.slime.getMoveHelper()).func_179920_a(this.slime.rotationYaw,
					this.slime.canDamagePlayer());
		}
	}

	static class AISlimeFaceRandom extends EntityAIBase {
		private EntitySlime slime;
		private float field_179459_b;
		private int field_179460_c;

		public AISlimeFaceRandom(EntitySlime parEntitySlime) {
			this.slime = parEntitySlime;
			this.setMutexBits(2);
		}

		public boolean shouldExecute() {
			return this.slime.getAttackTarget() == null
					&& (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava());
		}

		public void updateTask() {
			if (--this.field_179460_c <= 0) {
				this.field_179460_c = 40 + this.slime.getRNG().nextInt(60);
				this.field_179459_b = (float) this.slime.getRNG().nextInt(360);
			}

			((EntitySlime.SlimeMoveHelper) this.slime.getMoveHelper()).func_179920_a(this.field_179459_b, false);
		}
	}

	static class AISlimeFloat extends EntityAIBase {
		private EntitySlime slime;

		public AISlimeFloat(EntitySlime parEntitySlime) {
			this.slime = parEntitySlime;
			this.setMutexBits(5);
			((PathNavigateGround) parEntitySlime.getNavigator()).setCanSwim(true);
		}

		public boolean shouldExecute() {
			return this.slime.isInWater() || this.slime.isInLava();
		}

		public void updateTask() {
			if (this.slime.getRNG().nextFloat() < 0.8F) {
				this.slime.getJumpHelper().setJumping();
			}

			((EntitySlime.SlimeMoveHelper) this.slime.getMoveHelper()).setSpeed(1.2D);
		}
	}

	static class AISlimeHop extends EntityAIBase {
		private EntitySlime slime;

		public AISlimeHop(EntitySlime parEntitySlime) {
			this.slime = parEntitySlime;
			this.setMutexBits(5);
		}

		public boolean shouldExecute() {
			return true;
		}

		public void updateTask() {
			((EntitySlime.SlimeMoveHelper) this.slime.getMoveHelper()).setSpeed(1.0D);
		}
	}

	static class SlimeMoveHelper extends EntityMoveHelper {
		private float field_179922_g;
		private int field_179924_h;
		private EntitySlime slime;
		private boolean field_179923_j;

		public SlimeMoveHelper(EntitySlime parEntitySlime) {
			super(parEntitySlime);
			this.slime = parEntitySlime;
		}

		public void func_179920_a(float parFloat1, boolean parFlag) {
			this.field_179922_g = parFloat1;
			this.field_179923_j = parFlag;
		}

		public void setSpeed(double speedIn) {
			this.speed = speedIn;
			this.update = true;
		}

		public void onUpdateMoveHelper() {
			this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.field_179922_g, 30.0F);
			this.entity.rotationYawHead = this.entity.rotationYaw;
			this.entity.renderYawOffset = this.entity.rotationYaw;
			if (!this.update) {
				this.entity.setMoveForward(0.0F);
			} else {
				this.update = false;
				if (this.entity.onGround) {
					this.entity.setAIMoveSpeed((float) (this.speed * this.entity
							.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
					if (this.field_179924_h-- <= 0) {
						this.field_179924_h = this.slime.getJumpDelay();
						if (this.field_179923_j) {
							this.field_179924_h /= 3;
						}

						this.slime.getJumpHelper().setJumping();
						if (this.slime.makesSoundOnJump()) {
							this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(),
									((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F)
											* 0.8F);
						}
					} else {
						this.slime.moveStrafing = this.slime.moveForward = 0.0F;
						this.entity.setAIMoveSpeed(0.0F);
					}
				} else {
					this.entity.setAIMoveSpeed((float) (this.speed * this.entity
							.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
				}

			}
		}
	}
}