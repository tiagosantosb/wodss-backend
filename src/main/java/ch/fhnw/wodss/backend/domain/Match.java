package ch.fhnw.wodss.backend.domain;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

import org.hibernate.annotations.ColumnDefault;

import lombok.Data;

@Data
@Entity
public class Match {
	@Id 
	@GeneratedValue 
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetime;
	
	@ManyToOne
	private Stadium stadium;

	@Basic
	private int category;
	
	@ColumnDefault("false")
	private boolean finished;

	@ManyToOne(optional = true)
	private Team team1;

	@ManyToOne(optional = true)
	private Team team2;

	@Column(nullable = true)
	private String placeholderTeam1;

	@Column(nullable = true)
	private String placeholderTeam2;

	@Column(nullable = true)
	private String placeholderGroup1;

	@Column(nullable = true)
	private String placeholderGroup2;

	@ColumnDefault("0")
	@Min(0)
	private int team1Score;

	@ColumnDefault("0")
	@Min(0)
	private int team2Score;
	
	@OneToMany(mappedBy="match")
	private Set<Bet> bets;
 
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
 
    @Override
    public boolean equals(Object o) {
    	if (o == this) return true;
    	if (o == null) return false;
    	if (!(o instanceof Match)) return false;
    	if (((Match) o).getId().equals(getId())) return true;
    	return false;
    }
}
