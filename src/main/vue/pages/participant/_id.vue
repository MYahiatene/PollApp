<template>
    <!--Build page after PollData from created() method is here-->
    <div v-if="getPoll[1] !== undefined">
        <div v-if="participated === false">
            <v-container>
                <!--When/After the PollData "arrived" show the logo-->
                <div v-if="getPoll[1].data.logo !== null">
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
                                            <div v-if="question.textMultiline === true">
                                                <v-textarea
                                                    v-model="valueList[index]"
                                                    label="Antwort"
                                                    auto-grow
                                                    counter
                                                    :color="fontColor"
                                                    :rules="textQuestionRules"
                                                    rows="1"
                                                    @input="saveAnswerField($event, question)"
                                                >
                                                </v-textarea>
                                            </div>
                                            <div v-else>
                                                <v-text-field
                                                    v-model="valueList[index]"
                                                    label="Antwort"
                                                    :color="fontColor"
                                                    :rules="textQuestionRules"
                                                    @input="saveAnswerField($event, question)"
                                                ></v-text-field>
                                            </div>
                                        </v-card-text>
                                    </div>
                                    <div v-else-if="question.questionType === 'ChoiceQuestion'">
                                        <!--Radio Button since only one answer possible-->
                                        <!--Only to debug, otherwise numberOfPossibleAnswer === 1-->
                                        <div v-if="question.numberOfPossibleAnswers === 1">
                                            <div v-if="question.dropDown">
                                                <v-card-text>
                                                    <v-overflow-btn
                                                        v-model="valueList[index]"
                                                        class="my-2"
                                                        :items="question.answerPossibilities"
                                                        :value="valueList[index]"
                                                        :color="fontColor"
                                                        label="Auswahl"
                                                        @change="saveAnswerRadioButton(question, valueList[index])"
                                                    ></v-overflow-btn>
                                                </v-card-text>
                                            </div>
                                            <div v-else>
                                                <v-card-text>
                                                    <v-radio-group v-model="valueList[index]">
                                                        <v-radio
                                                            v-for="answer in question.answerPossibilities"
                                                            :key="answer.text"
                                                            :label="`${answer}`"
                                                            :color="fontColor"
                                                            :value="answer"
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
                                        </div>
                                        <div v-else>
                                            <!--Checkboxes, so that multiple answers are allowed-->
                                            <div v-if="question.dropDown">
                                                <v-card-text>
                                                    <v-select
                                                        v-model="valueList[index]"
                                                        class="my-2"
                                                        :color="fontColor"
                                                        :item-color="fontColor"
                                                        :items="question.answerPossibilities"
                                                        label="Auswahl"
                                                        multiple
                                                        clearable
                                                        :counter="question.numberOfPossibleAnswers"
                                                        :error="errorCheckbox(index, question)"
                                                        :error-messages="errorMessageCheckbox"
                                                        @change="
                                                            saveAnswerCheckbox(
                                                                $event,
                                                                question,
                                                                valueList[index][valueList[index].length - 1],
                                                                index
                                                            )
                                                        "
                                                    >
                                                        <template v-slot:selection="{ item, index }">
                                                            <v-chip v-if="index < dropDownAnswersDisplayedInSelect">
                                                                <span>{{ item }}</span>
                                                            </v-chip>
                                                            <span
                                                                v-if="index === dropDownAnswersDisplayedInSelect"
                                                                class="grey--text caption"
                                                            >
                                                                (und
                                                                {{
                                                                    valueList[index].length -
                                                                    dropDownAnswersDisplayedInSelect
                                                                }}
                                                                weitere)
                                                            </span>
                                                        </template>
                                                        <!--                                                        <template v-slot:selection="{ item, index }">-->
                                                        <!--                                                            <v-chip v-if="index < 3">-->
                                                        <!--                                                                <span>{{ item }}</span>-->
                                                        <!--                                                            </v-chip>-->
                                                        <!--                                                            <span v-if="index === 3" class="grey&#45;&#45;text caption">-->
                                                        <!--                                                                (und weitere)-->
                                                        <!--                                                            </span>-->
                                                        <!--                                                        </template>-->
                                                    </v-select>
                                                </v-card-text>
                                            </div>

                                            <div v-else>
                                                <v-list
                                                    v-for="answer in question.answerPossibilities"
                                                    :key="answer.text"
                                                >
                                                    <v-checkbox
                                                        v-model="valueList[index]"
                                                        class="my-n2 mx-3"
                                                        dense
                                                        :label="answer"
                                                        :color="fontColor"
                                                        :value="answer"
                                                        :error="errorCheckbox(index, question)"
                                                        :error-messages="errorMessageCheckbox"
                                                        @change="saveAnswerCheckbox($event, question, answer, index)"
                                                    ></v-checkbox>
                                                </v-list>
                                                <div v-if="question.userAnswers">
                                                    <v-text-field
                                                        class="mx-4"
                                                        :color="fontColor"
                                                        auto-grow
                                                        prepend-icon="mdi-plus"
                                                        clearable
                                                        @click:prepend="saveOwnAnswer(question)"
                                                        @input="saveAnswerPossibility($event, question)"
                                                    ></v-text-field>
                                                </div>
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
                                            <!--Thumb label is being shown-->
                                            <div v-if="question.hideValues === false">
                                                <v-slider
                                                    v-model="valueList[index]"
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
                                                    @click:append="addValue(question, index)"
                                                    @click:prepend="subValue(question, index)"
                                                    @change="saveAnswerSliderQuestion($event, question, index)"
                                                >
                                                </v-slider>
                                            </div>
                                            <!--Thumb-label is not being shown-->
                                            <div v-else-if="question.hideValues === true">
                                                <v-slider
                                                    v-model="valueList[index]"
                                                    :min="question.startValue"
                                                    :max="question.endValue"
                                                    :step="question.stepSize"
                                                    append-icon="mdi-plus"
                                                    prepend-icon="mdi-minus"
                                                    :color="fontColor"
                                                    :track-color="backgroundColor"
                                                    @click:append="addValue(question, index)"
                                                    @click:prepend="subValue(question, index)"
                                                    @change="saveAnswerSliderQuestion($event, question)"
                                                >
                                                </v-slider>
                                            </div>
                                        </v-card-text>
                                    </div>
                                    <div v-else-if="question.questionType === 'RangeQuestion'">
                                        <v-radio-group v-model="valueList[index]">
                                            <v-radio
                                                v-for="answer in question.answerPossibilities"
                                                :key="answer.text"
                                                class="mx-4"
                                                :color="fontColor"
                                                :label="`${answer}`"
                                                :value="answer"
                                                @change="saveAnswerRadioButton(question, answer)"
                                            ></v-radio>
                                        </v-radio-group>
                                    </div>

                                    <div v-else-if="question.questionType === 'SortQuestion'" class="pa-2">
                                        <v-subheader>
                                            Bitte bringen Sie die unten aufgeführten Möglichkeiten mittels Drag & Drop
                                            in eine von Ihnen präferierte Reihenfolge.
                                        </v-subheader>
                                        <draggable
                                            v-model="AnswerListsOfSortQuestions[index]"
                                            @change="saveAnswerSortQuestion(index, question)"
                                        >
                                            <div
                                                v-for="(answerPossibility, i) in AnswerListsOfSortQuestions[index]"
                                                :key="i"
                                            >
                                                <v-chip
                                                    class="my-1 mx-3"
                                                    :color="fontColor"
                                                    :outlined="!sqWasAnsweredList[index]"
                                                >
                                                    {{ answerPossibility }}</v-chip
                                                >
                                            </div>
                                        </draggable>
                                        <v-btn class="ma-2" @click="restoreAP(index, question)">
                                            Zurücksetzen
                                        </v-btn>
                                        <v-btn
                                            v-if="!sqWasAnsweredList[index]"
                                            class="ma-2"
                                            @click="saveAnswerSortQuestion(index, question)"
                                        >
                                            Reihenfolge so übernehmen
                                        </v-btn>
                                    </div>
                                </v-card>
                            </v-list>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="8">
                            <div v-if="getPoll[1].data.anonymityStatus !== '1'">
                                <!-- button to get to the previous category, if there is no previous one, the button is disabled,
                         else, the previous category is loaded by getPreviousCategory() if clicked -->
                                <v-btn class="pl-4" :disabled="hasNoPrevious" @click="getPreviousCategory()"
                                    >Vorherige Seite
                                </v-btn>
                                <!-- button to get to the next category, same principle as the one above -->
                                <v-btn class="pl-4" :disabled="hasNoNext" @click="getNextCategory()"
                                    >Nächste Seite</v-btn
                                >
                                <!--nuxt to="/AfterParticipated"-->
                                <v-btn class="ma-1 float-right" :color="fontColor" @click="saveParticipatedPoll()">
                                    Absenden
                                </v-btn>
                                <v-btn class="ma-1 float-right" :color="fontColor" nuxt to="/AfterSaved">
                                    Speichern
                                </v-btn>
                            </div>
                            <div v-else>
                                <!-- button to get to the previous category, if there is no previous one, the button is disabled,
                         else, the previous category is loaded by getPreviousCategory() if clicked -->
                                <v-btn class="pl-4" :disabled="hasNoPrevious" @click="getPreviousCategory()"
                                    >Vorherige Seite
                                </v-btn>
                                <!-- button to get to the next category, same principle as the one above -->
                                <v-btn class="pl-4" :disabled="hasNoNext" @click="getNextCategory()"
                                    >Nächste Seite</v-btn
                                >
                                <!--nuxt to="/AfterParticipated"-->
                                <v-btn :color="fontColor" @click="saveParticipatedPoll()">
                                    Absenden
                                </v-btn>
                            </div>
                        </v-col>
                    </v-row>
                </v-content>
            </v-container>
        </div>
        <div v-else>
            <v-container>
                <v-layout column justify-center align-center>
                    <v-layout xs12 sm8 md6>
                        <v-row>
                            <v-col cols="12" lg="12">
                                <v-row>
                                    <v-card>
                                        <v-card-title class="headline">
                                            Sie haben an dieser Umfrage bereits teilgenommen!
                                        </v-card-title>
                                        <v-card-text>
                                            Vielen Dank für ihre Teilnahme!
                                        </v-card-text>
                                    </v-card>
                                </v-row>
                            </v-col>
                            <br />
                            <v-col cols="12" lg="3"></v-col>
                        </v-row>
                    </v-layout>
                </v-layout>
            </v-container>
        </div>
    </div>
    <div v-else>
        <v-card>
            <v-card-title>Die Umfrage ist nicht aktiv</v-card-title>
        </v-card>
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
            answer: [],
            givenAnswers: [],
            category: ['Object'],
            question: ['Object'],
            categoryIndex: 1,
            categoryLength: 1,
            enabled: false,
            disableMe: false,
            slideValueList: [],
            answerObj: {
                username: 'Anonym',
                anonymityStatus: 'anonym',
                questionId: '1',
                answerList: [],
                answerId: '1',
                pollId: '1',
            },
            ownAnswers: [[]],
            rangeAnswers: [],
            AnswerListsOfSortQuestions: [],
            sqWasAnsweredList: [],
            lastInput: 'Letzte Eingabe',
            valueList: [],
            participated: false,
            errorMessageCheckbox: '',
            dropdown: false,
            dropDownAnswersDisplayedInSelect: 3,
        }
    },
    /**
     * Calls showPoll in methods to getPoll before/while the page is created.
     */
    async created() {
        this.id = this.$route.params.id
        await this.showPoll()
        await this.showAnswer()
        await this.showParticipated()
        await this.valuesForQuestions()
        if (this.poll[1].data.pollStatus === 3) {
            console.log('PollStatus 3')
            await this.$router.push('/pollOver')
        }
        if (this.poll[1].data.pollStatus === 0 || this.poll[1].data.pollStatus === 1) {
            console.log('PollStatus 0')
            await this.$router.push('/PollNotActive')
        }
    },

    mounted() {
        this.createListOfAnswerPossibilitiesForSortQuestions()
        this.categoryLength = this.getPoll[1].data.categoryList.length
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
            getParticipated: 'participant/getParticipated',
        }),
        /**
         * Generates a questionList for the current category from the origin questionList, where every rangeQuestion is
         * converted to a choiceboxQuestion with generated answerPossibilties and the other questions are taken over.
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

                    if (max != null && min != null && step != null) {
                        if (text1) {
                            rangeAnswers.push(text1)
                        }
                        const size = (max - min) / step
                        for (let i = 0; i < size; i++) {
                            const value = min + i * step
                            const nextValue = value + step
                            if (value >= 0 && nextValue >= 0) {
                                rangeAnswers.push(String(value) + ' - ' + String(nextValue))
                            } else if (value <= 0 && nextValue >= 0) {
                                rangeAnswers.push('(' + String(value) + ') - ' + String(nextValue))
                            } else if (value >= 0 && nextValue <= 0) {
                                rangeAnswers.push(String(value) + ' - (' + String(nextValue) + ')')
                            } else if (value <= 0 && nextValue <= 0) {
                                rangeAnswers.push('(' + String(value) + ') - (' + String(nextValue) + ')')
                            }
                        }
                        if (text2) {
                            rangeAnswers.push(text2)
                        }
                    }
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
                    c[i] = obj
                } else {
                    c[i] = this.getCategory.questionList[i]
                }
            }
            return c
        },

        /**
         * Gives back an array of rules (max, min characters) for the text field answer, based on choosen attributes.
         */
        textQuestionRules() {
            let min
            let max
            let index = 0
            for (let i = 0; i < this.getCategory.questionList.length; i++) {
                if (this.getCategory.questionList[i].questionId === this.answerObj.questionId) {
                    index = i
                }
            }
            if (!this.getCategory.questionList[index].textMinimum) {
                min = 0
            } else {
                min = this.getCategory.questionList[index].textMinimum
            }
            if (!this.getCategory.questionList[index].textMaximum) {
                max = 1000
            } else {
                max = this.getCategory.questionList[index].textMaximum
            }
            return [
                (v) => {
                    if (v) return v.length >= min || 'Eingabe muss länger als ' + min + ' Zeichen sein!'
                    else return true
                },
                (v) => {
                    if (v) return v.length <= max || 'Eingabe muss kürzer als ' + max + ' Zeichen sein!'
                    else return true
                },
            ]
        },
        /**
         * Get's the given FontColor from PollData.
         * @returns {fontColor}
         */
        fontColor() {
            if (this.getPoll[1].data.ownDesign) {
                return this.getPoll[1].data.fontColor
            } else {
                return 'bla'
            }
        },
        /**
         * Get's the given BackgroundColor from PollData.
         * @returns {backgroundColor}
         */
        backgroundColor() {
            if (!this.getPoll[1].data.ownDesign) {
                return this.$vuetify.theme.currentTheme.background
            }
            return this.getPoll[1].data.backgroundColor
        },
        /**
         * Get's the given fontColor from PollData.
         * @returns {color: fontColor}
         */
        fontColorText() {
            if (this.getPoll[1].data.ownDesign) {
                return 'color:' + this.getPoll[1].data.fontColor
            } else {
                return 'bla'
            }
        },
        /**
         * Returns true if there is no next category in the poll or if the ChangeOfCategories is not allowed in the poll
         * @returns {boolean}
         */
        hasNoNext() {
            console.log('categoryLength', this.categoryLength)
            console.log('categoryIndex', this.categoryIndex)
            return this.categoryIndex >= this.categoryLength
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
        errorCheckbox(i, question) {
            if (this.valueList[i]) {
                if (Object.keys(this.valueList[i]).length > question.numberOfPossibleAnswers) {
                    return true
                } else {
                    return false
                }
            }
            return false
        },
        /**
         * This method creates valueList which for every question type, but sortQuestion is used as v-model to show
         * already given answers by the user, or nothing at all, if there are no previously given answers.
         */
        valuesForQuestions() {
            this.createListOfAnswerPossibilitiesForSortQuestions()
            this.valueList = []
            console.log('givenAnswers', this.givenAnswers)
            console.log('computedQuestionList', this.givenAnswers)
            let index = 0
            for (let m = 0; m < this.getPoll[1].data.categoryList.length; m++) {
                if (this.getCategory.categoryId > this.getPoll[1].data.categoryList[m].categoryId) {
                    index += this.getPoll[1].data.categoryList[m].questionList.length
                } else {
                    break
                }
            }
            console.log('index', index)
            for (let i = 0; i < this.getCategory.questionList.length; i++) {
                const type = this.getCategory.questionList[i].questionType

                console.log('type', type)
                if (this.givenAnswers !== []) {
                    console.log('!== []')
                    if (this.givenAnswers[i + index] != null) {
                        console.log('!= null')
                        // for RadioButtons we need answer text and given back are the indizes
                        if (
                            (type === 'ChoiceQuestion' &&
                                this.getCategory.questionList[i].numberOfPossibleAnswers === 1) ||
                            type === 'RangeQuestion'
                        ) {
                            this.valueList.push(
                                this.computedQuestionList[i].answerPossibilities[
                                    this.givenAnswers[i + index].givenAnswerList
                                ]
                            )
                        } // for Checkboxes and RangeQuestions it works the same
                        else if (type === 'ChoiceQuestion') {
                            const array = []
                            for (let j = 0; j < this.givenAnswers[i + index].givenAnswerList.length; j++) {
                                console.log('dfg', this.givenAnswers[i + index].givenAnswerList[j])
                                array.push(
                                    this.computedQuestionList[i].answerPossibilities[
                                        this.givenAnswers[i + index].givenAnswerList[j]
                                    ]
                                )
                            }
                            this.valueList.push(array)
                        } else if (type === 'SortQuestion') {
                            const array = []
                            for (let j = 0; j < this.givenAnswers[i + index].givenAnswerList.length; j++) {
                                array.push(
                                    this.computedQuestionList[i].answerPossibilities[
                                        this.givenAnswers[i + index].givenAnswerList[j]
                                    ]
                                )
                            }
                            console.log('array', array)
                            for (let j = 0; j < array.length; j++) {
                                this.AnswerListsOfSortQuestions[i][j] = array[j]
                            }
                            this.sqWasAnsweredList[i] = true
                            // won't be used for SortQuestions, but needs to be correct index
                            this.valueList.push(array)
                        } else {
                            console.log('type', type)
                            console.log('textpush', this.givenAnswers[i + index].givenAnswerList[0])
                            this.valueList.push(this.givenAnswers[i + index].givenAnswerList[0])
                        }
                    } else {
                        switch (type) {
                            case 'TextQuestion':
                                this.valueList.push('')
                                break
                            case 'SlideQuestion':
                                this.valueList.push(this.getCategory.questionList[i].startValue)
                                break
                            case 'ChoiceQuestion':
                                this.valueList.push([])
                                break
                            case 'RangeQuestion':
                                this.valueList.push([])
                                break
                            case 'SortQuestion':
                                // won't be used for SortQuestions, but needs to be correct index
                                this.valueList.push(this.AnswerListsOfSortQuestions[i])
                                break
                        }
                    }
                } else {
                    switch (type) {
                        case 'TextQuestion':
                            this.valueList.push('')
                            break
                        case 'SlideQuestion':
                            this.valueList.push(this.getCategory.questionList[i].startValue)
                            break
                        case 'ChoiceQuestion':
                            this.valueList.push([])
                            break
                        case 'RangeQuestion':
                            this.valueList.push([])
                            break
                        case 'SortQuestion':
                            // won't be used for SortQuestions, but needs to be correct index
                            this.valueList.push(this.AnswerListsOfSortQuestions[i])
                            break
                    }
                }
            }
            console.log('AnswerSortQuestion', this.AnswerListsOfSortQuestions)
            console.log('ValueList is created: ', this.valueList)
        },
        /**
         * Get the given answer of the text field from the array ownAnswers and send the answer with the
         * questionId and pollId in the store to save the answer in the backend. Deletes after saving the answer
         * the entry in the array so that a new answer can be given.
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
         * Saves the given answer from the text field in an array with the associated QuestionId.
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
            this.valuesForQuestions()
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
            this.valuesForQuestions()
        },
        /**
         * Get's the given answer of a checkbox question and calls saveAnswer() to persist it in the database. This
         * happens after every change (check or uncheck) of a checkbox.
         * @param e (Change-Event)
         * @param question The question object, so it can get the QuestionID
         * @param answer The answer object, so it can get the answer possibilities.
         */
        saveAnswerRadioButton(question, answer) {
            this.answerObj.answerList = []
            for (let i = 0; i < question.answerPossibilities.length; i++) {
                if (answer === question.answerPossibilities[i]) {
                    this.answerObj.answerList.push(i) // index of true checkbox
                }
            }
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.answerObj.questionId = question.questionId

            this.saveAnswer() // alternative: Button after every TextField
        },
        /**
         * Get's the given answer of a checkbox question and calls saveAnswer() to persist it in the database. This
         * happens after every change (check or uncheck) of a checkbox.
         * @param e (Change-Event)
         * @param question The question object, so it can get the QuestionID
         * @param answer The answer object, so it can get the answer possibilities.
         */
        saveAnswerCheckbox(e, question, answer, index) {
            if (this.valueList[index]) {
                // check if number of answers is bigger than allowed answers
                if (Object.keys(this.valueList[index]).length > question.numberOfPossibleAnswers) {
                    this.errorMessageCheckbox = 'Maximal ' + question.numberOfPossibleAnswers + ' Antworten!'
                    for (let j = 0; j < Object.keys(this.valueList[index]).length; j++) {
                        if (this.valueList[index][j] === answer) {
                            // take last argument from list
                            this.valueList[index].splice(question.numberOfPossibleAnswers, 1)
                        }
                    }
                    return true
                } else {
                    // else: Answers are valid, so will be saved
                    this.errorMessageCheckbox = ''
                    this.showAnswerMultipleChoice()
                    const objectIndex = this.givenAnswers.indexOf('Object')
                    if (objectIndex !== -1) {
                        this.$store.commit('participant/takeOffAnswer', objectIndex)
                    }
                    let i
                    // checks if checkBox was checked, not unchecked
                    if (e !== []) {
                        for (i = 0; i < question.answerPossibilities.length; i++) {
                            if (answer === question.answerPossibilities[i]) {
                                this.$store.commit('participant/setAnswer', i)
                            }
                        }
                    } else {
                        for (i = 0; i < question.answerPossibilities.length; i++) {
                            if (answer === question.answerPossibilities[i]) {
                                this.$store.commit('participant/takeOffAnswer', i)
                            }
                        }
                    }
                    this.answerObj.answerList = []
                    for (let j = 0; j < e.length; j++) {
                        for (let k = 0; k < question.answerPossibilities.length; k++) {
                            if (e[j] === question.answerPossibilities[k]) {
                                this.answerObj.answerList.push(k)
                            }
                        }
                    }
                    this.answerObj.pollId = this.getPoll[1].data.pollId
                    this.answerObj.questionId = question.questionId

                    this.saveAnswer()
                }
            }
        },
        /**
         * Get's the given answer of a free text question and calls saveAnswer() to persist it in the database. This
         * happens after every single character.
         * @param e (Input-Event)
         * @param question The question object, so it can get the QuestionID
         */
        saveAnswerField(e, question) {
            const answerList = []
            let answer = ''
            let stringcounter = 0
            for (let i = 0; i < e.length; i++) {
                if (stringcounter === 255 || e[i] === '\n') {
                    answerList.push(answer)
                    answer = ''
                    stringcounter = 0
                }
                if (e[i] === '\n') {
                    stringcounter += 1
                } else {
                    answer = answer.concat(e[i])
                    stringcounter += 1
                }
            }
            answerList.push(answer)

            this.answerObj.answerList = answerList
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
        saveAnswerSliderQuestion(e, question, index) {
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
            this.sqWasAnsweredList[index] = true
            this.$forceUpdate()
            this.saveAnswer()
        },
        /**
         * Moves the slider one step to the left, if possible.
         * It's called by click on + Icon at a Range Question.
         */
        subValue(question, index) {
            console.log(this.valueList[index])
            // TODO: doesn't update, so value doesn't show on slider, but does change to "correct value, after click on
            // other slider...
            this.valueList[index] = this.valueList[index] - question.stepSize || question.startValue
            console.log(this.valueList[index])
            this.$forceUpdate()
        },
        /**
         * Moves the slider one step to the right, if possible.
         * It's called by click on + Icon at a Range Question.
         */
        addValue(question, index) {
            this.valueList[index] = this.valueList[index] + question.stepSize || question.endValue
            this.$forceUpdate()
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
                if (this.computedQuestionList[i].questionType === 'SortQuestion') {
                    const answerList = []
                    for (let j = 0; j < this.computedQuestionList[i].answerPossibilities.length; j++) {
                        answerList.push(this.computedQuestionList[i].answerPossibilities[j])
                    }
                    this.AnswerListsOfSortQuestions[i] = answerList
                }
            }
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
        // -------------------------------------------------------------------------------------------------------------
        // Get or save information to/from the Backend
        /**
         * Calls showPoll in store/participant.js and valuesForQuestion to get the given Answers
         */
        showPoll() {
            this.$store.dispatch('participant/showPoll', this.id)
            this.poll = this.getPoll
        },
        /**
         * Calls participated in store/participant.js.
         */
        async showParticipated() {
            const userObj = {
                pollTaker: this.getUsername,
                pollId: this.getPoll[1].data.pollId,
            }
            await this.$axios
                .post('/participated', userObj)
                .catch()
                .then((result) => {
                    this.participated = result.data
                })
        },
        /**
         * Calls saveAnswers from the store with the answerobj (cmdAnswer with all given input)
         */
        saveAnswer() {
            console.log('answerObj', this.answerObj)
            this.answerObj.username = this.getUsername
            this.$store.dispatch('participant/saveAnswer', this.answerObj)
        },
        /**
         * Calls showAnswer in store/participant. (Needed to get already given answers for multiple choice checkbox.)
         * Right now only used to get already checked boxes for multiple choice, but since alll answers from one
         * user are given back it can also be used for loading the page with already given answers, for non-anonym
         * and partialy anonym users, after they saved it.
         */
        async showAnswer() {
            this.answerObj.username = this.getUsername
            this.answerObj.pollId = this.getPoll[1].data.pollId
            let givenAnswersUnsorted = []
            // const givenAnswersSorted = []
            await this.$axios
                .post('/getPollResult', this.answerObj)
                .catch()
                .then((result) => {
                    if (result.data.answerList !== undefined) {
                        givenAnswersUnsorted = result.data.answerList
                    } else {
                        givenAnswersUnsorted = []
                    }
                })
            for (let i = 0; i < this.getPoll[1].data.categoryList.length; i++) {
                for (let j = 0; j < this.getPoll[1].data.categoryList[i].questionList.length; j++) {
                    const questionId = this.getPoll[1].data.categoryList[i].questionList[j].questionId
                    const length = this.givenAnswers.length
                    for (let k = 0; k < givenAnswersUnsorted.length; k++) {
                        if (givenAnswersUnsorted[k].questionId === questionId) {
                            this.givenAnswers.push(givenAnswersUnsorted[k])
                        }
                    }
                    if (length === this.givenAnswers.length) {
                        this.givenAnswers.push(null)
                    }
                }
            }
            console.log('givenAnswers', this.givenAnswers)
        },
        showAnswerMultipleChoice() {
            this.answerObj.username = this.getUsername
            this.answerObj.pollId = this.getPoll[1].data.pollId
            this.$store.dispatch('participant/showAnswer', this.answerObj)
            this.givenAnswers = this.getAnswer
        },
        /**
         * Calls saveParticipatedPoll in store/participat, to change participated Boolean.
         * Is called when clicked on "Absenden".
         */
        saveParticipatedPoll() {
            const userObj = {
                pollTaker: this.getUsername,
                pollId: this.getPoll[1].data.pollId,
            }
            this.$store.dispatch('participant/saveParticipatedPoll', userObj)
            this.$router.push('/AfterSaved')
        },
    },
}
</script>

<style scoped></style>
