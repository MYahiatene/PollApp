package gpse.umfrato.domain.evaluation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiagramData {

    public static final String DIVIDER_STRING = " / ";
    private static final String JSON_ID = "{\"id\": ";
    private static final String JSON_TYPE = ",\"type\": ";
    private static final String JSON_TITLE = ",\"title\": \"";
    private static final String HYPHEN = " - ";

    private final List<QuestionData> questionList = new ArrayList<>();
    @JsonIgnore
    private final QuestionService questionService;
    @JsonIgnore
    private final CategoryService categoryService;
    @JsonIgnore
    private final Poll poll;

    interface QuestionData {
        enum QuestionType { CHOICE_QUESTION, TEXT_QUESTION, RANGE_QUESTION, SLIDER_QUESTION }

        long getQuestionId();

        QuestionType getQuestionType();

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

        @Getter
        @Setter
        private static class Calculation
        {
            private List<Double> relative;
            private String median;
            private String mode;
        }

        ChoiceData(final long questionId, final String questionMessage,
                                 final List<String> answerPossibilities) {
            this.id = questionId;
            this.type = "choice";
            this.title = questionMessage;
            this.answerPossibilities = answerPossibilities;
            data = new ArrayList<>();
            for (final String ignored : answerPossibilities) {
                data.add(0);
            }
        }

        @Override
        public long getQuestionId()
        {
            return this.id;
        }

        public void addAnswer(final double answerPossibility) {
            int index = (int)((answerPossibility - start) / step);
            data.set(index, data.get(index) + 1);
        }

        public void setModifier(double start,double step)
        {
            this.start = start;
            if(step != 0)
            {
                this.step = step;
            }
        }

        @Override
        public QuestionType getQuestionType() {
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
            for (final Integer i : maxima) {
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
                    if (size % 2 == 0 && medianPos == 0)
                    {
                        if(i < answerPossibilities.size()) {
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
             ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(this);
                // System.out.println("ChoiceJSON = " + json);
                return json;
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

        @Getter
        @Setter
        @AllArgsConstructor
        private static class TextAnswer
        {
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
        public long getQuestionId()
        {
            return this.id;
        }

        public void addAnswer(Long id, String text, String editedDate, String creator)
        {
            answers.add(new TextAnswer(id, text ,editedDate, creator));
        }

        @Override
        public QuestionType getQuestionType() {
            return QuestionType.TEXT_QUESTION;
        }

        @Override
        public void statistics() {

        }

        @Override
        public String toJSON() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(this);
                // System.out.println("TextJSON = " + json);
                return json;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public DiagramData(final Poll poll, final List<PollResult> results, final CategoryService categoryService,
                       final QuestionService questionService) {
        this.categoryService = categoryService;
        this.questionService = questionService;
        this.poll = poll;
        loadData(results);
    }

    private void loadData(final List<PollResult> results) {
        final List<Category> categories = categoryService.getAllCategories(poll.getPollId());
        for (final Category c : categories) {
            for (final Question q : questionService.getAllQuestions(c.getCategoryId())) {
                // System.out.println(q.toString());
                QuestionData qd = null;
                DecimalFormat format = new DecimalFormat("#.######", DecimalFormatSymbols.getInstance( Locale.GERMAN ));
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
                        for (double i = q.getStartValue(); i < q.getEndValue();) {
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
                        for (double i = q.getStartValue(); i < q.getEndValue();) {
                            answerPossibilities2.add(format.format(i));
                            i += q.getStepSize();
                        }
                        // System.out.println(answerPossibilities2.toString());
                        qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), answerPossibilities2);
                        ((ChoiceData) qd).setModifier(q.getStartValue(),q.getStepSize());
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
        for (final PollResult pr : results) {
            // System.out.println(pr.toString());
            for (final Answer a : pr.getAnswerList()) {
                for (final QuestionData qd : questionList) {
                    if (qd.getQuestionId() == a.getQuestionId()) {
                        switch (qd.getQuestionType()) {
                            case CHOICE_QUESTION:
                                final ChoiceData cd = (ChoiceData) qd;
                                for (final String s: a.getGivenAnswerList()) {
                                    cd.addAnswer(Double.parseDouble(s));
                                }
                                break;
                            case TEXT_QUESTION:
                                if (!a.getGivenAnswerList().isEmpty()) {
                                    final TextData td = (TextData) qd;
                                    //vielleicht auch eine neue ID, aber ich wüsste nicht warum, da nur key für frontend
                                    td.addAnswer(pr.getPollResultId(),
                                            //TODO: nicht nur die neuste (letzte) Antwort
                                            a.getGivenAnswerList().get(a.getGivenAnswerList().size() - 1),
                                            pr.getLastEditAt(),pr.getPollTaker());
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        for (final QuestionData qd : questionList) {
            qd.statistics();
        }
    }

    public String toJSON() {
        final StringBuilder json = new StringBuilder();
        json.append('[');
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
