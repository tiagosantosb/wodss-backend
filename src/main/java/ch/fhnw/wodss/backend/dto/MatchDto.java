package ch.fhnw.wodss.backend.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MatchDto {
	private Long id;
	private Date datetime;
	private StadiumDto stadium;
	private int category;
	private boolean finished;
	private TeamDto team1;
	private TeamDto team2;
	private ResultDto result;
	private PlaceholderDto placeholder;
	private StatisticDto statistics;
}
