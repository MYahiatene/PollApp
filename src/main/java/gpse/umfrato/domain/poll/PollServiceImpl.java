package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.evaluation.session.SessionService;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.pollresult.PollResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
class PollServiceImpl implements PollService {

    /* default */ final CategoryRepository categoryRepository;
    private final PollRepository pollRepository;
    private final CategoryService categoryService;
    private final SessionService sessionService;
    private final PollResultService pollResultService;
    private final ParticipationLinkService participationLinkService;
    private final ConsistencyQuestionService consistencyQuestionService;
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
                           final CategoryRepository categoryRepository , final SessionService sessionService,
                           final PollResultService pollResultService,
                           final ParticipationLinkService participationLinkService,
                           final ConsistencyQuestionService consistencyQuestionService) {
        this.pollRepository = pollRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.sessionService = sessionService;
        this.pollResultService = pollResultService;
        this.participationLinkService = participationLinkService;
        this.consistencyQuestionService = consistencyQuestionService;
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
     * Saves the new poll in the repository.
     * @param poll
     * @return new poll
     */
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
        final List<Poll> finalPolls = new ArrayList<>();
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
        final Poll poll = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new);
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
     * This method lets the poll-status progress.
     * @param pollId the id of the poll which will be increased
     * @return returns the poll activation status
     */
    @Override
    public Integer increasePollStatus(final Long pollId) {
        final Poll poll = pollRepository.getOne(pollId);
        switch (poll.getPollStatus()) {
            case 0:
                poll.setPollStatus(1);
                break;
            case 1:
                poll.setPollStatus(2);
                poll.setActivatedDate(Calendar.getInstance());
                break;
            case 2:
                poll.setPollStatus(3);
                poll.setDeactivatedDate(Calendar.getInstance());
                break;
            default:
                break;
        }
        pollRepository.save(poll);
        return poll.getPollStatus();
    }

    /**
     * This method tries to decrease the poll-status.
     * if the given poll has the status 1 it will be returned to 0,
     * otherwise the method will do nothing.
     * @param pollId the id of the poll which will be decreased
     * @return returns the poll activation status
     */
    @Override
    public Integer decreasePollStatus(final Long pollId) {
        final Poll poll = pollRepository.getOne(pollId);
        if (poll.getPollStatus() == 1) {
            poll.setPollStatus(0);
        }
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
    public String deletePoll(final Long pollID) {
        pollRepository.deleteById(pollID);
        sessionService.deleteAllSessions(pollID);
        participationLinkService.deleteAllLinks(pollID);
        pollResultService.deleteAllPollResults(pollID);
        consistencyQuestionService.deleteAllConsistencyQuestions(pollID);
        return "Poll erfolgreich gelöscht";
    }
    @Override public List<Poll> getLastEditedPolls() {
        return pollRepository.findTop5ByOrderByLastEditAt();
    }

    /**
     * Reformat the date from Calendar to a better readable String.
     * @param date Calendar with date and time information
     * @return date as a String
     */
    @Override
    public String parseDate(final Calendar date) {
        final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy&HH:mm", Locale.GERMAN);
        final String res = df.format(date.getTime());
        return res;
    }

    /**
     * Checks if a poll should be automatically activated/deactivated and if the activationDAte/deactivationDate is
     * reached, the pollStatus is updated.
     * @param poll the poll to check
     * @return updated Poll
     */
    @Override
    public Poll checkActivationAndDeactivation(final Poll poll) {
        final Calendar now = Calendar.getInstance();
        // now.set(Calendar.MONTH, now.get(Calendar.MONTH)+1);
        if (poll.isActivated() && poll.getPollStatus() == 1 && poll.getActivatedDate().before(now)) {
            increasePollStatus(poll.getPollId());
        }
        if (poll.isDeactivated() && poll.getPollStatus() == 2 && poll.getDeactivatedDate().before(now)) {
            increasePollStatus(poll.getPollId());
        }
        return pollRepository.findById(poll.getPollId()).orElseThrow(EntityNotFoundException::new);
    }
}
