package ch.fhnw.wodss.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamDto {
	private String code;
	private String name;
	private String group;
}
