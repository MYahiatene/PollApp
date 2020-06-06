<template>
    <v-card class="ma-1 pa-5">
        <h2>Konsistenzfragen von Umfrage: "{{ polls[0].pollName }}" {{ currentId }}</h2>
        <v-card class="pa-2">
            <v-overflow-btn
                v-if="!askForCategory()"
                prefix="Kategorie: "
                :items="categoryTitles"
                v-model="selectedCategory"
                @input="updateCategoryIndex()"
                elevation="0"
                style="box-shadow: none;"
            >
            </v-overflow-btn>
            <p v-else>Kategorie: "{{ selectedCategory }}"</p>
            <v-spacer></v-spacer>
            <v-overflow-btn
                prefix="Frage: "
                dense
                :items="questionTitles"
                v-model="selectedQuestion"
                :no-data-text="'Keine Kategorie ausgewählt'"
                @input="updateQuestionIndex()"
                elevation="0"
                style="box-shadow: none;"
            >
            </v-overflow-btn>
            <v-select
                prefix="Mögliche Antwort(en): "
                :items="answerTitles"
                v-model="selectedAnswers"
                multiple
                :no-data-text="'Keine Fragen ausgewählt'"
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
        </v-card>

        Wird/Werden verknüpft mit:

        <v-card class="pa-2">
            <v-overflow-btn
                v-if="!askForCategory()"
                prefix="Kategorie: "
                :items="categoryTitles"
                v-model="selectedCategory2"
                @input="updateCategoryIndex2()"
                elevation="0"
                style="box-shadow: none;"
            >
            </v-overflow-btn>
            <p v-else>Kategorie: "{{ selectedCategory }}"</p>
            <v-spacer></v-spacer>
            <v-overflow-btn
                prefix="Frage: "
                dense
                :items="questionTitles2"
                v-model="selectedQuestion2"
                :no-data-text="'Keine Kategorie ausgewählt'"
                @input="updateQuestionIndex2()"
                elevation="0"
                style="box-shadow: none;"
            >
            </v-overflow-btn>
            <v-select
                prefix="Mögliche Antwort(en): "
                :items="answerTitles2"
                v-model="selectedAnswers2"
                multiple
                :no-data-text="'Keine Fragen ausgewählt'"
            >
                <template v-slot:selection="{ item, index }">
                    <v-chip
                        close
                        @click:close="selectedAnswers2.splice(index, 1)"
                        v-if="index < answerTitlesDisplayedInSelect"
                    >
                        <span>{{ item }}</span>
                    </v-chip>
                    <span v-if="index === answerTitlesDisplayedInSelect" class="grey--text caption"
                        >(oder
                        {{ selectedAnswers2.length - answerTitlesDisplayedInSelect }}
                        weitere)</span
                    >
                </template>
            </v-select>
        </v-card>

        <v-card-actions>
            <v-btn @click="addControlQuestion" class="mr-3">
                Speichern
            </v-btn>
            {{ buttonInfo }}
        </v-card-actions>

        <v-card class="ma-1 pa-1">
            <h3>
                Bereits erstellte Konsistenzfragen:
            </h3>

            <div v-for="(cq, index) in listOfControlQuestions" :key="index">
                <v-chip @click="openCq(index)">
                    {{ index }}
                </v-chip>
            </div>
        </v-card>
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
            categoryIndex2: -1,
            questionIndex2: -1,
            answerIndices2: [],
            selectedCategory: '',
            selectedCategory2: '',
            selectedQuestion: '',
            selectedQuestion2: '',
            selectedAnswers: [],
            selectedAnswers2: [],
            answerTitlesDisplayedInSelect: 5,
            listOfControlQuestions: [],
            buttonInfo: '',
            currentId: -1,
        }
    },
    props: {
        pollIndex: {
            type: Number,
            default: 0,
        },
        filterId: {
            type: Number,
            default: 0,
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

        this.computeWordsFromIndices()

        // if (this.categoryIndex === -1) {
        //     this.selectedCategory = ''
        // }
        // this.selectedCategory = this.categoryTitles[this.categoryIndex]
        //
        // if (this.questionIndex === -1) {
        //     this.selectedQuestion = ''
        // }
        // this.selectedQuestion = this.questionTitles[this.questionIndex]
        //
        // for (let i = 0; i < this.answerIndices.length; i++) {
        //     this.selectedAnswers.push(this.answerTitles[this.answerIndices[i]])
        // }
    },
    methods: {
        updateCategoryIndex() {
            this.categoryIndex = this.categoryTitles.indexOf(this.selectedCategory)
        },

        updateCategoryIndex2() {
            this.categoryIndex2 = this.categoryTitles.indexOf(this.selectedCategory2)
        },

        updateQuestionIndex() {
            this.questionIndex = this.questionTitles.indexOf(this.selectedQuestion)
        },

        updateQuestionIndex2() {
            this.questionIndex2 = this.questionTitles2.indexOf(this.selectedQuestion2)
        },

        updateAnswerIndices() {
            for (let i = 0; i < this.selectedAnswers.length; i++) {
                this.answerIndices[i] = this.answerTitles.indexOf(this.selectedAnswers[i])
            }
        },

        askForCategory() {
            const OnlyOneCategory = this.categories.length === 1
            if (OnlyOneCategory) {
                this.selectedCategory = this.categoryTitles[0]
                this.selectedCategory2 = this.categoryTitles[0]
                this.updateCategoryIndex()
                this.updateCategoryIndex2()
            }
            return OnlyOneCategory
        },

        addControlQuestion() {
            if (
                this.categoryIndex === -1 ||
                this.questionIndex === -1 ||
                this.answerIndices === [] ||
                this.categoryIndex2 === -1 ||
                this.questionIndex2 === -1 ||
                this.answerIndices2 === []
            ) {
                this.buttonInfo = 'Bitte fülle erst alle Felder aus!'
            } else {
                if (this.currentId === -1) {
                    this.listOfControlQuestions.push({
                        c1: this.categoryIndex,
                        q1: this.questionIndex,
                        a1: this.answerIndices,
                        c2: this.categoryIndex2,
                        q2: this.questionIndex2,
                        a2: this.answerIndices2,
                    })
                } else {
                    this.listOfControlQuestions[this.currentId] = {
                        c1: this.categoryIndex,
                        q1: this.questionIndex,
                        a1: this.answerIndices,
                        c2: this.categoryIndex2,
                        q2: this.questionIndex2,
                        a2: this.answerIndices2,
                    }
                }

                this.buttonInfo = ''
                this.currentId = -1
                this.categoryIndex = -1
                this.questionIndex = -1
                this.answerIndices = []
                this.categoryIndex2 = -1
                this.questionIndex2 = -1
                this.answerIndices2 = []
                this.computeWordsFromIndices()
            }
        },

        openCq(index) {
            this.currentId = index
            this.categoryIndex = this.listOfControlQuestions[index].c1
            this.questionIndex = this.listOfControlQuestions[index].q1
            this.answerIndices = this.listOfControlQuestions[index].a1
            this.categoryIndex2 = this.listOfControlQuestions[index].c2
            this.questionIndex2 = this.listOfControlQuestions[index].q2
            this.answerIndices2 = this.listOfControlQuestions[index].a2

            this.computeWordsFromIndices()
        },

        computeWordsFromIndices() {
            this.selectedCategory = this.categoryTitles[this.categoryIndex]
            this.selectedQuestion = this.questionTitles[this.questionIndex]
            for (let i = 0; i < this.answerIndices; i++) {
                this.selectedAnswers[i] = this.answerTitles[this.answerIndices[i]]
            }

            this.selectedCategory2 = this.categoryTitles[this.categoryIndex2]
            this.selectedQuestion2 = this.questionTitles2[this.questionIndex2]
            for (let i = 0; i < this.answerIndices2; i++) {
                this.selectedAnswers2[i] = this.answerTitles2[this.answerIndices2[i]]
            }

            // this.$forceUpdate()
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

        questionTitles2() {
            if (this.categoryIndex2 === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.categories[this.categoryIndex2].questionList.length; i++) {
                    l[i] = this.categories[this.categoryIndex2].questionList[i].questionMessage
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

        questions2() {
            if (this.categoryIndex2 === -1) {
                return []
            } else {
                return this.categories[this.categoryIndex2].questionList
            }
        },

        answerTitles() {
            if (this.questionIndex === -1) {
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

        answerTitles2() {
            if (this.questionIndex2 === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.questions2[this.questionIndex2].answerPossibilities.length; i++) {
                    l[i] = this.questions2[this.questionIndex2].answerPossibilities[i]
                }
                console.log(l)
                return l
            }
        },
    },
}
</script>

<style scoped></style>
