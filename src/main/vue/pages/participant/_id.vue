<template>
    <!--Build page after PollData from created() method is here-->
    <div v-if="getPoll[1] !== undefined">
        <v-container>
            <!--When/After the PollData "arrived" show the logo-->
            <div v-if="getPoll[1].data.logo !== undefined">
                <img :src="getPoll[1].data.logo" alt="failedToLoadLogo" height="275" />
            </div>
            <v-content>
                <v-row> </v-row>
                <v-row>
                    <v-col cols="8">
                        <v-card>
                            <h1 class="my-3 pa-5" :style="fontColorText">
                                {{ getPoll[1].data.pollName }}
                            </h1>
                        </v-card>
                        <!-- loads the questions from the current category in a list-->
                        <v-list
                            v-for="(question, index) in computedQuestionList"
                            :key="question.questionId"
                            :color="backgroundColor"
                            two-line
                        >
                            <!-- every question is in a Kard and consists of the questionMessageand the way to answer
                            it, and depending on the settings of the poll, the number of questions -->
                            <v-card class="mx-auto">
                                <v-card-title class="col" :style="fontColorText">
                                    <!--the visibility of the index of the current questions in relation to the total
                                    number of questions, given in the settings of the poll-->
                                    <div v-if="getVisibility">{{ (index+1) }}/{{ getNumberOfQuestions }}</div>
                                    <div class="ps-4">{{ question.questionMessage }}</div>
                                </v-card-title>
                                <div v-if="question.questionType === 'TextQuestion'">
                                    <v-card-text>
                                        <!--:rules="textQuestionRules"-->
                                        <!--question.textMultiline ===-->
                                        <div v-if="true">
                                            <v-textarea
                                                label="Antwort"
                                                auto-grow
                                                counter
                                                :color="fontColor"
                                                :rules="titleRules"
                                                rows="1"
                                                @input="saveAnswerField($event, question)"
                                            >
                                            </v-textarea>
                                        </div>
                                        <div v-else>
                                            <v-text-field
                                                label="Antwort"
                                                :color="fontColor"
                                                @input="saveAnswerField($event, question)"
                                            ></v-text-field>
                                        </div>
                                    </v-card-text>
                                </div>
                                <div v-else-if="question.questionType === 'ChoiceQuestion'">
                                    <!--Hi Nina, hier ist ein typo, damit meine SortQuestions angezeigt werden und ich sie debuggen kann -->
                                    <!--Radio Button since only one answer possible-->
                                    <div v-if="question.numberOfPossibleAnswers === 1">
                                        <v-card-text>
                                            <v-radio-group>
                                                <v-radio
                                                    v-for="answer in question.answerPossibilities"
                                                    :key="answer.text"
                                                    :label="`${answer}`"
                                                    @change="saveAnswerRadioButton(question, answer)"
                                                ></v-radio>
                                            </v-radio-group>
                                            <div v-if="question.userAnswers">
                                                <v-text-field
                                                    class="mx-4"
                                                    auto-grow
                                                    prepend-icon="mdi-plus"
                                                    clearable
                                                    @click:prepend="saveOwnAnswer(question)"
                                                    @input="saveAnswerPossibility($event, question)"
                                                ></v-text-field>
                                            </div>
                                        </v-card-text>
                                    </div>
                                    <div v-else>
                                        <!--Checkboxes, so that multiple answers are technically allowed,
                                        not able to save them yet-->
                                        <v-list v-for="answer in question.answerPossibilities" :key="answer.text">
                                            <v-checkbox
                                                class="my-n2 mx-3"
                                                dense
                                                :label="answer"
                                                :color="fontColor"
                                                @change="saveAnswerCheckbox($event, question, answer)"
                                            ></v-checkbox>
                                        </v-list>
                                        <div v-if="question.userAnswers">
                                            <v-text-field
                                                class="mx-4"
                                                auto-grow
                                                prepend-icon="mdi-plus"
                                                clearable
                                                @click:prepend="saveOwnAnswer(question)"
                                                @input="saveAnswerPossibility($event, question)"
                                            ></v-text-field>
                                        </div>
                                    </div>
                                </div>
                                <div v-else-if="question.questionType === 'SliderQuestion'">
                                    <v-card-text>
                                        <!--might have to check if attributes aren't empty, depending on data bank-->
                                        <p>
                                            {{ question.belowMessage }}
                                            <span class="float-right">{{ question.aboveMessage }}</span>
                                        </p>
                                        <!--Currently choosen value are supposed to be shown-->
                                        <div v-if="question.hideValues === false">
                                            <v-slider
                                                v-model="value1"
                                                :min="question.startValue"
                                                :max="question.endValue"
                                                :step="question.stepSize"
                                                ticks
                                                tick-size="4"
                                                thumb-label="always"
                                                append-icon="mdi-plus"
                                                prepend-icon="mdi-minus"
                                                :color="fontColor"
                                                :track-color="backgroundColor"
                                                @click:append="addValue"
                                                @click:prepend="subValue"
                                                @change="saveAnswerSliderQuestion($event, question)"
                                            >
                                            </v-slider>
                                        </div>
                                        <!--Thumb-label is not being shown-->
                                        <div v-else-if="question.hideValues === true">
                                            <v-slider
                                                v-model="value2"
                                                min="0"
                                                max="10"
                                                ticks
                                                tick-size="4"
                                                append-icon="mdi-plus"
                                                prepend-icon="mdi-minus"
                                                :color="fontColor"
                                                :track-color="backgroundColor"
                                                @click:append="addValue"
                                                @click:prepend="subValue"
                                                @change="saveAnswerSliderQuestion($event, question)"
                                            >
                                            </v-slider>
                                        </div>
                                    </v-card-text>
                                </div>
                                <!-- @change="getRangeQuestionAnswers"-->
                                <div v-else-if="question.questionType === 'RangeQuestion'">
                                    <!--rangeAnswer in rangeAnswers-->
                                    <v-list v-for="answer in question.answerPossibilities" :key="answer">
                                        <v-checkbox
                                            class="my-n2 mx-3"
                                            :label="answer"
                                            :color="fontColor"
                                            @change="saveAnswerCheckbox($event, question, answer)"
                                        ></v-checkbox>
                                    </v-list>
                                </div>
                                <div v-else-if="question.questionType === 'SortQuestion'"></div>

                                <!--Hi Nina, da muss natürlich 'SortQuestion' hin, aber zum debuggen mache ich das erstmal mit den ChoiceQuestions -->

                                <div v-else-if="question.questionType === 'ChoicQuestion'" class="pa-2">
                                    <v-subheader>
                                        Bitte bringen Sie die unten aufgeführten Möglichkeiten mittels Drag & Drop in
                                        eine von Ihnen präferierte Reihenfolge.
                                    </v-subheader>
                                    <draggable
                                        v-model="AnswerListsOfSortQuestions[index]"
                                        @change="saveAnswerSortQuestion(index, question)"
                                    >
                                        <div
                                            v-for="(answerPossibility, i) in AnswerListsOfSortQuestions[index]"
                                            :key="i"
                                        >
                                            <v-chip class="my-1 mx-3" :color="fontColor">
                                                {{ answerPossibility }}</v-chip
                                            >
                                        </div>
                                    </draggable>
                                    <v-btn @click="restoreAP(index, question)" :color="backgroundColor" class="ma-2">
                                        Zurücksetzen
                                    </v-btn>
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
                            >Vorherige Seite
                        </v-btn>
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
import draggable from 'vuedraggable'

export default {
    name: 'Participant',
    components: {
        draggable,
    },
    layout: 'participant', // uses special layout/participant instead of default-layout
    data() {
        return {
            radioGroup: 1,
            id: 0,
            poll: ['Object'],
            answer: ['Object'],
            category: ['Object'],
            question: ['Object'],
            categoryIndex: 1,
            categoryLength: 0,
            enabled: false,
            disableMe: false,
            value1: 0,
            value2: 0,
            answerObj: {
                username: 'Anonym',
                anonymityStatus: 'anonym',
                questionId: '1',
                answerList: [[]],
                answerId: '1',
                pollId: '1',
            },
            ownAnswers: [[]],
            rangeAnswers: [],
            textQuestionRules: [
                (v) => v.length >= 10 || 'Eingabe muss länger als 10 Zeichen sein!',
                (v) => v.length <= 100 || 'Eingabe muss kürzer als 100 Zeichen sein!',
            ],
            AnswerListsOfSortQuestions: [[1], [2], [3]],
        }
    },
    /**
     * Calls showPoll in methods to getPoll before/while the page is created.
     */
    created() {
        this.id = this.$route.params.id
        this.showPoll()
    },

    mounted() {
        this.createListOfAnswerPossibilitiesForSortQuestions()
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
        }),
        /**
         * Generates a questionList for the current category from the origin questionList, where every rangeQuestion is
         * converted to choiceboxQuestion and the other questions are taken over.
         * @returns c, the new QuestionList without rangeQuestions
         **/
        computedQuestionList() {
            const c = []
            for (let i = 0; i < this.getCategory.questionList.length; i++) {
                if (this.getCategory.questionList[i].questionType === 'RangeQuestion') {
                    const rangeAnswers = []
                    const max = this.getCategory.questionList[i].endValue
                    const min = this.getCategory.questionList[i].startValue
                    const step = this.getCategory.questionList[i].stepSize
                    const text1 = this.getCategory.questionList[i].belowMessage
                    const text2 = this.getCategory.questionList[i].aboveMessage

                    /* const rangeAnswers = []
                    const max = 100
                    const min = 10
                    const step = 10
                    const text1 = 'id'
                    const text2 = 'hjsrfb' */
                    if (max != null && min != null && step != null) {
                        if (text1 != null) {
                            rangeAnswers.push(text1)
                        }
                        const size = (max - min) / step
                        for (let i = 0; i < size; i++) {
                            const value = min + i * step
                            const nextValue = value + step
                            rangeAnswers.push(String(value) + ' - ' + String(nextValue))
                        }
                        if (text2 != null) {
                            rangeAnswers.push(text2)
                        }
                    }
                    // console.log(rangeAnswers)
                    // Arrays.asList(rangeAnswers)
                    const obj = {
                        questionType: this.getCategory.questionList[i].questionType,
                        questionMessage: this.getCategory.questionList[i].questionMessage,
                        categoryIndex: this.getCategory.questionList[i].categoryIndex,
                        questionId: this.getCategory.questionList[i].questionId,
                        answerPossibilities: rangeAnswers,
                        endValue: this.getCategory.questionList[i].endValue,
                        startValue: this.getCategory.questionList[i].startValue,
                        stepSize: this.getCategory.questionList[i].stepSize,
                        belowMessage: this.getCategory.questionList[i].belowMessage,
                        aboveMessage: this.getCategory.questionList[i].aboveMessage,
                        hideValues: this.getCategory.questionList[i].hideValues,
                    }
                    // this makes unexpected side effects
                    // this.getCategory.questionList[i].answerPossibilites = ['test1', 'test2']
                    // console.log(obj)
                    c[i] = obj
                } else {
                    c[i] = this.getCategory.questionList[i]
                }
            }
            return c
        },
        titleRules: [
            (v) => {
                if (v)
                    return (
                        v.length >= computedQuestionList[index].textMinimum ||
                        'Eingabe muss länger als 10 Zeichen sein!'
                    )
                else return true
            },
            (v) => {
                if (v)
                    return (
                        v.length <= computedQuestionList[index].textMaximum ||
                        'Eingabe muss kürzer als 100 Zeichen sein!'
                    )
                else return true
            },
        ],
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
            if (this.getPoll[1].data.backgroundColor === null) {
                return this.$vuetify.theme.currentTheme.background
            }
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
            // console.log('Hi im in the computed method!')
            this.generateRangeQuestionAnswers()
            return 'answer in rangeAnswers'
        }, // do we need this anywhere?
    },
    methods: {
        /**
         * Holt sich die gegebene Antwort des Textfeldes aus dem Array ownAnswers und schickt die Antwort mit der
         * questionId und pollId in den store, um die Antwort im Backend zu speichern. Löscht nach speichern der Antwort
         * den Eintrag im Array, damit eine neue Antwort gegeben werden kann.
         **/
        saveOwnAnswer(question) {
            // save the given answerPossibility from the array ownAnswer in the backend
            let x = 0
            for (let i = 0; i < this.ownAnswers.length; i++) {
                if (this.ownAnswers[i].length === 2) {
                    if (this.ownAnswers[i][1] === question.questionId) {
                        x = this.ownAnswers[i]
                    }
                }
            }
            // wird nur ausgeführt, wenn im Textfeld was steht
            if (x !== 0) {
                x[2] = this.getPoll[1].data.pollId
                console.log('Answer to save: ', x)
                this.$store.dispatch('participant/saveAnswerPossibility', x)
                // delete ownAnswer of the now saved question from the array
                for (let i = 0; i < this.ownAnswers.length; i++) {
                    if (this.ownAnswers[i][0] === x[0]) {
                        this.ownAnswers[i][1] = ''
                    }
                }
            }
            // TODO: Textfeld nach speichern wieder leeren
        },
        /**
         * Speichert die gegebene Antwort aus dem Textfeld in einem Array mit der zugehörigen QuestionId ab.
         **/
        saveAnswerPossibility(e, question) {
            let x = 0
            for (let i = 0; i < this.ownAnswers.length; i++) {
                if (this.ownAnswers[i].length === 2) {
                    if (this.ownAnswers[i][1] === question.questionId) {
                        this.ownAnswers[i][0] = e
                        x = 1
                    }
                }
            }
            // adds the new Answer possibility to the array
            if (x === 0) {
                this.ownAnswers.push([e, question.questionId])
            }
            // console.log('ownAnswers: ', this.ownAnswers)
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
        saveAnswerRadioButton(question, answer) {
            console.log(question)
            console.log(answer)
            this.answerObj.answerList = []
            for (let i = 0; i < question.answerPossibilities.length; i++) {
                if (answer === question.answerPossibilities[i]) {
                    this.answerObj.answerList.push(i) // index of true checkbox
                }
            }
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            this.saveAnswer() // alternative: Button after every TextField

            // this.showAnswer() // TODO: Fails with 405???
        },
        /**
         * Get's the given answer of a checkbox question and calls saveAnswer() to persist it in the database. This
         * happens after every change (check or uncheck) of a checkbox.
         * @param e (Change-Event)
         * @param question The question object, so it can get the QuestionID
         * @param answer The answer object, so it can get the answer possibilities.
         */
        saveAnswerCheckbox(e, question, answer) {
            console.log(e)
            console.log(question)
            console.log(answer)
            this.answerObj.answerList = []
            let i
            // checks if checkBox was checked, not unchecked
            console.log(e)
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

            // this.showAnswer() // TODO: Fails with 405???
        },
        /**
         * Get's the given answer of a free text question and calls saveAnswer() to persist it in the database. This
         * happens after every single character.
         * @param e (Input-Event)
         * @param question The question object, so it can get the QuestionID
         */
        saveAnswerField(e, question) {
            /* const answerListMultiline = [[]]
            let counter = 0
            for (let i = 0; i < e.length; i++) {
                if (e[i] !== '\n') {
                    answerListMultiline[counter].push(e[i])
                } else {
                    answerListMultiline.push([])
                    counter++
                }
            }
            console.log(answerListMultiline)
            // console.log(question.textMultiline)
            // console.log(question.textMax)
            // console.log(question.textMin)
            this.answerObj.answerList = answerListMultiline */
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
        saveAnswerSliderQuestion(e, question) {
            this.answerObj.answerList = [e]
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            this.saveAnswer()
        },

        /**
         * Get's the given answer of a Sort Question and calls saveAnswer() to persist it in the database.
         * This happens after every change to the Sorting list.
         * @param index the position of the question within the computedQuestionList
         * @param question The question object, so it can get the QuestionID
         */
        saveAnswerSortQuestion(index, question) {
            // new empty list, so the answers don't add up
            this.answerObj.answerList = []
            for (let i = 0; i < this.AnswerListsOfSortQuestions[index].length; i++) {
                this.answerObj.answerList.push(
                    this.computedQuestionList[index].answerPossibilities.indexOf(
                        this.AnswerListsOfSortQuestions[index][i]
                    )
                )
            }
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId
            console.log('AnswerObj.answerList:')
            console.log(this.answerObj.answerList)
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
        generateRangeQuestionAnswers() {
            // console.log('Hi, the function is called!')
            /*
                // how can I access this??? Both ways don't work
                const max = this.getCategory.data.questio...
                const min = this.getCategory.questionList[questionId].startValue
                const step = this.getCategory.questionList[questionId].stepSize
                const text1 = this.getCategory.questionList[questionId].belowMessage
                const text2 = this.getCategory.questionList[questionId].aboveMessage
                */
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
                    const value = min + i * step
                    this.rangeAnswers.push(value)
                }
                if (text2 != null) {
                    this.rangeAnswers.push(text2)
                }
            }
            // console.log(this.question)
            // console.log(this.rangeAnswers)
            // console.log('Hi Im in the getRangeQuestionAnswersMutation method!')
        },
        // -------------------------------------------------------------------------------------------------------------
        // Get or save information to/from the Backend
        /**
         * Calls showPoll in store/participant.js.
         */
        showPoll() {
            this.$store.dispatch('participant/showPoll', this.id)
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
            console.log('Hi, from Participant page pre store.dispatch')
            this.$store.dispatch('participant/showAnswer', this.answerObj)
            console.log('Hi, from Participant page post store.dispatch')
            this.answer = this.getAnswer
            console.log('This is the answer object:')

            console.log(this.answer) // never ending object, something is wrong: Start here to debug...
        },

        /**
         * copies all the AnswerLists of SortQuestions from computedQuestionList
         * stores them in AnswerListsOfSortQuestions
         * for every question that isn't a sortQuestion an empty Array is added, so that the index to access is question is the same for cpmputedQuestionList
         *
         */
        createListOfAnswerPossibilitiesForSortQuestions() {
            for (let i = 0; i < this.computedQuestionList.length; i++) {
                this.AnswerListsOfSortQuestions.push([])
                if (this.computedQuestionList[i].questionType === 'ChoiceQuestion') {
                    // nur zum testen! Eigentlich 'SortQuestion'
                    const answerList = []
                    for (let j = 0; j < this.computedQuestionList[i].answerPossibilities.length; j++) {
                        answerList.push(this.computedQuestionList[i].answerPossibilities[j])
                    }
                    this.AnswerListsOfSortQuestions[i] = answerList
                }
            }
            // console.log('AnswerListOfSortQuestions:' + this.AnswerListsOfSortQuestions[0])
            // console.log('AnswerListOfSortQuestions:' + this.AnswerListsOfSortQuestions[1])
        },

        /**
         * restores the original order in AnswerListsOfSortQuestions for one question
         *
         * @param questionIndex indes of the question whos anserlist should be restored to the original order
         */

        restoreAP(questionIndex, question) {
            for (let i = 0; i < this.computedQuestionList[questionIndex].answerPossibilities.length; i++) {
                this.AnswerListsOfSortQuestions[questionIndex][i] = this.computedQuestionList[
                    questionIndex
                ].answerPossibilities[i]
            }

            this.$forceUpdate()
            this.saveAnswerSortQuestion(questionIndex, question)
        },
    },
}
</script>

<style scoped></style>
