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

    /**
     * This class constructor initializes the poll repository.
     *
     * @param pollRepository the poll repository
     */
    @Autowired
    public PollServiceImpl(final PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    /**
     * This method creates a poll with all required parameters.
     *
     * @param pollname        name of the poll
     * @param pollcreator     name of the creator of the poll
     * @param pollCreatedAt   date when the poll was created
     * @param lastEditAt      date when the poll was last edit
     * @param activatedAt     date when the poll was activated
     * @param deactivatedAt   date when the poll was deactivated
     * @param anonymityStatus status of the poll if it is anonymous, part anonymous and non anonymous
     * @param pollStatus      status of the poll if it is activated or deactivated
     * @return the created poll
     */
    @Override
    public Poll createPoll(final String pollname, final String pollcreator, final LocalDateTime pollCreatedAt,
                           final LocalDateTime lastEditAt, final LocalDateTime activatedAt,
                           final LocalDateTime deactivatedAt, final String anonymityStatus, final int pollStatus) {
        final Poll poll = new Poll(pollname);

        pollRepository.save(poll);

        return poll;
    }

    /**
     * This method returns a list with all polls.
     *
     * @return a list with all polls
     */
    @Override
    public List<Poll> getAllPolls() {
        final List<Poll> poll = pollRepository.findAll();

        if (poll.isEmpty()) {
            throw new BadRequestException();
        }

        return poll;
    }

    /**
     * This method return a requested poll.
     *
     * @param id the id of the requested poll
     * @return the requested poll
     */
    @Override
    public Poll getPoll(final String id) {
        final Long pollId = Long.valueOf(id);
        return pollRepository.findById(pollId).orElseThrow(() -> new EntityNotFoundException());
    }
}
