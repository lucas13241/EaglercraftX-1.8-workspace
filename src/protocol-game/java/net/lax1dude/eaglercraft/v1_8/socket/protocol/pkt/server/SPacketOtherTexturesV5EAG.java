/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class SPacketOtherTexturesV5EAG implements GameMessagePacket {

	public int requestId;
	public int skinID;
	public byte[] customSkin;
	public int capeID;
	public byte[] customCape;

	public SPacketOtherTexturesV5EAG() {
	}

	public SPacketOtherTexturesV5EAG(int requestId, int skinID, byte[] customSkin, int capeID, byte[] customCape) {
		this.requestId = requestId;
		this.skinID = skinID;
		this.customSkin = customSkin;
		this.capeID = capeID;
		this.customCape = customCape;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		requestId = buffer.readVarInt();
		skinID = buffer.readVarInt();
		capeID = buffer.readVarInt();
		if (skinID < 0) {
			customSkin = new byte[12288];
			buffer.readFully(customSkin);
		}
		if (capeID < 0) {
			customCape = new byte[1173];
			buffer.readFully(customCape);
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		buffer.writeVarInt(requestId);
		buffer.writeVarInt(skinID);
		buffer.writeVarInt(capeID);
		if (skinID < 0) {
			if (customSkin.length != 12288) {
				throw new IOException("Custom skin data length is not 12288 bytes! (" + customSkin.length + ")");
			}
			buffer.write(customSkin);
		}
		if (capeID < 0) {
			if (customCape.length != 1173) {
				throw new IOException("Custom cape data length is not 1173 bytes! (" + customCape.length + ")");
			}
			buffer.write(customCape);
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int i = GamePacketOutputBuffer.getVarIntSize(skinID) + GamePacketOutputBuffer.getVarIntSize(capeID)
				+ GamePacketOutputBuffer.getVarIntSize(requestId);
		if (skinID < 0)
			i += 12288;
		if (capeID < 0)
			i += 1173;
		return i;
	}

}
