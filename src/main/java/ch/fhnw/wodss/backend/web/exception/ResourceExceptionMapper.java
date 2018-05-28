package ch.fhnw.wodss.backend.web.exception;

import fr.xebia.extras.selma.IgnoreMissing;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;

@Mapper(withIoC = IoC.SPRING, withIgnoreMissing = IgnoreMissing.DESTINATION)
public interface ResourceExceptionMapper {
	ResourceExceptionDto convertToDto(ResourceException resourceException);
}
