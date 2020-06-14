<!--this widget enables the user to configure a general question, including all of its common features, to specify-->
<!--the final question type and to preview (wysiwyg) the final question as it will be displayed to the participants-->
<template>
    <div v-if="loadedIndex === true">
        <div>
            <v-card flat class="ma-0">
                <v-container>
                    <v-row no-gutters>
                        <v-col>
                            <h3>Frage erstellen</h3>
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
                            v-model="questionMessage"
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
                        <component :is="questionTypeChoice"></component>
                    </v-row>
                </v-container>
            </v-card>
        </div>
    </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import ChoiceQuestion from './ChoiceQuestion'
import TextQuestion from './SavetextQuestion'
import RangeQuestion from './rangeQuestion'

export default {
    name: 'QuestionBuildWidget2',
    components: { ChoiceQuestion, TextQuestion, RangeQuestion },
    props: {
        pollData: { type: Array },
        loadedIndex: {
            type: Boolean,
        },
    },
    data() {
        return {
            questionTypeChoice: '',
            questionMessage: '',
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
        }),
    },
    methods: {
        createQuestion(NewQuestionMessage, NewQuestionType) {
            this.buildIndex = false
            const question = {
                pollId: '1',
                questionMessage: 'testFrage7',
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
            deleteQuestion: 'pollOverview/deleteQuestion',
        }),
    },
}
</script>

<style scoped></style>
