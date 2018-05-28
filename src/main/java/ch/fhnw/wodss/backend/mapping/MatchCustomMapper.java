package ch.fhnw.wodss.backend.mapping;

import java.util.Set;

import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.Bet;
import ch.fhnw.wodss.backend.dto.StatisticDto;

@Service
public class MatchCustomMapper {
	public StatisticDto statistics(Set<Bet> bets) {
		return new StatisticDto(
				bets.stream().filter(bet -> bet.getTeam1Score() > bet.getTeam2Score()).count(),
				bets.stream().filter(bet -> bet.getTeam1Score() < bet.getTeam2Score()).count(),
				bets.stream().filter(bet -> bet.getTeam1Score() == bet.getTeam2Score()).count());
	}
}
