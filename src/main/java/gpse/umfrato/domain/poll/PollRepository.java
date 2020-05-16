package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PollRepository extends JpaRepository<Poll, Long> {
}
