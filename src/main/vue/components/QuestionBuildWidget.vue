<!--this widget enables the user to configure a general question, including all of its common features, to specify-->
<!--the final question type and to preview (wysiwyg) the final question as it will be displayed to the participants-->
<template>
    <div v-if="buildIndex > 0">
        <div>
            <v-card flat class="ma-0">
                <v-container>
                    <v-row no-gutters>
                        <v-col>
                            <h3>{{ header }}</h3>
                        </v-col>
                    </v-row>
                    <v-row no-gutters>
                        <v-textarea
                            v-model="questionMessageModel"
                            label="Fragentext"
                            hint="Was soll den Umfrageteilnehmer gefragt werden?"
                            rows="1"
                        >
                        </v-textarea>
                    </v-row>
                    <v-row no-gutters>
                        <v-select v-model="questionType" :items="questionWidgets" label="Fragenart"></v-select>
                    </v-row>
                    <v-row>
                        <component
                            :questionData="questionData"
                            :is="questionType"
                            :categoryData="categoryData"
                            :buildIndex:="buildIndex"
                        ></component>
                    </v-row>
                    <v-row>
                        <v-col cols="9"></v-col>
                        <v-col cols="2">
                            <v-btn :disabled="questionMessageBool" @click="createQuestion">
                                <v-icon color="primary" left>
                                    mdi-plus
                                </v-icon>
                                Speichern
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-container>
            </v-card>
        </div>
    </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import ChoiceQuestion from './ChoiceQuestion'
import TextQuestion from './TextQuestion'
import RangeQuestion from './RangeQuestion'
import SortQuestion from './SortQuestion'
import SliderQuestion from './SliderQuestion'

export default {
    name: 'QuestionBuildWidget',
    components: { ChoiceQuestion, TextQuestion, SortQuestion, RangeQuestion, SliderQuestion },
    props: {
        categoryData: { type: Array },
    },
    data() {
        return {
            questionData: { questionTypeChoice: this.questionTypeChoice },
            questionWidgets: [
                {
                    text: 'Auswahlfrage',
                    value: 'ChoiceQuestion',
                },
                {
                    text: 'Freitextfrage',
                    value: 'TextQuestion',
                },
                {
                    text: 'Sortierfrage',
                    value: 'SortQuestion',
                },
                {
                    text: 'Sliderfrage',
                    value: 'SliderQuestion',
                },
                {
                    text: 'Reichweitenfrage',
                    value: 'RangeQuestion',
                },
            ],
        }
    },
    computed: {
        ...mapGetters({
            getQuestion: 'questionOverview/getQuestion',
            questionToLoad: 'pollOverview/questionToLoad',
            getQuestionMessage: 'questionOverview/getQuestionMessage',
            getBuildIndex: 'questionOverview/getBuildIndex',
            getSaveButtonStatus: 'questionOverview/getSaveButtonStatus',
        }),
        questionMessageBool: {
            get() {
                if (this.questionType === 'TextQuestion' || this.questionType === 'RangeQuestion') {
                    return !(this.questionType.length && this.questionMessageModel.length && this.getSaveButtonStatus)
                } else {
                    return !(
                        this.questionType.length &&
                        this.questionMessageModel.length &&
                        this.getQuestion.answerPossibilities.length &&
                        this.getSaveButtonStatus
                    )
                }
            },
        },
        buildIndex: {
            get() {
                return this.getBuildIndex
            },
            set(value) {
                this.setBuildIndex(value)
            },
        },
        header() {
            return this.buildIndex === 1 ? 'Frage erstellen' : 'Frage editieren'
        },
        questionMessageModel: {
            get() {
                return this.getQuestionMessage
            },
            set(value) {
                this.setQM(value)
            },
        },
        questionType: {
            get() {
                return this.getQuestion.questionType
            },
            set(value) {
                this.setQuestionType(value)
            },
        },
    },
    methods: {
        createQuestion() {
            if (this.buildIndex === 1) {
                this.$axios
                    .post('/addquestion', {
                        pollId: this.$route.params.QuestionOverview,
                        questionType: this.getQuestion.questionType,
                        questionMessage: this.getQuestion.questionMessage,
                        answerPossibilities: this.getQuestion.answerPossibilities,
                        numberOfPossibleAnswers: this.getQuestion.numberOfPossibleAnswers,
                        userAnswers: this.getQuestion.userAnswers,
                        textMultiline: this.getQuestion.textMultiline,
                        textMinimum: this.getQuestion.textMinimum,
                        textMinBool: this.getQuestion.textMinBool,
                        textMaximum: this.getQuestion.textMaximum,
                        textMaxBool: this.getQuestion.textMaxBool,
                        endValue: this.getQuestion.endValue,
                        startValue: this.getQuestion.startValue,
                        stepSize: this.getQuestion.stepSize,
                        belowMessage: this.getQuestion.belowMessage,
                        aboveMessage: this.getQuestion.aboveMessage,
                        hideValues: this.getQuestion.hideValues,
                        dropDown: this.getQuestion.dropDown,
                    })
                    .then((response) => {
                        this.categoryData[0].questionList.push(response.data)
                    })
            } else {
                this.$axios.put('/editquestion', {
                    pollId: this.$route.params.QuestionOverview,
                    questionType: this.getQuestion.questionType,
                    questionMessage: this.getQuestion.questionMessage,
                    answerPossibilities: this.getQuestion.answerPossibilities,
                    numberOfPossibleAnswers: this.getQuestion.numberOfPossibleAnswers,
                    userAnswers: this.getQuestion.userAnswers,
                    questionId: this.getQuestion.questionId,
                    textMultiline: this.getQuestion.textMultiline,
                    textMinimum: this.getQuestion.textMinimum,
                    textMinBool: this.getQuestion.textMinBool,
                    textMaximum: this.getQuestion.textMaximum,
                    textMaxBool: this.getQuestion.textMaxBool,
                    endValue: this.getQuestion.endValue,
                    startValue: this.getQuestion.startValue,
                    stepSize: this.getQuestion.stepSize,
                    belowMessage: this.getQuestion.belowMessage,
                    aboveMessage: this.getQuestion.aboveMessage,
                    hideValues: this.getQuestion.hideValues,
                    dropDown: this.getQuestion.dropDown,
                })
            }
            this.setBuildIndex(0)
        },
        ...mapMutations({
            setSaveButtonStatus: 'questionOverview/setSaveButtonStatus',
            setQuestionMessage: 'pollOverview/setQuestionMessage',
            setQuestionType: 'questionOverview/setQuestionType',
            setBuildIndex: 'questionOverview/setBuildIndex',
            setQuestion: 'questionOverview/setQuestion',
        }),
        ...mapActions({
            setQM: 'questionOverview/setQuestionMessage',
            deleteQuestion: 'pollOverview/deleteQuestion',
        }),
    },
}
</script>

<style scoped></style>
