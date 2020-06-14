<!--this widget enables the user to configure a general question, including all of its common features, to specify-->
<!--the final question type and to preview (wysiwyg) the final question as it will be displayed to the participants-->
<template>
    <div v-if="editIndex === true">
        <div>
            <v-card flat class="ma-0">
                <v-container>
                    <v-row no-gutters>
                        <v-col>
                            <h3>Frage bearbeiten</h3>
                        </v-col>
                        <v-spacer></v-spacer>
                        <v-spacer></v-spacer>
                        <v-col>
                            <v-btn @click="deleteQuestion">
                                <v-icon color="primary" left>
                                    mdi-delete
                                </v-icon>
                                Löschen
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
                        <v-overflow-btn
                            v-model="questionTypeChoice"
                            :items="questionWidgets"
                            label="Fragenart"
                        ></v-overflow-btn>
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
import TextQuestion from './EditTextQuestion'
import RangeQuestion from './rangeQuestion'

export default {
    name: 'QuestionBuildWidget',
    components: { ChoiceQuestion, TextQuestion, RangeQuestion },
    props: {
        editIndex: {
            type: Boolean,
        },
    },
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
        ...mapGetters({
            question: 'pollOverview/getQuestion',
            questionToLoad: 'pollOverview/questionToLoad',
        }),
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
        }),
        ...mapActions({
            deleteQuestion: 'pollOverview/deleteQuestion',
        }),
    },
}
</script>

<style scoped></style>
