package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

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
public class S01PacketJoinGame implements Packet<INetHandlerPlayClient> {
	private int entityId;
	private boolean hardcoreMode;
	private WorldSettings.GameType gameType;
	private int dimension;
	private EnumDifficulty difficulty;
	private int maxPlayers;
	private WorldType worldType;
	private boolean reducedDebugInfo;

	public S01PacketJoinGame() {
	}

	public S01PacketJoinGame(int entityIdIn, WorldSettings.GameType gameTypeIn, boolean hardcoreModeIn, int dimensionIn,
			EnumDifficulty difficultyIn, int maxPlayersIn, WorldType worldTypeIn, boolean reducedDebugInfoIn) {
		this.entityId = entityIdIn;
		this.dimension = dimensionIn;
		this.difficulty = difficultyIn;
		this.gameType = gameTypeIn;
		this.maxPlayers = maxPlayersIn;
		this.hardcoreMode = hardcoreModeIn;
		this.worldType = worldTypeIn;
		this.reducedDebugInfo = reducedDebugInfoIn;
	}

	/**+
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer parPacketBuffer) throws IOException {
		this.entityId = parPacketBuffer.readInt();
		int i = parPacketBuffer.readUnsignedByte();
		this.hardcoreMode = (i & 8) == 8;
		i = i & -9;
		this.gameType = WorldSettings.GameType.getByID(i);
		this.dimension = parPacketBuffer.readByte();
		this.difficulty = EnumDifficulty.getDifficultyEnum(parPacketBuffer.readUnsignedByte());
		this.maxPlayers = parPacketBuffer.readUnsignedByte();
		this.worldType = WorldType.parseWorldType(parPacketBuffer.readStringFromBuffer(16));
		if (this.worldType == null) {
			this.worldType = WorldType.DEFAULT;
		}

		this.reducedDebugInfo = parPacketBuffer.readBoolean();
	}

	/**+
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer parPacketBuffer) throws IOException {
		parPacketBuffer.writeInt(this.entityId);
		int i = this.gameType.getID();
		if (this.hardcoreMode) {
			i |= 8;
		}

		parPacketBuffer.writeByte(i);
		parPacketBuffer.writeByte(this.dimension);
		parPacketBuffer.writeByte(this.difficulty.getDifficultyId());
		parPacketBuffer.writeByte(this.maxPlayers);
		parPacketBuffer.writeString(this.worldType.getWorldTypeName());
		parPacketBuffer.writeBoolean(this.reducedDebugInfo);
	}

	/**+
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient inethandlerplayclient) {
		inethandlerplayclient.handleJoinGame(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public boolean isHardcoreMode() {
		return this.hardcoreMode;
	}

	public WorldSettings.GameType getGameType() {
		return this.gameType;
	}

	public int getDimension() {
		return this.dimension;
	}

	public EnumDifficulty getDifficulty() {
		return this.difficulty;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public WorldType getWorldType() {
		return this.worldType;
	}

	public boolean isReducedDebugInfo() {
		return this.reducedDebugInfo;
	}
}