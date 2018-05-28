package ch.fhnw.wodss.backend.mapping;

import ch.fhnw.wodss.backend.domain.Bet;
import ch.fhnw.wodss.backend.dto.BetDto;
import fr.xebia.extras.selma.Field;
import fr.xebia.extras.selma.IgnoreMissing;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;

@Mapper(withIoC = IoC.SPRING, withIgnoreMissing = IgnoreMissing.ALL, withCustomFields = {
        @Field({"matchId","match.id"})
})
public interface BetMapper {
	BetDto asBetDto(Bet bet);
}
