package gpse.umfrato.domain.evaluation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;

import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * This class represents the entire evaluation data needed to display the filtered data in the frontend .
 * It consists of a list of QuestionData each representing one question or diagram and the data needed to display it
 */
public class DiagramData {

    public static final String DIVIDER_STRING = " / ";
    private static final String HYPHEN = " - ";
    private static final Logger LOGGER = Logger.getLogger("DiagramData");

    private final List<QuestionData> questionList = new ArrayList<>();
    @JsonIgnore
    private final QuestionService questionService;
    @JsonIgnore
    private final CategoryService categoryService;
    @JsonIgnore
    private final Poll poll;
    @JsonIgnore
    private final List<Long> questionIds;
    @JsonIgnore
    private boolean showParticipantsOverTime = true;
    @JsonIgnore
    private ChoiceData participantsOverTime;

    interface QuestionData {
        enum QuestionType { CHOICE_QUESTION, TEXT_QUESTION, RANGE_QUESTION, SLIDER_QUESTION, SORT_QUESTION }

        long questionId(); //No 'get' to prevent Jackson from finding it

        QuestionType questionType(); //No 'get' to prevent Jackson from finding it

        void statistics();

        String toJSON();
    }

    @Getter
    @Setter
    protected static class ChoiceData implements QuestionData {
        private long id;
        private String title;
        private String type;
        private List<String> answerPossibilities;
        private List<Integer> data;
        private Calculation calculated = new Calculation();
        @JsonIgnore
        private double start = 0;
        @JsonIgnore
        private double step = 1;

        @Getter @Setter private static class Calculation {
            private List<Double> relative;
            private String median;
            private String mode;
        }

        ChoiceData(final long questionId, final String questionMessage, final List<String> answerPossibilities) {
            this.id = questionId;
            this.type = "choice";
            this.title = questionMessage;
            this.answerPossibilities = answerPossibilities;
            data = new ArrayList<>();
            for (final String ignored: answerPossibilities) {
                data.add(0);
            }
        }

        @Override
        public long questionId() {
            return this.id;
        }

        public void addAnswer(final double answerPossibility) {
            final int index = (int) ((answerPossibility - start) / step);
            data.set(index, data.get(index) + 1);
        }

        public void setModifier(final double start, final double step) {
            this.start = start;
            if (step != 0) {
                this.step = step;
            }
        }

        @Override
        public QuestionType questionType() {
            return QuestionType.CHOICE_QUESTION;
        }

        @Override
        public void statistics() {
            int size = 0;
            final List<Integer> maxima = new ArrayList<>();
            int max = 0;
            //Häufigste Antwort und Menge der Antworten raussuchen
            for (int i = 0; i < data.size(); i++) {
                size += data.get(i);
                if (data.get(i) > max) {
                    maxima.clear();
                    maxima.add(i);
                    max = data.get(i);
                } else if (data.get(i) == max) {
                    maxima.add(i);
                }
            }
            final StringBuilder modeText = new StringBuilder();
            for (final Integer i: maxima) {
                modeText.append(answerPossibilities.get(i)).append(DIVIDER_STRING);
            }
            modeText.replace(modeText.lastIndexOf(DIVIDER_STRING), modeText.length(), "");
            calculated.mode = modeText.toString();
            //Median ist an mittlerer Stelle
            double medianPos = size / 2.0;
            calculated.relative = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                //Relative Häufigkeiten nur Summe vor Division
                calculated.relative.add(data.get(i).doubleValue() / (double) size);
                medianPos -= data.get(i);
                // [1,1,1,1,1,1,2,2,2,3,3,3,4,6](originalDaten) => [0,6,3,3,1,0,1](data)
                // => 14(data.size) => 7(/2) => 1(-6) => -2(-3) => 2(Position) => Median
                if (medianPos <= 0 && calculated.median == null) {
                    if (size % 2 == 0 && medianPos == 0) {
                        if (i < answerPossibilities.size()) {
                            int j = i + 1;
                            while (j < data.size() - 1 && data.get(j) == 0) {
                                j++;
                            }
                            calculated.median = answerPossibilities.get(i) + (j >= data.size() ? "" : DIVIDER_STRING + answerPossibilities.get(j));
                        }
                    } else {
                        calculated.median = answerPossibilities.get(i);
                    }
                }
            }
        }

        @Override
        public String toJSON() {
            final ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Getter
    @Setter
    protected static class SortData implements QuestionData {
        private long id;
        private String title;
        private String type;
        private List<String> answerPossibilities;
        @JsonIgnore
        private List<List<Integer>> data = new ArrayList<>();
        private List<List<SortValues>> meanOrder = new ArrayList<>();

        SortData(final long questionId, final String questionMessage, final List<String> answerPossibilities) {
            this.id = questionId;
            this.type = "sort";
            this.title = questionMessage;
            this.answerPossibilities = answerPossibilities;
        }

        @Getter @Setter private static class SortValues {
            private int itemID;
            private String itemName;
            private List<Integer> wasAtPositionNumbers;
            private double meanPositionValue;
            private int meanPosition;
            private int twin;
            private double variance;
            private double standardDeviation;
        }

        void addAnswers(List<String> answers)
        {
            List<Integer> newAnswer = new ArrayList<>();
            for (String s :answers)
            {
                newAnswer.add(Integer.valueOf(s));
            }
            data.add(newAnswer);
        }

        @Override
        public long questionId() {
            return id;
        }

        @Override
        public QuestionType questionType() {
            return QuestionType.SORT_QUESTION;
        }

        @Override
        public void statistics() {

            if (data.size() == 0) {
                LOGGER.info("Keine Antworten");
                return;

            }


             if (answerPossibilities.size() != data.get(0).size()) {
                LOGGER.info("Fehlerhafte Daten");
                return;

            }



            // initializing a array that has size nxn with n = number of items

            List<SortValues> arrayOfValues = new ArrayList<>();

            for (int i = 0; i < data.get(0).size(); i++) {
                arrayOfValues.add(new SortValues());
                arrayOfValues.get(i).itemID = i;
                arrayOfValues.get(i).itemName = answerPossibilities.get(i);
                    arrayOfValues.get(i).wasAtPositionNumbers= new ArrayList<>();
                    arrayOfValues.get(i).meanPositionValue = 0;
                    arrayOfValues.get(i).meanPosition = -1;
                    arrayOfValues.get(i).twin = -1;
                    arrayOfValues.get(i).variance = 0;
                    arrayOfValues.get(i).standardDeviation = 0;


                for (int j = 0; j < data.get(0).size(); j++) {
                    arrayOfValues.get(i).wasAtPositionNumbers.add(0);
                }
            }

            // in this array we store for each item (i) and each position (j) how many times the item was placed in that position

            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.get(i).size(); j++) {
                    int index = data.get(i).get(j);
                    arrayOfValues.get(index).wasAtPositionNumbers.set(j,arrayOfValues.get(index).wasAtPositionNumbers.get(j) + 1);
                }
            }

            // Now we compute the mean order.

            // every item gets a mean position value computed from the number it was placed on a position times the index of that position

            for (int i = 0; i < arrayOfValues.size(); i++) {
                for (int j = 0; j < arrayOfValues.get(i).wasAtPositionNumbers.size(); j++) {
                    arrayOfValues.get(i).meanPositionValue += arrayOfValues.get(i).wasAtPositionNumbers.get(j) * (j + 1);
                }

                arrayOfValues.get(i).meanPositionValue =
                    arrayOfValues.get(i).meanPositionValue / data.size();
            }

            // we sort the items according to their mean position values and save the order in orderedItems

            int [] orderedItems = new int [data.get(0).size()];

            double lowestMeanPositionValue = arrayOfValues.size() * arrayOfValues.size();
            int firstItem = arrayOfValues.size() + 1;

            for (int j = 0; j < arrayOfValues.size(); j++) {
                for (int i = 0; i < arrayOfValues.size(); i++) {
                    if (arrayOfValues.get(i).meanPosition == -1) {
                        if (lowestMeanPositionValue >= arrayOfValues.get(i).meanPositionValue) {
                            if (lowestMeanPositionValue == arrayOfValues.get(i).meanPositionValue) {
                                if (firstItem != i) {
                                    LOGGER.info("item " + i + "is duplicate to " + firstItem);
                                    arrayOfValues.get(i).twin = firstItem;
                                }
                            }
                            lowestMeanPositionValue = arrayOfValues.get(i).meanPositionValue;
                            firstItem = i;
//                            console.log('i: ' + i)
//                            console.log('first: ' + firstItem)
                        }
                    }
                }

//                console.log('first ' + firstItem + 'position ' + j)

                arrayOfValues.get(firstItem).meanPosition = j;
                orderedItems[j] = firstItem;
                lowestMeanPositionValue = arrayOfValues.size() + 1;
            }

            // Now we compute the variance and standard deviation of the wasAtPositionNumbers

            for (int i = 0; i < arrayOfValues.size(); i++) {
                for (int j = 0; j < arrayOfValues.get(i).wasAtPositionNumbers.size(); j++) {
                    arrayOfValues.get(i).variance +=
                        arrayOfValues.get(i).wasAtPositionNumbers.get(j) *
                            Math.pow(j + 1 - arrayOfValues.get(i).meanPositionValue,2);
                }

                arrayOfValues.get(i).variance = arrayOfValues.get(i).variance / data.size();

                arrayOfValues.get(i).standardDeviation = Math.sqrt(arrayOfValues.get(i).variance);
            }

            // Now we build the meanOrder Array by pushing an Array of items for each position



            int position = 0;
            for (int i = 0; i < orderedItems.length; i++) {
//                meanOrder.push([])
                boolean twinsLeft = true;
                SortValues currentItem = arrayOfValues.get(orderedItems[i]);
//                console.log('arrayOfValues[orderedItems[i] with i as ' + i + 'is')
//                console.log(currentItem)
                meanOrder.add(new ArrayList<>());
                while (twinsLeft) {

                    meanOrder.get(position).add(currentItem);

//                    console.log('added item ' + currentItem.itemID + 'at position :' + position)
                    if (currentItem.twin == -1) {
//                        console.log('no twins for ' + i)
                        twinsLeft = false;
                    } else {
                        currentItem = arrayOfValues.get(currentItem.twin);
                        i++;
                    }
                }
                position++;
            }

//            console.log(this.arrayOfValues)
//            console.log('ordered')
//            console.log(orderedItems)
//            console.log('meanOrder: ')
//            console.log(this.meanOrder)

        }

        @Override public String toJSON() {
            final ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Getter
    @Setter
    protected static class TextData implements QuestionData {
        private long id;
        private String type;
        private String title;
        List<TextAnswer> answers = new ArrayList<>();

        @Getter @Setter @AllArgsConstructor private static class TextAnswer {
            private Long id;
            private String text;
            private String edited;
            private String creator;
        }

        TextData(final long questionId, final String questionMessage) {
            this.id = questionId;
            this.type = "text";
            this.title = questionMessage;
        }

        @Override
        public long questionId() {
            return this.id;
        }

        public void addAnswer(final Long id,final String text, final String editedDate, final String creator) {
            answers.add(new TextAnswer(id, text, editedDate, creator));
        }

        @Override
        public QuestionType questionType() {
            return QuestionType.TEXT_QUESTION;
        }

        @Override
        public void statistics() {

        }

        @Override
        public String toJSON() {
            final ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public DiagramData(final Poll poll, final List<PollResult> results, final boolean showParticipantsOverTime,
                       final List<Long> questionIds, final CategoryService categoryService,
                       final QuestionService questionService) {
        this.categoryService = categoryService;
        this.questionService = questionService;
        this.poll = poll;
        this.questionIds = questionIds;
        this.showParticipantsOverTime = showParticipantsOverTime;
        loadData(results);
    }

    private void loadData(final List<PollResult> results) {
        final List<Category> categories = categoryService.getAllCategories(poll.getPollId());
        for (final Category c: categories) {
            for (final Question q: questionService.getAllQuestions(c.getCategoryId())) {
                if (!questionIds.contains(q.getQuestionId())) {
                    continue;
                }
                // System.out.println(q.toString());
                QuestionData qd = null;
                final DecimalFormat format = new DecimalFormat("#.######", DecimalFormatSymbols.getInstance(Locale.GERMAN));
                switch (q.getQuestionType()) {
                    case "ChoiceQuestion":
                        qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), q.getAnswerPossibilities());
                        // System.out.println(qd.toString());
                        break;
                    case "TextQuestion":
                        qd = new TextData(q.getQuestionId(), q.getQuestionMessage());
                        // System.out.println(qd.toString());
                        break;
                    case "RangeQuestion":
                        final List<String> answerPossibilities = new ArrayList<>();
                        if (q.getBelowMessage() != null && !q.getBelowMessage().isEmpty()) {
                            answerPossibilities.add(q.getBelowMessage());
                        }
                        for (double i = q.getStartValue(); i < q.getEndValue(); ) {
                            answerPossibilities.add(format.format(i) + HYPHEN + (format.format(i += q.getStepSize())));
                        }
                        if (q.getAboveMessage() != null && !q.getAboveMessage().isEmpty()) {
                            answerPossibilities.add(q.getAboveMessage());
                        }
                        // System.out.println(answerPossibilities.toString());
                        qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), answerPossibilities);
                        // System.out.println(qd.toString());
                        break;
                    case "SliderQuestion":
                        final List<String> answerPossibilities2 = new ArrayList<>();
                        for (double i = q.getStartValue(); i < q.getEndValue(); ) {
                            answerPossibilities2.add(format.format(i));
                            i += q.getStepSize();
                        }
                        // System.out.println(answerPossibilities2.toString());
                        qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), answerPossibilities2);
                        ((ChoiceData) qd).setModifier(q.getStartValue(), q.getStepSize());
                        // System.out.println(qd.toString());
                        break;
                    case "SortQuestion":
                        qd = new SortData(q.getQuestionId(), q.getQuestionMessage(), q.getAnswerPossibilities());
                        // System.out.println(qd.toString());
                        break;
                    default:
                        break;
                }
                if (qd != null) {
                    questionList.add(qd);
                }
            }
        }
        final List<ZonedDateTime> datesList = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss",Locale.GERMANY);
        for (final PollResult pr: results) {
            if(showParticipantsOverTime) {
                datesList.add(ZonedDateTime.parse(pr.getLastEditAt(),df));
            }
            for (final Answer a: pr.getAnswerList()) {
                for (final QuestionData qd: questionList) {
                    if (qd.questionId() == a.getQuestionId()) {
                        switch (qd.questionType()) {
                            case CHOICE_QUESTION:
                                final ChoiceData cd = (ChoiceData) qd;
                                for (final String s: a.getGivenAnswerList()) {
                                    cd.addAnswer(Double.parseDouble(s));
                                }
                                break;
                            case TEXT_QUESTION:
                                if (!a.getGivenAnswerList().isEmpty()) {
                                    final TextData td = (TextData) qd;
                                    td.addAnswer(pr.getPollResultId(), a.getGivenAnswerList().get(a.getGivenAnswerList().size() - 1), pr.getLastEditAt(), pr.getPollTaker());
                                }
                                break;
                            case SORT_QUESTION:
                                if (!a.getGivenAnswerList().isEmpty()) {
                                    final SortData sd = (SortData) qd;
                                    sd.addAnswers(a.getGivenAnswerList());
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        if (showParticipantsOverTime) {
            if (datesList.isEmpty()) {
                showParticipantsOverTime = false;
            } else {
                ZonedDateTime min = datesList.get(0);
                ZonedDateTime max = datesList.get(0);
                for (final ZonedDateTime d: datesList) {
                    if (d.compareTo(max) > 0) {
                        max = d;
                    } else if (d.compareTo(min) < 0) {
                        min = d;
                    }
                }
                // System.out.println(min);
                // System.out.println(max);
                final long start = min.toEpochSecond();
                final long end = max.toEpochSecond();
                final long diff = end - start;
                long step = diff / 9;
                if (step < 1L) {
                    step = 1L;
                }
                // System.out.println(start);
                // System.out.println(end);
                // System.out.println(diff);
                // System.out.println(step);
                final List<String> answerPossibilities = new ArrayList<>();
                String patternString = "MM.yyyy";
                if (step < 60 * 60 * 24 * 30) {
                    patternString = "dd." + patternString;
                }
                if (step < 60 * 60 * 24) {
                    patternString += " HH";
                }
                if (step < 60 * 60) {
                    patternString += ":mm";
                }
                if (step < 60) {
                    patternString += ":ss";
                }
                // System.out.println(patternString);
                final DateTimeFormatter finalDf = DateTimeFormatter.ofPattern(patternString, Locale.GERMANY);

                for (ZonedDateTime date = min; date.compareTo(max) < 0; date.plusSeconds(step)) {
                    answerPossibilities.add(finalDf.format(date));
                }
                // System.out.println(answerPossibilities);
                participantsOverTime = new ChoiceData(0, "Teilnahmen über Zeit", answerPossibilities);
                for (final ZonedDateTime d: datesList) {
                    final long slot = (d.toEpochSecond() - start) / step;
                    participantsOverTime.addAnswer((double) slot);
                }
                participantsOverTime.statistics();
            }
        }
        for (final QuestionData qd: questionList) {
            qd.statistics();
        }
    }

    public String toJSON() {
        final StringBuilder json = new StringBuilder();
        json.append('[');
        if (showParticipantsOverTime) {
            json.append(participantsOverTime.toJSON());
            if (!questionList.isEmpty()) {
                json.append(',');
            }
        }
        for (int i = 0; i < questionList.size(); i++) {
            json.append(questionList.get(i).toJSON());
            if (i + 1 < questionList.size()) {
                json.append(',');
            }
        }
        json.append(']');
        return json.toString();
    }
}
