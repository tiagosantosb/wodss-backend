package ch.fhnw.wodss.backend.mapping;

import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.security.UserPrincipal;

@Service
public class CommunityPublicCustomMapper {
    public Long userAsLong(User user) {
        return user.getId();
    }
    
    public boolean joinRequested(Set<User> joinRequesters) {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		if (joinRequesters != null) return joinRequesters.stream().anyMatch(u -> u.getId() == userId);
		else return false;
    }
}
