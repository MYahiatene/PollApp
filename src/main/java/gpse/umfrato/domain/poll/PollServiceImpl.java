package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.user.UserRepository;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    /**
     * This class constructor initializes the poll repository.
     *
     * @param pollRepository the poll repository
     */
    @Autowired
    public PollServiceImpl(final PollRepository pollRepository, final UserRepository userRepository,
                           final CategoryService categoryService, final CategoryRepository categoryRepository) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    /**
     * This method creates a poll with all required parameters.
     *
     * @param pollName        name of the poll
     * @param pollCreator     name of the creator of the poll
     * @param anonymityStatus status of the poll if it is anonymous, part anonymous and non anonymous
     * @param pollStatus      status of the poll if it is activated or deactivated
     * @return the created poll
     */
    @Override
    @Transactional
    public Poll createPoll(final String pollCreator, final String anonymityStatus, final String pollName,
                           final String activatedAt, final String deactivatedAt, final int pollStatus) {
        final Poll poll = new Poll(pollCreator, pollName, anonymityStatus, activatedAt, deactivatedAt, pollStatus);
        pollRepository.save(poll);
        categoryRepository.save(categoryService.createCategory("Keine Kategorie", poll.getPollId()));

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
        return pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new);
    }
}
