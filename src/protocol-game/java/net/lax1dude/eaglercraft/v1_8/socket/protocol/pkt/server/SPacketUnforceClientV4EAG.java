/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

public class SPacketUnforceClientV4EAG implements GameMessagePacket {

	public boolean resetSkin;
	public boolean resetCape;
	public boolean resetFNAW;

	public SPacketUnforceClientV4EAG() {
	}

	public SPacketUnforceClientV4EAG(boolean resetSkin, boolean resetCape, boolean resetFNAW) {
		this.resetSkin = resetSkin;
		this.resetCape = resetCape;
		this.resetFNAW = resetFNAW;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		int i = buffer.readUnsignedByte();
		resetSkin = (i & 1) != 0;
		resetCape = (i & 2) != 0;
		resetFNAW = (i & 4) != 0;
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		int i = 0;
		if (resetSkin)
			i |= 1;
		if (resetCape)
			i |= 2;
		if (resetFNAW)
			i |= 4;
		buffer.writeByte(i);
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 1;
	}

}
