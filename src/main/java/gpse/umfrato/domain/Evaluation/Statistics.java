package gpse.umfrato.domain.Evaluation;

import gpse.umfrato.domain.Evaluation.FilterBlocks.FilterImpl.DataFilter;
import gpse.umfrato.domain.Evaluation.FilterBlocks.FilterImpl.Filter;
import gpse.umfrato.domain.Evaluation.FilterBlocks.FilterImpl.QuestionFilter;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.pollresult.PollResultServiceImpl;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.logging.Logger;

public class Statistics {

    private static final Logger LOGGER = Logger.getLogger("EvaluationController");
    private final AnswerService answerService;
    private final UserService userService;
    private final QuestionService questionService;
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final CategoryService categoryService;
    private List<Filter> filters = new ArrayList<>();
    private Long pollId;
    private final List<Long> questionIds = new ArrayList<>();

    public Statistics(final AnswerService answerService, final UserService userService, final QuestionService questionService, final PollService pollService, final PollResultService pollResultService, final CategoryService categoryService, FilterCmd data) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
        pollId = Long.valueOf(data.getBasePollId());
        for(String qid:data.getBaseQuestionIds())
        {
            questionIds.add(Long.valueOf(qid));
        }
    }

public void loadFilter(List<FilterCmd> input)
    {
        for(FilterCmd cmd:input)
        {
            Filter f = null;
            if(cmd.getFilterType().equals("questionAnswer"))
            {
                f = new QuestionFilter(Long.valueOf(cmd.getTargetPollId()),Long.valueOf(cmd.getTargetQuestionId()),cmd.getTargetAnswerPossibilities(),false);
            }
            if(f != null)
            {
                filters.add(f);
            }
        }
    }

    public String generateDiagram()
    {
        List<PollResult> prs = pollResultService.getPollResults(pollId);
        for(Filter f:filters) {
            prs = f.filter(prs);
        }
        System.out.println("Gefiltert " + prs.size());
        DiagramData dd = new DiagramData(pollService.getPoll(prs.get(0).getPollId().toString()),prs,questionService);
        return "{\"name\":\"" + pollService.getPoll(pollId.toString()).getPollName() + "\",\"questionList\": " + dd.toJSON() + "}";
    }

    /**
     * This function cumulates a list of generic types and returns the percentage of values being under the given threshold.
     * @param values list of values to cumulate.
     * @param threshold to use in function.
     * @param <T> generic type of items used in values.
     * @return
     */
    private <T extends Number> double cumulate(List<T> values, T threshold){ //Kumulierte Häufigkeit
        double cumulated = 0;
        Iterator<T> listIterator = values.listIterator();
        while(listIterator.hasNext()){
            if(listIterator.next().doubleValue() < threshold.doubleValue()) { cumulated++; }
        }
        return cumulated;
    }

    public static <T> List<Answer> filterByAnswer(List<Answer> input, List<T> wantedAnswers){
        ListIterator<Answer> iter = input.listIterator();
        List<Answer> output = new ArrayList<>();
        while(iter.hasNext()){
            Answer index = iter.next();
            if(!Collections.disjoint(Arrays.asList(index.getGivenAnswerList()), wantedAnswers))
                output.add(index);
        }
        return output;
    }

    public static <T> List<Answer> filterByUser(String pollID, String username){
        /**TODO: PollResultServiceImpl NEEDS POLLRESULTS ELSE FILTERING BY USER WONT WORK*/
        //List<PollResult> unfilteredResults = pollResultService.getAllPollResults();
        //List<Answer> filteredResults = pollResultService.getUserAnswers(unfilteredResults, username);
        //return filteredResults;
        return null;
    }
}
