package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PollRepository extends JpaRepository<Poll, Long> {
}
