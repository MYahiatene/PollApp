package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.question.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
class PollServiceImpl implements PollService {

    /* default */ final CategoryRepository categoryRepository;
    private final PollRepository pollRepository;
    private final CategoryService categoryService;
    private final QuestionRepository questionRepository;
    private int anonymUsername = 0;

    /**
     * This class constructor initializes the poll repository.
     *
     * @param pollRepository     the poll repository
     * @param categoryService    the object category service
     * @param categoryRepository the repository where the categories are saved
     */
    @Autowired
    public PollServiceImpl(final PollRepository pollRepository, final CategoryService categoryService,
                           final CategoryRepository categoryRepository, final QuestionRepository questionRepository) {
        this.pollRepository = pollRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * This method creates a poll with all required parameters.
     *
     * @return the created poll
     */
    @Override
    @Transactional
    public Poll createPoll(final Poll poll) {
        pollRepository.save(poll);
        categoryService.createCategory("Standardkategorie", poll.getPollId());

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

    /**
     * This method creates a unique username for anonym polls.
     *
     * @return a number as an anonym Username
     */
    @Override
    public String createAnonymUsername() {
        this.anonymUsername++;
        return String.valueOf(this.anonymUsername);
    }

    @Override
    public Integer activatePoll(final Long pollId) {
        final Poll poll = pollRepository.getOne(pollId);
        poll.setPollStatus(poll.getPollStatus() + 1);
        pollRepository.save(poll);
        return poll.getPollStatus();
    }

    @Override
    public void editPollName(Long pollId, String pollName) {
        final Poll poll = pollRepository.getOne(pollId);
        poll.setPollName(pollName);
        pollRepository.save(poll);
    }

    @Override
    public String deletePoll(String pollID) {
        pollRepository.deleteById(Long.valueOf(pollID));
        return ("Poll erfolgreich gelöscht");
    }
}
