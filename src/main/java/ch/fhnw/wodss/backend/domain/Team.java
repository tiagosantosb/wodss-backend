package ch.fhnw.wodss.backend.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Team {
	@Id 
	@NotNull
	private String code;
	
	@NotNull
	private String name;
	
	@NotNull
	private String groupStage;
}
