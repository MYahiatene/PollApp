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
                        <v-spacer></v-spacer>
                        <v-spacer></v-spacer>
                        <v-col>
                            <v-btn @click="createQuestion">
                                <v-icon color="primary" left>
                                    mdi-plus
                                </v-icon>
                                Speichern
                            </v-btn>
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
                        <v-select v-model="questionTypeChoice" :items="questionWidgets" label="Fragenart"></v-select>
                    </v-row>
                    <v-row>
                        <component
                            :questionData="questionData"
                            :pollData="pollData"
                            :is="questionTypeChoice"
                            :buildIndex:="buildIndex"
                        ></component>
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
            questionTypeChoice: '',
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
            question: 'pollOverview/getQuestion',
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
    },
    methods: {
        createQuestion(NewQuestionMessage, NewQuestionType) {
            this.buildIndex = false
            const question = {
                pollId: '1',
                questionType: 'freitext',
            }
            this.$axios.post('/addquestion', question).then((response) => {
                question.questionId = response.data.questionId
            })
        },
        ...mapMutations({
            setQuestionMessage: 'pollOverview/setQuestionMessage',
            setQuestionType: 'pollOverview/setQuestionType',
        }),
        ...mapActions({
            setQM: 'questionOverview/setQuestionMessage',
            deleteQuestion: 'pollOverview/deleteQuestion',
        }),
    },
}
</script>

<style scoped></style>
