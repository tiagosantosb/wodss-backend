package ch.fhnw.wodss.backend.mapping;

import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.User;

@Service
public class CommunityPrivateCustomMapper {
    public Long userAsLong(User user) {
        return user.getId();
    }
}
