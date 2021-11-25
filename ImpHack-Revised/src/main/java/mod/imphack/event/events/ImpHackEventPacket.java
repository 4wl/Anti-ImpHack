package mod.imphack.event.events;

import mod.imphack.event.ImpHackEventCancellable;
import net.minecraft.network.Packet;

public class ImpHackEventPacket extends ImpHackEventCancellable {
	private final Packet<?> packet;

	public ImpHackEventPacket(Packet<?> packet) {
		super();

		this.packet = packet;
	}

	public Packet<?> get_packet() {
		return this.packet;
	}

	public static class ReceivePacket extends ImpHackEventPacket {
		public ReceivePacket(Packet<?> packet) {
			super(packet);
		}
	}

	public static class SendPacket extends ImpHackEventPacket {
		public SendPacket(Packet<?> packet) {
			super(packet);
		}

		
	}
}