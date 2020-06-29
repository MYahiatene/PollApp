package gpse.umfrato.domain.evaluation;

import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
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
    private final ConsistencyQuestionService consistencyQuestionService;
    private final List<Filter> filters = new ArrayList<>();
    private final Long pollId;
    private final List<Long> questionIds = new ArrayList<>();
    private final boolean showParticipantsOverTime;

    public Statistics(final AnswerService answerService, final UserService userService, final QuestionService questionService, final PollService pollService, final PollResultService pollResultService, final CategoryService categoryService, final ConsistencyQuestionService consistencyQuestionService, final FilterCmd data) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
        this.consistencyQuestionService = consistencyQuestionService;
        pollId = data.getBasePollId();
        if(data.getTimeDiagram() == null) {
            showParticipantsOverTime = false;
        }
        else {
            showParticipantsOverTime = data.getTimeDiagram();
        }
        final List<Category> categories = categoryService.getAllCategories(pollId);
        if (data.getBaseQuestionIds().isEmpty()) {
            for (final Category c: categories) {
                for (final Question q: questionService.getAllQuestions(c.getCategoryId())) {
                    questionIds.add(q.getQuestionId());
                }
            }
            /* LOGGER.info(questionIds.toString());*/
        } else {
            questionIds.addAll(data.getBaseQuestionIds());
        }
    }

    public void loadFilter(final List<FilterCmd> input) {
        for (final FilterCmd cmd: input) {
            Filter filter = null;
            switch (cmd.getFilterType()) {
                case "questionAnswer":
                    filter = new QuestionFilter(pollId, cmd.getTargetQuestionId(), cmd.getTargetAnswerPossibilities(), cmd.getInvertFilter(), false);
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
                    for(Filter f:filters) {
                        if (f.getFilterType().equals("date")) {
                            orFilter.add(f);
                            filters.remove(f);
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
