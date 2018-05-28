package ch.fhnw.wodss.backend.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
@Entity
public class Community {
	@Id 
	@GeneratedValue 
	private Long id;

	@Column(unique = true)
	@NotNull
	@Length(min=3, max=20)
	private String name;

	@ManyToOne
	@NotNull
	private User creator;
	
	@ManyToMany
	@JoinTable(
			name = "community_member",
			joinColumns = @JoinColumn(name="community_id"),
			inverseJoinColumns = @JoinColumn(name="user_id"))
	private Set<User> members = new HashSet<>();
	
	@ManyToMany
	@JoinTable(
			name = "community_joinrequest",
			joinColumns = @JoinColumn(name="community_id"),
			inverseJoinColumns = @JoinColumn(name="user_id"))
	private Set<User> joinRequesters = new HashSet<>();

	public void addJoinRequester(User user) {
		joinRequesters.add(user);
	}

	public void acceptJoinRequest(User user) {
		joinRequesters.remove(user);
		members.add(user);
	}

	public void removeJoinRequest(User user) {
		joinRequesters.remove(user);
	}

	public void removeMember(User user) {
		members.remove(user);
	}
 
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
 
    @Override
    public boolean equals(Object o) {
    	if (o == this) return true;
    	if (o == null) return false;
    	if (!(o instanceof Community)) return false;
    	if (((Community) o).getId().equals(getId())) return true;
    	return false;
    }
}
