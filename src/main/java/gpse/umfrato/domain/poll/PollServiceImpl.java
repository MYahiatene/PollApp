package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestion;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.evaluation.session.SessionService;
import gpse.umfrato.domain.participationlinks.ParticipationLink;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Logger;

@Service
class PollServiceImpl implements PollService {

    private static final Logger LOGGER = Logger.getLogger("PollServiceImpl");
    private final CategoryRepository categoryRepository;
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
                           final CategoryRepository categoryRepository , final SessionService sessionService
                                       , final PollResultService pollResultService
                                       , final ParticipationLinkService participationLinkService
                                       , final ConsistencyQuestionService consistencyQuestionService) {
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
    public Poll getPoll(final Long pollId) throws EntityNotFoundException {
        final Poll poll = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException :: new);
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
                poll.setActivatedDate(ZonedDateTime.now());
                break;
            case 2:
                poll.setPollStatus(3);
                poll.setDeactivatedDate(ZonedDateTime.now());
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
     *
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
     *
     * @param date Calendar with date and time information
     * @return date as a String
     */
    @Override public String parseDate(final ZonedDateTime date) {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy&HH:mm", Locale.GERMAN);
        final String res = df.format(date);
        return res;
    }

    /**
     * Checks if a poll should be automatically activated/deactivated and if the activationDAte/deactivationDate is
     * reached, the pollStatus is updated.
     * @param poll the poll to check
     * @return updated Poll
     */
    @Override public Poll checkActivationAndDeactivation(final Poll poll) {
        final ZonedDateTime now = ZonedDateTime.now();
        // now.set(Calendar.MONTH, now.get(Calendar.MONTH)+1);
        if (poll.isActivated() && poll.getPollStatus() == 1 && poll.getActivatedDate().before(now)) {
            increasePollStatus(poll.getPollId());
        }
        if (poll.isDeactivated() && poll.getPollStatus() == 2 && poll.getDeactivatedDate().before(now)) {
            increasePollStatus(poll.getPollId());
        }
        return pollRepository.findById(poll.getPollId()).orElseThrow(EntityNotFoundException :: new);
    }

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

    @Override public void calculateNextDate(final Poll poll) {
        LOGGER.info("Start calculateNextDate");
        // TODO: ähnliche if schleifen zu Methoden extrahieren für bessere übersichtlichkeit
        // TODO: bei Monaten Anzahl an Tagen berücksichtigen
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
                LOGGER.info("day of the week: " + prev.getDayOfWeek().getValue());
                LOGGER.info("week of the month: " + getWeekOfMonth(prev));
                // days in weeks per months
                boolean first = true;
                // more than one week and more than one day per week given
                for (int i = 0; i < week.size() - 1; i++) {
                    LOGGER.info("hier");
                    for (int j = 0; j < day.size() - 1; j++) {
                        LOGGER.info("hier rein");
                        // Original                        =      kein + 1                                    =     kein (  + 1) % week.size()
                        LOGGER.info("fuck: " + getWeekOfMonthWithDay(prev, day.get(0)));
                        LOGGER.info("fuck you: " + week.get((i + 1)));
                        if (prev.getDayOfWeek().getValue() < day.get(j + 1) && first && getWeekOfMonthWithDay(prev, day.get(0)) <= week.get((i + 1) % week.size())) {
                            LOGGER.info("next day in week");
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    if (first && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(i)) {
                        /* next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                        next = next.set(Calendar.WEEK_OF_MONTH, week.get(i+1)); */
                        LOGGER.info("last day in week");
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(i + 1), DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // more than one day per week given
                for (int j = 0; j < day.size() - 1; j++) {
                    // Original                        =      kein + 1                                          prev.getDayOfWeek().getValue()
                    if (prev.getDayOfWeek().getValue() < day.get(j + 1) && first && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(0)) {
                        LOGGER.info("only days, adds day");
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // last day of cycle reached, calculates the first day of the new one
                if (first) {
                    LOGGER.info("next month");
                    next = next.plusMonths(repeat);
                    next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            }
        } else if (level == 3) {
            // days per year given
            LOGGER.info("day of the year" + prev.getDayOfYear());
            LOGGER.info("week of the year" + prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
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
                        if (first && prev.getDayOfWeek().getValue() < day.get(j + 1) && prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(i)) {
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // next week of the cycle
                    if (first && prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) < week.get(i + 1)) {
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                        next = next.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week.get(i + 1));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // more than one day per week given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.getDayOfWeek().getValue() < day.get(j + 1) && first && (prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(0) || prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(week.size() - 1))) {
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // end of cycle reached
                if (first) {
                    next = next.plusYears(repeat);
                    next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                    next = next.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week.get(0));
                    poll.setNextSeries(next);
                }
                LOGGER.info("next: " + next.toString());
                LOGGER.info(parseDate(next));
            } else if (week.size() == 0) {
                final int prevMonth = prev.getMonthValue();
                // days per month given
                boolean first = true;
                // more than one day and more than one month given
                for (int i = 0; i < month.size() - 1; i++) {
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.getDayOfMonth() < day.get(j + 1) && first && prevMonth == month.get(i)) {
                            next = next.withDayOfMonth(day.get(j + 1));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // one day per month given; starts at future day in same month
                    if (first && prev.getDayOfMonth() < day.get(0) && prevMonth == month.get(i)) {
                        next = next.withDayOfMonth(day.get(0));
                        poll.setNextSeries(next);
                        first = false;
                    }
                    // next month of the cycle
                    if (first && prevMonth < month.get(i + 1)) {
                        next = next.withDayOfMonth(day.get(0));
                        next = next.withMonth(month.get(i + 1));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // more than one day per month given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.getDayOfMonth() < day.get(j + 1) && first && (prevMonth == month.get(0) || prevMonth == month.get(month.size() - 1))) {
                        next = next.withDayOfMonth(day.get(j + 1));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // one day and one month given; starts at future day in same year
                if (first && ((prev.getDayOfMonth() < day.get(0) && prevMonth == month.get(0)) || prevMonth < month.get(0))) {
                    next = next.withMonth(month.get(0));
                    next = next.withDayOfMonth(day.get(0));
                    poll.setNextSeries(next);
                    first = false;
                }
                // end of cycle reached
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
                            if (first && prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(j) && prevMonth == month.get(i)) {
                                LOGGER.info("8");
                                next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                                poll.setNextSeries(next);
                                first = false;
                            }
                        } // end day
                        // one day more than one week and more than one month given, starts on future date in same week
                        if (first && prev.getDayOfWeek().getValue() < day.get(0) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(j) && prevMonth == month.get(i)) {
                            LOGGER.info("9");
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                        // next week / only one day given
                        if (first && (prev.getDayOfWeek().getValue() == day.get(0) || prev.getDayOfWeek().getValue() == day.get(day.size() - 1)) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(j) && prevMonth == month.get(i)) {
                            LOGGER.info("3");
                            next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(j + 1), DayOfWeek.of(day.get(0))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    } // end week
                    // one day, more than one week and more than one month given, starts on future date in same week
                    if (first && getWeekOfMonthWithDay(prev, day.get(0)) < week.get(0) && prevMonth == month.get(i)) {
                        LOGGER.info("15");
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(getWeekOfMonthWithDay(prev, day.get(0)), DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                    // more than one day per week given
                    for (int k = 0; k < day.size() - 1; k++) {
                        // next day in week cycle
                        if (first && prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(week.size() - 1) && prevMonth == month.get(i)) {
                            LOGGER.info("7");
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // end of week cycle reached / one day, one week, more than one month given
                    if (first && prev.getDayOfWeek().getValue() < day.get(day.size() - 1) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(week.size() - 1) && prevMonth == month.get(i)) {
                        LOGGER.info("4");
                        next = next.withMonth(month.get(i + 1));
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                // end month
                // one month but more than one week and more than one day
                for (int j = 0; j < week.size() - 1; j++) {
                    for (int k = 0; k < day.size() - 1; k++) {
                        // next day in week cycle
                        if (first && prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(j) && prevMonth == month.get(month.size() - 1)) {
                            LOGGER.info("6");
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                            poll.setNextSeries(next);
                            first = false;
                        }
                    }
                    // next week / only one day given
                    if (first && (getWeekOfMonthWithDay(prev, day.get(0)) == week.get(j) || getWeekOfMonthWithDay(prev, day.get(day.size() - 1)) == week.get(j)) && prevMonth == month.get(month.size() - 1)) {
                        LOGGER.info("2");
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(j + 1), DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                    /* if (first && getWeekOfMonthWithDay(prev, day.get(0)) < week.get(j) && prevMonth == month.get(month.size() - 1)) { TODO: unnötig?
                        LOGGER.info("13");
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(j), DayOfWeek.of(day.get(0))));
                        poll.setNextSeries(next);
                        first = false;
                    } */
                }
                if (first && getWeekOfMonthWithDay(prev, day.get(0)) < week.get(week.size() - 1) && prevMonth == month.get(0)) {
                    LOGGER.info("14");
                    next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(week.size() - 1), DayOfWeek.of(day.get(0))));
                    poll.setNextSeries(next);
                    first = false;
                }
                // more than one day given, only one week and one month given
                for (int k = 0; k < day.size() - 1; k++) {
                    // next day in week cycle
                    if (first && prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(week.size() - 1) && prevMonth == month.get(month.size() - 1)) {
                        LOGGER.info("5");
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                        poll.setNextSeries(next);
                        first = false;
                    }
                }
                /*if (first && prevMonth < month.get(month.size() - 1)) { TODO: unnötig?
                    LOGGER.info("16");
                    next = next.withMonth(month.get(month.size() - 1));
                    next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                    poll.setNextSeries(next);
                    first = false;
                }*/
                // one day one week and one month given; starts at future day in same year
                if (first && ((prev.getDayOfWeek().getValue() < day.get(0) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(0) && prevMonth == month.get(0)) || // nur tag kleiner rest passt
                    (getWeekOfMonthWithDay(prev, day.get(0)) < week.get(0) && prevMonth == month.get(0)) ||
                    prevMonth < month.get(0)))
                {
                    LOGGER.info("12");
                    next = next.withMonth(month.get(0));
                    next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                    poll.setNextSeries(next);
                    first = false;
                }
                // one day, one week, one month given or end of cycle reached
                if (first) {
                    LOGGER.info("1");
                    next = next.plusYears(repeat);
                    next = next.withMonth(month.get(0));
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

    ZonedDateTime deltaNew(ZonedDateTime originalStart, ZonedDateTime originalEnd, ZonedDateTime nowStart) {
        Duration delta = Duration.between(originalStart.toLocalDate(), originalEnd.toLocalDate());
        ZonedDateTime nowEnd = nowStart.plus(delta);
        return nowEnd;
    }

    private int getWeekOfMonth(ZonedDateTime date) {
        ZonedDateTime firstOfMonth = date.withDayOfMonth(1);
        return 1 + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - firstOfMonth.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    private int getWeekOfMonthWithDay(ZonedDateTime date, int day) {
        ZonedDateTime firstOfMonth = date.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.of(day)));
        return 1 + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - firstOfMonth.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }
}
