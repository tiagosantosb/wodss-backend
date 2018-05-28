package ch.fhnw.wodss.backend.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.wodss.backend.domain.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	PasswordResetToken findFirstByTokenAndUserEmail(String token, String userEmail);
	void deleteByUserId(Long userId);
}
