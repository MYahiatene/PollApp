<!--this widget enables the user to configure a general question, including all of its common features, to specify-->
<!--the final question type and to preview (wysiwyg) the final question as it will be displayed to the participants-->
<template>
    <div v-if="questionToLoad !== 0">
        <v-card>
            <v-container>
                <v-row no-gutters>
                    <v-col>
                        <h3>Frage erstellen</h3>
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
                    <v-overflow-btn
                        v-model="questionTypeChoice"
                        :items="questionWidgets"
                        label="Fragenart"
                    ></v-overflow-btn>
                </v-row>
                <v-row>
                    <component :is="questionTypeChoice"></component>
                </v-row>
                <v-row no-gutters>
                    <v-btn color="primary" class="mr-4" @click="sendQuestion()">Frage speichern</v-btn>
                </v-row>
            </v-container>
        </v-card>
    </div>
</template>

<script>
import { mapMutations, mapGetters } from 'vuex'
import ChoiceQuestion from './ChoiceQuestion'
import TextQuestion from './textQuestion'
import RangeQuestion from './rangeQuestion'

export default {
    name: 'QuestionBuildWidget',
    components: { ChoiceQuestion, TextQuestion, RangeQuestion },
    data() {
        return {
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
        ...mapGetters({ question: 'pollOverview/getQuestion', questionToLoad: 'pollOverview/questionToLoad' }),
        questionMessage: {
            get() {
                return this.question.questionMessage
            },
            set(questionMessage) {
                this.setQuestionMessage(questionMessage)
            },
        },
        questionTypeChoice: {
            get() {
                switch (this.question.questionType) {
                    case 'ChoiceQuestion':
                        return 'ChoiceQuestion'
                    case 'TextQuestion':
                        return 'TextQuestion'
                    case 'RangeQuestion':
                        return 'RangeQuestion'
                    default:
                        return ''
                }
            },
            set(questionType) {
                this.setQuestionType(questionType)
            },
        },
    },
    methods: {
        ...mapMutations({
            setQuestionMessage: 'pollOverview/setQuestionMessage',
            setQuestionType: 'pollOverview/setQuestionType',
            sendQuestion: 'pollOverview/sendQuestion',
        }),
    },
}
</script>

<style scoped></style>
