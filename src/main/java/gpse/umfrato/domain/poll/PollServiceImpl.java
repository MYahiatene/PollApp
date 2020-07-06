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
        // TODO: Wochentage richtig setzen
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
        // TODO: List<String> von week, day, month zu List <int> umwandeln?
        // TODO: ähnliche if schleifen zu Methoden extrahieren fuer bessere übersichtlichkeit
        // TODO: setzt voraus, dass day nicht leer ist => frontend
        // TODO: setzt voraus, dass die reihenfolge im array, der reihenfolge in einer Woche entspricht
        // TODO: Sonntag als 1. Element der Woche in Kalender, aber nicht normal
        // TODO: bei Monaten Anzahl an Tagne berücksichtigen
        // TODO: beim ersten mal startet es erst in 2 Wochen/Monaten etc. -> noch im selben monat starten?
        // TODO: week_of_day ist was anderes als wir es verstehen
        final String repeat = poll.getRepeat();
        final List<String> day = poll.getDay();
        final List<String> week = poll.getWeek();
        final List<String> month = poll.getMonth();
        final int level = poll.getLevel();
        final Calendar prev = poll.getActivatedDate();
        Calendar next = prev;

        LOGGER.info("Start calculateNextDate");
        if (level == 0) {
            // adds the number of days, which represent the repetition, to the current date
            next.add(Calendar.DATE, Integer.parseInt(repeat));
            LOGGER.info("next: " + next.toString());
            LOGGER.info(parseDate(next));
            poll.setNextSeries(next);
        } else if (level == 1) {
            // days per x weeks given
            boolean first = true;
            // if there are later days within the cycle, the next activation date will be the next day in days[]
            for (int i = 0; i < day.size()-1; i++) {
                if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(i)) && first) {
                    next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(i + 1)));
                    poll.setNextSeries(next);
                    first = false;
                }
            }
            // if the last day of the cycle is reached the new day will be in the given repeated time at the first day
            // of the cycle
            if (first) {
                next.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(repeat));
                next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                poll.setNextSeries(next);
            }
            LOGGER.info("next: " + next.toString());
            LOGGER.info(parseDate(next));
        } else if (level == 2) {
            if (week.size() == 0) {
                LOGGER.info("no week given");
                // days per x months given
                boolean first = true;
                // if there are later days within the cycle, the next activation date will be the next day in days[]
                for (int i = 0; i < day.size() - 1; i++) {
                    if (prev.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day.get(i)) && first) {
                        next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(i + 1)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // if the last day of the cycle is reached the new day will be in the given repeated time at the first
                // day of the next cycle
                if (first) {
                    next.add(Calendar.MONTH, Integer.parseInt(repeat));
                    next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(0)));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else {
                LOGGER.info("day, week and month given");
                LOGGER.info("day of the week: " + prev.get(Calendar.DAY_OF_WEEK));
                LOGGER.info("week of the month: " + prev.get(Calendar.WEEK_OF_MONTH));
                // days in weeks per months
                boolean first = true;
                // more than one week and more than one day per week given
                for (int i = 0; i < week.size() - 1; i++) {
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(j)) && first
                            && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(i))) {
                            next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(j + 1)));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    if (first && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(i))) {
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                        next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week.get(i+1)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // more than one day per week given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(j)) && first
                        && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(0))) {
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(j + 1)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // last day of cycle reached, calculates the first day of the new one
                if (first) {
                    next.add(Calendar.MONTH, Integer.parseInt(repeat));
                    next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week.get(0)));
                    next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            }
        } else if (level == 3) {
            // days per year given
            LOGGER.info("day of the year" + prev.get(Calendar.DAY_OF_YEAR));
            if (week.size() == 0 && month.size() == 0) {
                boolean first = true;
                for (int i = 0; i < day.size() - 1; i++) {
                    if (prev.get(Calendar.DAY_OF_YEAR) == Integer.parseInt(day.get(i))) {
                        next.add(Calendar.DATE, Integer.parseInt(day.get(i + 1)) - Integer.parseInt(day.get(i)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                if (first) {
                    next.add(Calendar.YEAR, Integer.parseInt(repeat));
                    next.set(Calendar.DAY_OF_YEAR, Integer.parseInt(day.get(0)));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else if(month.size() == 0) {
                // days of calendar weeks per year
                boolean first = true;
                // more than one day and more than one week given
                for (int i = 0; i < week.size() - 1; i++) {
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(j))
                            && prev.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(week.get(i))) {
                            next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(j + 1)));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // next week of the cycle
                    if (first && prev.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(week.get(i))) {
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                        next.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week.get(i+1)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // more than one day per week given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(j)) && first
                        && prev.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(week.get(0))) {
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(j + 1)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // end of cycle reached
                if (first) {
                    next.add(Calendar.YEAR, Integer.parseInt(repeat));
                    next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                    next.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week.get(0)));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else if (week.size() == 0) {
                final int prevMonth = prev.get(Calendar.MONTH) + 1;
                // TODO: month +, prevMonth, weil month bei 0 startet
                // days per month given
                boolean first = true;
                // more than one day and more than one month given
                for (int i = 0; i < month.size() - 1; i++) {
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day.get(j)) && first
                            && prevMonth == Integer.parseInt(month.get(i))) {
                            next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(j + 1)));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // next month of the cycle
                    if (first && prevMonth == Integer.parseInt(month.get(i))) {
                        next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(0)));
                        next.set(Calendar.MONTH, Integer.parseInt(month.get(i + 1)) - 1);
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // more than one day per month given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day.get(j)) && first
                        && prevMonth == Integer.parseInt(month.get(0))) {
                        next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(j + 1)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                //end of cycle reached
                if (first) {
                    next.add(Calendar.YEAR, Integer.parseInt(repeat));
                    next.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(0)));
                    next.set(Calendar.MONTH, Integer.parseInt(month.get(0)) - 1);
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else {
                final int prevMonth = prev.get(Calendar.MONTH) + 1;
                LOGGER.info("day of week: " + prev.get(Calendar.DAY_OF_WEEK));
                LOGGER.info("week of month: " + prev.get(Calendar.WEEK_OF_MONTH));
                LOGGER.info("month: " + prevMonth);
                // days per weeks per months per years
                boolean first = true;
                // more than one day, more than one week and more than one month given
                for (int i = 0; i < month.size() - 1; i++) {
                    for (int j = 0; j < week.size() - 1; j++) {
                        for (int k = 0; k < day.size() - 1; k++) {
                            // next day in week cycle
                            if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(k)) && first
                                && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(j))
                                && prevMonth == Integer.parseInt(month.get(i))) {
                                LOGGER.info("8");
                                next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(k + 1)));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        // next week / only one day given
                        if (first && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(j))
                            && prevMonth == Integer.parseInt(month.get(i))) {
                            LOGGER.info("3");
                            next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week.get(j + 1)));
                            next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // more than one day per week given
                    for (int k = 0; k < day.size() - 1; k++) {
                        // next day in week cycle
                        if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(k)) && first
                            && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(0))
                            && prevMonth == Integer.parseInt(month.get(i))) {
                            LOGGER.info("7");
                            next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(k + 1)));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // end of week cycle reached / one day, one week, more than one month given
                    if (first && prevMonth == Integer.parseInt(month.get(i))) {
                        LOGGER.info("4");
                        next.set(Calendar.MONTH, Integer.parseInt(month.get(i + 1)) - 1);
                        next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week.get(0)));
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // one month but more than one week and more than one day
                for (int j = 0; j < week.size() - 1; j++) {
                    for (int k = 0; k < day.size() - 1; k++) {
                        // next day in week cycle
                        if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(k)) && first
                            && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(j))
                            && prevMonth == Integer.parseInt(month.get(0))) {
                            LOGGER.info("6");
                            next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(k + 1)));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // next week / only one day given
                    if (first && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(j))
                        && prevMonth == Integer.parseInt(month.get(0))) {
                        LOGGER.info("2");
                        next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week.get(j + 1)));
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // more than one day given, only one week and one month given
                for (int k = 0; k < day.size() - 1; k++) {
                    // next day in week cycle
                    if (prev.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(day.get(k)) && first
                        && prev.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(week.get(0))
                        && prevMonth == Integer.parseInt(month.get(0))) {
                        LOGGER.info("5");
                        next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(k + 1)));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // one day, one week, one month given or end of cycle reached
                if (first) {
                    LOGGER.info("1");
                    next.add(Calendar.YEAR, Integer.parseInt(repeat));
                    next.set(Calendar.MONTH, Integer.parseInt(month.get(0)) - 1);
                    next.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week.get(0)));
                    next.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day.get(0)));
                    poll.setNextSeries(next);
                    first = false;
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            }
        }
    }
}
