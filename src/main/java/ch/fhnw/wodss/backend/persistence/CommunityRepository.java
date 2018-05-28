package ch.fhnw.wodss.backend.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.wodss.backend.domain.Community;

public interface CommunityRepository extends JpaRepository<Community, Long> {
	
}
