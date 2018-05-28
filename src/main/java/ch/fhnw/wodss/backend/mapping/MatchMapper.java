package ch.fhnw.wodss.backend.mapping;

import ch.fhnw.wodss.backend.domain.Match;
import ch.fhnw.wodss.backend.dto.MatchDto;
import fr.xebia.extras.selma.IgnoreMissing;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Field;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withIoC = IoC.SPRING, withIgnoreMissing = IgnoreMissing.ALL,
	withCustomFields = {
			@Field({"TeamDto.group", "Team.groupStage"}),
			@Field({"MatchDto.statistics", "Match.bets"})
		}
	, withCustom = MatchCustomMapper.class)
public interface MatchMapper {
	@Maps(withCustom = MatchInterceptor.class)
	MatchDto asMatchDto(Match match);
}
