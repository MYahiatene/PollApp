package gpse.umfrato.domain.evaluation;

import gpse.umfrato.domain.evaluation.filterblocks.filterimpl.Filter;
import gpse.umfrato.domain.evaluation.filterblocks.filterimpl.QuestionFilter;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;

import java.util.*;
import java.util.logging.Logger;

public class Statistics {

    private static final double MEDIAN_QUANTILE = 0.5;
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

    public Statistics(final AnswerService answerService, final UserService userService,
                      final QuestionService questionService, final PollService pollService,
                      final PollResultService pollResultService, final CategoryService categoryService,
                      final FilterCmd data) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
        pollId = Long.valueOf(data.getBasePollId());
        if (data.getBaseQuestionIds().isEmpty()) {
            for (final Question q: questionService.getAllQuestions(pollId)) {
                questionIds.add(q.getQuestionId());
            }
        } else {
            for (final String qid: data.getBaseQuestionIds()) {
                questionIds.add(Long.valueOf(qid));
            }
        }
    }

    public void loadFilter(final List<FilterCmd> input) {
        for (final FilterCmd cmd:input) {
            Filter filter = null;
            if (cmd.getFilterType().equals("questionAnswer")) {
                filter = new QuestionFilter(Long.valueOf(cmd.getTargetPollId()),
                        Long.valueOf(cmd.getTargetQuestionId()), cmd.getTargetAnswerPossibilities(), false);
            }
            if (filter != null) {
                filters.add(filter);
            }
        }
    }

    public String generateDiagram() {
        if (pollId == null) {
            return "{\"name\":\"Ungültige Umfrage\"}";
        }
        List<PollResult> prs = pollResultService.getPollResults(pollId);
        if (prs.isEmpty()) {
            return "{\"name\":\"" + pollService.getPoll(pollId.toString()).getPollName() + "\",\"questionList\": []}";
        }
        for (final Filter f:filters) {
            prs = f.filter(prs);
        }
        final DiagramData dd = new DiagramData(pollService.getPoll(prs.get(0).getPollId().toString()), prs,
                questionService);
        return "{\"name\":\"" + pollService.getPoll(pollId.toString()).getPollName() + "\",\"questionList\": "
                + dd.toJSON() + "}";
    }

    /**
     * This method takes a absolute value and converts it to a relative value.
     * @param value absolute value.
     * @param totalNumber total number of values.
     * @return relative value.
     */
    public static double getRelativeFrequencyOfOneValue(final double value,
                                                        final double totalNumber) throws ArithmeticException {
        if (totalNumber < value) {
            throw new ArithmeticException("totalNumber must be larger than value!");
        }
        return value / totalNumber;
    }

    // Maybe list<pollresult>, depends
    public static double getRelativeFrequencyOfOneValue(final String value,
                                                        final double totalNumber) throws ArithmeticException {
        return getRelativeFrequencyOfOneValue(Double.parseDouble(value), totalNumber);
    }

    /**
     * This method takes a List of double values that represent an absolute number
     * and converts each of them into the corresponding relative value.
     * @param values all absolute values that were given in a poll.
     * @return a list of relative values.
     */
    /*public static List<Double> getRelativeFrequencyOfDoubleValues(List< Double> values){
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
    }*/

    public static List<List<Double>> getRelativeFrequencyOfDoubleValues(final List<PollResult> values) {

        final List<Double> totalNumbers = new ArrayList<>();
        ListIterator<PollResult> answerIterator = values.listIterator();
        while (answerIterator.hasNext()) {
            final PollResult next = answerIterator.next();
            final ListIterator<Answer> answersForOneUser = next.getAnswerList().listIterator();
            while (answersForOneUser.hasNext()) {
                double totalNumber = 0;
                final Answer nextAnswer = answersForOneUser.next();
                totalNumber += Double.parseDouble(nextAnswer.getGivenAnswerList().get(0));
                totalNumbers.add(totalNumber);
            }
        }
        //We've got a list of totalNumbers for each of the PollResults --> Each PollResult has one totalNumber
        final List<List<Double>> listOfValues = new ArrayList<>();
        answerIterator = values.listIterator();
        while (answerIterator.hasNext()) {
            final PollResult next = answerIterator.next();
            final ListIterator<Answer> answersForOneUser = next.getAnswerList().listIterator();
            while (answersForOneUser.hasNext()) {
                final List<Double> innerValues = new ArrayList<>();
                final double totalNumber = 0;
                final Answer nextAnswer = answersForOneUser.next();
                for (int i = 0; i < nextAnswer.getGivenAnswerList().size(); i++) {
                    innerValues.add(getRelativeFrequencyOfOneValue(nextAnswer.getGivenAnswerList().get(i),
                            totalNumber));
                }
                listOfValues.add(innerValues);
            }
        }
        return listOfValues;
    }

    /** DONT NEED THAT SINCE WE'RE USING POLLRESULTS
     * This method uses normalizeAllDoubleValues to convert absolute numbers into relative numbers.
     * It casts the Integer values into double values first in order to use the method.
     * @param values a list of absolute integer values.
     * @return a list of corresponding relative double values.
     */
    /*public static List<Double> getRelativeFrequencyOfAllIntegerValues(List<Integer> values){

        List<Double> listOfDoubleValues = new ArrayList<>();

        for (int i = 0; i < values.size() ; i++) {
            listOfDoubleValues.add((double) values.get(i));
        }

        return getRelativeFrequencyOfDoubleValues(listOfDoubleValues);
    }*/


    /**
     * This function returns the modus of a list of absolute values.
     * @param allValues absolute values.
     * @return the highest value.
     */
    /*public static double modus(List<Double> allValues){

        double currentHighest = allValues.get(0);

        for (int i = 0; i < allValues.size(); i++) {
            if(allValues.get(i)>currentHighest){
                currentHighest = allValues.get(i);
            }
        }

        return currentHighest;

    }*/

    public static List<Double> modus(final List<PollResult> allValues) {
        final List<Double> modi = new ArrayList<>();
        // Iterate over answers
        for (final PollResult allValue: allValues) {
            // Iterate over questions for answer i
            for (int j = 0; j < allValue.getAnswerList().size(); j++) {
                // If Question j from answer i is higher than the current highest set that element
                if (Double.parseDouble(allValue.getAnswerList().get(j).getGivenAnswerList().get(0)) > modi.get(j)) {
                    modi.set(j, Double.parseDouble(allValue.getAnswerList().get(j).getGivenAnswerList().get(0)));
                }
            }
        }
        return modi;

    }

    /**
     * This function casts the provided value to an integer and limits it to a range of zero to max minus one
     * to avoid out of range exceptions while accessing arrays or lists.
     * @param val the value to limit or the possibly out of bounds index to access.
     * @param max one above the biggest value val can be or the size of the array to access.
     * @return the constricted integer.
     */
    private static <T> int constrict(final T val, int max) {
        max -= 1;
        final int casted = Integer.parseInt((String) val);
        if (casted < 0) {
            return 0;
        }
        return Math.min(casted, max);
    }


    private List<List<Answer>> toNormalList(final List<PollResult> input) {
        final List<List<Answer>> outputList = new ArrayList<>();
        Answer[][] output = new Answer[input.size()][input.get(0).getAnswerList().size()];
        for (int i = 0; i < input.size(); i++) { //Iterate over singular pollResults
            for (int j = 0; j < input.get(i).getAnswerList().size(); j++) { //Iterate over singular Answers
                output[i][j] = input.get(i).getAnswerList().get(j);
            }
        }

        Answer[][] intermediateList = new Answer[input.get(0).getAnswerList().size()][input.size()];

        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i).getAnswerList().size(); j++) {
                //Transpose array so that columns are Arrays of answers for one question
                intermediateList[j][i] = input.get(i).getAnswerList().get(j);
            }
        }
        for (int i = 0; i < input.size(); i++) {
            List<Answer> intermediate;
            intermediate = Arrays.asList(intermediateList[i]);
            outputList.add(intermediate);
        }
        return outputList;
    }

    private List<Double> toFirstValuesList(final List<Answer> input) {
        final List<Double> allFirstValues = new ArrayList<>();
        for (final Answer answer: input) {
            final Double next = Double.parseDouble(answer.getGivenAnswerList().get(0));
            allFirstValues.add(next);
        }
        return allFirstValues;
    }

    /**
     * This function returns the p-Quantile of the provided values.
     * If the list is empty the function returns null.
     * If p is below zero or above one it will be set to zero or one respectively.
     * list of values to pick the quantile from. The values will be sorted inside the function.
     * @param p the parameter to calculate the quantile for example p=0.5 equals the median and p=1 equals the maximum.
     * @return the value corresponding to the p.
     **/
    /*private static Double pQuantile(List<Double> values, double p)
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
    }*/

    private List<Double> pQuantile(final List<PollResult> allValues, double p) {
        if (allValues.isEmpty()) {
            return null;
        }
        if (p < (double) 0) {
            p = 0;
        } else if (p > (double) 1) {
            p = 1;
        }

        final List<List<Answer>> answers = toNormalList(allValues);
        final List<Double> quantiles = new ArrayList<>();
        final ListIterator<List<Answer>> answerIterator = answers.listIterator();
        while (answerIterator.hasNext()) {
            final List<Answer> questions = answerIterator.next();
            /**Now we can operate on a list of answers for one singular question at a goddamn time*/
            // final ListIterator<Answer> questionIterator = questions.listIterator();
            /**Build up a thing that makes a sortable list of all the first elements of this list*/
            final List<Double> allFirstValues = toFirstValuesList(questions);
            Collections.sort(allFirstValues);
            final int size = allFirstValues.size();
            double xnp;
            final double xnp1 = allFirstValues.get(constrict(size * p, allFirstValues.size()));
            if (size * p % 1.0 == (double) 0) {
                xnp = allFirstValues.get(constrict(size * p - 1, allFirstValues.size()));
                quantiles.add((xnp + xnp1) / 2);
            } else {
                quantiles.add(xnp1);
            }
        }
        return quantiles;
    }

    /**
    * This function returns the median of a given list of values, if the list is not empty.
    * @param values list of values to calculate the median from.
    * @return will return null for empty lists, otherwise will return the median of given list of values.
    */
    /*private static Double median(List<Double> values)
    {
        return pQuantile(values, 0.5);
    }*/

    private List<Double> median(final List<PollResult> values) {
        return pQuantile(values, MEDIAN_QUANTILE);
    }

    /**
     * This function cumulates a list of generic types and returns the percentage of values being under the given
     * threshold.
     * @param values list of values to cumulate.
     * @param threshold to use in function.
     * @param <T> generic type of items used in values.
     * @return cumulated values
     */
    private <T extends Number> double cumulate(final List<T> values, final T threshold) { //Kumulierte Häufigkeit
        double cumulated = 0;
        final Iterator<T> listIterator = values.listIterator();
        while (listIterator.hasNext()) {
            if (listIterator.next().doubleValue() < threshold.doubleValue()) {
                cumulated++;
            }
        }
        return cumulated;
    }

    //Kumulierte Häufigkeit
    private List<List<Double>> cumulate(final List<PollResult> pollAnswers, final Double threshold) {
        final List<List<Double>> outputList = new ArrayList<>();
        final Iterator<PollResult> listIterator = pollAnswers.listIterator();
        while (listIterator.hasNext()) {
            final List<Double> intermediateList = new ArrayList<>();
            final PollResult next = listIterator.next();
            final Iterator<Answer> answerIterator = next.getAnswerList().listIterator();
            while (answerIterator.hasNext()) {
                double cumulated = 0;
                final Answer individualAnswer = answerIterator.next();
                final ListIterator<String> checkBoxIterator = individualAnswer.getGivenAnswerList().listIterator();
                while (checkBoxIterator.hasNext()) {
                    if (Double.parseDouble(checkBoxIterator.next()) < threshold) {
                        cumulated++;
                    } //Only take first element, won't work with anything else anyway
                }
                intermediateList.add(cumulated);
            }
            outputList.add(intermediateList);
        }
        return outputList;
    }

    public static <T> List<Answer> filterByAnswer(final List<Answer> input, final List<T> wantedAnswers) {
        final ListIterator<Answer> iter = input.listIterator();
        final List<Answer> output = new ArrayList<>();
        while (iter.hasNext()) {
            final Answer index = iter.next();
            if (!Collections.disjoint(index.getGivenAnswerList(), wantedAnswers)) {
                output.add(index);
            }
        }
        return output;
    }

    public static <T> List<Answer> filterByUser(final String pollID, final String username) {
        /**TODO: PollResultServiceImpl NEEDS POLLRESULTS ELSE FILTERING BY USER WONT WORK*/
        //List<PollResult> unfilteredResults = pollResultService.getAllPollResults();
        //List<Answer> filteredResults = pollResultService.getUserAnswers(unfilteredResults, username);
        //return filteredResults;
        return null;
    }
}
