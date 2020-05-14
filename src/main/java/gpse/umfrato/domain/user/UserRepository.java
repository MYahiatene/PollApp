package gpse.umfrato.domain.user;

import gpse.umfrato.domain.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
}
