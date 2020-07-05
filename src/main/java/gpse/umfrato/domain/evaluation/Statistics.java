package gpse.umfrato.domain.evaluation;

import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.evaluation.session.SessionService;
import gpse.umfrato.domain.evaluation.filter.filterimpl.*;
import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;

import java.util.*;
import java.util.logging.Logger;

/**
 * this class represents the entire evaluation process from gathering data,
 * filtering and processing it and converting it to JSON for transfer to the frontend.
 */
public class Statistics {

    private static final String NAME_STRING = "{\"name\":\"";
    private static final String PARTICIPANTS_STRING = "\",\"particpantCount\":\"";
    private static final Logger LOGGER = Logger.getLogger("Statistics");
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
     * This function is not Autowired and needs to be filled manually.
     * @param questionService a questionServiceImpl
     * @param pollService a pollServiceImpl
     * @param pollResultService a pollResultServiceImpl
     * @param categoryService a categoryServiceImpl
     * @param consistencyQuestionService a consistencyQuestionServiceImpl
     * @param data filter one containing the pollId and a selection of questionIds if the questionIds is a List of only
     *             one (-1) the questionList will get filled with all questions contained in the poll
     */
    public Statistics(final QuestionService questionService, final PollService pollService, final PollResultService pollResultService, final CategoryService categoryService, final ConsistencyQuestionService consistencyQuestionService, final SessionService sessionService, final FilterCmd data) {
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
        this.consistencyQuestionService = consistencyQuestionService;
        this.sessionService = sessionService;
        pollId = data.getBasePollId();
        if (data.getTimeDiagram() == null) {
            showParticipantsOverTime = false;
        } else {
            showParticipantsOverTime = data.getTimeDiagram();
        }
        final List<Category> categories = categoryService.getAllCategories(pollId);
        questionIds.addAll(data.getBaseQuestionIds());
        if (!data.getBaseQuestionIds().isEmpty() && data.getBaseQuestionIds().get(0).equals(-1L)) {
            for (final Category c: categories) {
                for (final Question q: questionService.getAllQuestions(c.getCategoryId())) {
                    questionIds.add(q.getQuestionId());
                }
            }
            /* LOGGER.info(questionIds.toString());*/
        }
    }

    /**
     * constructs a list of filters from the input.
     * @param input a list of filterCmd
     */
    public void loadFilter(final List<FilterCmd> input) {
        filters = new ArrayList<>();
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
                    final List<Filter> orFilter = new ArrayList<>();
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

    /**
     * loads the filterList from a session.
     * @param sessionId of the session to
     * @return whether there have been loaded any filters or not
     */
    public boolean loadSessionFilters(final Long sessionId) {
        filters = new ArrayList<>();
        loadFilter(sessionService.getFilters(sessionId));
        return !filters.isEmpty();
    }

    /**
     * returns the filtered list of pollResults to use by other functions like the exportController.
     * @return a filtered list of pollResults
     */
    public List<PollResult> filteredResults() {
        List<PollResult> prs = pollResultService.getPollResults(pollId);
        for (final Filter f: filters) {
            prs = f.filter(prs);
        }
        return prs;
    }

    /**
     * returns the processed Poll as a JSON to display it in the frontend.
     * @return a filtered list of pollResults
     */
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
        final Poll poll = pollService.getPoll(pollId);
        final String response = NAME_STRING + poll.getPollName() + PARTICIPANTS_STRING + participantCountFiltered + "/" + participantCountUnfiltered + "\",\"questionList\": ";
        if (prs.isEmpty()) {
            LOGGER.warning("Leere Umfrage");
            return response + "[]}";
        }
        final DiagramData dd = new DiagramData(poll, prs, showParticipantsOverTime, questionIds, categoryService, questionService);
        return response + dd.toJSON() + "}";
    }
}
