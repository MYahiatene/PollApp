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
                        <v-list
                            v-for="question in getCategory.questionList"
                            :key="question.questionId"
                            two-line
                            :color="backgroundColor"
                        >
                            <!-- every question is in a card and consists of the questionMessageand the way to answer
                            it, and depending on the settings of the poll, the number of questions -->
                            <v-card class="mx-auto">
                                <v-card-title class="col" :style="fontColorText">
                                    <!--the visibility of the index of the current questions in relation to the total
                                    number of questions, given in the settings of the poll-->
                                    <div v-if="getVisibility">{{ getQuestionIndex() }}/{{ getNumberOfQuestions }}</div>
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
                                <div v-else-if="question.questionType === 'RangeQuestion'">
                                    <v-card-text>
                                        <!--might have to check if attributes aren't empty, depending on data bank-->
                                        <!--min="question.startValue"-->
                                        <!--max="question.endValue"-->
                                        <!--step="question.stepSize-->
                                        <v-slider
                                            v-model="value"
                                            min="0"
                                            max="10"
                                            ticks
                                            tick-size="4"
                                            thumb-label="always"
                                            append-icon="mdi-plus"
                                            prepend-icon="mdi-minus"
                                            @click:append="addValue"
                                            @click:prepend="subValue"
                                            @change="saveAnswerRangeQuestion($event, question)"
                                            :color="fontColor"
                                            :track-color="backgroundColor"
                                        >
                                        </v-slider>
                                    </v-card-text>
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
import AuthGate from '../../components/AuthGate'
export default {
    name: 'Participant',
    layout: 'participant', // uses special layout/participant instead of default-layout
    components: { AuthGate },
    data() {
        return {
            poll: ['Object'],
            category: ['Object'],
            questionIndex: 1,
            categoryIndex: 1,
            categoryLength: 0,
            enabled: false,
            disableMe: false,
            value: 0,
            answerObj: {
                username: 'Nina',
                questionId: '1',
                answerList: [],
                answerId: '1',
                pollId: '1',
            },
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
            isAuthenticated: 'login/isAuthenticated',
            getVisibility: 'participant/getVisibility',
            getNumberOfQuestions: 'participant/getNumberOfQuestions',
            getCategoryIndex: 'participant/getCategoryIndex',
            getCategory: 'participant/getCategory',
            getChangeOfCategories: 'participant/getChangeOfCategories',
        }),
        /**
         * Get's the given FontColor from PollData.
         * @returns {fontColor}
         */
        fontColor() {
            return this.getPoll[1].data.fontColor
        },
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
    },
    methods: {
        /**
         * Increases the questionIndex at the page and gives back the current questionIndex. Is called when the
         * visibility is true and gives the index of the question inside the category.
         * @returns (questionIndex: number)
         */
        getQuestionIndex() {
            // this.questionIndex += 1
            return this.questionIndex // - 1
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

            this.saveAnswer() // alternative: Button after every TextField
        },
        /**
         * Get's the given answer of a free text question and calls saveAnswer() to persist it in the database. This
         * happens after every single character.
         * @param e (Input-Event)
         */
        saveAnswerField(e, question) {
            this.answerObj.answerList = [e]
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            this.saveAnswer() // alternative: Button after every TextField
        },
        saveAnswerRangeQuestion(e, question) {},
        subValue() {
            this.value = this.value - 1
            console.log('In here!')
        },
        addValue() {
            this.value = this.value + 1
            console.log('In here!2')
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
            this.$store.dispatch('participant/saveAnswer', this.answerObj)
        },
    },
}
</script>

<style scoped></style>
