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
import org.dom4j.Text;
import org.json.JSONObject;

import java.text.*;
import java.util.*;
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
        String[] positiveWords = {
            "gut", "super", "großartig", "fesch", "schnafte", "töfte", "flott", "positiv", "überzeugend", "erfreut", "softwerkende", "beeindruckend", "durchdacht", "eifrig", "eindrucksvoll", "schön", "reizend", "idyllisch", "groovy", "nice", "urcool", "megaaffentittengeil", "bäumig", "pfundig", "endgeil", "benutzerfreundlich", "angenehm", "anregend", "ansprechend", "atemberaubend", "attraktiv", "auffallend", "aufmerksam", "aufregend", "ausgewogen", "ausgezeichnet", "außergewöhnlich", "beeindruckend", "befriedigend", "begeisternd", "bejahend", "bekräftigend", "belebend", "beliebt", "bemerkenswert", "beneidenswert", "benutzerfreundlich", "bequem", "berauschend", "beruhigend", "berühmt", "beschwingt", "bestätigend", "bewährt", "bewundernswert", "brüderlich", "chancengleich", "charismatisch", "charmant", "couragiert", "dankbar", "durchdacht", "edel", "ehrgeizig", "ehrlich", "eifrig", "eindeutig", "eindrucksvoll", "einfallsreich", "einfühlend", "einwandfrei", "ekstatisch", "elegant", "elektrisierend", "empfehlenswert", "energisch", "engagiert", "entgegenkommend", "entspannt", "entzückend", "erfolgreich", "erfreulich", "erfüllend", "erhellend", "erleuchtend", "erreichbar", "erstaunlich", "erstklassig", "euphorisch", "exquisit", "exzellent", "fähig", "fantastisch", "faszinierend", "fehlerfrei", "feierlich", "fesselnd", "festlich", "fleißig", "freundlich", "friedlich", "frisch", "froh", "fröhlich", "frohlockend", "furchtlos", "gedeihlich", "geduldig", "geerdet", "gefeiert", "genial", "genießerisch", "genussvoll", "geschätzt", "geschickt", "geschmackvoll", "gestärkt", "gesund", "gewinnend", "glänzend", "glaubwürdig", "glücklich", "göttlich", "grandios", "großzügig", "handlich", "harmonisch", "heilig", "heilsam", "heiter", "herausragend", "herrlich", "hervorragend", "herzlich", "hilfreich", "hinreißend", "hochgeschätzt", "höflich", "humorvoll", "ideal", "idyllisch", "inspirierend", "interessant", "intuitiv", "jubelnd", "jugendlich", "klug", "kompetent", "königlich", "köstlich", "kraftvoll", "lächelnd", "langlebig", "lebendig", "leidenschaftlich", "leuchtend", "liebenswert", "liebenswürdig", "liebevoll", "lobenswert", "luxuriös", "makellos", "malerisch", "meisterhaft", "motivierend", "mutig", "niedlich", "nutzbringend", "offen", "ordentlich", "organisiert", "perfekt", "phänomenal", "positiv", "prächtig", "prachtvoll", "prickelnd", "problemlos", "produktiv", "pünktlich", "reibungslos", "reichhaltig", "renommiert", "respektvoll", "romantisch", "rücksichtsvoll", "sauber", "schick", "schmeichelnd", "schön", "schwungvoll", "seriös", "sicher", "solidarisch", "spektakulär", "spielerisch", "spontan", "stilvoll", "sympathisch", "tadellos", "tapfer", "tolerant", "treu", "triumphierend", "tüchtig", "überraschend", "überschwänglich", "überzeugend", "umsichtig", "unberührt", "unbeschwert", "uneigennützig", "unglaublich", "unkompliziert", "unterstützend", "unwiderstehlich", "verantwortungsvoll", "verführerisch", "vergnüglich", "verjüngend", "verliebt", "verlockend", "vertrauensvoll", "verwöhnend", "verzaubert", "verzückt", "vollendet", "vorteilhaft", "warm", "warmherzig", "wegweisend", "weise", "wendig", "wertvoll", "wichtig", "wirksam", "wohlerzogen", "wohlmeinend", "wohltätig", "wohltuend", "wunderbar", "wünschenswert", "würdevoll", "zauberhaft", "zugänglich", "zuverlässig"
        };
        String[] negativeWords = {
            "doof", "schlecht", "negativ", "scheiße", "enttäuschend", "enttäuschung", "enttäuscht", "uff", "yikes", "fuck", "lahm", "afd", "NN", "abgefeimt", "affektiert", "aggressiv", "ambivalent", "angeberisch", "anmaßend", "arglistig", "argwöhnisch", "arrogant", "aufdringlich", "aufgeblasen", "beratungsresistent", "blasiert", "borniert", "boshaft", "cholerisch", "dekadent", "demagogisch", "deprimiert", "despotisch", "distanziert", "dogmatisch", "dominant", "dreist", "egoistisch", "egozentrisch", "eifersüchtig", "eigenmächtig", "einfältig", "eingebildet", "einseitig", "eitel", "ekelerregend", "elitär", "fies", "jähzornig", "garstig", "gefallsüchtig", "gefrustet", "gnädig", "gönnerhaft", "großkotzig", "großspurig", "großtuerisch", "heimtückig", "herablassen", "hinterhältig", "hintertrieben", "hochfahrend", "hochmütig", "hoffärtig", "hoffnungslos", "hysterisch", "ignorant", "infam", "intrigant", "kleinkariert", "kompliziert", "kopfhängerisch", "langweilig", "lethargisch", "lügnerisch", "maliziös", "manipulativ", "mutlos", "naiv", "narzisstisch", "neurotisch", "oberflächlich", "pedantisch", "phlegmatisch", "protzig", "reserviert", "reserviert", "resigniert", "rücksichtslos", "scheinheilig", "schlampig", "schuftig", "selbstgefällig", "selbstgerecht", "selbstsüchtig", "selbstverliebt", "skrupellos", "spießig", "stur", "überheblich", "unbeweglich", "ungeduldig", "unnahbar", "unsozial", "unzugänglich", "verbohrt", "verlogen", "vernagelt", "verschlagen", "versnobt", "snobistisch", "verstiegen", "willkürlich", "zynisch"
        };

        @Getter
        @Setter
        @AllArgsConstructor
        private static class TextAnswer {
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

        public void addAnswer(final Long id, final String text, final String editedDate, final String creator) {
            answers.add(new TextAnswer(id, text, editedDate, creator));
        }

        @Override
        public QuestionType questionType() {
            return QuestionType.TEXT_QUESTION;
        }

        @Override
        public void statistics() {
        }

        int calcTendency(String input){
            int tendency = 0;
            List<String> wordList = Arrays.asList(input.split("[ .,;:?\\-_=()/&%$§!#'+*~|<>]+"));
            for(String word : wordList)
                if(Arrays.asList(positiveWords).contains(word.toLowerCase()))
                    tendency += Collections.frequency(wordList, word.toLowerCase());
                else if(Arrays.asList(negativeWords).contains(word.toLowerCase()))
                    tendency -= Collections.frequency(wordList, word.toLowerCase());
            return tendency;
        }

        List<JSObject> doWordStuff(List<TextAnswer> input){ //Antworten Darstellen
            List<JSObject> outputList = new ArrayList<>();
            for(TextAnswer ta : input)
                outputList.add(new JSObject(ta.text.split("[ .,;:?\\-_=()/&%$§!#'+*~|<>]+").length, ta.text, calcTendency(ta.text), ta.edited, ta.creator)); // Antwortlänge, Antwort, Antworttendenz
            return outputList;
        }

        List<JSObject> doWordFrequencyStuff(List<TextAnswer> input){ //Wortfrequenzen darstekken
            List<String> answers = new ArrayList<>();
            for(TextAnswer ta : input)
                answers.add(ta.text);
            List<JSObject> duplicateList = new ArrayList<>();
            for(String answer : answers) //Wir brauchen nur die Strings
                for(String word : answer.split("[ .,;:?\\-_=()/&%$§!#'+*~|<>]+"))
                    duplicateList.add(new JSObject(Collections.frequency(input, word), word, 0, "", ""));
            Set<JSObject> s = new HashSet<JSObject>(duplicateList);
            return new ArrayList<JSObject>(s);
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

        protected static class JSObject {
            public int value;
            public String text;
            public int tendency;
            public String edited;
            public String creator;

            JSObject(int value, String text, int tendency, String edited, String creator){
                this.value = value;
                this.text = text;
                this.tendency = tendency;
                this.edited = edited;
                this.creator = creator;
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
        final List<Date> datesList = new ArrayList<>();
        for (final PollResult pr: results) {
            if(showParticipantsOverTime) {
                try {
                    datesList.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pr.getLastEditAt()));
                } catch (ParseException pe) {
                    continue;
                }
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
                Date min = datesList.get(0);
                Date max = datesList.get(0);
                for (final Date d: datesList) {
                    if (d.after(max)) {
                        max = d;
                    } else if (d.before(min)) {
                        min = d;
                    }
                }
                // System.out.println(min);
                // System.out.println(max);
                final long start = min.getTime() / 1000;
                final long end = max.getTime() / 1000;
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
                final DateFormat df = new SimpleDateFormat(patternString);
                for (long date = min.getTime(); date <= max.getTime(); date += step*1000) {
                    answerPossibilities.add(df.format(new Date(date)));
                }
                // System.out.println(answerPossibilities);
                participantsOverTime = new ChoiceData(0, "Teilnahmen über Zeit", answerPossibilities);
                for (final Date d: datesList) {
                    final long slot = (d.getTime() / 1000 - start) / step;
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
