package ch.fhnw.wodss.backend.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.wodss.backend.domain.Bet;

public interface BetRepository extends JpaRepository<Bet, Long> {
	List<Bet> findByUserId(Long userId);
}
