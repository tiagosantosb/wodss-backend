package ch.fhnw.wodss.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticDto {
	private long team1;
	private long team2;
	private long draw;
}
