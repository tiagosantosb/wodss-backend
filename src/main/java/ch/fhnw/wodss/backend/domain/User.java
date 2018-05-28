package ch.fhnw.wodss.backend.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
public class User {
	@Id 
	@GeneratedValue 
	private Long id;
	
	@Column(unique = true)
	@NotNull
	@Email
	private String email;

	@NotNull
	@Length(min=8)
	private String password;
	
	@NotNull
	@Length(min=3, max=20)
	private String name;
	
	@Basic
	private boolean admin;
	
	@ManyToMany(mappedBy="members")
	private Set<Community> communities = new HashSet<>();
	
	@ManyToMany(mappedBy="joinRequesters")
	private Set<Community> joinRequests = new HashSet<>();
	
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	private Set<Bet> bets = new HashSet<>();
	
	@Transient
	@Setter(AccessLevel.NONE)
	private int points;
	
	public int getPoints() {
		return bets.stream().mapToInt(b -> b.getPoints()).sum();
	}
 
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
 
    @Override
    public boolean equals(Object o) {
    	if (o == this) return true;
    	if (o == null) return false;
    	if (!(o instanceof User)) return false;
    	if (((User) o).getId().equals(getId())) return true;
    	return false;
    }
}
