<template>
    <!--Build page after PollData from created() method is here-->
    <div v-if="getPoll[1] !== undefined">
        <v-btn
            :disabled="disableMe"
            @click="
                () => {
                    this.disableMe = true
                }
            "
            >Klick mich
        </v-btn>
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
                        <!--v-list v-for="poll in getPoll.data" :key="poll.id"-->
                        <v-list v-for="question in getCategory.questionList" :key="question.questionId" two-line>
                            <!--v-list v-for="question in category.questionList" :key="question.questionId" two-line-->
                            <v-card class="mx-auto">
                                <v-card-title class="col" :style="fontColorText">
                                    <!--the visibility of the number of questions depends on the poll configuration-->
                                    <div v-if="getVisibility">{{ getQuestionIndex() }}/{{ getNumberOfQuestions }}</div>
                                    <div class="ps-4">{{ question.questionMessage }}</div>
                                </v-card-title>
                                <div v-if="question.questionType === 'textfield'">
                                    <v-text-field
                                        label="Antwort"
                                        :color="fontColor"
                                        @input="saveAnswerField($event, question)"
                                    >
                                    </v-text-field>
                                </div>
                                <div v-else-if="question.questionType === 'choicebox'">
                                    <v-list v-for="answer in question.answerPossibilities" :key="answer.text">
                                        <v-checkbox
                                            class="ma-4 red--text"
                                            :label="answer"
                                            :color="fontColor"
                                            @change="saveAnswerCheckbox($event, question, answer)"
                                        ></v-checkbox>
                                    </v-list>
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
                            </v-card> </v-list
                        ><!--/v-list-->
                        <!--/v-list-->
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="8">
                        <v-btn class="pl-4" :disabled="hasNoPrevious" @click="getPreviousCategory()"
                            >Vorherige Seite</v-btn
                        >
                        <v-btn class="pl-4" :disabled="hasNoNext" @click="getNextCategory()">Nächste Seite</v-btn>
                        <v-btn color="primary" nuxt to="/AfterParticipated">
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
            category: ['Object'],
            questionIndex: 1,
            categoryIndex: 1,
            categoryLength: 0,
            enabled: false,
            disableMe: false,
            answerObj: {
                username: 'Nina',
                questionId: '1',
                answerList: [],
                answerId: '1', // hard coded, because I don't know what it's supposed to be
                pollId: '1',
            },
            answerList: [],
            // for checkbox this should be initialised with questionAnswers false/null at least for multiple choice options
            // but probably for all of them
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
        /**
         * Get's the given fontColor from PollData.
         * @returns {color: fontColor}
         */
        fontColorText() {
            return 'color:' + this.getPoll[1].data.fontColor
        },
        hasNoNext() {
            console.log('hasNoNext: ', this.categoryIndex)
            console.log(this.categoryIndex === this.categoryLength)
            return this.categoryIndex === this.categoryLength || !this.getChangeOfCategories
        },
        hasNoPrevious() {
            console.log('hasNoPrevious: ', this.categoryIndex)
            console.log(this.categoryIndex === 1)
            return this.categoryIndex === 1 || !this.getChangeOfCategories
        },
    },
    methods: {
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
            // this.answerObj.pollID = this.getPoll[1]
            console.log('calling saveAnswers in Participant.vue')
            console.log('answerobj:', this.answerObj)
            this.$store.dispatch('participant/saveAnswer', this.answerObj)
            // nuxt to afterparticipated
        },

        // -------------------------------------------------------------------------------------------------------------
        // Hilfsfunktionen innerhalb der .vue page
        getQuestionIndex() {
            this.questionIndex += 1
            return this.questionIndex - 1
        },
        getNextCategory() {
            this.$store.commit('participant/setCategory', 1)
            this.categoryIndex = this.getCategoryIndex
            this.categoryLength = this.getPoll[1].data.categoryList.length
            console.log(this.categoryIndex)
            this.category = this.getCategory
            this.$forceUpdate()
        },
        getPreviousCategory() {
            this.$store.commit('participant/setCategory', -1)
            this.categoryIndex = this.getCategoryIndex
            console.log(this.categoryIndex)
            if (this.categoryIndex !== 0) {
                this.category = this.getCategory
            }
            this.$forceUpdate()
        },
        /**
         * Get's the given answer of a checkbox question.
         * @param e (Change-Event)
         */
        saveAnswerCheckbox(e, question, answer) {
            this.answerObj.answerList = []
            let i
            // if checkBox was checked, not unchecked
            if (e === true) {
                for (i = 0; i < question.answerPossibilities.length; i++) {
                    if (answer === question.answerPossibilities[i]) {
                        this.answerObj.answerList.push(i) // index of true checkboxes
                    }
                }
            }
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            console.log(e)
            console.log(question)
            console.log('answer: ', this.answerObj.answerList)
            console.log('pollId: ', this.answerObj.pollId)
            console.log('questionId: ', this.answerObj.questionId)

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

            console.log('answer: ', this.answerObj.answerList)
            console.log('pollId: ', this.answerObj.pollId)
            console.log('questionId: ', this.answerObj.questionId)

            this.saveAnswer() // alternative: Button after every TextField
        },
    },
}
</script>

<style scoped></style>
