<template>
    <v-card close @click:close="deleteFromFilterList(filter)">
        Nur Teilnehmer, die in Kategorie

        <v-overflow-btn :items="Categories" v-model="value.selectedCategory" elevation="0" style="box-shadow: none;">
        </v-overflow-btn>

        bei Frage
        <v-overflow-btn :items="Questions" v-model="value.selectedQuestion" elevation="0" style="box-shadow: none;">
        </v-overflow-btn>
        die Antwort
        <v-overflow-btn
            :items="Answers"
            v-model="value.selectedAnswer"
            flat
            elevation="0"
            style="box-shadow: none;"
            @input="updateData"
        >
        </v-overflow-btn>
        ausgewählt haben.
    </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'QAFilter',
    data() {
        return {
            // selectedQuestion: '',
            // selectedAnswer: '',
            // selectedCategory: '',
        }
    },
    props: {
        pollIndex: {
            type: Number,
        },
        filterId: {
            type: Number,
        },
        value: {
            type: Object,
        },
    },

    methods: {
        updateData() {
            console.log('updateData called')
            this.$emit(
                'updateQAFilter',
                this.filterId,
                this.selectedCategory,
                this.selectedQuestion,
                this.selectedAnswer
            )
        },
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),

        categoryIndex() {
            return this.Categories.indexOf(this.selectedCategory)
        },

        questionIndex() {
            return this.Questions.indexOf(this.selectedQuestion)
        },

        pollTitles() {
            const pollTitles = Object.assign([{}], this.polls)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.polls[i].pollName
            }

            return pollTitles
        },

        Categories() {
            const l = []

            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                l[i] = this.polls[this.pollIndex].categoryList[i].categoryName
            }

            return l

            // const questions = this.items[this.pollTitles.indexOf(this.chosenPoll)].categoryList[0].questionList[0].title
        },

        Questions() {
            if (this.selectedCategory === '') {
                return []
            } else {
                const categories = this.polls[this.pollIndex].categoryList

                const l = []
                for (let i = 0; i < categories[this.categoryIndex].questionList.length; i++) {
                    l[i] = categories[this.categoryIndex].questionList[i].questionMessage
                }
                return l
            }

            // const questions = this.items[this.pollTitles.indexOf(this.chosenPoll)].categoryList[0].questionList[0].title
        },

        Answers() {
            if (this.selectedQuestion === '') {
                return []
            } else {
                const categories = this.polls[this.pollIndex].categoryList

                const l = []
                for (let i = 0; i < categories[0].questionList[this.questionIndex].answerPossibilities.length; i++) {
                    l[i] = categories[0].questionList[this.questionIndex].answerPossibilities[i]
                }
                return l
            }
        },
    },
}
</script>

<style scoped></style>
