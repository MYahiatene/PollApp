package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
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
        poll.setActivatedDate(ZonedDateTime.now());
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
        poll.setDeactivatedDate(ZonedDateTime.now());
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
    public String parseDate(final ZonedDateTime date) {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy&HH:mm", Locale.GERMAN);
        final String res = df.format(date);
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
        final ZonedDateTime now = ZonedDateTime.now();
        // now.set(Calendar.MONTH, now.get(Calendar.MONTH)+1);
        if (poll.isActivated() && poll.getPollStatus() == 0 && poll.getActivatedDate().isBefore(now)) {
            activatePoll(poll.getPollId());
        }
        if (poll.isDeactivated() && poll.getPollStatus() == 1 && poll.getDeactivatedDate().isBefore(now)) {
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
            name = name.replace(":tg:", String.valueOf(poll.getActivatedDate().getDayOfMonth()));
        }
        if (name.contains(":mt:")) {
            name = name.replace(":mt:", String.valueOf(poll.getActivatedDate().getMonthValue()));
        }
        if (name.contains(":jr:")) {
            name = name.replace(":jr:", String.valueOf(poll.getActivatedDate().getYear()));
        }
        if (name.contains(":kw:")) {
            name = name.replace(":kw:", String.valueOf(poll.getActivatedDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
        }
        if (name.contains(":wt:")) {
            name = name.replace(":wt:", poll.getActivatedDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN));
        }
        return name;
    }

    @Override
    public void calculateNextDate(final Poll poll) {
        LOGGER.info("Start calculateNextDate");
        // TODO: ähnliche if schleifen zu Methoden extrahieren für bessere übersichtlichkeit
        // TODO: bei Monaten Anzahl an Tagne berücksichtigen
        // TODO: beim ersten mal startet es erst in 2 Wochen/Monaten etc. -> noch im selben monat starten?
        // TODO: week_of_day ist was anderes als wir es verstehen
        // TODO: zwischen dem Zyklus einsteigen (dafür prev < .get(i + 1) statt prev == .get(i))
        for (int repetitions = 0; repetitions < 5; repetitions++) {
            final int repeat = poll.getRepeat();
            final List<Integer> day = poll.getDay();
            final List<Integer> week = poll.getWeek();
            final List<Integer> month = poll.getMonth();
            final int level = poll.getLevel();
            final ZonedDateTime prev = poll.getActivatedDate();
            ZonedDateTime next = prev;
            LOGGER.info("given");
            LOGGER.info("repeat: " + repeat);
            LOGGER.info("day: " + day.toString());
            LOGGER.info("week: " + week.toString());
            LOGGER.info("month: " + month.toString());
            LOGGER.info("level: " + level);
            LOGGER.info("prev: " + prev.toString());
            if (level == 0) {
                // adds the number of days, which represent the repetition, to the current date
                next = next.plusDays(repeat);
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
                poll.setNextSeries(next);
            } else if (level == 1) {
                // days per x weeks given
                boolean first = true;
                // if there are later days within the cycle, the next activation date will be the next day in days[]
                for (int i = 0; i < day.size() - 1; i++) {
                    if (prev.getDayOfWeek().getValue() < day.get(i + 1) && first) {
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(i + 1))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // if the last day of the cycle is reached the new day will be in the given repeated time at the first day
                // of the cycle
                if (first) {
                    next = next.plusWeeks(repeat - 1);
                    next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
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
                        if (prev.getDayOfMonth() < day.get(i + 1) && first) {
                            next = next.withDayOfMonth(day.get(i + 1));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // if the last day of the cycle is reached the new day will be in the given repeated time at the first
                    // day of the next cycle
                    if (first) {
                        next = next.plusMonths(repeat);
                        next = next.withDayOfMonth(day.get(0));
                        poll.setNextSeries(next);
                    }
                    LOGGER.info("next: " + next.toString());
                    LOGGER.info(parseDate(next));
                } else {
                    LOGGER.info("day, week and month given");
                    LOGGER.info("day of the week: " + prev.getDayOfWeek());
                    LOGGER.info("week of the month: " + getWeekOfMonth(prev));
                    // days in weeks per months
                    boolean first = true;
                    // more than one week and more than one day per week given
                    for (int i = 0; i < week.size() - 1; i++) {
                        for (int j = 0; j < day.size() - 1; j++) {
                            // Original                        =      kein + 1                                   =     kein (  + 1) % week.size()
                            if (prev.getDayOfWeek().getValue() < day.get(j + 1) && first && getWeekOfMonth(prev) < week.get((i + 1) % week.size())) {
                                next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        if (first && getWeekOfMonth(prev) == week.get(i)) {
                        /* next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                        next = next.set(Calendar.WEEK_OF_MONTH, week.get(i+1)); */
                            next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(i + 1), DayOfWeek.of(day.get(0))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // more than one day per week given
                    for (int j = 0; j < day.size() - 1; j++) {
                        // Original                        =      kein + 1
                        if (prev.getDayOfWeek().getValue() < day.get(j + 1) && first && getWeekOfMonth(prev) == week.get(0)) {
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // last day of cycle reached, calculates the first day of the new one
                    if (first) {
                        next = next.plusMonths(repeat);
                    /* next = next.set(Calendar.WEEK_OF_MONTH, week.get(0));
                    next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0)))); */
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                    }
                    LOGGER.info("next: " + next.toString());
                    LOGGER.info(parseDate(next));
                }
            } else if (level == 3) {
                // days per year given
                LOGGER.info("day of the year" + prev.getDayOfYear());
                if (week.size() == 0 && month.size() == 0) {
                    boolean first = true;
                    for (int i = 0; i < day.size() - 1; i++) {
                        if (prev.getDayOfYear() == day.get(i)) {
                            next = next.plusDays(day.get(i + 1) - day.get(i));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    if (first) {
                        next = next.plusYears(repeat);
                        next = next.withDayOfYear(day.get(0));
                        poll.setNextSeries(next);
                    }
                    LOGGER.info("next: " + next.toString());
                    LOGGER.info(parseDate(next));
                } else if (month.size() == 0) {
                    // days of calendar weeks per year
                    boolean first = true;
                    // more than one day and more than one week given
                    for (int i = 0; i < week.size() - 1; i++) {
                        for (int j = 0; j < day.size() - 1; j++) {
                            if (prev.getDayOfWeek().getValue() == day.get(j) && prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(i)) {
                                next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        // next week of the cycle
                        if (first && prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(i)) {
                            next = next.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week.get(i + 1));
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // more than one day per week given
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.getDayOfWeek().getValue() == day.get(j) && first && prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(0)) {
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // end of cycle reached
                    if (first) {
                        next = next.plusYears(repeat);
                        next = next.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week.get(0));
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                    }
                    LOGGER.info("next: " + next.toString());
                    LOGGER.info(parseDate(next));
                } else if (week.size() == 0) {
                    final int prevMonth = prev.getMonthValue();
                    // TODO: month +, prevMonth, weil month bei 0 startet
                    // days per month given
                    boolean first = true;
                    // more than one day and more than one month given
                    for (int i = 0; i < month.size() - 1; i++) {
                        for (int j = 0; j < day.size() - 1; j++) {
                            if (prev.getDayOfMonth() == day.get(j) && first && prevMonth == month.get(i)) {
                                next = next.withDayOfMonth(day.get(j + 1));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        // next month of the cycle
                        if (first && prevMonth == month.get(i)) {
                            next = next.withDayOfMonth(day.get(0));
                            next = next.withMonth(month.get(i + 1));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // more than one day per month given
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.getDayOfMonth() == day.get(j) && first && prevMonth == month.get(0)) {
                            next = next.withDayOfMonth(day.get(j + 1));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    //end of cycle reached
                    if (first) {
                        next = next.plusYears(repeat);
                        next = next.withDayOfMonth(day.get(0));
                        next = next.withMonth(month.get(0));
                        poll.setNextSeries(next);
                    }
                    LOGGER.info("next: " + next.toString());
                    LOGGER.info(parseDate(next));
                } else {
                    final int prevMonth = prev.getMonthValue();
                    LOGGER.info("day of week: " + prev.getDayOfWeek().getValue());
                    LOGGER.info("week of month: " + getWeekOfMonth(prev));
                    LOGGER.info("month: " + prevMonth);
                    // days per weeks per months per years
                    boolean first = true;
                    // more than one day, more than one week and more than one month given
                    for (int i = 0; i < month.size() - 1; i++) {
                        for (int j = 0; j < week.size() - 1; j++) {
                            for (int k = 0; k < day.size() - 1; k++) {
                                // next day in week cycle
                                if (prev.getDayOfWeek().getValue() == day.get(k) && first && getWeekOfMonth(prev) == week.get(j) && prevMonth == month.get(i)) {
                                    LOGGER.info("8");
                                    next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                                    poll.setNextSeries(next);
                                    first = false;
                                }
                            }
                            // next week / only one day given
                            if (first && getWeekOfMonth(prev) == week.get(j) && prevMonth == month.get(i)) {
                                LOGGER.info("3");
                            /* next = next.set(Calendar.WEEK_OF_MONTH, week.get(j + 1));
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0)))); */
                                next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(j + 1), DayOfWeek.of(day.get(0))));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        // more than one day per week given
                        for (int k = 0; k < day.size() - 1; k++) {
                            // next day in week cycle
                            if (prev.getDayOfWeek().getValue() == day.get(k) && first && getWeekOfMonth(prev) == week.get(0) && prevMonth == month.get(i)) {
                                LOGGER.info("7");
                                next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        // end of week cycle reached / one day, one week, more than one month given
                        if (first && prevMonth == month.get(i)) {
                            LOGGER.info("4");
                            next = next.withMonth(month.get(i + 1));
                        /* next = next.set(Calendar.WEEK_OF_MONTH, week.get(0));
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0)))); */
                            next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // one month but more than one week and more than one day
                    for (int j = 0; j < week.size() - 1; j++) {
                        for (int k = 0; k < day.size() - 1; k++) {
                            // next day in week cycle
                            if (prev.getDayOfWeek().getValue() == day.get(k) && first && getWeekOfMonth(prev) == week.get(j) && prevMonth == month.get(0)) {
                                LOGGER.info("6");
                                next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        }
                        // next week / only one day given
                        if (first && getWeekOfMonth(prev) == week.get(j) && prevMonth == month.get(0)) {
                            LOGGER.info("2");
                        /* next = next.set(Calendar.WEEK_OF_MONTH, week.get(j + 1));
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0)))); */
                            next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(j + 1), DayOfWeek.of(day.get(0))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // more than one day given, only one week and one month given
                    for (int k = 0; k < day.size() - 1; k++) {
                        // next day in week cycle
                        if (prev.getDayOfWeek().getValue() == day.get(k) && first && getWeekOfMonth(prev) == week.get(0) && prevMonth == month.get(0)) {
                            LOGGER.info("5");
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // one day, one week, one month given or end of cycle reached
                    if (first) {
                        LOGGER.info("1");
                        next = next.plusYears(repeat);
                        next = next.withMonth(month.get(0));
                    /* next = next.set(Calendar.WEEK_OF_MONTH, week.get(0));
                    next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0)))); */
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                    LOGGER.info("next: " + next.toString());
                    LOGGER.info(parseDate(next));
                }
            }
            poll.setActivatedDate(next);
        }
    }

    private int getWeekOfMonth(ZonedDateTime date) {
        ZonedDateTime firstOfMonth = date.withDayOfMonth(1);
        return 1 + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - firstOfMonth.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }
}
