package ch.fhnw.wodss.backend.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.wodss.backend.domain.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

}
