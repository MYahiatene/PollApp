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
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    private final ZoneId timeZone;
    @JsonIgnore
    private boolean showParticipantsOverTime = true;
    @JsonIgnore
    private boolean participantsOverRelativeTime = false;
    @JsonIgnore
    private ChoiceData participantsOverTime;

    interface QuestionData {
        enum QuestionType { CHOICE_QUESTION, TEXT_QUESTION, RANGE_QUESTION, SLIDER_QUESTION, SORT_QUESTION }

        /**
         * questionId to compare while running through all questions.
         * this function has no 'get' in its title to prevent Jackson from finding it
         * @return questionId represented by the questionData
         */
        long questionId();

        /**
         * questionType for casting.
         * this function has no 'get' in its title to prevent Jackson from finding it
         * @return QuestionType
         */
        QuestionType questionType();

        /**
         * statistics will run for every question once after the data is gathered from pollresults.
         */
        void statistics();

        void print();

        /**
         * prepares the object for transfer.
         * @return json string representing the QuestionData
         */
        String toJSON();
    }

    /**
     * this class represents all the data of a choiceQuestionEvaluationWidget.
     */
    @Getter
    @Setter
    protected static class ChoiceData implements QuestionData {
        private long id;
        private String title;
        private String type;
        private List<String> answerPossibilities;
        private List<List<Integer>> data;
        private List<Calculation> calculated = new ArrayList<>();
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
            data.add(new ArrayList<>());
            for (final String ignored: answerPossibilities) {
                data.get(0).add(0);
            }
            calculated.add(new Calculation());
        }

        @Override
        public long questionId() {
            return this.id;
        }

        public void addAnswer(final double answerPossibility) {
            final int index = (int) ((answerPossibility - start) / step);
            try {
                data.get(0).set(index, data.get(0).get(index) + 1);
            } catch(IndexOutOfBoundsException e){
                data.get(0).add(index, 0);
                data.get(0).set(index, data.get(0).get(index) + 1);
            }
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
        public void print() {
            LOGGER.info(String.valueOf(this.id));
            LOGGER.info(this.title);
            LOGGER.info(this.type);
        }

        @Override
        public void statistics() {
            int size = 0;
            final List<Integer> maxima = new ArrayList<>();
            int max = 0;
            //Häufigste Antwort und Menge der Antworten raussuchen
            for (int i = 0; i < data.get(0).size(); i++) {
                size += data.get(0).get(i);
                if (data.get(0).get(i) > max) {
                    maxima.clear();
                    maxima.add(i);
                    max = data.get(0).get(i);
                } else if (data.get(0).get(i) == max) {
                    maxima.add(i);
                }
            }
            final StringBuilder modeText = new StringBuilder();
            for (final Integer i: maxima) {
                modeText.append(answerPossibilities.get(i)).append(DIVIDER_STRING);
            }
            modeText.replace(modeText.lastIndexOf(DIVIDER_STRING), modeText.length(), "");
            calculated.get(0).mode = modeText.toString();
            //Median ist an mittlerer Stelle
            double medianPos = size / 2.0;
            calculated.get(0).relative = new ArrayList<>();
            for (int i = 0; i < data.get(0).size(); i++) {
                //Relative Häufigkeiten nur Summe vor Division
                calculated.get(0).relative.add(data.get(0).get(i).doubleValue() / (double) size);
                medianPos -= data.get(0).get(i);
                // [1,1,1,1,1,1,2,2,2,3,3,3,4,6](originalDaten) => [0,6,3,3,1,0,1](data)
                // => 14(data.size) => 7(/2) => 1(-6) => -2(-3) => 2(Position) => Median
                if (medianPos <= 0 && calculated.get(0).median == null) {
                    if (size % 2 == 0 && medianPos == 0) {
                        if (i < answerPossibilities.size()) {
                            int j = i + 1;
                            while (j < data.get(0).size() - 1 && data.get(0).get(j) == 0) {
                                j++;
                            }
                            calculated.get(0).median = answerPossibilities.get(i) + (j >= data.get(0).size() ? "" : DIVIDER_STRING + answerPossibilities.get(j));
                        }
                    } else {
                        calculated.get(0).median = answerPossibilities.get(i);
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

    /**
     * this class represents all the data of a sortQuestionEvaluationWidget.
     */
    @Getter
    @Setter
    protected static class SortData implements QuestionData {
        private long id;
        private String title;
        private String type;
        private List<String> answerPossibilities;
        @JsonIgnore
        private List<List<Integer>> data = new ArrayList<>();
        private List<List<List<SortValues>>> meanOrder = new ArrayList<>();

        SortData(final long questionId, final String questionMessage, final List<String> answerPossibilities) {
            this.id = questionId;
            this.type = "sort";
            this.title = questionMessage;
            this.answerPossibilities = answerPossibilities;
            this.meanOrder.add(new ArrayList<>());
        }

        @Getter
        @Setter
        private static class SortValues {
            private int itemID;
            private String itemName;
            private List<Integer> wasAtPositionNumbers;
            private double meanPositionValue;
            private int meanPosition;
            private int twin;
            private double variance;
            private double standardDeviation;
        }

        void addAnswers(final List<String> answers) {
            final List<Integer> newAnswer = new ArrayList<>();
            for (final String s: answers) {
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
        public void print() {
            LOGGER.info(String.valueOf(this.id));
            LOGGER.info(this.title);
            LOGGER.info(this.type);
        }

        @Override
        public void statistics() {

            if (data.isEmpty()) {
                LOGGER.info("Keine Antworten");
                return;
            }
            if (answerPossibilities.size() != data.get(0).size()) {
                LOGGER.info("Fehlerhafte Daten");
                return;
            }

            // initializing a array that has size nxn with n = number of items

            final List<SortValues> arrayOfValues = new ArrayList<>();

            for (int i = 0; i < data.get(0).size(); i++) {
                arrayOfValues.add(new SortValues());
                arrayOfValues.get(i).itemID = i;
                arrayOfValues.get(i).itemName = answerPossibilities.get(i);
                arrayOfValues.get(i).wasAtPositionNumbers = new ArrayList<>();
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

            for (final List<Integer> datum: data) {
                for (int j = 0; j < datum.size(); j++) {
                    final int index = datum.get(j);
                    arrayOfValues.get(index).wasAtPositionNumbers.set(j, arrayOfValues.get(index).wasAtPositionNumbers.get(j) + 1);
                }
            }

            // Now we compute the mean order.

            // every item gets a mean position value computed from the number it was placed on a position times the index of that position

            for (SortValues arrayOfValue: arrayOfValues) {
                for (int j = 0; j < arrayOfValue.wasAtPositionNumbers.size(); j++) {
                    arrayOfValue.meanPositionValue += arrayOfValue.wasAtPositionNumbers.get(j) * (j + 1);
                }

                arrayOfValue.meanPositionValue = arrayOfValue.meanPositionValue / data.size();
            }

            // we sort the items according to their mean position values and save the order in orderedItems

            int[] orderedItems = new int[data.get(0).size()];

            double lowestMeanPositionValue = arrayOfValues.size() * arrayOfValues.size();
            int firstItem = arrayOfValues.size() + 1;

            for (int j = 0; j < arrayOfValues.size(); j++) {
                for (int i = 0; i < arrayOfValues.size(); i++) {
                    if (arrayOfValues.get(i).meanPosition == -1) {
                        if (lowestMeanPositionValue >= arrayOfValues.get(i).meanPositionValue) {
                            if (lowestMeanPositionValue == arrayOfValues.get(i).meanPositionValue && firstItem != i) {
                                LOGGER.info("item " + i + "is duplicate to " + firstItem);
                                arrayOfValues.get(i).twin = firstItem;

                            }
                            lowestMeanPositionValue = arrayOfValues.get(i).meanPositionValue;
                            firstItem = i;
                            // console.log('i: ' + i)
                            // console.log('first: ' + firstItem)
                        }
                    }
                }
                // console.log('first ' + firstItem + 'position ' + j)
                arrayOfValues.get(firstItem).meanPosition = j;
                orderedItems[j] = firstItem;
                lowestMeanPositionValue = arrayOfValues.size() + 1;
            }

            // Now we compute the variance and standard deviation of the wasAtPositionNumbers

            for (SortValues arrayOfValue: arrayOfValues) {
                for (int j = 0; j < arrayOfValue.wasAtPositionNumbers.size(); j++) {
                    arrayOfValue.variance += arrayOfValue.wasAtPositionNumbers.get(j) * Math.pow(j + 1 - arrayOfValue.meanPositionValue, 2);
                }
                arrayOfValue.variance = arrayOfValue.variance / data.size();
                arrayOfValue.standardDeviation = Math.sqrt(arrayOfValue.variance);
            }

            // Now we build the meanOrder Array by pushing an Array of items for each position

            int position = 0;
            for (int i = 0; i < orderedItems.length; i++) {
                // meanOrder.push([])
                boolean twinsLeft = true;
                SortValues currentItem = arrayOfValues.get(orderedItems[i]);
                // console.log('arrayOfValues[orderedItems[i] with i as ' + i + 'is')
                // console.log(currentItem)
                meanOrder.get(0).add(new ArrayList<>());
                while (twinsLeft) {

                    meanOrder.get(0).get(position).add(currentItem);

                    // console.log('added item ' + currentItem.itemID + 'at position :' + position)
                    if (currentItem.twin == -1) {
                        // console.log('no twins for ' + i)
                        twinsLeft = false;
                    } else {
                        currentItem = arrayOfValues.get(currentItem.twin);
                        i++;
                    }
                }
                position++;
            }
            // console.log(this.arrayOfValues)
            // console.log('ordered')
            // console.log(orderedItems)
            // console.log('meanOrder: ')
            // console.log(this.meanOrder)
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

    /**
     * this class represents all the data of a textQuestionEvaluationWidget.
     */
    @Getter
    @Setter
    protected static class TextData implements QuestionData {
        private long id;
        private String type;
        private String title;
        @JsonIgnore
        List<TextAnswer> answers = new ArrayList<>();
        List<JSObject> frequency = new ArrayList<>();
        List<JSObject> texts = new ArrayList<>();
        @JsonIgnore
        String[] positiveWords = {
            "gut", "super", "großartig", "fesch", "schnafte", "töfte", "flott", "positiv", "erfreut", "beeindruckend", "eifrig", "eindrucksvoll", "schön", "reizend", "idyllisch", "groovy", "nice", "urcool", "megaaffentittengeil", "bäumig", "pfundig", "endgeil", "benutzerfreundlich", "angenehm", "anregend", "ansprechend", "atemberaubend", "attraktiv", "auffallend", "aufmerksam", "aufregend", "ausgewogen", "ausgezeichnet", "außergewöhnlich", "beeindruckend", "befriedigend", "begeisternd", "bejahend", "bekräftigend", "belebend", "beliebt", "bemerkenswert", "beneidenswert", "benutzerfreundlich", "bequem", "berauschend", "beruhigend", "berühmt", "beschwingt", "bestätigend", "bewährt", "bewundernswert", "brüderlich", "chancengleich", "charismatisch", "charmant", "couragiert", "dankbar", "durchdacht", "edel", "ehrgeizig", "ehrlich", "eifrig", "eindeutig", "eindrucksvoll", "einfallsreich", "einfühlend", "einwandfrei", "ekstatisch", "elegant", "elektrisierend", "empfehlenswert", "energisch", "engagiert", "entgegenkommend", "entspannt", "entzückend", "erfolgreich", "erfreulich", "erfüllend", "erhellend", "erleuchtend", "erreichbar", "erstaunlich", "erstklassig", "euphorisch", "exquisit", "exzellent", "fähig", "fantastisch", "faszinierend", "fehlerfrei", "feierlich", "fesselnd", "festlich", "fleißig", "freundlich", "friedlich", "frisch", "froh", "fröhlich", "frohlockend", "furchtlos", "gedeihlich", "geduldig", "geerdet", "gefeiert", "genial", "genießerisch", "genussvoll", "geschätzt", "geschickt", "geschmackvoll", "gestärkt", "gesund", "gewinnend", "glänzend", "glaubwürdig", "glücklich", "göttlich", "grandios", "großzügig", "handlich", "harmonisch", "heilig", "heilsam", "heiter", "herausragend", "herrlich", "hervorragend", "herzlich", "hilfreich", "hinreißend", "hochgeschätzt", "höflich", "humorvoll", "ideal", "idyllisch", "inspirierend", "interessant", "intuitiv", "jubelnd", "jugendlich", "klug", "kompetent", "königlich", "köstlich", "kraftvoll", "lächelnd", "langlebig", "lebendig", "leidenschaftlich", "leuchtend", "liebenswert", "liebenswürdig", "liebevoll", "lobenswert", "luxuriös", "makellos", "malerisch", "meisterhaft", "motivierend", "mutig", "niedlich", "nutzbringend", "offen", "ordentlich", "organisiert", "perfekt", "phänomenal", "positiv", "prächtig", "prachtvoll", "prickelnd", "problemlos", "produktiv", "pünktlich", "reibungslos", "reichhaltig", "renommiert", "respektvoll", "romantisch", "rücksichtsvoll", "sauber", "schick", "schmeichelnd", "schön", "schwungvoll", "seriös", "sicher", "solidarisch", "spektakulär", "spielerisch", "spontan", "stilvoll", "sympathisch", "tadellos", "tapfer", "tolerant", "treu", "triumphierend", "tüchtig", "überraschend", "überschwänglich", "überzeugend", "umsichtig", "unberührt", "unbeschwert", "uneigennützig", "unglaublich", "unkompliziert", "unterstützend", "unwiderstehlich", "verantwortungsvoll", "verführerisch", "vergnüglich", "verjüngend", "verliebt", "verlockend", "vertrauensvoll", "verwöhnend", "verzaubert", "verzückt", "vollendet", "vorteilhaft", "warm", "warmherzig", "wegweisend", "weise", "wendig", "wertvoll", "wichtig", "wirksam", "wohlerzogen", "wohlmeinend", "wohltätig", "wohltuend", "wunderbar", "wünschenswert", "würdevoll", "zauberhaft", "zugänglich", "zuverlässig"
        };
        @JsonIgnore
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
        public void print() {
            LOGGER.info(String.valueOf(this.id));
            LOGGER.info(this.title);
            LOGGER.info(this.type);
        }

        @Override
        public void statistics() {
            this.texts = doWordStuff(this.answers);
            this.frequency = doWordFrequencyStuff(this.answers);
        }

        /**
         * @param stringList Input-String-Eingabeliste
         * @param input Der String für den die Tendenz berechnet werden muss
         * Diese Funktion berechnet den Tendenzindex für die Wörter im Input
         * */
        int calcTendency(final List<String> stringList, final String input) {
            int tendency = 0;
            final String[] wordList = input.split("[ .,;:?\\-_=()/&%$§!#'+*~|<>]+");
            for (final String word: wordList) {
                if (Arrays.asList(positiveWords).contains(word.toLowerCase())) {
                    tendency += 1;
                } else if (Arrays.asList(negativeWords).contains(word.toLowerCase())) {
                    tendency -= 1;
                }
            }
            return tendency;
        }

        /**
         * @param input Die Eingabeliste an TextAnswers welche zu einer Liste an Strings gemacht werden soll
         * Diese Funktion macht eine Liste an TextAnswers zu einer Liste an Strings um für diese die Frequenz und Tendenz zu berechnen
         * */
        List<String> getWordList(List<TextAnswer> input) {
            List<String> stringList = new ArrayList<>();
            for (TextAnswer ta: input) {
                stringList.addAll(Arrays.asList(ta.text.toLowerCase().split("[ .,;:?\\-_=()/&%$§!#'+*~|<>]+")));
            }
            return stringList;
        }

        /**
         * @param input Die Eingabeliste an Antworten
         * Diese Funktion generiert eine Liste an JSObjects um die Frequenz, die Antwortlänge und den ganzen andeden Kram in einer Klasse darzustellen
         * */
        List<JSObject> doWordStuff(final List<TextAnswer> input) { //Antworten Darstellen
            final List<JSObject> outputList = new ArrayList<>();
            for (final TextAnswer ta: input) {
                outputList.add(new JSObject(ta.text.split("[ .,;:?\\-_=()/&%$§!#'+*~|<>]+").length,
                    ta.text, calcTendency(getWordList(input), ta.text),
                    getWordFrequency(input, ta), ta.edited, ta.creator)); // Antwortlänge, Antwort, Antworttendenz
            }
            return outputList;
        }

        /**
         * @param input Die Eingabe-Antwortliste
         * @param ta Die Textantwort für die die Wortfrequenz berechnet werden soll
         * Diese Funktion berechnet die Frequenz eines einzelenen Wortes so wie die Funktion genutzt wird
         * */
        int getWordFrequency(List<TextAnswer> input, TextAnswer ta) {
            List<String> wordList = getWordList(input);
            return Collections.frequency(wordList, ta.text);

        }

        /**
         * @param duplicates Duplikatenthaltende Liste
         * Diese Funktion entfernt die Duplikate aus einer Liste an JSObjects
         * */
        List<JSObject> removeDuplicates(final List<JSObject> duplicates) {
            List<String> keyList = new ArrayList<>();
            List<JSObject> noDuplicates = new ArrayList<>();
            for (JSObject jso: duplicates) {
                if (!keyList.contains(jso.text)) {
                    noDuplicates.add(jso);
                    keyList.add(jso.text);
                }
            }
            return noDuplicates;
        }

        /**
         * @param input Eingabe-Antwort-Liste
         * Diese Funktion berechnet die Wortfrequenz und das ganze Zeug welches im TextQuestionEvaluationWidget unter Wortfrequenz dargestellt wird.
         * */
        List<JSObject> doWordFrequencyStuff(final List<TextAnswer> input) { //Wortfrequenzen darstellen
            final List<String> answers = new ArrayList<>();
            for (final TextAnswer ta: input) {
                answers.add(ta.text);
            }
            final List<JSObject> duplicateList = new ArrayList<>();
            for (final String answer: answers) { //Wir brauchen nur die Strings
                for (final String word: answer.split("[ .,;:?\\-_=()/&%$§!#'+*~|<>]+")) {
                    duplicateList.add(new JSObject(0, word, 0, Collections.frequency(getWordList(input),
                        word.toLowerCase()), "", ""));
                }
            }
            return removeDuplicates(duplicateList);
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

        protected static class JSObject {
            public int value;
            public String text;
            public int tendency;
            public int frequency;
            public String edited;
            public String creator;

            JSObject(final int value, final String text, final int tendency, int frequency, final String edited,
                     final String creator) {
                this.value = value;
                this.text = text;
                this.tendency = tendency;
                this.frequency = frequency;
                this.edited = edited;
                this.creator = creator;
            }
        }
    }

    public DiagramData(final Poll poll, final List<PollResult> results, final boolean showParticipantsOverTime,
                       final boolean participantsOverRelativeTime, final List<Long> questionIds, final ZoneId timeZone,
                       final CategoryService categoryService, final QuestionService questionService) {
        this.categoryService = categoryService;
        this.questionService = questionService;
        this.poll = poll;
        this.questionIds = questionIds;
        this.timeZone = timeZone;
        this.showParticipantsOverTime = showParticipantsOverTime;
        this.participantsOverRelativeTime = participantsOverRelativeTime;
        loadData(results);
    }

    List<QuestionData> getQuestionList() {
        return this.questionList;
    }

    /**
     * generates the QuestionData (the diagrams) needed for display and statistical calculations.
     */
    private void generateQuestionData() {
        final List<Category> categories = categoryService.getAllCategories(poll.getPollId());
        for (final Category c: categories) {
            for (final Question q: questionService.getAllQuestions(c.getCategoryId())) {
                if (!questionIds.contains(q.getQuestionId())) {
                    continue;
                }
                QuestionData qd = null;
                final DecimalFormat format = new DecimalFormat("#.######",
                    DecimalFormatSymbols.getInstance(Locale.GERMAN));
                switch (q.getQuestionType()) {
                    case "ChoiceQuestion":
                        qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), q.getAnswerPossibilities());
                        break;
                    case "TextQuestion":
                        qd = new TextData(q.getQuestionId(), q.getQuestionMessage());
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
                        qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), answerPossibilities);
                        break;
                    case "SliderQuestion":
                        final List<String> answerPossibilities2 = new ArrayList<>();
                        for (double i = q.getStartValue(); i < q.getEndValue();) {
                            answerPossibilities2.add(format.format(i));
                            i += q.getStepSize();
                        }
                        qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), answerPossibilities2);
                        ((ChoiceData) qd).setModifier(q.getStartValue(), q.getStepSize());
                        break;
                    case "SortQuestion":
                        qd = new SortData(q.getQuestionId(), q.getQuestionMessage(), q.getAnswerPossibilities());
                        break;
                    default:
                        break;
                }
                if (qd != null) {
                    questionList.add(qd);
                }
            }
        }
    }

    /**
     * gathers all lastEditedAt dates and prepares them to be displayed in a diagram.
     * @param results the pollResults from which the dates come
     */
    private void generateParticipantsOverTime(final List<PollResult> results, final ZonedDateTime pollStartTime) {
        final List<ZonedDateTime> datesList = new ArrayList<>();
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",Locale.GERMANY).withZone(timeZone); //'2020-07-08 19:55:21'
        for (final PollResult pr: results) {
            /*System.out.println("PollResult pr: "+pr);*/
            if(showParticipantsOverTime) {
                datesList.add(pr.getLastEditAt());
            }
        }
        if (datesList.isEmpty()) {
            showParticipantsOverTime = false;
        } else {
            ZonedDateTime min = datesList.get(0);
            ZonedDateTime max = datesList.get(0);
            for (final ZonedDateTime d: datesList) {
                if (d.isAfter(max)) {
                    max = d;
                } else if (d.isBefore(min)) {
                    min = d;
                }
            }
            final long start = min.toEpochSecond();
            final long end = max.toEpochSecond();
            final long diff = end - start;
            long step = diff / 9;
            if (step < 1L) {
                step = 1L;
            }
            final List<String> answerPossibilities = new ArrayList<>();
            if(participantsOverRelativeTime)
            {
                // System.out.println(patternString);
                //final DateTimeFormatter finalDf = DateTimeFormatter.ofPattern(tPlus, Locale.GERMANY);
                String tPlus = "";
                for (ZonedDateTime date = min; date.compareTo(max) <= 0;date = date.plusSeconds(step)) {
                    String deltaString = "T+";
                    //Instant endT = Instant.ofEpochSecond(end);
                    //System.out.println("EndT: "+endT);
                    //ZonedDateTime endTime = endT.atZone(ZoneId.of("Europe/Berlin")); //ZonedDateTime.parse(String.valueOf(end), df.withZone(ZoneId.of("Europe/Berlin")));
                    //System.out.println("endtime : "+endTime);
                    //Instant endTime = Instant.ofEpochSecond(end);
                    Duration delta = Duration.between(pollStartTime, date);
                    if (step < 60 * 60 * 24 * 30) {
                        tPlus = deltaString + String.format("%dd", delta.toDays());
                    }
                    if (step < 60 * 60 * 24) {
                        tPlus = deltaString + String.format("%dh", delta.toHours());
                    }
                    if (step < 60 * 60) {
                        tPlus = deltaString + String.format("%dm", delta.toMinutes());
                    }
                    if (step < 60) {
                        tPlus = deltaString + String.format("%ds", delta.toSeconds());
                    }
                    /*System.out.println("APLUS: "+tPlus);*/
                    answerPossibilities.add(tPlus);
                }

                // System.out.println(answerPossibilities);
                participantsOverTime = new ChoiceData(0, "Teilnahmen über Zeit", answerPossibilities);
                /*System.out.println(datesList.toString());*/
                for (final ZonedDateTime d: datesList) {
                    final long slot = (d.toEpochSecond() - start) / step;
                    /*System.out.println("Slot: "+slot);
                    System.out.println("Start: "+start);
                    System.out.println("Step: "+step);*/
                    participantsOverTime.addAnswer((double) slot);
                }

                participantsOverTime.statistics();
            }
            else {
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
                final DateTimeFormatter finalDf = DateTimeFormatter.ofPattern(patternString, Locale.GERMAN).withZone(timeZone);
                for (ZonedDateTime date = min; date.compareTo(max) <= 0; date = date.plusSeconds(step)) {
                    answerPossibilities.add(finalDf.format(date));
                }
            }
            participantsOverTime = new ChoiceData(0, "Teilnahmen über Zeit", answerPossibilities);
            for (final ZonedDateTime d: datesList) {
                final long slot = (d.toEpochSecond() - start) / step;
                participantsOverTime.addAnswer((double) slot);
            }
            participantsOverTime.statistics();
        }
    }

    /**
     * fills them with data and calls their statistics function.
     * @param results the raw data to get processed and prepared for display
     */
    private void loadData(final List<PollResult> results) {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN).withZone(timeZone);
        generateQuestionData();
        for (final PollResult pr: results) {
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
                                    td.addAnswer(pr.getPollResultId(),
                                            a.getGivenAnswerList().get(a.getGivenAnswerList().size() - 1), df.format(pr.getLastEditAt()), pr.getPollTaker());
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
            generateParticipantsOverTime(results,poll.getActivatedDate());
        }
        for (final QuestionData qd: questionList) {
            qd.statistics();
        }
    }

    /**
     * creates the JSON string from the diagramData.
     * @return the diagramData as a JSON-String
     */
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

    public void combine(DiagramData other) {
        if(showParticipantsOverTime)
        {
            this.participantsOverTime.data.addAll(other.participantsOverTime.getData());
            this.participantsOverTime.calculated.addAll(other.participantsOverTime.calculated);
        }
        for(int i = 0; i < getQuestionList().size();i++)
        {
            QuestionData qd = this.getQuestionList().get(i);
            QuestionData od = other.getQuestionList().get(i);
            LOGGER.info("this");
            qd.print();
            LOGGER.info("other");
            od.print();
            switch (qd.questionType()) {
                case SORT_QUESTION: {
                    SortData tsd = (SortData) qd;
                    SortData osd = (SortData) od;
                    tsd.meanOrder.addAll(osd.meanOrder);
                    break;
                }
                case TEXT_QUESTION: {
                    TextData ttd = (TextData) qd;
                    TextData otd = (TextData) od;
                    ttd.texts.addAll(otd.texts);
                    ttd.frequency.addAll(otd.frequency);
                    break;
                }
                case CHOICE_QUESTION: {
                    ChoiceData tcd = (ChoiceData) qd;
                    ChoiceData ocd = (ChoiceData) od;
                    tcd.data.addAll(ocd.getData());
                    tcd.calculated.addAll(ocd.calculated);
                    break;
                }
            }
        }
    }
}
