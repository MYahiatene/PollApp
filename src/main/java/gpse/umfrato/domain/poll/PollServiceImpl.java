package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
class PollServiceImpl implements PollService {

    private static final Logger LOGGER = Logger.getLogger("PollServiceImpl");
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

    @Override
    @Transactional
    public Poll createSeriesPoll(final Poll poll) {
        poll.setPollName(createSeriesPollName(poll));
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
     * This method activates the poll.
     * @param pollId the id of the poll which will be activated
     * @return returns the poll activation status
     */
    @Override
    public Integer activatePoll(final Long pollId) {
        final Poll poll = pollRepository.getOne(pollId);
        poll.setPollStatus(1);
        poll.setActivatedDate(Calendar.getInstance());
        // TODO: poll.setNextSeries(calculateNextDate(poll));
        calculateNextDate(poll);
        pollRepository.save(poll);
        return poll.getPollStatus();
    }

    /**
     * This method activates the poll.
     * @param pollId the id of the poll which will be activated
     * @return returns the poll activation status
     */
    @Override
    public Integer deactivatePoll(final Long pollId) {
        final Poll poll = pollRepository.getOne(pollId);
        poll.setPollStatus(2);
        poll.setDeactivatedDate(Calendar.getInstance());
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
     * @param poll
     * @return updated Poll
     */
    @Override
    public Poll checkActivationAndDeactivation(final Poll poll) {
        final Calendar now = Calendar.getInstance();
        // now.set(Calendar.MONTH, now.get(Calendar.MONTH)+1);
        if (poll.isActivated() && poll.getPollStatus() == 0 && poll.getActivatedDate().before(now)) {
            activatePoll(poll.getPollId());
        }
        if (poll.isDeactivated() && poll.getPollStatus() == 1 && poll.getDeactivatedDate().before(now)) {
            activatePoll(poll.getPollId());
        }
        return pollRepository.findById(poll.getPollId()).orElseThrow(EntityNotFoundException::new);
    }

    @SuppressWarnings("checkstyle:AvoidNestedBlocks")
    @Override
    public String createSeriesPollName(final Poll poll) {
        String name = poll.getPollName();
        if (name.contains(":nr:")) {
            name = name.replace(":nr:", poll.getSeriesCounter().toString());
        }
        if (name.contains(":tg:")) {
            name = name.replace(":tg:", String.valueOf(poll.getActivatedDate().get(Calendar.DAY_OF_MONTH)));
        }
        if (name.contains(":mt:")) {
            name = name.replace(":mt:", String.valueOf(poll.getActivatedDate().get(Calendar.MONTH) + 1));
        }
        if (name.contains(":jr:")) {
            name = name.replace(":jr:", String.valueOf(poll.getActivatedDate().get(Calendar.YEAR)));
        }
        if (name.contains(":kw:")) {
            name = name.replace(":kw:", String.valueOf(poll.getActivatedDate().get(Calendar.WEEK_OF_YEAR)));
        }
        if (name.contains(":wt:")) {
            final int weekday = poll.getActivatedDate().get(Calendar.DAY_OF_WEEK);
            String wd = "";
            switch (weekday) {
                case 1: {
                    wd = "Sonntag";
                    break;
                }
                case 2: {
                    wd = "Montag";
                    break;
                }
                case 3: {
                    wd = "Dienstag";
                    break;
                }
                case 4: {
                    wd = "Mittwoch";
                    break;
                }
                case 5: {
                    wd = "Donnerstag";
                    break;
                }
                case 6: {
                    wd = "Freitag";
                    break;
                }
                case 7: {
                    wd = "Samstag";
                    break;
                }
                default: {
                    wd = "";
                    break;
                }
            }
            name = name.replace(":wt:", wd);
        }
        return name;
    }

    @Override
    public void calculateNextDate(final Poll poll) {
        // TODO: day[0] oder day[day.length-1] ? -> letztes element der schleife auch noch prüfen?
        // TODO: String arrays zu int arrays umwandeln?
        // TODO: ähnliche if schleifen zu Methoden extrahieren fuer bessere übersichtlichkeit
        final String repeat = poll.getRepeat();
        final String[] day = poll.getDay();
        final String[] week = poll.getDay();
        final String[] month = poll.getMonth();
        final int level = poll.getLevel();
        final Calendar prev = poll.getActivatedDate();
        Calendar next = prev;

        LOGGER.info("Start calculateNextDate");
        if (level == 0) {
            next.add(Calendar.DATE, Integer.parseInt(repeat));
            LOGGER.info("next: " + next.toString());
            LOGGER.info(parseDate(next));
            poll.setNextSeries(next);
        } else if (level == 1) {
            // TODO: setzt voraus, dass day nicht leer ist => frontend
            // TODO: setzt voraus, dass die reihenfolge im array, der reihenfolge in einer Woche entspricht
            // TODO: Sonntag als 1. Element der Woche in Kalender, aber nicht normal
            boolean first = true;
            for (int i = 0; i < day.length-1; i++) {
                if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day[i])) {
                    next.add(Calendar.DATE, Integer.parseInt(day[i + 1]) - Integer.parseInt(day[i]));
                    poll.setNextSeries(next);
                    first = false;
                }
            }
            if (first) {
                next.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(repeat));
                next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[0]));
                poll.setNextSeries(next);
            }
            LOGGER.info("next: " + next.toString());
            LOGGER.info(parseDate(next));
        } else if (level == 2) {
            // TODO: setzt voraus, dass day nicht leer ist => frontend
            // TODO: setzt voraus, dass die reihenfolge im array, der reihenfolge in einer Woche entspricht
            // nur Tage im Monat angegeben
            if (week.length == 0) {
                boolean first = true;
                for (int i = 0; i < day.length - 1; i++) {
                    if (prev.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day[i])) {
                        next.add(Calendar.DATE, Integer.parseInt(day[i + 1]) - Integer.parseInt(day[i]));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                if (first) {
                    next.add(Calendar.MONTH, Integer.parseInt(repeat));
                    next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[0]));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else {
                // Tage in Wochen des Monats angegeben
                boolean first = true;
                for (int i = 0; i < week.length - 1; i++) {
                    for (int j = 0; j < day.length - 1; j++) {
                        if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day[j])
                            && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week[i])) {
                            next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[j + 1]));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    if (first && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week[i])) {
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[0]));
                        next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week[i+1]));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                if (first) {
                    next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[0]));
                    next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week[0]));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            }
        } else if (level == 3) {
            // nur Tag im Jahr angegeben
            if (week.length == 0 && month.length == 0) {
                boolean first = true;
                for (int i = 0; i < day.length - 1; i++) {
                    if (prev.get(Calendar.DAY_OF_YEAR) == Integer.parseInt(day[i])) {
                        next.add(Calendar.DATE, Integer.parseInt(day[i + 1]) - Integer.parseInt(day[i]));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                if (first) {
                    next.add(Calendar.YEAR, Integer.parseInt(repeat));
                    next.set(Calendar.DAY_OF_YEAR, Integer.parseInt(day[0]));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else if(month.length == 0) {
                // Tage in bestimmten Kalenderwochen angegeben
                boolean first = true;
                for (int i = 0; i < week.length - 1; i++) {
                    for (int j = 0; j < day.length - 1; j++) {
                        if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day[j])
                            && prev.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(week[i])) {
                            next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[j + 1]));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    if (first && prev.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(week[i])) {
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[0]));
                        next.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week[i+1]));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                if (first) {
                    next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[0]));
                    next.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week[0]));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else if (week.length == 0) {
                // Tage in bestimmten Monaten angegeben
                boolean first = true;
                for (int i = 0; i < month.length - 1; i++) {
                    for (int j = 0; j < day.length - 1; j++) {
                        if (prev.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day[j])
                            && prev.get(Calendar.MONTH) == Integer.parseInt(month[i])) {
                            next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[j + 1]));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    if (first && prev.get(Calendar.MONTH) == Integer.parseInt(month[i])) {
                        next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[0]));
                        next.set(Calendar.MONTH, Integer.parseInt(month[i + 1]));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                if (first) {
                    next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[0]));
                    next.set(Calendar.MONTH, Integer.parseInt(month[0]));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else {
                // Tage in bestimmten Wochen bestimmter Monate angegeben
                boolean first = true;
                for (int i = 0; i < month.length - 1; i++) {
                    for (int j = 0; j < week.length - 1; j++) {
                        for (int k = 0; k < day.length - 1; k++) {
                            if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day[k])
                                && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week[j])
                                && prev.get(Calendar.MONTH) == Integer.parseInt(month[i])) {
                                next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day[k + 1]));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        if (first && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week[j])
                            &&prev.get(Calendar.MONTH) == Integer.parseInt(month[i])) {
                            next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[0]));
                            next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week[j + 1]));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    if (first && prev.get(Calendar.MONTH) == Integer.parseInt(month[i])) {
                        next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[0]));
                        next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week[0]));
                        next.set(Calendar.MONTH, Integer.parseInt(month[i + 1]));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                if (first) {
                    next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[0]));
                    next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week[0]));
                    next.set(Calendar.MONTH, Integer.parseInt(month[0]));
                    poll.setNextSeries(next);
                    first = false;
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            }
        }
    }
}
