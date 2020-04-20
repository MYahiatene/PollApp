package gpse.umfrato.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class User implements UserDetails {

    private static final long serialVersionUID = 0L;

    @Id
    @Column
    @Getter @Setter
    private String username;

    @Column
    @Getter @Setter
    private String firstname;

    @Column
    @Getter @Setter
    private String lastname;

    @JsonIgnore
    @Column
    @Getter @Setter
    private String password;

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles; //<3>

    protected User() {

    }

    public User(final String username, final String firstname, final String lastname, final String password) { //<4>
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void addRole(String role) {
        if (roles == null) {
            this.roles = new ArrayList<>();
        }

        this.roles.add(role);
    }
}
