package ch.fhnw.wodss.backend.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.wodss.backend.domain.Team;

public interface TeamRepository extends JpaRepository<Team, String> {

}
