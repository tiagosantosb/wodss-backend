package ch.fhnw.wodss.backend.messaging;

import java.util.UUID;

import lombok.Data;

@Data
public class UpdateMessage {
	private MessageType type;
	private String uuid;
	private Object object;
	
	public UpdateMessage(MessageType type, Object object) {
		this.type = type;
		this.uuid = UUID.randomUUID().toString();
		this.object = object;
	}
}