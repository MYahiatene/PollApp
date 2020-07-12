<template>
    <!--    This is a widget that is part of filterForm. It provides a UI that users can use to specify Question Answer Filter.
They can pick a specific question (by deciding on a category first) and specify one or multiple answers
causing the EvaluationWidgets to only display participants who ansered in that specific way.
This selection can also be inverted so that only participants who di not answer in that way are displayed-->
    <v-card flat class="pa-2 ma-2">
        <!--        switch for inverting-->
        <v-switch v-model="invertFilter" label="Invertieren" @change="updateData" />
        <!--        dropdown to pick category-->
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
        <!--        drop down to pick question-->
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

        <!--        drop down to pick answers-->

        <v-select
            v-if="questions[questionIndex] && !(questions[questionIndex].questionType === 'SliderQuestion')"
            v-model="selectedAnswers"
            :prefix="conditional + ' aus den Antworten'"
            :items="answerTitles"
            multiple
            :no-data-text="'Keine Fragen ausgewählt'"
            @input="updateData"
        >
            <!--            there is a limit to how many answers are displayed-->
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
        <!--        if we have a sliderQuestion instead of a dropdown a slider displayed to specify the range-->

        <div v-if="questions[questionIndex] && questions[questionIndex].questionType === 'SliderQuestion'">
            {{ sliderConditional }} Antwort in folgendem Bereich
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
            // data of the filter that is send to its parent (filterForm)
            categoryIndex: -1,
            questionIndex: -1,
            answerIndices: [],
            selectedCategory: '',
            selectedQuestion: '',
            selectedAnswers: [],
            invertFilter: false,
            range: [0, 0],
            // indicates how many answerTitles are displayed at once
            answerTitlesDisplayedInSelect: 5,
        }
    },

    /**
     * In mounted all the props that are passed by the parent (filterForm) are copied into the v-models
     */

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
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),

        /**
         * makes a String that indicates wether the filter is inverted
         * @returns {string}
         */
        conditional() {
            if (this.invertFilter) {
                return 'NICHT'
            } else {
                return ''
            }
        },

        /**
         * makes a String that indicates wether a filter with a slide question is inverted
         * @returns {string}
         */

        sliderConditional() {
            if (this.invertFilter) {
                return 'KEINE'
            } else {
                return 'eine'
            }
        },

        /**
         * computes a list of all the categories
         * @returns {[]|default.methods.poll.categoryList}
         */

        categories() {
            return this.polls[this.pollIndex].categoryList
        },

        /**
         * computes a list of all the categoryTitles in the poll
         * @returns {[]}
         */

        categoryTitles() {
            const l = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                l[i] = this.polls[this.pollIndex].categoryList[i].categoryName
            }
            return l
        },

        /**
         * Computes a list of all the questions in the chosen category
         * @returns {[]|*[]}
         */
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

        /**
         * computes a list of all the questionTitles in the chosen category
         * @returns {[]|*[]}
         */

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

        /**
         * computes a list of all the answerTitles of the chosen question
         * @returns {[]|*[]}
         */

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

    methods: {
        /**
         * computes the index of the Category that was picked in the CategoryPicker and stores it in categoryIndex
         */
        updateCategoryIndex() {
            this.categoryIndex = this.categoryTitles.indexOf(this.selectedCategory)
        },

        /**
         * computes the index of the question that was picked in the questionPicker and stores it in questionIndex
         */
        updateQuestionIndex() {
            this.questionIndex = this.questionTitles.indexOf(this.selectedQuestion)
        },

        /**
         * computes the indices of the answers that were picked in the answerPicker and stores it in answerIndices
         */

        updateAnswerIndices() {
            this.answerIndices = []
            for (let i = 0; i < this.selectedAnswers.length; i++) {
                this.answerIndices.push(this.answerTitles.indexOf(this.selectedAnswers[i]))
            }
        },

        /**
         * Updates the lower value of a slider (when a slider question is picked)
         * @param value
         */
        updateLower(value) {
            this.$set(this.range, 0, value)
            this.updateData()
        },

        /**
         * updates the upper value of a slider (when a slider question is picked)
         * @param value
         */
        updateUpper(value) {
            this.$set(this.range, 1, value)
            this.updateData()
        },

        /**
         * emits a message to the parent sending the values to filterForm
         * it needs to distinguish if it has to send range (when there was a slider question ) or answerIndices (for choice questions)
         */
        updateData() {
            this.updateAnswerIndices()

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

        /**
         * This method queries if there is more than one category or not
         * if there is only one category it automatically sets the chosen category to the first and only one available
         *
         * @returns {boolean} true if there was only one category
         */
        askForCategory() {
            const OnlyOneCategory = this.categories.length === 1
            if (OnlyOneCategory) {
                this.selectedCategory = this.categoryTitles[0]
                this.updateCategoryIndex()
            }
            return OnlyOneCategory
        },
        /**
         * deletes an answerPossibility from selected answers if the user doesn't want it
         * @param index of the answerPossibility to be deleted
         */
        deleteChip(index) {
            this.selectedAnswers.splice(index, 1)
            this.updateAnswerIndices()
            this.updateData()
        },
    },
}
</script>

<style scoped></style>
