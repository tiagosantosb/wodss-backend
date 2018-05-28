package ch.fhnw.wodss.backend.mapping;

import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.UserPrivateDto;
import ch.fhnw.wodss.backend.dto.UserPublicDto;
import fr.xebia.extras.selma.IgnoreMissing;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;

@Mapper(withIoC = IoC.SPRING, withIgnoreMissing = IgnoreMissing.ALL)
public interface UserMapper {
	UserPublicDto asUserPublicDto(User user);
	UserPrivateDto asUserPrivateDto(User user);
}
