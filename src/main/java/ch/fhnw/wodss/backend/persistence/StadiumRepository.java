package ch.fhnw.wodss.backend.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.wodss.backend.domain.Stadium;

public interface StadiumRepository extends JpaRepository<Stadium, Long>{

}
