<template>
    <v-card min-width="150">
        <!--Umfrage?-->
        <!--Frage-->
        <v-card-text>alle, die bei Frage</v-card-text>
        <v-overflow-btn
            v-model="questionSet"
            class="my-2"
            :items="questionList"
            label="Frage"
            @change="changeQuestionID()"
        ></v-overflow-btn>
        <div v-if="questionSet !== ''">
            <v-checkbox class="my-0 py-0" v-model="inverted" label="nicht"> </v-checkbox>
            <v-card-text class="mt-0 py-0">die Antwort</v-card-text>
            <!--Antwort-->
            <div v-for="answer in answerList" :key="answer">
                <v-checkbox class="my-0 py-0" :label="answer"></v-checkbox>
            </div>
        </div>
        <v-card-text class="my-0 py-0">ausgewählt haben</v-card-text>
    </v-card>
</template>

<script>
export default {
    name: 'QuestionFilterBlock',
    data() {
        return {
            questionList: [],
            answerList: [],
        }
    },
    mounted() {
        console.log('mounted')
        for (let i = 0; i < this.questions.length; i++) {
            this.questionList.push(this.questions[i].fragentext)
        }
        if (this.questionSet !== '') {
            this.changeQuestionID()
        }
    },
    props: {
        blockType: {
            type: String,
        },
        questionSet: {
            type: String,
            default: '',
        },
        inverted: {
            type: Boolean,
            default: false,
        },
        questions: {
            type: [Object],
        },
        checkedAnswers: {
            type: [Boolean],
        },
    },
    methods: {
        changeQuestionID() {
            this.answerList = this.questions[this.questionList.indexOf(this.questionSet)].answers
            for (let i = 0; i < this.answerList; i++) {
                this.checkedAnswers.push(false)
            }
        },
    },
}
</script>

<style scoped></style>
