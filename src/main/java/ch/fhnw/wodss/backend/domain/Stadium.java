package ch.fhnw.wodss.backend.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Stadium {
	@Id
	@GeneratedValue
	private Long id;
	
	@Basic
	public String name;
	
	@Basic
	public String city;
}
