package ch.fhnw.wodss.backend.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.wodss.backend.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
	User findByName(String name);
	boolean existsByEmail(String email);
}
