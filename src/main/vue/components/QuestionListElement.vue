<template>
    <!--    Here we need the negative margin in order to cancel out the margin applied by the v-cards-->
    <v-card outlined hover flat :color="color" class="my-n1">
        <v-row no-gutters class="ma-n2 my-n4 pa-4">
            <v-col cols="10">
                <v-list-item-title>
                    {{ question.questionMessage }}
                </v-list-item-title>
                <v-list-item-subtitle>
                    {{ showType(question.questionType) }}
                </v-list-item-subtitle>
            </v-col>
            <v-col cols="1">
                <v-btn icon @click="editQuestion(question)" color="primary">
                    <v-icon>
                        mdi-pencil
                    </v-icon>
                </v-btn>
            </v-col>
            <v-col cols="1">
                <v-btn icon @click="deleteQuestion(question)" color="primary">
                    <v-icon>
                        mdi-delete
                    </v-icon>
                </v-btn>
            </v-col>
        </v-row>
    </v-card>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'

export default {
    name: 'QuestionListElement',
    components: {},
    props: {
        question: { type: Object },
        pollData: { type: Object },
        pollId: {
            type: Number,
        },
        categoryId: {
            type: Number,
        },
        category: { type: Object },
    },
    computed: {
        buildIndex: {
            get() {
                return this.getBuildIndex
            },
            set(value) {
                this.setBuildIndex(value)
            },
            ...mapGetters({
                currentQuestion: 'pollOverview/getQuestion',
                getBuildIndex: 'questionOverview/getBuildIndex',
            }),
            choppedTitle() {
                if (this.question.questionMessage.length > 30) {
                    return this.question.questionMessage.substr(0, 25) + '...?'
                } else {
                    return this.question.questionMessage
                }
            },
            translatedQuestionType() {
                switch (this.question.questionType) {
                    case 'ChoiceQuestion':
                        return 'Auswahlfrage'
                    case 'TextQuestion':
                        return 'Freitextfrage'
                    case 'RangeQuestion':
                        return 'Intervallfrage'
                    default:
                        return ''
                }
            },

            // the color is applied to the question when its the one being edited, so the user can see it more easily

            color() {
                if (this.currentQuestion !== null) {
                    if (
                        this.currentQuestion.questionMessage === this.questionMessage &&
                        this.currentQuestion.categoryId === this.categoryId &&
                        this.currentQuestion.questionId === this.questionId
                    ) {
                        return this.$vuetify.theme.currentTheme.softAccent
                    } else {
                        return ''
                    }
                }

                return ''
            },
        },
    },
    methods: {
        showType(questionType) {
            if (questionType === 'ChoiceQuestion') {
                if (this.question.numberOfPossibleAnswers > 1) {
                    return 'Multiple Choice Question'
                } else {
                    return 'Single Choice Question'
                }
            }
            return questionType
        },
        editQuestion(question) {
            this.setQuestion(question)
            this.buildIndex = 2
        },
        deleteQuestion(question) {
            const index = this.category.questionList.indexOf(question)
            confirm('Sind sie sich sicher, dass sie diese Frage löschen möchten?') &&
                this.category.questionList.splice(index, 1) &&
                this.$axios.put('/removequestion', {
                    categoryId: this.categoryId,
                    questionId: this.question.questionId,
                })
        },

        ...mapMutations({
            setToLoad: 'pollOverview/setToLoad',
            setBuildIndex: 'questionOverview/setBuildIndex',
            setQuestion: 'questionOverview/setQuestion',
        }),
    },
}
</script>

<style scoped></style>
