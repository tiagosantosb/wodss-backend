package ch.fhnw.wodss.backend.mapping;

import ch.fhnw.wodss.backend.domain.Team;
import ch.fhnw.wodss.backend.dto.TeamDto;
import fr.xebia.extras.selma.Field;
import fr.xebia.extras.selma.IgnoreMissing;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;

@Mapper(withIoC = IoC.SPRING, withIgnoreMissing = IgnoreMissing.ALL, withCustomFields = {
		@Field({"group", "groupStage"})
})
public interface TeamMapper {
	TeamDto asTeamDto(Team team);
}
