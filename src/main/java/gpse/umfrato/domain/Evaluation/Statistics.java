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
                f = new QuestionFilter(Long.valueOf(cmd.getTargetPollId()),Long.valueOf(cmd.getTargetQuestionId()),cmd.getTargetAnswerPossibilities(),true);
            }
            if(f != null)
            {
                System.out.println("addFilter");
                filters.add(f);
            }
        }
    }

    public String generateDiagram()
    {
        List<PollResult> prs = pollResultService.getPollResults(pollId);
        System.out.println(prs.size());
        System.out.println(filters.size());
        for(Filter f:filters) {
            prs = f.filter(prs);
        }
        System.out.println(prs.size());
        DiagramData dd = new DiagramData(pollService.getPoll(prs.get(0).getPollId().toString()),prs,questionService);
        return "{\"name\":\"" + pollService.getPoll(pollId.toString()).getPollName() + "\",\"questionList\": " + dd.toJSON() + "}";
    }

    /**
     * This method takes a absolute value and converts it to a relative value.
     * @param value absolute value.
     * @param totalNumber total number of values.
     * @return relative value.
     */
    public static double getRelativeFrequencyOfOneValue(double value, double totalNumber) throws ArithmeticException {
        if(totalNumber<value){
            throw new ArithmeticException("totalNumber must be larger than value!");
        }
        return value / totalNumber;
    }

    public static double getRelativeFrequencyOfOneValue(String value, double totalNumber) throws ArithmeticException { /** Maybe list<pollresult>, depends */
        if(totalNumber<Integer.parseInt(value)){
            throw new ArithmeticException("totalNumber must be larger than value!");
        }
        return Integer.parseInt(value) / totalNumber;
    }

    /**
     * This method takes a List of double values that represent an absolute number
     * and converts each of them into the corresponding relative value.
     * @param values all absolute values that were given in a poll.
     * @return a list of relative values.
     */
    public static List<Double> getRelativeFrequencyOfDoubleValues(List< Double> values){
        double totalNumber = 0;
        for (int i = 0; i < values.size(); i++) {
            totalNumber += values.get(i);
        }

        List<Double> listOfValues = new ArrayList<>();

        for (int i = 0; i < values.size() ; i++) {
            try {
                listOfValues.add(getRelativeFrequencyOfOneValue(values.get(i), totalNumber));
            }
            catch (Exception e)
            {
                return null;
            }
        }

        return listOfValues;
    }

    public static List<Double> getRelativeFrequencyOfDoubleValues(Answer values){ /**Needs to be answer because PollResult contains list of answers*/
        double totalNumber = 0;
        for (int i = 0; i < values.getGivenAnswerList().size(); i++) {
            totalNumber += Integer.parseInt(values.getGivenAnswerList().get(i));
        }

        List<Double> listOfValues = new ArrayList<>();

        for (int i = 0; i < values.getGivenAnswerList().size() ; i++) {
            try {
                listOfValues.add(getRelativeFrequencyOfOneValue(values.getGivenAnswerList().get(i), totalNumber));
            }
            catch (Exception e)
            {
                return null;
            }
        }

        return listOfValues;
    }

    /**
     * This method uses normalizeAllDoubleValues to convert absolute numbers into relative numbers.
     * It casts the Integer values into double values first in order to use the method.
     * @param values a list of absolute integer values.
     * @return a list of corresponding relative double values.
     */
    public static List<Double> getRelativeFrequencyOfAllIntegerValues(List<Integer> values){

        List<Double> listOfDoubleValues = new ArrayList<>();

        for (int i = 0; i < values.size() ; i++) {
            listOfDoubleValues.add((double) values.get(i));
        }

        return getRelativeFrequencyOfDoubleValues(listOfDoubleValues);
    }

    /**
     * This function returns the modus of a list of absolute values.
     * @param allValues absolute values.
     * @return the highest value.
     */
    public static double modus(List<Double> allValues){

        double currentHighest = allValues.get(0);

        for (int i = 0; i < allValues.size(); i++) {
            if(allValues.get(i)>currentHighest){
                currentHighest = allValues.get(i);
            }
        }

        return currentHighest;

    }

    public static double modus(Answer allValues){

        double currentHighest = Double.parseDouble(allValues.getGivenAnswerList().get(0));

        for (int i = 0; i < allValues.getGivenAnswerList().size(); i++) {
            if(Double.parseDouble(allValues.getGivenAnswerList().get(i))>currentHighest){
                currentHighest = Double.parseDouble(allValues.getGivenAnswerList().get(i));
            }
        }

        return currentHighest;

    }

    /**
     * This function casts the provided value to an integer and limits it to a range of zero to max minus one
     * to avoid out of range exceptions while accessing arrays or lists.
     * @param val the value to limit or the possibly out of bounds index to access.
     * @param max one above the biggest value val can be or the size of the array to access.
     * @return the constricted integer.
     */
    private static <T> int constrict(T val, int max)
    {
        max -= 1;
        int casted = Integer.parseInt(val.toString()); /**Ugly but effective*/
        if(casted < 0)
        {
            return 0;
        }
        if(casted > max)
        {
            return max;
        }
        return casted;
    }

    /**
     * This function returns the p-Quantile of the provided values.
     * If the list is empty the function returns null.
     * If p is below zero or above one it will be set to zero or one respectively.
     * @param values list of values to pick the quantile from. The values will be sorted inside the function.
     * @param p the parameter to calculate the quantile for example p=0.5 equals the median and p=1 equals the maximum.
     * @return the value corresponding to the p.
     **/
    private static Double pQuantile(List<Double> values, double p)
    {
        if(values.isEmpty())
        {
            return null;
        }
        if(p < 0.0)
        {
            p = 0.0;
        }
        else if(p > 1.0)
        {
            p = 1.0;
        }
        Collections.sort(values);
        int n = values.size();
        double xnp = 0.0;
        double xnp1 = values.get(constrict(n * p, values.size()));
        if(n * p % 1.0 == 0.0)
        {
            xnp = values.get(constrict((n * p) - 1,values.size()));
            return (xnp + xnp1) / 2;
        }
        else
        {
            return xnp1;
        }
    }

    private static Double pQuantile(Answer values, double p)
    {
        if(values.getGivenAnswerList().isEmpty())
        {
            return null;
        }
        if(p < 0.0)
        {
            p = 0.0;
        }
        else if(p > 1.0)
        {
            p = 1.0;
        }
        Collections.sort(values.getGivenAnswerList());
        int n = values.getGivenAnswerList().size();
        double xnp = 0.0;
        double xnp1 = Double.parseDouble(values.getGivenAnswerList().get(constrict(n * p, values.getGivenAnswerList().size())));
        if(n * p % 1.0 == 0.0)
        {
            xnp = Double.parseDouble(values.getGivenAnswerList().get(constrict((n * p) - 1,values.getGivenAnswerList().size())));
            return (xnp + xnp1) / 2;
        }
        else
        {
            return xnp1;
        }
    }

    /**
    * This function returns the median of a given list of values, if the list is not empty.
    * @param values list of values to calculate the median from.
    * @return will return null for empty lists, otherwise will return the median of given list of values.
    */
    private static Double median(List<Double> values)
    {
        return pQuantile(values, 0.5);
    }

    private static Double median(Answer values)
    {
        return pQuantile(values, 0.5);
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

    private double cumulate(Answer values, Double threshold){ //Kumulierte Häufigkeit
        double cumulated = 0;
        Iterator<String> listIterator = values.getGivenAnswerList().listIterator();
        while(listIterator.hasNext()){
            if(Double.parseDouble(listIterator.next()) < threshold.doubleValue()) { cumulated++; }
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
