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
                            :pollData="pollData"
                            :is="questionType"
                            :buildIndex:="buildIndex"
                        ></component>
                    </v-row>
                    <v-row>
                        <v-col cols="10"></v-col>
                        <v-col cols="2">
                            <v-btn @click="createQuestion">
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

export default {
    name: 'QuestionBuildWidget2',
    components: { ChoiceQuestion, TextQuestion, RangeQuestion },
    props: {
        pollData: { type: Object },
        buildIndex: {
            type: Number,
        },
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
                    text: 'Intervallfrage',
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
        }),
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
            this.buildIndex = 0
            this.$axios.post('/addquestion', {
                pollId: this.$route.params.QuestionOverview,
                questionType: this.getQuestion.questionType,
                questionMessage: this.getQuestion.questionMessage,
                answerPossibilities: this.getQuestion.answerPossibilities,
                numberOfPossibleAnswers: this.getQuestion.numberOfPossibleAnswers,
                userAnswers: this.getQuestion.userAnswers,
            })
        },
        ...mapMutations({
            setQuestionMessage: 'pollOverview/setQuestionMessage',
            setQuestionType: 'questionOverview/setQuestionType',
        }),
        ...mapActions({
            setQM: 'questionOverview/setQuestionMessage',
            deleteQuestion: 'pollOverview/deleteQuestion',
        }),
    },
}
</script>

<style scoped></style>
