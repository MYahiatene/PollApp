package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;

@Service
class PollServiceImpl implements PollService {

    /* default */ static final Logger LOGGER = Logger.getLogger("PollServiceImpl");
    /* default */ final CategoryRepository categoryRepository;
    private final PollRepository pollRepository;
    private final CategoryService categoryService;
    private int anonymousUsername = 0;

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
        final List<Poll> polls = pollRepository.findAll();
        final ListIterator<Poll> it = polls.listIterator();
        List<Poll> finalPolls = new ArrayList<>();
        while (it.hasNext()) {
            finalPolls.add(checkActivationAndDeactivation(it.next()));
        }
        return finalPolls;
    }

    /**
     * This method return a requested poll.
     *
     * @param pollId the id of the requested poll
     * @return the requested poll
     */
    @Override
    public Poll getPoll(final Long pollId) {
        LOGGER.info(pollId.toString());
        Poll poll = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new);
        return checkActivationAndDeactivation(poll);
    }

    /**
     * This method creates a unique username for anonym polls.
     *
     * @return a number as an anonym Username
     */
    @Override
    public String createAnonymousUsername() {
        this.anonymousUsername++;
        return String.valueOf(this.anonymousUsername);
    }

    /**
     * This method activates the poll.
     * @param pollId the id of the poll which will be activated
     * @return returns the poll activation status
     */
    @Override
    public Integer activatePoll(final Long pollId) {
        final Poll poll = pollRepository.getOne(pollId);
        poll.setPollStatus(poll.getPollStatus() + 1);
        pollRepository.save(poll);
        return poll.getPollStatus();
    }

    /**
     * This method edits a poll name.
     * @param pollId the id of the poll which name will be edited
     * @param pollName the new name of the poll
     */
    @Override
    public void editPollName(final Long pollId, final String pollName) {
        final Poll poll = pollRepository.getOne(pollId);
        poll.setPollName(pollName);
        pollRepository.save(poll);
    }

    /**
     * This method deletes a poll.
     * @param pollID the id of the poll which will be deleted
     * @return returns a confirmation String
     */
    @Override
    public String deletePoll(final String pollID) {
        pollRepository.deleteById(Long.valueOf(pollID));
        return "Poll erfolgreich gelöscht";
    }
    @Override public List<Poll> getLastEditedPolls() {
        return pollRepository.findTop5ByOrderByLastEditAt();
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

    @Override
    public Poll checkActivationAndDeactivation(final Poll poll) {
        LOGGER.info("begin check with poll: " + poll);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)+1);
        LOGGER.info(now.toString());
        if(poll.isActivated() && poll.getPollStatus() == 0) {
            LOGGER.info("isActivated");
            LOGGER.info(poll.getActivatedDate().toString());
            if (poll.getActivatedDate().equals(now) || poll.getActivatedDate().before(now)) {
                LOGGER.info("activate");
                final int pollStatus = activatePoll(poll.getPollId());
                LOGGER.info("pollStatus: " + pollStatus);
            }
        }
        if(poll.isDeactivated() && poll.getPollStatus() == 1) {
            LOGGER.info("isDeactivated");
            if (poll.getDeactivatedDate().equals(now) || poll.getDeactivatedDate().before(now)) {
                LOGGER.info("deactivate");
                final int pollStatus = activatePoll(poll.getPollId());
                LOGGER.info("pollStatus: " + pollStatus);
            }
        }
        return pollRepository.findById(poll.getPollId()).orElseThrow(EntityNotFoundException::new);
    }
}
