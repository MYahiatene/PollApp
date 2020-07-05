<template>
    <v-card flat class="pa-2 ma-2">
        <v-switch v-model="invertFilter" label="Invertieren" @change="updateData" />
        <v-overflow-btn
            v-if="!askForCategory()"
            v-model="selectedCategory"
            prefix="Nur Teilnehmer, die in Kategorie"
            :items="categoryTitles"
            elevation="0"
            style="box-shadow: none;"
            @input="updateCategoryIndex()"
        >
        </v-overflow-btn>
        <p v-else>Nur Teilnehmer anzeigen, die in der Kategorie "{{ selectedCategory }}"</p>
        <v-spacer></v-spacer>
        <v-overflow-btn
            v-model="selectedQuestion"
            prefix="bei der Frage"
            :items="questionTitles"
            :no-data-text="
                selectedCategory === ''
                    ? 'Keine Kategorie ausgewählt'
                    : 'Keine nutzbaren Fragen in dieser Kategorie. Bitte wählen Sie eine andere!'
            "
            elevation="0"
            style="box-shadow: none;"
            @input="updateQuestionIndex()"
        >
        </v-overflow-btn>
        <v-select
            v-if="questions[questionIndex] && !(questions[questionIndex].questionType === 'SliderQuestion')"
            v-model="selectedAnswers"
            :prefix="conditional + ' aus den Antworten'"
            :items="answerTitles"
            multiple
            :no-data-text="'Keine Fragen ausgewählt'"
            @input="updateData"
        >
            <template v-slot:selection="{ item, index }">
                <v-chip v-if="index < answerTitlesDisplayedInSelect" close @click:close="deleteChip(index)">
                    <span>{{ item }}</span>
                </v-chip>
                <span v-if="index === answerTitlesDisplayedInSelect" class="grey--text caption"
                    >(oder
                    {{ selectedAnswers.length - answerTitlesDisplayedInSelect }}
                    weitere)</span
                >
            </template>
        </v-select>
        <div v-if="questions[questionIndex] && questions[questionIndex].questionType === 'SliderQuestion'">
            eine Antwort in folgendem Bereich
            <v-range-slider
                v-model="range"
                :min="questions[questionIndex].startValue"
                :max="questions[questionIndex].endValue"
                :step="questions[questionIndex].stepSize"
                :thumb-label="true"
                @change="updateData"
            >
                <template v-slot:prepend>
                    <v-text-field
                        :value="range[0]"
                        class="mt-0 pt-0"
                        hide-details
                        single-line
                        type="number"
                        style="width: 60px;"
                        @input="updateLower"
                    ></v-text-field>
                </template>
                <template v-slot:append>
                    <v-text-field
                        :value="range[1]"
                        class="mt-0 pt-0"
                        hide-details
                        single-line
                        type="number"
                        style="width: 60px;"
                        @input="updateUpper"
                    ></v-text-field>
                </template>
            </v-range-slider>
        </div>

        ausgewählt haben.
    </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'QAFilter',
    props: {
        pollIndex: {
            type: Number,
            default: -1,
        },
        filterId: {
            type: Number,
            default: -1,
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
        initialInvert: {
            type: Boolean,
            default: false,
        },
        initialIsSlider: {
            type: Boolean,
            default: false,
        },
    },
    data() {
        return {
            categoryIndex: -1,
            questionIndex: -1,
            answerIndices: [],
            selectedCategory: '',
            selectedQuestion: '',
            selectedAnswers: [],
            answerTitlesDisplayedInSelect: 5,
            invertFilter: false,
            range: [0, 0],
        }
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),
        conditional() {
            if (this.invertFilter) {
                return 'NICHT'
            } else {
                return ''
            }
        },
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
                for (let i = 0; i < this.questions.length; i++) {
                    if (
                        !(this.questions[i].questionType === 'TextQuestion') &&
                        !(this.questions[i].questionType === 'SortQuestion')
                    ) {
                        l.push(this.questions[i].questionMessage)
                    }
                }
                return l
            }
        },
        questions() {
            if (this.categoryIndex === -1) {
                return []
            } else {
                const c = []
                for (let i = 0; i < this.categories[this.categoryIndex].questionList.length; i++) {
                    if (
                        this.categories[this.categoryIndex].questionList[i].questionType !== 'TextQuestion' &&
                        this.categories[this.categoryIndex].questionList[i].questionType !== 'SortQuestion'
                    ) {
                        c.push(this.categories[this.categoryIndex].questionList[i])
                    }
                }
                console.log('questions: ')
                console.log(c)
                return c
            }
        },
        answerTitles() {
            if (this.questionIndex === -1) {
                return []
            } else {
                const l = []
                if (this.questions[this.questionIndex].questionType === 'RangeQuestion') {
                    l.push(this.questions[this.questionIndex].belowMessage)
                    for (
                        let i = this.questions[this.questionIndex].startValue;
                        i < this.questions[this.questionIndex].endValue;
                        i += this.questions[this.questionIndex].stepSize
                    ) {
                        l.push('' + i + ' - ' + (i + this.questions[this.questionIndex].stepSize))
                    }
                    l.push(this.questions[this.questionIndex].aboveMessage)
                } else {
                    for (let i = 0; i < this.questions[this.questionIndex].answerPossibilities.length; i++) {
                        l[i] = this.questions[this.questionIndex].answerPossibilities[i]
                    }
                }
                return l
            }
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
        this.invertFilter = this.initialInvert

        if (this.initialIsSlider) {
            this.range[0] = this.initialAnswerIndices[0]
            this.range[1] = this.initialAnswerIndices[1]
        }
        this.$forceUpdate()
    },
    methods: {
        updateCategoryIndex() {
            this.categoryIndex = this.categoryTitles.indexOf(this.selectedCategory)
        },
        updateQuestionIndex() {
            this.questionIndex = this.questionTitles.indexOf(this.selectedQuestion)
        },
        updateAnswerIndices() {
            this.answerIndices = []
            for (let i = 0; i < this.selectedAnswers.length; i++) {
                this.answerIndices.push(this.answerTitles.indexOf(this.selectedAnswers[i]))
            }
        },
        updateLower(value) {
            this.$set(this.range, 0, value)
            this.updateData()
        },
        updateUpper(value) {
            this.$set(this.range, 1, value)
            this.updateData()
        },
        updateData() {
            console.log('updateData')
            this.updateAnswerIndices()
            console.log(this.filterId)
            console.log(this.categoryIndex)
            console.log(this.questionIndex)
            console.log(this.answerIndices)
            if (this.range[0] > this.range[1]) {
                const tmp = this.range[1]
                this.range[0] = Number(this.range[1])
                this.range[1] = Number(tmp)
            }
            if (this.questionIndex !== -1) {
                this.$emit('updateData', [
                    this.filterId,
                    this.categoryIndex,
                    this.questionIndex,
                    this.questions[this.questionIndex].questionId,
                    this.questions[this.questionIndex].questionType === 'SliderQuestion'
                        ? this.range
                        : this.answerIndices,
                    this.questions[this.questionIndex].questionType === 'SliderQuestion',
                    this.invertFilter,
                ])
            }
        },
        askForCategory() {
            const OnlyOneCategory = this.categories.length === 1
            if (OnlyOneCategory) {
                this.selectedCategory = this.categoryTitles[0]
                this.updateCategoryIndex()
            }
            return OnlyOneCategory
        },
        deleteChip(index) {
            this.selectedAnswers.splice(index, 1)
            this.updateAnswerIndices()
            this.updateData()
        },
    },
}
</script>

<style scoped></style>
