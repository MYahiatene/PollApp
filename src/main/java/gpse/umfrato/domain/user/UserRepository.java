package gpse.umfrato.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The repository in which users are stored.
 */
public interface UserRepository extends JpaRepository<User, String> {
}
