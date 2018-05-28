package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class MatchChangeDto {
	private boolean finished;
	private TeamDto team1;
	private TeamDto team2;
	
	@Min(0)
	private int team1Score;
	
	@Min(0)
	private int team2Score;
}
