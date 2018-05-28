package ch.fhnw.wodss.backend.dto;

import lombok.Data;

@Data
public class UserPublicDto {
	private Long id;
	private String name;
	private int points;
}
