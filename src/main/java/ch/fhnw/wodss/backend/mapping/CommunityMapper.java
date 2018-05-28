package ch.fhnw.wodss.backend.mapping;

import ch.fhnw.wodss.backend.domain.Community;
import ch.fhnw.wodss.backend.dto.CommunityPrivateDto;
import ch.fhnw.wodss.backend.dto.CommunityPublicDto;
import fr.xebia.extras.selma.Field;
import fr.xebia.extras.selma.IgnoreMissing;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withIoC = IoC.SPRING, withIgnoreMissing = IgnoreMissing.ALL)
public interface CommunityMapper {
	
	@Maps(withCustomFields = { 
			@Field({"CommunityPublicDto.joinRequested", "Community.joinRequesters"})
		}, withCustom = CommunityPublicCustomMapper.class)
	CommunityPublicDto asCommunityPublicDto(Community community);
	
	@Maps(withCustom = CommunityPrivateCustomMapper.class)
	CommunityPrivateDto asCommunityPrivateDto(Community community);
}
