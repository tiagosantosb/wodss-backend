package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BetDto {
	private Long id;
	
	@NotNull
	private Long matchId;
	
	@Min(0)
	private int team1Score;
	
	@Min(0)
	private int team2Score;
	
	private int points;
}
