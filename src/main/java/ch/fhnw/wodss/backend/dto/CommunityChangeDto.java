package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CommunityChangeDto {
	@NotNull
	private Long acceptMemberId;
}
