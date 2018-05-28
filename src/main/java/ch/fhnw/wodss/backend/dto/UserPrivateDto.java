package ch.fhnw.wodss.backend.dto;

import lombok.Data;

@Data
public class UserPrivateDto {
	private Long id;
	private String name;
	private String email;
	private boolean admin;
	private int points;
}
