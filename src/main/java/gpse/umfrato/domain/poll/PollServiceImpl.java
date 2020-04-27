package gpse.umfrato.domain.poll;

import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;

    @Autowired
    public PollServiceImpl(final PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    public Poll createPoll(final String pollname, final String pollcreator, final LocalDateTime pollCreatedAt,
                           final LocalDateTime lastEditAt, final LocalDateTime activatedAt,
                           final LocalDateTime deactivatedAt, final String anonymityStatus, final int pollStatus) {
        final Poll poll = new Poll(pollname);

        pollRepository.save(poll);

        return poll;
    }

    @Override
    public List<Poll> getAllPolls() {
        final List<Poll> poll = pollRepository.findAll();

        if (poll.isEmpty()) {
            throw new BadRequestException();
        }

        return poll;
    }

    @Override
    public Poll getPoll(String id) {
        final Long pollId = Long.valueOf(id);
        return pollRepository.findById(pollId).orElseThrow(() -> new EntityNotFoundException());
    }
}
