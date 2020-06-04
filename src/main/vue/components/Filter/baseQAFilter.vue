<template>
    <v-card flat class="pa-2 ma-2">
        <v-overflow-btn
            prefix="Nur Teilnehmer, die in Kategorie"
            dense
            :items="categories"
            v-model="selectedCategory"
            elevation="0"
            style="box-shadow: none;"
        >
        </v-overflow-btn>
        <v-overflow-btn
            prefix="bei Frage"
            dense
            :items="questions"
            v-model="selectedQuestion"
            :no-data-text="'Keine Kategorie ausgewählt'"
            elevation="0"
            style="box-shadow: none;"
        >
        </v-overflow-btn>
        <v-overflow-btn
            prefix="die Antwort"
            :items="answers"
            dense
            v-model="selectedAnswer"
            flat
            :no-data-text="'Keine Frage ausgewählt'"
            elevation="0"
            style="box-shadow: none;"
            @input="updateData"
        >
        </v-overflow-btn>
        ausgewählt haben.
    </v-card>
</template>

<script>
// TODO: Bug! Füge zwei Filter hinzu fülle einen aus und verschiebe einen. Die Daten werden dubliziert.
import { mapGetters } from 'vuex'
export default {
    name: 'QAFilter',
    data() {
        return {}
    },
    props: {
        pollIndex: {
            type: Number,
        },
        filterId: {
            type: Number,
        },
        categoryIndex: {
            default: -1,
            type: Number,
        },
        questionIndex: {
            default: -1,
            type: Number,
        },
        answerIndex: {
            default: -1,
            type: Number,
        },
    },
    methods: {
        updateData() {
            console.log('updateData')
            this.$emit('updateData', [this.filterId, this.selectedCategory, this.selectedQuestion, this.selectedAnswer])
        },
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),
        selectedCategory() {
            if (this.categoryIndex === -1) {
                return ''
            }
            return this.categories[this.categoryIndex]
        },
        selectedQuestion() {
            if (this.questionIndex === -1) {
                return ''
            }
            return this.questions[this.questionIndex]
        },
        pollTitles() {
            const pollTitles = Object.assign([{}], this.polls)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.polls[i].pollName
            }
            return pollTitles
        },
        categories() {
            console.log('categories')
            const l = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                l[i] = this.polls[this.pollIndex].categoryList[i].categoryName
            }
            return l
        },
        questions() {
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
        },
        answers() {
            console.log('Answers')
            if (this.selectedQuestion === '') {
                return []
            } else {
                const questions = this.polls[this.pollIndex].categoryList[this.categoryIndex].questionList
                const l = []
                for (let i = 0; i < questions[this.questionIndex].answerPossibilities.length; i++) {
                    l[i] = questions[this.questionIndex].answerPossibilities[i]
                }
                return l
            }
        },
    },
}
</script>

<style scoped></style>
