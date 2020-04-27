package gpse.umfrato.domain.poll;


import java.util.List;
import java.util.Optional;

public interface PollService {
    Poll createPoll(String pollname);

    List<Poll> getAllPolls();

    Optional<Poll> getPoll(String id);
}
