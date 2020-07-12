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

import javax.persistence.EntityNotFoundException;
import java.time.ZoneId;
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
    private final Long pollId;
    private final List<Long> questionIds = new ArrayList<>();
    private final boolean showParticipantsOverTime;
    private final boolean participantsOverRelativeTime;
    private final ZoneId timeZone;
    private final Integer numberOfPastPollsToEvaluate;
    private List<Filter> filters = new ArrayList<>();

    /**
     * Initializes the Statistic.
     * This function is not Autowired and needs to be filled manually.
     *
     * @param questionService            a questionServiceImpl
     * @param pollService                a pollServiceImpl
     * @param pollResultService          a pollResultServiceImpl
     * @param categoryService            a categoryServiceImpl
     * @param consistencyQuestionService a consistencyQuestionServiceImpl
     * @param sessionService             a sessionServiceImpl
     * @param timeZone                   the timeZone of the client
     * @param data                       filter one containing the pollId and a selection of questionIds if the
     *                                   questionIds is a List of only
     *                                   one (-1) the questionList will get filled with all questions contained in
     *                                   the poll
     *
     */
    public Statistics(final QuestionService questionService, final PollService pollService,
                      final PollResultService pollResultService, final CategoryService categoryService,
                      final ConsistencyQuestionService consistencyQuestionService, final SessionService sessionService,
                      final ZoneId timeZone, final FilterCmd data) {
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
        this.consistencyQuestionService = consistencyQuestionService;
        this.sessionService = sessionService;
        this.timeZone = timeZone;
        pollId = data.getBasePollId();
        if (data.getTimeDiagram() == null) {
            showParticipantsOverTime = false;
        } else {
            showParticipantsOverTime = data.getTimeDiagram();
        }
        if (data.getTimeDiagramRelative() == null) {
            participantsOverRelativeTime = false;
        } else {
            participantsOverRelativeTime = data.getTimeDiagramRelative();
        }
        if (data.getNumberOfEvaluatedSeriesPolls() == null) {
            numberOfPastPollsToEvaluate = -1;
        } else {
            numberOfPastPollsToEvaluate = data.getNumberOfEvaluatedSeriesPolls();
        }
        final List<Category> categories = categoryService.getAllCategories(pollId);
        questionIds.addAll(data.getBaseQuestionIds());
        if (!data.getBaseQuestionIds().isEmpty() && data.getBaseQuestionIds().get(0).equals(-1L)) {
            for (final Category c : categories) {
                for (final Question q : questionService.getAllQuestions(c.getCategoryId())) {
                    questionIds.add(q.getQuestionId());
                }
            }
            /* LOGGER.info(questionIds.toString());*/
        }
    }

    /**
     * constructs a list of filters from the input.
     *
     *
     * @param input a list of filterCmd
     */
    public void loadFilter(final List<FilterCmd> input) {
        filters = new ArrayList<>();
        for (final FilterCmd cmd : input) {
            Filter filter = null;
            switch (cmd.getFilterType()) {
                case "questionAnswer":
                    filter = new QuestionFilter(pollId, cmd.getTargetQuestionId(), cmd.getTargetAnswerPossibilities(),
                        cmd.getInvertFilter(), cmd.getIsSlider(), false);
                    break;
                case "consistency":
                    filter = new ConsistencyFilter(consistencyQuestionService.getAllConsistencyQuestions(pollId),
                        cmd.getMinSuccesses());
                    break;
                case "date":
                    filter = new DateFilter(cmd.getStartDate(), cmd.getEndDate(), cmd.getInvertFilter());
                    break;
                case "user":
                    if (pollService.getPoll(pollId).getAnonymityStatus().equals("3")) {
                        filter = new UserFilter(cmd.getUserNames(), cmd.getInvertFilter());
                    }
                    break;
                case "or":
                    final List<Filter> orFilter = new ArrayList<>();
                    for (int i = 0; i < filters.size(); i++) {
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
     *
     *
     * @param sessionId of the session to
     * @return whether or not there have been loaded any filters
     */
    public boolean loadSessionFilters(final Long sessionId) {
        filters = new ArrayList<>();
        loadFilter(sessionService.getFilters(sessionId));
        return !filters.isEmpty();
    }

    /**
     * returns the filtered list of pollResults to use by other functions like the exportController.
     *
     *
     * @return a filtered list of pollResults
     */
    public List<PollResult> filteredResults() {
        List<PollResult> prs = pollResultService.getPollResults(pollId);
        for (final Filter f : filters) {
            prs = f.filter(prs);
        }
        return prs;
    }

    /**
     *
     * returns the processed Poll as a JSON to display it in the frontend.
     *
     * @return a filtered list of pollResults
     */
    public String generateDiagram() {
        if (pollId == null) {
            LOGGER.warning("Ungültige Umfrage");
            return "{\"name\":\"Ungültige Umfrage\"}";
        }
        String response;
        final List<DiagramData> diagramDataList = new ArrayList<>();
        int prev = 0;
        Poll poll = pollService.getPoll(pollId);
        List<Long> questionIds = this.questionIds;
        int participantCountUnfiltered = 0;
        int participantCountFiltered = 0;
        while(numberOfPastPollsToEvaluate == -1 || prev <= numberOfPastPollsToEvaluate) {
            List<PollResult> prs = pollResultService.getPollResults(poll.getPollId());
            if(poll.getPollId().equals(pollId)) {
                participantCountUnfiltered = prs.size();
            }
            for (final Filter f: filters) {
                prs = f.filter(prs);
            }
            if(poll.getPollId().equals(pollId)) {
                participantCountFiltered = prs.size();
            }
            if (prs.size() > 0)
            {
                final DiagramData dd = new DiagramData(poll, prs, showParticipantsOverTime,
                    participantsOverRelativeTime, questionIds, timeZone, categoryService, questionService);
                diagramDataList.add(dd);
            }
            Poll nextPoll;
            try {
                nextPoll = pollService.getPoll(poll.getPrevInSeries());
            } catch (EntityNotFoundException e) {
                break;
            }
            questionIds = translateQuestionIds(poll, questionIds, nextPoll);
            LOGGER.info("prev " + poll.getPollId());
            poll = nextPoll;
            LOGGER.info("next " + poll.getPollId());
            prev++;
        }
        response = NAME_STRING + pollService.getPoll(this.pollId).getPollName() + PARTICIPANTS_STRING +
                participantCountFiltered + "/" + participantCountUnfiltered + "\",\"questionList\": ";
        if (diagramDataList.isEmpty()) {
            return response + "[]}";
        }
        final DiagramData dd = combineDiagramData(diagramDataList);
        return response + dd.toJSON() + "}";
    }

    /**
     * This method matches the questionIds of the original Poll to the questionIds of an other Poll by rating the
     * equality of each question.
     * This is necessary to allow the User to correct mistakes in the question and to allow the participant to
     * add answer-possibilities
     *
     *
     *
     * @param original            the original poll
     * @param originalQuestionIds the original questionIds that will be translated
     * @param pollToTranslateTo   the other poll to translate the question-ids to
     * @return a list of question-ids matching the originals but translated to the context of pollToTranslateTo
     */
    private List<Long> translateQuestionIds(final Poll original, final List<Long> originalQuestionIds,
                                            final Poll pollToTranslateTo) {
        final List<Long> translatedIds = new ArrayList<>();
        for (final Long qid : originalQuestionIds) {
            int categoryIndex = 0;
            for (final Category oc : original.getCategoryList()) {
                int questionIndex = 0;
                for (final Question oq : oc.getQuestionList()) {
                    if (oq.getQuestionId().equals(qid)) {
                        final Question translatedQuestion = pollToTranslateTo.getCategoryList().get(categoryIndex)
                            .getQuestionList().get(questionIndex);
                        final double questionConfidence = questionSimilarity(oq, translatedQuestion);
                        if (questionSimilarity(oq, translatedQuestion) > 0.8) {
                            translatedIds.add(translatedQuestion.getQuestionId());
                            LOGGER.info("+ORIGINAL: " + oq.getQuestionMessage() + " " + oq.getQuestionId());
                            LOGGER.info("+TRANSATED: " + translatedQuestion.getQuestionMessage() + " "
                                + translatedQuestion.getQuestionId());
                        } else {
                            Question bestMatch = translatedQuestion;
                            double bestConfidence = questionConfidence;
                            for (final Category tc : pollToTranslateTo.getCategoryList()) {
                                for (final Question tq : tc.getQuestionList()) {
                                    final double newQuestionConfidence = questionSimilarity(oq, tq);
                                    if (newQuestionConfidence > bestConfidence) {
                                        bestConfidence = newQuestionConfidence;
                                        bestMatch = tq;
                                    }
                                }
                            }
                            translatedIds.add(bestMatch.getQuestionId());
                            LOGGER.info(">ORIGINAL: " + oq.getQuestionMessage() + " " + oq.getQuestionId());
                            LOGGER.info(">TRANSATED: " + bestMatch.getQuestionMessage() + " " + bestMatch
                                .getQuestionId());
                        }
                    }
                    questionIndex++;
                }
                categoryIndex++;
            }
        }
        LOGGER.info("Original IDs: " + originalQuestionIds.toString());
        LOGGER.info("Translated IDs: " + translatedIds.toString());
        return translatedIds;
    }

    /**
     * This method rates the similarity of question a and question b by comparing the question-message and the
     * answer-possibilities.
     *
     *
     *
     * @param a question a
     * @param b question b
     * @return a similarity value between 0 (not equal at all) and 1 (completely identical)
     */
    private double questionSimilarity(final Question a, final Question b) {
        if (a.getQuestionType().equals(b.getQuestionType())) {
            double confidence = stringSimilarity(a.getQuestionMessage(), b.getQuestionMessage());
            if (a.getAnswerPossibilities().isEmpty()) {
                double answerConfidence = 0.0;
                for (final String as : a.getAnswerPossibilities()) {
                    for (final String bs : b.getAnswerPossibilities()) {
                        answerConfidence += stringSimilarity(as, bs);
                    }
                }
                confidence += answerConfidence / (Math.max(a.getAnswerPossibilities().size(),
                    b.getAnswerPossibilities().size()));
                confidence += a.getNumberOfPossibleAnswers() == b.getNumberOfPossibleAnswers() ? 1.0 : 0.0;
                return confidence / 3;
            }
            return confidence;
        }
        return 0.0;
    }

    /**
     * This method rates the equality of two strings a and b between 0 and 1.
     *
     * @param a string a
     * @param b string b
     * @return a similarity value between 0 (not equal at all) and 1 (completely identical)
     */
    private double stringSimilarity(final String a, final String b) {
        if (a.equals(b)) {
            return 1.0;
        }
        String longer = a;
        String shorter = b;
        if (a.length() < b.length()) {
            longer = b;
            shorter = a;
        }
        final int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    private static int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();

        int[] costs = new int[b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (a.charAt(i - 1) != b.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[b.length()] = lastValue;
            }
        }
        return costs[b.length()];
    }

    /**
     * This method combines a list of DiagramData's to one DiagramData to display serialPolls in a single Evaluation.
     *
     * @param diagramDataList a list of DiagramData's
     * @return a combination of all DiagramData's
     */
    private DiagramData combineDiagramData(final List<DiagramData> diagramDataList) {
        while (diagramDataList.size() > 1) {
            diagramDataList.get(0).combine(diagramDataList.get(1));
            diagramDataList.remove(1);
        }
        if(showParticipantsOverTime) {
            diagramDataList.get(0).generateParticipantsOverTime();
        }
        return diagramDataList.get(0);
    }
}
