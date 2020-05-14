package gpse.umfrato.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.answer.Answer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * This attribute represents the username of the user.
     */
    @Id
    private String username;

    /**
     * This attribute represents the first name of the user.
     */
    private String firstname;

    /**
     * This attribute represents the last name of the user.
     */
    private String lastname;

    /**
     * This attribute represents the password of the user.
     */
    @JsonIgnore
    private String password;

    /**
     * This attribute represents a list with the roles of this user.
     */


    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    /**
     * This attribute represents a list with all answers for this question.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> givenAnswers;

    /**
     * This constructor receives the required parameter for the user object.
     *
     * @param username  the username of user
     * @param firstname the first name of user
     * @param lastname  the last name of user
     * @param password  the password of user
     */
    public User(final String username, final String firstname, final String lastname, final String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }

    /**
     * This method returns the authorities from the user.
     *
     * @return user authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * This method returns the password of the user.
     *
     * @return user password
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * This method returns the username of the user.
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return null;
    }

    /**
     * This method returns false if the account is expired.
     *
     * @return false if account is expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    /**
     * This method returns false if the account is locked.
     *
     * @return false if account is locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    /**
     * This method returns false if the credentials is expired.
     *
     * @return false if the credentials is expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    /**
     * This method returns true if the user is enabled.
     *
     * @return false if user account is disabled
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * This method adds a role to the user.
     *
     * @param role the role which is given
     */
    public void addRole(final String role) {
        if (roles == null) {
            this.roles = new ArrayList<>();
        }

        this.roles.add(role);
    }
}
