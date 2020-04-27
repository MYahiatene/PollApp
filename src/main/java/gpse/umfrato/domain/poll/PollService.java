package gpse.umfrato.domain.poll;


import java.time.LocalDateTime;
import java.util.List;

public interface PollService {
    Poll createPoll(final String pollname, final String pollcreator, final LocalDateTime pollCreatedAt,
                    final LocalDateTime lastEditAt, final LocalDateTime activatedAt,
                    final LocalDateTime deactivatedAt, final String anonymityStatus, final int pollStatus);

    List<Poll> getAllPolls();

    Poll getPoll(String id);


}
