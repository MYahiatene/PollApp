<template>
    <!--Build page after PollData from created() method is here-->
    <div v-if="getPoll[1] !== undefined">
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container>
            <!--When/After the PollData "arrived" show the logo-->
            <div v-if="getPoll[1].data.logo !== undefined">
                <img :src="getPoll[1].data.logo" alt="failedToLoadLogo" />
            </div>
            <v-subheader class="display-1 font-weight-bold" :style="fontColorText"> Umfrage 1</v-subheader>
            <v-content>
                <v-row>
                    <v-col cols="8">
                        <!-- loads the questions from the current category in a list-->
                        <v-list v-for="question in getCategory.questionList" :key="question.questionId" two-line>
                            <!-- every question is in a card and consists of the questionMessageand the way to answer
                            it, and depending on the settings of the poll, the number of questions -->
                            <v-card class="mx-auto">
                                <v-card-title class="col" :style="fontColorText">
                                    <!--the visibility of the index of the current questions in relation to the total
                                    number of questions, given in the settings of the poll-->
                                    <div v-if="getVisibility">{{ getIndex() }}/{{ getNumberOfQuestions }}</div>
                                    <div class="ps-4">{{ question.questionMessage }}</div>
                                </v-card-title>
                                <div v-if="question.questionType === 'TextQuestion'">
                                    <v-card-text>
                                        <v-text-field
                                            label="Antwort"
                                            :color="fontColor"
                                            @input="saveAnswerField($event, question)"
                                        >
                                        </v-text-field>
                                    </v-card-text>
                                </div>
                                <div v-else-if="question.questionType === 'ChoiceQuestion'">
                                    <v-list v-for="answer in question.answerPossibilities" :key="answer.text">
                                        <v-checkbox
                                            class="ma-4"
                                            :label="answer"
                                            :color="fontColor"
                                            @change="saveAnswerCheckbox($event, question, answer)"
                                        ></v-checkbox>
                                    </v-list>
                                    <!-- checks the individual response options for every question, not valid with
                                    the backend -->
                                    <!--div v-if="question.ownAnswersAllowed">
                                        <v-checkbox
                                            v-model="enabled"
                                            hide-details
                                            class="ma-4"
                                            label="Eigene Antwort:"
                                        ></v-checkbox>
                                        <v-col>
                                            <v-text-field></v-text-field>
                                        </v-col>
                                    </div-->
                                </div>
                                <div v-else-if="question.questionType === 'SliderQuestion'">
                                    <v-card-text>
                                        <!--might have to check if attributes aren't empty, depending on data bank-->
                                        <!--min="question.startValue"-->
                                        <!--max="question.endValue"-->
                                        <!--step="question.stepSize-->
                                        <!--:thumb-label="hideValues" //noch schreiben-->
                                        <!--<p>{{question.text1}}<span class="float-right">{{question.text2}}</span></p>-->
                                        <p>No<span class="float-right">Yes</span></p>
                                        <v-slider
                                            v-model="value"
                                            min="0"
                                            max="10"
                                            ticks
                                            tick-size="4"
                                            thumb-label="always"
                                            append-icon="mdi-plus"
                                            prepend-icon="mdi-minus"
                                            :color="fontColor"
                                            :track-color="backgroundColor"
                                            @click:append="addValue"
                                            @click:prepend="subValue"
                                            @change="saveAnswerRangeQuestion($event, question)"
                                        >
                                        </v-slider>
                                    </v-card-text>
                                </div>
                                <!-- @change="getRangeQuestionAnswers"-->
                                <div
                                    v-else-if="question.questionType === 'RangeQuestion'"
                                    @change="getRangeQuestionAnswers"
                                >
                                    <!--rangeAnswer in rangeAnswers-->
                                    <v-list v-for="rangeAnswer in rangeAnswers" :key="rangeAnswer.text">
                                        <v-checkbox
                                            class="ma-4"
                                            :label="rangeAnswer"
                                            :color="fontColor"
                                            @change="saveAnswerCheckbox($event, question, answer)"
                                        ></v-checkbox>
                                    </v-list>
                                </div>
                            </v-card>
                        </v-list>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="8">
                        <!-- button to get to the previous category, if there is no previous one, the button is disabled,
                         else, the previous category is loaded by getPreviousCategory() if clicked -->
                        <v-btn class="pl-4" :disabled="hasNoPrevious" @click="getPreviousCategory()"
                            >Vorherige Seite</v-btn
                        >
                        <!-- button to get to the next category, same principle as the one above -->
                        <v-btn class="pl-4" :disabled="hasNoNext" @click="getNextCategory()">Nächste Seite</v-btn>
                        <v-btn :color="fontColor" nuxt to="/AfterParticipated">
                            Absenden
                        </v-btn>
                    </v-col>
                </v-row>
            </v-content>
        </v-container>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'
export default {
    name: 'Participant',
    layout: 'participant', // uses special layout/participant instead of default-layout
    components: { AuthGate },
    data() {
        return {
            poll: ['Object'],
            answer: ['Object'],
            category: ['Object'],
            questionIndex: 1,
            categoryIndex: 1,
            categoryLength: 0,
            enabled: false,
            disableMe: false,
            value: 0,
            answerObj: {
                username: 'Nina',
                anonymityStatus: 'anonym',
                questionId: '1',
                answerList: [],
                answerId: '1',
                pollId: '1',
            },
            rangeAnswers: [],
        }
    },
    /**
     * Calls showPoll in methods to getPoll before/while the page is created.
     */
    created() {
        this.showPoll()
    },
    computed: {
        /**
         * Calls mapGetters defined in store/participant or store/login.
         */
        ...mapGetters({
            getPoll: 'participant/getPoll',
            getAnswer: 'participant/getAnswer',
            isAuthenticated: 'login/isAuthenticated',
            getVisibility: 'participant/getVisibility',
            getNumberOfQuestions: 'participant/getNumberOfQuestions',
            getCategoryIndex: 'participant/getCategoryIndex',
            getCategory: 'participant/getCategory',
            getChangeOfCategories: 'participant/getChangeOfCategories',
            getUsername: 'participant/getUsername',
            getQuestionIndex: 'participant/getQuestionIndex',
        }),
        /**
         * Get's the given FontColor from PollData.
         * @returns {fontColor}
         */
        fontColor() {
            return this.getPoll[1].data.fontColor
        },
        /**
         * Get's the given BackgroundColor from PollData.
         * @returns {backgroundColor}
         */
        backgroundColor() {
            return this.getPoll[1].data.backgroundColor
        },
        /**
         * Get's the given fontColor from PollData.
         * @returns {color: fontColor}
         */
        fontColorText() {
            return 'color:' + this.getPoll[1].data.fontColor
        },
        /**
         * Returns true if there is no next category in the poll or if the ChangeOfCategories is not allowed in the poll
         * @returns {boolean}
         */
        hasNoNext() {
            return this.categoryIndex === this.categoryLength || !this.getChangeOfCategories
        },
        /**
         * Returns true if there is no previous category in the poll or if the ChangeOfCategories is not allowed in
         * the poll
         * @returns {boolean}
         */
        hasNoPrevious() {
            return this.categoryIndex === 1 || !this.getChangeOfCategories
        },
        /**
         * Generates the range answers out of min, max and possible texts and gives it back in format for v-for.
         * Don't know how attributes are saved, so this is a placeholder: change min, max, text1, text2
         * Don't know if I have to declare the appedning thing an answer or if it works like this.
         * */
        getRangeQuestionAnswers() {
            /* const max = 100
            const min = 10
            const step = 10
            const text1 = 'under 10'
            const text2 = 'over 10'
            if (text1 != null) {
                this.rangeAnswers.push({ text1 })
            }
            const size = (max - min) / step
            for (let i = 1; i <= size; i++) {
                const value = min + i * step
                this.rangeAnswers.push({ value })
            }
            if (text2 != null) {
                this.rangeAnswers.push({ text2 })
            } */
            this.getRangeQuestionAnswersMutation()
            return 'answer in rangeAnswers'
        },
    },
    methods: {
        /**
         * Increases the questionIndex in the store and gives back the current questionIndex. Is called when the
         * visibility is true and gives the index of the question inside the category.
         * @returns (questionIndex: number)
         */
        getIndex() {
            this.$store.commit('participant/setQuestionIndex')
            this.questionIndex = this.getQuestionIndex
            return this.questionIndex
        },
        /**
         * Calls setCategory in the store to get the next category in the poll and save it at the page, if there is one
         * and sets the categoryIndex from the getter getCategoryIndex from the store, the total amount of questions as
         * the categoryLength and force updates the page to load the questions from th new category.
         */
        getNextCategory() {
            this.$store.commit('participant/setCategory', 1)
            this.categoryIndex = this.getCategoryIndex
            this.categoryLength = this.getPoll[1].data.categoryList.length
            this.category = this.getCategory
            this.$forceUpdate()
        },
        /**
         * Calls setCategory in the store to get the previous category in the poll and loads the category etc. like
         * in getNextcategory().
         */
        getPreviousCategory() {
            this.$store.commit('participant/setCategory', -1)
            this.categoryIndex = this.getCategoryIndex
            this.categoryLength = this.getPoll[1].data.categoryList.length
            this.category = this.getCategory
            this.$forceUpdate()
        },
        /**
         * Get's the given answer of a checkbox question and calls saveAnswer() to persist it in the database. This
         * happens after every change (check or uncheck) of a checkbox.
         * @param e (Change-Event)
         * @param question The question object, so it can get the QuestionID
         * @param answer The answer object, so it can get the answer possibilities.
         */
        saveAnswerCheckbox(e, question, answer) {
            this.answerObj.answerList = []
            let i
            // checks if checkBox was checked, not unchecked
            if (e === true) {
                for (i = 0; i < question.answerPossibilities.length; i++) {
                    if (answer === question.answerPossibilities[i]) {
                        this.answerObj.answerList.push(i) // index of true checkbox
                    }
                }
            }
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            this.showAnswer() // TODO: Fails with 405???

            // this.saveAnswer() // alternative: Button after every TextField
        },
        /**
         * Get's the given answer of a free text question and calls saveAnswer() to persist it in the database. This
         * happens after every single character.
         * @param e (Input-Event)
         * @param question The question object, so it can get the QuestionID
         */
        saveAnswerField(e, question) {
            this.answerObj.answerList = [e]
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            this.saveAnswer() // alternative: Button after every TextField
        },
        /**
         * Get's the given answer of a Range Question and calls saveAnswer() to persist it in the database.
         * This happens after every change to the slider.
         * @param e (Change-Event)
         * @param question The question object, so it can get the QuestionID
         */
        saveAnswerRangeQuestion(e, question) {
            this.answerObj.answerList = [e]
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            this.saveAnswer()
        },
        /**
         * Moves the slider one step to the left, if possible.
         * It's called by click on + Icon at a Range Question.
         */
        subValue() {
            this.value = this.value - 1
        },
        /**
         * Moves the slider one step to the right, if possible.
         * It's called by click on + Icon at a Range Question.
         */
        addValue() {
            this.value = this.value + 1
        },
        /**
         * Generates the range answers out of min, max and possible texts and gives it back in format for v-for.
         * Don't know how attributes are saved, so this is a placeholder: change min, max, text1, text2
         * Don't know if I have to declare the appedning thing an answer or if it works like this.
         * */
        getRangeQuestionAnswersMutation() {
            const max = 100
            const min = 10
            const step = 10
            const text1 = 'under 10'
            const text2 = 'over 90'
            this.rangeAnswers = [] // set it to null from previous questions
            if (max != null && min != null && step != null) {
                if (text1 != null) {
                    this.rangeAnswers.push(text1)
                }
                const size = (max - min) / step
                for (let i = 0; i < size; i++) {
                    console.log(i)
                    const value = min + i * step
                    this.rangeAnswers.push(value)
                }
                if (text2 != null) {
                    this.rangeAnswers.push(text2)
                }
            }
            console.log(this.rangeAnswers)
            console.log("Hi I'm in the getRangeQuestionAnswers method!")
        },
        // -------------------------------------------------------------------------------------------------------------
        // Get or save information to/from the Backend
        /**
         * Calls showPoll in store/participant.js.
         */
        showPoll() {
            this.$store.dispatch('participant/showPoll')
            this.poll = this.getPoll
        },
        /**
         * Calls saveAnswers from the store with the answerobj (cmdAnswer with all given input)
         */
        saveAnswer() {
            this.answerObj.username = this.getUsername
            this.$store.dispatch('participant/saveAnswer', this.answerObj)
        },
        /**
         * Calls showAnswer in store/participant. (Needed to get already given answers for multiple choice checkbox.)
         * Right now only used to get already checked boxes for multiple choice, but since alll answers from one
         * user are given back it can also be used for loading the page with already given answers, for non-anonym
         * and partialy anonym users, after they saved it.
         */
        showAnswer() {
            this.answerObj.username = this.getUsername
            this.answerObj.pollId = this.getPoll[1].data.pollId
            // console.log('Hi, from Participant page pre store.dispatch')
            this.$store.dispatch('participant/showAnswer', this.answerObj)
            // console.log('Hi, from Participant page post store.dispatch')
            this.answer = this.getAnswer
            console.log('This is the answer object:')

            console.log(this.answer) // never ending object, something is wrong: Start here to debug...
        },
    },
}
</script>

<style scoped></style>
