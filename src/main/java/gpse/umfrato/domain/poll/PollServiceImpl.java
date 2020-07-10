package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.pollresult.PollResultRepository;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.evaluation.session.SessionService;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Logger;

@Service
class PollServiceImpl implements PollService {

    private static final Logger LOGGER = Logger.getLogger("PollServiceImpl");
    private static final String STANDRDKATEGORIE = "Standardkategorie";
    private final CategoryRepository categoryRepository;
    private final PollRepository pollRepository;
    private final CategoryService categoryService;
    private final QuestionService questionService;
    private final SessionService sessionService;
    private final PollResultService pollResultService;
    private final ParticipationLinkService participationLinkService;
    private final ConsistencyQuestionService consistencyQuestionService;
    private final PollResultRepository pollResultRepository;
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
                           final CategoryRepository categoryRepository, final SessionService sessionService,
                           final PollResultService pollResultService,
                           final ParticipationLinkService participationLinkService,
                           final ConsistencyQuestionService consistencyQuestionService,
                           final PollResultRepository pollResultRepository, final QuestionService questionService) {
        this.pollRepository = pollRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.pollResultRepository = pollResultRepository;
        this.sessionService = sessionService;
        this.pollResultService = pollResultService;
        this.participationLinkService = participationLinkService;
        this.consistencyQuestionService = consistencyQuestionService;
        this.questionService = questionService;
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
        categoryService.createCategory(STANDRDKATEGORIE, poll.getPollId());
        return poll;
    }

    /**
     * Saves the new poll in the repository.
     *
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
     * This method creates the first poll from a series of polls and sets the standard for the series.
     * @param poll
     * @return created poll
     */
    @Override
    @Transactional
    public Poll createSeriesPoll(final Poll poll) {
        poll.setSeriesPollName(poll.getPollName());
        poll.setPollName(createSeriesPollName(poll));
        poll.setNextSeries(calculateNextDate(poll));
        LOGGER.info("next series: " + parseDate(poll.getNextSeries()));
        pollRepository.save(poll);
        categoryService.createCategory(STANDRDKATEGORIE, poll.getPollId());
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
        final Poll poll = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new);
        return checkActivationAndDeactivation(poll);
    }

    @Override
    public void newLastEdit(Long pollId, ZonedDateTime lastEditAt, String lastEditFrom, ZoneId timeZone) {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy&HH:mm");
        final Poll poll = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new);
        // final ZonedDateTime lastEditAtZone = LocalDateTime.parse(lastEditAt, df).atZone(timeZone);
        poll.setLastEditAt(lastEditAt);
        LOGGER.info("lastEditedAt" + lastEditAt);
        poll.setLastEditFrom(lastEditFrom);
        LOGGER.info("lastEditedFrom" + lastEditFrom);
        pollRepository.save(poll);
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
     *
     * @param pollId the id of the poll which will be increased
     * @return returns the poll activation status
     */
    @Override
    public Integer increasePollStatus(final Long pollId) {
        LOGGER.info("increasePollStatus");
        final Poll poll = pollRepository.getOne(pollId);
        switch (poll.getPollStatus()) {
            case 0:
                poll.setPollStatus(1);
                break;
            case 1:
                poll.setPollStatus(2);
                if (poll.getLevel() == -1) {
                    LOGGER.info("hier");
                    poll.setActivatedDate(ZonedDateTime.now());
                }
                break;
            case 2:
                LOGGER.info("increase to deactivation");
                LOGGER.info("poll: " + poll.toString());
                poll.setPollStatus(3);
                if (poll.getLevel() != -1) {
                    LOGGER.info("poll level: " + poll.getLevel());
                    if (!stoppingReason(poll)) {
                        LOGGER.info("create next series Poll");
                        createNextSeriesPoll(poll);
                        poll.setLevel(-1);
                        pollRepository.save(poll);
                    } else {
                        LOGGER.info("end series");
                        poll.setLevel(-1);
                    }
                }
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
     *
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
     *
     * @param pollId   the id of the poll which name will be edited
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

    @Override
    public List<Poll> getLastEditedPolls() {
        return pollRepository.findTop5ByOrderByLastEditAt();
    }

    /**
     * Reformat the date from Calendar to a better readable String.
     *
     * @param date Calendar with date and time information
     * @return date as a String
     */
    @Override
    public String parseDate(final ZonedDateTime date) {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy&HH:mm", Locale.GERMAN);
        return df.format(date);
    }

    /**
     * Checks if a poll should be automatically activated/deactivated and if the activationDAte/deactivationDate is
     * reached, the pollStatus is updated.
     *
     * @param poll the poll to check
     * @return updated Poll
     */
    @Override
    public Poll checkActivationAndDeactivation(final Poll poll) {
        LOGGER.info("check");
        final ZonedDateTime now = ZonedDateTime.now();
        if ((poll.isActivated() || poll.getLevel() != -1)
            && poll.getPollStatus() == 1 && poll.getActivatedDate().isBefore(now)) {
            LOGGER.info("activate");
            increasePollStatus(poll.getPollId());
        }
        if (poll.getPollStatus() == 2 && poll.getDeactivatedDate().isBefore(now)) {
            if (poll.isDeactivated()) {
                LOGGER.info("deactivate1");
                increasePollStatus(poll.getPollId());
            } else if (poll.getLevel() != -1) {
                LOGGER.info("deactivate2");
                increasePollStatus(poll.getPollId());
            }
        }
        return pollRepository.findById(poll.getPollId()).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * This method sets the current data in placeholders in the title of a poll for series polls.
     * @param poll
     * @return the pollname
     */
    @Override
    public String createSeriesPollName(final Poll poll) {
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
            name = name.replace(":kw:", String.valueOf(poll.getActivatedDate().get(
                IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
        }
        if (name.contains(":wt:")) {
            name = name.replace(":wt:", poll.getActivatedDate().getDayOfWeek().getDisplayName(TextStyle.FULL,
                Locale.GERMAN));
        }
        return name;
    }

    /**
     * This method calculates the activation date for the next poll in a series from given data days, weeks, months,
     * the level of reputation (days, weeks, months, years) and the reputation number.
     * @param poll
     * @return next activation date
     */
    @Override
    public ZonedDateTime calculateNextDate(final Poll poll) {
        LOGGER.info("Start calculateNextDate");
        // TODO: ähnliche if schleifen zu Methoden extrahieren für bessere übersichtlichkeit
        final int repeat = poll.getRepeat();
        final List<Integer> day = poll.getDay();
        final List<Integer> week = poll.getWeek();
        final List<Integer> month = poll.getMonth();
        final int level = poll.getLevel();
        final ZonedDateTime prev = poll.getActivatedDate();
        final boolean checkLeapYear = poll.getCheckLeapYear();
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
            return next;
        } else if (level == 1) {
            // days per x weeks given
            // if there are later days within the cycle, the next activation date will be the next day in days[]
            for (int i = 0; i < day.size() - 1; i++) {
                if (prev.getDayOfWeek().getValue() < day.get(i + 1)) {
                    next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(i + 1))));
                    return next;
                }
            }
            // if the last day of the cycle is reached the new day will be in the given repeated time at the first day
            // of the cycle
            next = next.plusWeeks(repeat - 1);
            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
            return next;
        } else if (level == 2) {
            if (week.size() == 0) {
                LOGGER.info("no week given");
                // days per x months given
                // if there are later days within the cycle, the next activation date will be the next day in days[]
                for (int i = 0; i < day.size() - 1; i++) {
                    if (prev.getDayOfMonth() < day.get(i + 1)) {
                        next = saveDayOfMonth(next, day.get(i + 1));
                        return next;
                    }
                }
                // if the last day of the cycle is reached the new day will be in the given repeated time at the first
                // day of the next cycle
                next = next.plusMonths(repeat);
                next = saveDayOfMonth(next, day.get(0));
                return next;
            } else {
                LOGGER.info("day, week and month given");
                LOGGER.info("day of the week: " + prev.getDayOfWeek().getValue());
                LOGGER.info("week of the month: " + getWeekOfMonthWithDay(prev, 1));
                // days in weeks per months
                // more than one week and more than one day per week given
                for (int i = 0; i < week.size() - 1; i++) {
                    LOGGER.info("hier");
                    for (int j = 0; j < day.size() - 1; j++) {
                        LOGGER.info("hier rein");
                        if (prev.getDayOfWeek().getValue() < day.get(j + 1) && getWeekOfMonthWithDay(prev, day.get(0))
                            <= week.get((i + 1) % week.size())) {
                            LOGGER.info("next day in week");
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                            return next;
                        }
                    }
                    if (getWeekOfMonthWithDay(prev, day.get(0)) == week.get(i)) {
                        LOGGER.info("last day in week");
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(i + 1), DayOfWeek.of(day.get(0))));
                        return next;
                    }
                }
                // more than one day per week given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.getDayOfWeek().getValue() < day.get(j + 1) && getWeekOfMonthWithDay(prev, day.get(0))
                        == week.get(0)) {
                        LOGGER.info("only days, adds day");
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                        return next;
                    }
                }
                // last day of cycle reached, calculates the first day of the new one
                LOGGER.info("next month");
                next = next.plusMonths(repeat);
                next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                return next;
            }
        } else if (level == 3) {
            // days per year given
            LOGGER.info("day of the year" + prev.getDayOfYear());
            LOGGER.info("week of the year" + prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
            if (week.size() == 0 && month.size() == 0) {
                for (int i = 0; i < day.size() - 1; i++) {
                    if (prev.getDayOfYear() == day.get(i)) {
                        next = next.plusDays(day.get(i + 1) - day.get(i));
                        final LocalDate ld = next.toLocalDate();
                        if (checkLeapYear && ld.isLeapYear() && day.get(i + 1) > 59) {
                            next = next.plusDays(1);
                        }
                        return next;
                    }
                }
                next = next.plusYears(repeat);
                next = next.withDayOfYear(day.get(0));
                final LocalDate ld = next.toLocalDate();
                if (checkLeapYear && ld.isLeapYear() && day.get(day.size() - 1) > 59) {
                    next = next.plusDays(1);
                }
                return next;
            } else if (month.size() == 0) {
                // days of calendar weeks per year
                // more than one day and more than one week given
                for (int i = 0; i < week.size() - 1; i++) {
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.getDayOfWeek().getValue() < day.get(j + 1) && prev.get(
                            IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(i)) {
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                            return next;
                        }
                    }
                    // next week of the cycle
                    if (prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) < week.get(i + 1)) {
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                        next = next.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week.get(i + 1));
                        return next;
                    }
                }
                // more than one day per week given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.getDayOfWeek().getValue() < day.get(j + 1) && (prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                        == week.get(0) || prev.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week.get(week.size() - 1))) {
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(j + 1))));
                        return next;
                    }
                }
                // end of cycle reached
                next = next.plusYears(repeat);
                next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                next = next.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week.get(0));
                return next;
            } else if (week.size() == 0) {
                final int prevMonth = prev.getMonthValue();
                // days per month given
                // more than one day and more than one month given
                for (int i = 0; i < month.size() - 1; i++) {
                    for (int j = 0; j < day.size() - 1; j++) {
                        if (prev.getDayOfMonth() < day.get(j + 1) && prevMonth == month.get(i)) {
                            next = saveDayOfMonth(next, day.get(j + 1));
                            return next;
                        }
                    }
                    // one day per month given; starts at future day in same month
                    if (prev.getDayOfMonth() < day.get(0) && prevMonth == month.get(i)) {
                        next = saveDayOfMonth(next, day.get(0));
                        return next;
                    }
                    // next month of the cycle
                    if (prevMonth < month.get(i + 1)) {
                        next = saveDayOfMonth(next, day.get(0));
                        next = next.withMonth(month.get(i + 1));
                        return next;
                    }
                }
                // more than one day per month given
                for (int j = 0; j < day.size() - 1; j++) {
                    if (prev.getDayOfMonth() < day.get(j + 1) && (prevMonth == month.get(0) || prevMonth == month
                        .get(month.size() - 1))) {
                        next = saveDayOfMonth(next, day.get(j + 1));
                        return next;
                    }
                }
                // one day and one month given; starts at future day in same year
                if (((prev.getDayOfMonth() < day.get(0) && prevMonth == month.get(0)) || prevMonth < month.get(0))) {
                    next = next.withMonth(month.get(0));
                    next = saveDayOfMonth(next, day.get(0));
                    return next;
                }
                // end of cycle reached
                next = next.plusYears(repeat);
                next = saveDayOfMonth(next, day.get(0));
                next = next.withMonth(month.get(0));
                return next;
            } else {
                final int prevMonth = prev.getMonthValue();
                LOGGER.info("day of week: " + prev.getDayOfWeek().getValue());
                LOGGER.info("week of month: " + getWeekOfMonthWithDay(prev, 1));
                LOGGER.info("month: " + prevMonth);
                // days per weeks per months per years
                // more than one day, more than one week and more than one month given
                for (int i = 0; i < month.size() - 1; i++) {
                    for (int j = 0; j < week.size() - 1; j++) {
                        for (int k = 0; k < day.size() - 1; k++) {
                            // next day in week cycle
                            if (prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day
                                .get(0)) == week.get(j) && prevMonth == month.get(i)) {
                                next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                                return next;
                            }
                        } // end day
                        // one day more than one week and more than one month given, starts on future date in same week
                        if (prev.getDayOfWeek().getValue() < day.get(0) && getWeekOfMonthWithDay(prev, day.get(0))
                            == week.get(j) && prevMonth == month.get(i)) {
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(0))));
                            return next;
                        }
                        // next week / only one day given
                        if ((prev.getDayOfWeek().getValue() == day.get(0) || prev.getDayOfWeek().getValue()
                            == day.get(day.size() - 1)) && getWeekOfMonthWithDay(prev, day.get(0)) == week.get(j)
                            && prevMonth == month.get(i)) {
                            next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(j + 1), DayOfWeek.of(
                                day.get(0))));
                            return next;
                        }
                    } // end week
                    // one day, more than one week and more than one month given, starts on future date in same week
                    if (getWeekOfMonthWithDay(prev, day.get(0)) < week.get(0) && prevMonth == month.get(i)) {
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(getWeekOfMonthWithDay(prev, day.get(0)),
                            DayOfWeek.of(day.get(0))));
                        return next;
                    }
                    // more than one day per week given
                    for (int k = 0; k < day.size() - 1; k++) {
                        // next day in week cycle
                        if (prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day.get(0))
                            == week.get(week.size() - 1) && prevMonth == month.get(i)) {
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                            return next;
                        }
                    }
                    // end of week cycle reached / one day, one week, more than one month given
                    if (prev.getDayOfWeek().getValue() < day.get(day.size() - 1) && getWeekOfMonthWithDay(prev,
                        day.get(0)) == week.get(week.size() - 1) && prevMonth == month.get(i)) {
                        next = next.withMonth(month.get(i + 1));
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                        return next;
                    }
                }
                // end month
                // one month but more than one week and more than one day
                for (int j = 0; j < week.size() - 1; j++) {
                    for (int k = 0; k < day.size() - 1; k++) {
                        // next day in week cycle
                        if (prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day.get(0))
                            == week.get(j) && prevMonth == month.get(month.size() - 1)) {
                            next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                            return next;
                        }
                    }
                    // next week / only one day given
                    if ((getWeekOfMonthWithDay(prev, day.get(0)) == week.get(j) || getWeekOfMonthWithDay(prev,
                        day.get(day.size() - 1)) == week.get(j)) && prevMonth == month.get(month.size() - 1)) {
                        next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(j + 1), DayOfWeek.of(day.get(0))));
                        return next;
                    }
                }
                if (getWeekOfMonthWithDay(prev, day.get(0)) < week.get(week.size() - 1) && prevMonth == month.get(0)) {
                    next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(week.size() - 1),
                        DayOfWeek.of(day.get(0))));
                    return next;
                }
                // more than one day given, only one week and one month given
                for (int k = 0; k < day.size() - 1; k++) {
                    // next day in week cycle
                    if (prev.getDayOfWeek().getValue() < day.get(k + 1) && getWeekOfMonthWithDay(prev, day.get(0))
                        == week.get(week.size() - 1) && prevMonth == month.get(month.size() - 1)) {
                        next = next.with(TemporalAdjusters.next(DayOfWeek.of(day.get(k + 1))));
                        return next;
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
                if ((prev.getDayOfWeek().getValue() < day.get(0) && getWeekOfMonthWithDay(prev, day.get(0))
                    == week.get(0) && prevMonth == month.get(0)) || // nur tag kleiner rest passt
                    (getWeekOfMonthWithDay(prev, day.get(0)) < week.get(0) && prevMonth == month.get(0))
                    || prevMonth < month.get(0)) {
                    next = next.withMonth(month.get(0));
                    next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                    return next;
                }
                // one day, one week, one month given or end of cycle reached

                next = next.plusYears(repeat);
                next = next.withMonth(month.get(0));
                next = next.with(TemporalAdjusters.dayOfWeekInMonth(week.get(0), DayOfWeek.of(day.get(0))));
                return next;
            }
        }
        // poll.setActivatedDate(next);
        return next;
    }

    /**
     * This method gets the current week of the month, calculated from week where the given day first appears.
     * @param date
     * @param day
     * @return number of the week of the month
     */
    private int getWeekOfMonthWithDay(final ZonedDateTime date, final int day) {
        final ZonedDateTime firstOfMonth = date.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.of(day)));
        return 1 + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - firstOfMonth.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    /**
     * This method returns true, if a series of polls should be deactivated, i.e. when the deactivation reason is
     * reached.
     * @param poll
     * @return boolean for stopping the series
     */
    private boolean stoppingReason(final Poll poll) {
        switch (poll.getStoppingReason()) {
            case 0: /*Datum*/
                LOGGER.info("1");
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss",
                    Locale.GERMAN).withZone(poll.getActivatedDate().getZone());
                LOGGER.info("getRepeatUntil: " + poll.getRepeatUntil());
                final ZonedDateTime endTime = ZonedDateTime.parse(poll.getRepeatUntil() + " 23:59:59", formatter);
                return poll.getNextSeries().compareTo(endTime) > 0;
            case 1: /*Wiederholungen*/
                LOGGER.info("2");
                return poll.getSeriesCounter() >= Integer.parseInt(poll.getRepeatUntil());
            case 2: /*Teilnehmer*/
                LOGGER.info("3");
                final int size = pollResultService.getPollResults(poll.getPollId()).size();
                LOGGER.info("size of pollresults: " + size);
                return size < Integer.parseInt(poll.getRepeatUntil());
            default:
                return false;
        }
    }

    /**
     * This method calculates the deactivation date for a poll in a series from the previous poll of the series.
     * @param oldPoll
     * @param newPoll
     * @return deactivation date
     */
    private ZonedDateTime calculateNewDeactivation (final Poll oldPoll, final Poll newPoll) {
        LOGGER.info("calculateNewDeactivation");
        LOGGER.info("oldPoll: " + oldPoll);
        LOGGER.info("newPoll: " + newPoll);
        Period period = Period.between(oldPoll.getActivatedDate().toLocalDate(), oldPoll.getDeactivatedDate().toLocalDate());
        LOGGER.info("period: " + period.getDays());
        ZonedDateTime newDeactivation = newPoll.getActivatedDate().plusDays(period.getDays());
        newDeactivation = newDeactivation.withHour(newPoll.getDeactivatedDate().getHour());
        newDeactivation = newDeactivation.withMinute(newPoll.getDeactivatedDate().getMinute());
        return newDeactivation;
    }

    /**
     * This method initializes a new poll in a series from the data of the previous poll of the series and saves it in
     * the repository.
     * @param poll
     */
    private void createNextSeriesPoll(final Poll poll) {
        // TODO: lastEditFrom = null -> schlimm?
        LOGGER.info("createNextSeriesPoll start");
        LOGGER.info("old poll: " + poll);
        final Poll nextSeriesPoll = new Poll(poll.getPollCreator(), poll.getAnonymityStatus(), poll.getSeriesPollName(),
            poll.getCreationDate(), poll.getNextSeries(), poll.getDeactivatedDate(), 1,
            poll.getBackgroundColor(), poll.getFontColor(), poll.getLogo(), poll.isVisibility(),
            poll.isCategoryChange(),
            poll.isActivated(), poll.isDeactivated(), poll.getOwnDesign(), poll.getRepeat(), poll.getRepeatUntil(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), poll.getStoppingReason(), poll.getLevel(),
            poll.getSeriesCounter(), poll.getCheckLeapYear(), poll.getLastEditAt(), poll.getLastEditFrom());
        nextSeriesPoll.setDay(createElementCollectionCopies(poll.getDay()));
        nextSeriesPoll.setWeek(createElementCollectionCopies(poll.getWeek()));
        nextSeriesPoll.setMonth(createElementCollectionCopies(poll.getMonth()));
        pollRepository.save(nextSeriesPoll);
        LOGGER.info("newPoll: " + nextSeriesPoll);
        nextSeriesPoll.setLastEditAt(ZonedDateTime.now());
        final List<Category> categories = categoryService.getAllCategories(poll.getPollId());
        for (final Category category : categories) {
            final Category newCategory = categoryService.createCategory(category.getCategoryName(),
                nextSeriesPoll.getPollId());
            questionService.copyQuestions(newCategory.getCategoryId(), nextSeriesPoll.getPollId(),
                category.getQuestionList());
        }
        nextSeriesPoll.setLastEditFrom(poll.getLastEditFrom());
        nextSeriesPoll.setCreationDate(ZonedDateTime.now());
        nextSeriesPoll.setSeriesPollName(poll.getSeriesPollName());
        nextSeriesPoll.setSeriesCounter(nextSeriesPoll.getSeriesCounter() + 1);
        nextSeriesPoll.setPollName(createSeriesPollName(nextSeriesPoll));
        nextSeriesPoll.setPrevInSeries(poll.getPollId());
        nextSeriesPoll.setNextSeries(calculateNextDate(nextSeriesPoll));
        nextSeriesPoll.setDeactivatedDate(calculateNewDeactivation(poll, nextSeriesPoll));
        participationLinkService.updateLinks(poll.getPollId(), nextSeriesPoll.getPollId());
        pollRepository.save(nextSeriesPoll);
        LOGGER.info("new series poll: " + nextSeriesPoll);
    }

    /**
     * This method copies all elements from one list in a new list, used for ElementCollections tha can not be copied.
     * @param value
     * @return copied List
     */
    private List<Integer> createElementCollectionCopies(final List<Integer> value) {
        final List<Integer> copiedValues = new ArrayList<>();
        for (final Integer x : value) {
            copiedValues.add(x);
        }
        return copiedValues;
    }

    /**
     * This method sets the new day of the month and if its a leap year, calculates the day of the month with regard to
     * the leap year.
     * @param date
     * @param day
     * @return date with new day of the month
     */
    private ZonedDateTime saveDayOfMonth(final ZonedDateTime date, final int day) {
        try {
            return date.withDayOfMonth(day);
        } catch (DateTimeException d) {
            return saveDayOfMonth(date, day - 1);
        }
    }
}
