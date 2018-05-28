package ch.fhnw.wodss.backend.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
public class PasswordResetToken {
	@Id
	@GeneratedValue
	private Long id;

	@Basic
	private String token;

	@OneToOne
	@JoinColumn(nullable = false, unique = true, name = "user_id")
	private User user;

	@Temporal(TemporalType.TIMESTAMP)
	private Date expiry;
}