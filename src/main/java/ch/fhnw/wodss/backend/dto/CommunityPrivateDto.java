package ch.fhnw.wodss.backend.dto;

import java.util.Set;

import lombok.Data;

@Data
public class CommunityPrivateDto implements CommunityDataDto {
	private Long id;
	private String name;
	private Long creator;
	private Set<Long> members;
	private Set<Long> joinRequesters;
}
