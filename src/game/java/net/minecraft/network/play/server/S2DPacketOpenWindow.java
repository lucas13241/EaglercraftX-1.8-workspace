package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

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
public class S2DPacketOpenWindow implements Packet<INetHandlerPlayClient> {
	private int windowId;
	private String inventoryType;
	private IChatComponent windowTitle;
	private int slotCount;
	private int entityId;

	public S2DPacketOpenWindow() {
	}

	public S2DPacketOpenWindow(int incomingWindowId, String incomingWindowTitle, IChatComponent windowTitleIn) {
		this(incomingWindowId, incomingWindowTitle, windowTitleIn, 0);
	}

	public S2DPacketOpenWindow(int windowIdIn, String guiId, IChatComponent windowTitleIn, int slotCountIn) {
		this.windowId = windowIdIn;
		this.inventoryType = guiId;
		this.windowTitle = windowTitleIn;
		this.slotCount = slotCountIn;
	}

	public S2DPacketOpenWindow(int windowIdIn, String guiId, IChatComponent windowTitleIn, int slotCountIn,
			int incomingEntityId) {
		this(windowIdIn, guiId, windowTitleIn, slotCountIn);
		this.entityId = incomingEntityId;
	}

	/**+
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient inethandlerplayclient) {
		inethandlerplayclient.handleOpenWindow(this);
	}

	/**+
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer parPacketBuffer) throws IOException {
		this.windowId = parPacketBuffer.readUnsignedByte();
		this.inventoryType = parPacketBuffer.readStringFromBuffer(32);
		this.windowTitle = parPacketBuffer.readChatComponent();
		this.slotCount = parPacketBuffer.readUnsignedByte();
		if (this.inventoryType.equals("EntityHorse")) {
			this.entityId = parPacketBuffer.readInt();
		}

	}

	/**+
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer parPacketBuffer) throws IOException {
		parPacketBuffer.writeByte(this.windowId);
		parPacketBuffer.writeString(this.inventoryType);
		parPacketBuffer.writeChatComponent(this.windowTitle);
		parPacketBuffer.writeByte(this.slotCount);
		if (this.inventoryType.equals("EntityHorse")) {
			parPacketBuffer.writeInt(this.entityId);
		}

	}

	public int getWindowId() {
		return this.windowId;
	}

	public String getGuiId() {
		return this.inventoryType;
	}

	public IChatComponent getWindowTitle() {
		return this.windowTitle;
	}

	public int getSlotCount() {
		return this.slotCount;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public boolean hasSlots() {
		return this.slotCount > 0;
	}
}