package gpse.umfrato.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
// im Spring Tutorial CruedRepository, einfach austauschbar?
