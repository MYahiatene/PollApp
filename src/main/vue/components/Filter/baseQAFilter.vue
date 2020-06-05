<template>
    <v-card flat class="pa-2 ma-2">
        <v-overflow-btn
            v-if="!askForCategory()"
            prefix="Nur Teilnehmer, die in Kategorie"
            :items="categoryTitles"
            v-model="selectedCategory"
            @input="updateCategoryIndex()"
            elevation="0"
            style="box-shadow: none;"
        >
        </v-overflow-btn>
        <p v-else>Nur Teilnehmer, die in der Kategorie "{{ selectedCategory }}"</p>
        <v-spacer></v-spacer>
        <v-overflow-btn
            prefix="bei der Frage"
            :items="questionTitles"
            v-model="selectedQuestion"
            :no-data-text="'Keine Kategorie ausgewählt'"
            @input="updateQuestionIndex()"
            elevation="0"
            style="box-shadow: none;"
        >
        </v-overflow-btn>
        <v-select
            prefix="eine der folgenden Antwort(en)"
            :items="answerTitles"
            v-model="selectedAnswers"
            multiple
            :no-data-text="'Keine Fragen ausgewählt'"
            @input="updateData"
        >
            <template v-slot:selection="{ item, index }">
                <v-chip
                    close
                    @click:close="selectedAnswers.splice(index, 1)"
                    v-if="index < answerTitlesDisplayedInSelect"
                >
                    <span>{{ item }}</span>
                </v-chip>
                <span v-if="index === answerTitlesDisplayedInSelect" class="grey--text caption"
                    >(oder
                    {{ selectedAnswers.length - answerTitlesDisplayedInSelect }}
                    weitere)</span
                >
            </template>
        </v-select>
        ausgewählt haben.
    </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'QAFilter',
    data() {
        return {
            categoryIndex: -1,
            questionIndex: -1,
            answerIndices: [],
            selectedCategory: '',
            selectedQuestion: '',
            selectedAnswers: [],
            answerTitlesDisplayedInSelect: 5,
        }
    },
    props: {
        pollIndex: {
            type: Number,
        },
        filterId: {
            type: Number,
        },
        initialCategoryIndex: {
            default: -1,
            type: Number,
        },
        initialQuestionIndex: {
            default: -1,
            type: Number,
        },
        initialAnswerIndices: {
            default: () => [],
            type: Array,
        },
    },
    mounted() {
        this.categoryIndex = this.initialCategoryIndex
        this.questionIndex = this.initialQuestionIndex
        this.answerIndices = this.initialAnswerIndices
        if (this.categoryIndex === -1) {
            this.selectedCategory = ''
        }
        this.selectedCategory = this.categoryTitles[this.categoryIndex]

        if (this.questionIndex === -1) {
            this.selectedQuestion = ''
        }
        this.selectedQuestion = this.questionTitles[this.questionIndex]

        for (let i = 0; i < this.answerIndices.length; i++) {
            this.selectedAnswers.push(this.answerTitles[this.answerIndices[i]])
        }
    },
    methods: {
        updateCategoryIndex() {
            this.categoryIndex = this.categoryTitles.indexOf(this.selectedCategory)
        },

        updateQuestionIndex() {
            this.questionIndex = this.questionTitles.indexOf(this.selectedQuestion)
        },
        updateAnswerIndices() {
            for (let i = 0; i < this.selectedAnswers.length; i++) {
                this.answerIndices[i] = this.answerTitles.indexOf(this.selectedAnswers[i])
            }
        },

        updateData() {
            console.log('updateData')
            this.updateAnswerIndices()
            console.log(this.filterId)
            console.log(this.categoryIndex)
            console.log(this.questionIndex)
            console.log(this.answerIndices)
            this.$emit('updateData', [this.filterId, this.categoryIndex, this.questionIndex, this.answerIndices])
        },

        askForCategory() {
            const OnlyOneCategory = this.categories.length === 1
            if (OnlyOneCategory) {
                this.selectedCategory = this.categoryTitles[0]
                this.updateCategoryIndex()
            }
            return OnlyOneCategory
        },
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),

        pollTitles() {
            const pollTitles = Object.assign([{}], this.polls)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.polls[i].pollName
            }
            return pollTitles
        },
        categoryTitles() {
            const l = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                l[i] = this.polls[this.pollIndex].categoryList[i].categoryName
            }
            return l
        },

        categories() {
            return this.polls[this.pollIndex].categoryList
        },

        questionTitles() {
            if (this.categoryIndex === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.categories[this.categoryIndex].questionList.length; i++) {
                    l[i] = this.categories[this.categoryIndex].questionList[i].questionMessage
                }
                return l
            }
        },

        questions() {
            if (this.categoryIndex === -1) {
                return []
            } else {
                return this.categories[this.categoryIndex].questionList
            }
        },

        answerTitles() {
            console.log('heyho')
            if (this.questionIndex === -1) {
                console.log('fnkdjs')
                return []
            } else {
                const l = []
                for (let i = 0; i < this.questions[this.questionIndex].answerPossibilities.length; i++) {
                    l[i] = this.questions[this.questionIndex].answerPossibilities[i]
                }
                console.log(l)
                return l
            }
        },
    },
}
</script>

<style scoped></style>
