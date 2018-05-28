package ch.fhnw.wodss.backend.dto;

import java.util.Set;

import lombok.Data;

@Data
public class CommunityPublicDto implements CommunityDataDto {
	private Long id;
	private String name;
	private Long creator;
	private Set<Long> members;
	private boolean joinRequested;
}
