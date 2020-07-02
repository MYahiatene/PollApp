package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

@Service
class PollServiceImpl implements PollService {

    /* default */ static final Logger LOGGER = Logger.getLogger("PollServiceImpl");
    /* default */ final CategoryRepository categoryRepository;
    private final PollRepository pollRepository;
    private final CategoryService categoryService;
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
        pollRepository.save(poll);
        categoryService.createCategory("Standardkategorie", poll.getPollId());

        return poll;
    }

    @Override
    @Transactional
    public Poll createCopyPoll(final Poll poll) {
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
        LOGGER.info(id);
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
    public String parseDate(final Calendar date) {
        final String day =  String.valueOf(date.get(Calendar.DATE));
        final String month =  String.valueOf(date.get(Calendar.MONTH));
        final String year =  String.valueOf(date.get(Calendar.YEAR)-1900);
        final String hour =  String.valueOf(date.get(Calendar.HOUR_OF_DAY));
        final String minute =  String.valueOf(date.get(Calendar.MINUTE));
        LOGGER.info(day + "." + month + "." + year + "&" + hour + ":" + minute);
        return day + "." + month + "." + year + "&" + hour + ":" + minute;
    }
}
