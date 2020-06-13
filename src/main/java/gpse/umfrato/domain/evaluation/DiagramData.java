package gpse.umfrato.domain.evaluation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DiagramData {

    public static final String DIVIDER_STRING = " / ";

    private final List<QuestionData> questions = new ArrayList<>();
    @JsonIgnore
    private final QuestionService questionService;
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
    private class ChoiceData implements QuestionData {
        long questionId;
        String questionTitle;
        private List<String> answerPossibilities;
        private List<Integer> data;
        private List<Double> relative;
        private String median;
        private String mode;

        ChoiceData(final long questionId, final String questionMessage, final List<String> answerPossibilities) {
            this.questionId = questionId;
            this.questionTitle = questionMessage;
            this.answerPossibilities = answerPossibilities;
            data = new ArrayList<>();
            for (final String ignored: answerPossibilities) {
                data.add(0);
            }
        }

        public void addAnswer(final int answerPossibility) {
            data.set(answerPossibility, data.get(answerPossibility) + 1);
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
            for (final Integer i: maxima) {
                modeText.append(answerPossibilities.get(i)).append(DIVIDER_STRING);
            }
            modeText.replace(modeText.lastIndexOf(DIVIDER_STRING), modeText.length(), "");
            mode = modeText.toString();
            //Median ist an mittlerer Stelle
            int medianPos = size / 2;
            relative = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                //Relative Häufigkeiten nur Summe vor Division
                relative.add(data.get(i).doubleValue() / (double) size);
                medianPos -= data.get(i);
                // [1,1,1,1,1,1,2,2,2,3,3,3,4,6](originalDaten) => [0,6,3,3,1,0,1](data)
                // => 14(data.size) => 7(/2) => 1(-6) => -2(-3) => 2(Position) => Median
                if (medianPos <= 0 && median == null) {
                    if (medianPos == 0 && i < answerPossibilities.size()) {
                        median = answerPossibilities.get(i) + DIVIDER_STRING + answerPossibilities.get(i + 1);
                    } else {
                        median = answerPossibilities.get(i);
                    }
                }
            }
        }

        @Override
        public String toJSON() {
            final StringBuilder json = new StringBuilder();
            json.append("{\"id\": ").append(questionId)
                    .append(",\"type\": ").append("\"choice\"")
                    .append(",\"title\": \"").append(questionTitle)
                    .append("\",\"answerPossibilities\": [");
            for (int i = 0; i < answerPossibilities.size(); i++) {
                json.append("\"").append(answerPossibilities.get(i)).append("\"");
                if (i < answerPossibilities.size() - 1) {
                    json.append(",");
                }
            }
            json.append("],\"data\": ").append(data)
                    .append(",\"calculated\": {")
                    .append("\"relative\": ").append(relative)
                    .append(",\"median\": \"").append(median)
                    .append("\",\"mode\": \"").append(mode)
                    .append("\"}}");
            return json.toString();
        }
    }

    @Getter
    @Setter
    private class TextData implements QuestionData {
        long questionId;
        String questionTitle;
        private List<Long> ids = new ArrayList<>();
        private List<String> texts = new ArrayList<>();
        private List<String> editedDates = new ArrayList<>();
        private List<String> creator = new ArrayList<>();

        TextData(final long questionId, final String questionMessage) {
            this.questionId = questionId;
            this.questionTitle = questionMessage;
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
            final StringBuilder json = new StringBuilder();
            json.append("{\"id\": ").append(questionId)
                    .append(",\"type\": ").append("\"text\"")
                    .append(",\"title\": \"").append(questionTitle)
                    .append("\",\"answers\": [");
            for (int i = 0; i < ids.size(); i++) {
                json.append("{")
                        .append("\"id\": ").append(ids.get(i))
                        .append(",\"text\": \"").append(texts.get(i))
                        .append("\",\"answered\": \"").append(editedDates.get(i))
                        .append("\",\"creator\": \"").append(creator.get(i))
                        .append("\"}");
                if (i + 1 < ids.size()) {
                    json.append(",");
                }
            }
            json.append("]}");
            return json.toString();
        }
    }

    @Getter
    @Setter
    private static class RangeData implements QuestionData {
        long questionId;
        String questionTitle;

        RangeData(final long questionId, final String questionMessage) {
            this.questionId = questionId;
            this.questionTitle = questionMessage;
        }

        @Override public QuestionType getQuestionType() {
            return QuestionType.RANGE_QUESTION;
        }

        @Override public void statistics() {

        }

        @Override public String toJSON() {
            return "{\"id\": " + questionId + ",\"type\": " + "\"text\"" + ",\"title\": " + questionTitle + "}";
        }
    }

    @Getter
    @Setter
    private static class SliderData implements QuestionData {
        long questionId;
        String questionTitle;

        SliderData(final long questionId, final String questionMessage) {
            this.questionId = questionId;
            this.questionTitle = questionMessage;
        }

        @Override public QuestionType getQuestionType() {
            return QuestionType.SLIDER_QUESTION;
        }

        @Override public void statistics() {

        }

        @Override public String toJSON() {
            return "{\"id\": " + questionId + ",\"type\": " + "\"text\"" + ",\"title\": " + questionTitle + "}";
        }
    }



    public DiagramData(final Poll poll, final List<PollResult> results, final QuestionService questionService) {
        this.questionService = questionService;
        this.poll = poll;
        loadData(results);
    }

    private void loadData(final List<PollResult> results) {
        for (final Question q: questionService.getAllQuestions(poll.getPollId())) {
            QuestionData qd = null;
            switch (q.getQuestionType()) {
                case "ChoiceQuestion": {
                    qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), q.getAnswerPossibilities());
                    break;
                }
                case "TextQuestion": {
                    qd = new TextData(q.getQuestionId(), q.getQuestionMessage());
                    break;
                }
                case "RangeQuestion": {
                    qd = new RangeData(q.getQuestionId(), q.getQuestionMessage());
                    break;
                }
                case "SliderQuestion": {
                    qd = new SliderData(q.getQuestionId(), q.getQuestionMessage());
                    break;
                }
                default: {

                    break;

                }
            }
            if (qd != null) {
                questions.add(qd);
            }
        }
        for (final PollResult pr: results) {
            for (final Answer a: pr.getAnswerList()) {
                for (final QuestionData qd: questions) {
                    if (qd.getQuestionId() == a.getQuestionId()) {
                        switch (qd.getQuestionType()) {
                            case CHOICE_QUESTION: {
                                final ChoiceData cd = (ChoiceData) qd;
                                for (final String s: a.getGivenAnswerList()) {
                                    cd.addAnswer(Integer.parseInt(s));
                                }
                                break;
                            }
                            case TEXT_QUESTION: {
                                if (!a.getGivenAnswerList().isEmpty()) {
                                    final TextData td = (TextData) qd;
                                    td.getCreator().add(pr.getPollTaker());
                                    //nur die neuste (letzte) Antwort
                                    td.getTexts().add(a.getGivenAnswerList().get(a.getGivenAnswerList().size() - 1));
                                    td.getEditedDates().add(pr.getLastEditAt());
                                    //vielleicht auch eine neue ID, aber ich wüsste nciht warum
                                    td.getIds().add(pr.getPollResultId());
                                }
                                break;
                            }
                            case RANGE_QUESTION: {
                                final RangeData rd = (RangeData) qd;
                                break;
                            }
                            case SLIDER_QUESTION: {
                                final SliderData sd = (SliderData) qd;
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (final QuestionData qd: questions) {
            qd.statistics();
        }
    }

    public String toJSON() {
        final StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < questions.size(); i++) {
            json.append(questions.get(i).toJSON());
            if (i + 1 < questions.size()) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

}
