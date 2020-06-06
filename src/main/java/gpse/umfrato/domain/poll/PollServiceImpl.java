package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    /**
     * This class constructor initializes the poll repository.
     *
     * @param categoryService    the object category service
     * @param categoryRepository the repository where the categories are saved
     * @param pollRepository     the poll repository
     */
    @Autowired
    public PollServiceImpl(final PollRepository pollRepository, final CategoryService categoryService,
                           final CategoryRepository categoryRepository) {
        this.pollRepository = pollRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    /**
     * This method creates a poll with all required parameters.
     *
     * @return the created poll
     */
    @Override
    @Transactional
    public Poll createPoll(final Poll poll) {
        // final Poll newPoll = poll;
        pollRepository.save(poll);
        categoryRepository.save(categoryService.createCategory("Standardkategorie", poll.getPollId()));

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
