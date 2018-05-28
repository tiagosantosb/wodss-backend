package ch.fhnw.wodss.backend.mapping;

import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.Match;
import ch.fhnw.wodss.backend.dto.MatchDto;
import ch.fhnw.wodss.backend.dto.PlaceholderDto;
import ch.fhnw.wodss.backend.dto.ResultDto;

@Service
public class MatchInterceptor {
	public void intercept(Match in, MatchDto out) {
		out.setPlaceholder(new PlaceholderDto(
				in.getPlaceholderTeam1(), 
				in.getPlaceholderGroup1(),
				in.getPlaceholderTeam2(),
				in.getPlaceholderGroup2()));
		out.setResult(new ResultDto(
				in.getTeam1Score(), 
				in.getTeam2Score()));
	}
}
