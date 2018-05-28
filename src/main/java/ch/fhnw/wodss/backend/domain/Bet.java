package ch.fhnw.wodss.backend.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.ColumnDefault;

import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "match_id"}) }) 
public class Bet {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name="match_id")
	private Match match;
	
	@ColumnDefault("0")
	@Min(0)
	private int team1Score;
	
	@ColumnDefault("0")
	@Min(0)
	private int team2Score;
	
	@ColumnDefault("0")
	@Min(0)
	@Max(4)
	private int points;
 
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
 
    @Override
    public boolean equals(Object o) {
    	if (o == this) return true;
    	if (o == null) return false;
    	if (!(o instanceof Bet)) return false;
    	if (((Bet) o).getId().equals(getId())) return true;
    	return false;
    }
}
