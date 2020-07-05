package gpse.umfrato.domain.evaluation;

import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.evaluation.Session.SessionService;
import gpse.umfrato.domain.evaluation.filter.FilterData;
import gpse.umfrato.domain.evaluation.filter.filterimpl.*;
import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Logger;

/**
 * this class represents the entire evaluation process from gathering data,
 * filtering and processing it and converting it to JSON for transfer to the frontend.
 */
public class Statistics {

    private static final String NAME_STRING = "{\"name\":\"";
    private static final String PARTICIPANTS_STRING = "\",\"particpantCount\":\"";
    private static final double MEDIAN_QUANTILE = 0.5;
    private static final Logger LOGGER = Logger.getLogger("Statistics");
    private final AnswerService answerService;
    private final UserService userService;
    private final QuestionService questionService;
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final CategoryService categoryService;
    private final SessionService sessionService;
    private final ConsistencyQuestionService consistencyQuestionService;
    private List<Filter> filters = new ArrayList<>();
    private final Long pollId;
    private final List<Long> questionIds = new ArrayList<>();
    private final boolean showParticipantsOverTime;

    /**
     * Initializes the Statistic.
     * @param answerService
     * @param userService
     * @param questionService
     * @param pollService
     * @param pollResultService
     * @param categoryService
     * @param consistencyQuestionService
     * @param data
     */
    public Statistics(final AnswerService answerService, final UserService userService, final QuestionService questionService, final PollService pollService, final PollResultService pollResultService, final CategoryService categoryService, final ConsistencyQuestionService consistencyQuestionService, SessionService sessionService, final FilterCmd data) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
        this.consistencyQuestionService = consistencyQuestionService;
        this.sessionService = sessionService;
        pollId = data.getBasePollId();
        if(data.getTimeDiagram() == null) {
            showParticipantsOverTime = false;
        }
        else {
            showParticipantsOverTime = data.getTimeDiagram();
        }
        final List<Category> categories = categoryService.getAllCategories(pollId);
        questionIds.addAll(data.getBaseQuestionIds());
        if ((!data.getBaseQuestionIds().isEmpty()) && data.getBaseQuestionIds().get(0).equals(-1L)) {
            for (final Category c: categories) {
                for (final Question q: questionService.getAllQuestions(c.getCategoryId())) {
                    questionIds.add(q.getQuestionId());
                }
            }
            /* LOGGER.info(questionIds.toString());*/
        }
    }

    public void loadFilter(final List<FilterCmd> input) {
        for (final FilterCmd cmd: input) {
            Filter filter = null;
            switch (cmd.getFilterType()) {
                case "questionAnswer":
                    filter = new QuestionFilter(pollId, cmd.getTargetQuestionId(), cmd.getTargetAnswerPossibilities(), cmd.getInvertFilter(),cmd.getIsSlider(),  false);
                    break;
                case "consistency":
                    filter = new ConsistencyFilter(consistencyQuestionService.getAllConsistencyQuestions(pollId), cmd.getMinSuccesses());
                    break;
                case "date":
                    filter = new DateFilter(cmd.getStartDate(), cmd.getEndDate(), cmd.getInvertFilter());
                    break;
                case "user":
                    if(pollService.getPoll(pollId).getAnonymityStatus().equals("2"))
                    {
                        filter = new UserFilter(cmd.getUserNames(), cmd.getInvertFilter());
                    }
                    break;
                case "or":
                    List<Filter> orFilter = new ArrayList<>();
                    for(int i = 0; i < filters.size();i++) {
                        if (filters.get(i).getFilterType().equals("date")) {
                            orFilter.add(filters.get(i));
                            filters.remove(i);
                            i--;
                        }
                    }
                    filter = new OrFilter(orFilter);
                    break;
                default:
                    break;
            }
            if (filter != null) {
                filters.add(filter);
            }
        }
    }

    public boolean loadSessionFilters(final Long sessionId) {
        filters = new ArrayList<>();
        loadFilter(sessionService.getFilters(sessionId));
        return filters.size() > 0;
    }

    public List<PollResult> filteredResults()
    {
        List<PollResult> prs = pollResultService.getPollResults(pollId);
        for (final Filter f: filters) {
            prs = f.filter(prs);
        }
        return prs;
    }

    public String generateDiagram() {
        if (pollId == null) {
            LOGGER.warning("Ungültige Umfrage");
            return "{\"name\":\"Ungültige Umfrage\"}";
        }
        List<PollResult> prs = pollResultService.getPollResults(pollId);
        final int participantCountUnfiltered = prs.size();
        for (final Filter f: filters) {
            prs = f.filter(prs);
        }
        final int participantCountFiltered = prs.size();
        LOGGER.info(prs.toString());
        final Poll p = pollService.getPoll(pollId);
        final String response = NAME_STRING + p.getPollName() + PARTICIPANTS_STRING + participantCountFiltered + "/" + participantCountUnfiltered + "\",\"questionList\": ";
        if (prs.isEmpty()) {
            LOGGER.warning("Leere Umfrage");
            return response + "[]}";
        }
        final DiagramData dd = new DiagramData(p, prs, showParticipantsOverTime, questionIds, categoryService, questionService);
        return response + dd.toJSON() + "}";
    }
}
