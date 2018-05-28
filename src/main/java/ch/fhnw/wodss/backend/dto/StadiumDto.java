package ch.fhnw.wodss.backend.dto;

import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StadiumDto {
	private Long id;
	private String name;
	private String city;
}
