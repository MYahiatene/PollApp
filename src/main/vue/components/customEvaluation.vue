<template>
    <div>
        <v-dialog overlay-color="background" persistent v-model="dialog" width="500" overlay-opacity="0.95" fullscreen>
            <template v-slot:activator="{ on }">
                <v-btn color="primary" v-on="on">
                    Analyse
                </v-btn>
            </template>
            <v-card>
                <v-container>
                    <v-container v-if="polls.length > 0">
                        <v-layout row justify-space-around>
                            <v-flex md6 lg3>
                                <v-card class="ma-3 pa-3">
                                    <v-card-title>Filter</v-card-title>
                                    <template>
                                        <v-expansion-panels accordion multiple hover>
                                            <v-expansion-panel v-for="(item, index) in kategories" :key="index">
                                                <v-expansion-panel-header :color="item.color">
                                                    {{ item.name }}
                                                </v-expansion-panel-header>
                                                <v-expansion-panel-content :color="item.color">
                                                    <div class="overline">{{ item.explanation }}</div>
                                                    <div v-for="(option, jdex) in item.options" :key="jdex">
                                                        <draggable
                                                            :list="item.options"
                                                            :clone="clone"
                                                            :group="{ name: 'filter', pull: 'clone' }"
                                                        >
                                                            <v-chip
                                                                class="ma-1 filter"
                                                                :color="item.darkColor"
                                                                draggable
                                                                @click="addFilter(option)"
                                                            >
                                                                {{ option.title }}
                                                            </v-chip>
                                                        </draggable>
                                                    </div>
                                                </v-expansion-panel-content>
                                            </v-expansion-panel>
                                        </v-expansion-panels>
                                    </template>
                                </v-card>
                            </v-flex>
                            <v-flex md6 lg9>
                                <v-card class="ma-3 pa-3">
                                    <v-card-title
                                        >Analyse
                                        <v-col align="right">
                                            <v-btn color="primary" @click="saveToStore()">
                                                Anwenden
                                            </v-btn>
                                        </v-col>
                                    </v-card-title>
                                    <v-card class="pa-2">
                                        <v-responsive :aspect-ratio="16 / 5">
                                            <v-overflow-btn
                                                prefix="Basisdaten:"
                                                :items="pollTitles"
                                                dense
                                                v-model="chosenPoll"
                                            >
                                            </v-overflow-btn>
                                            <v-divider></v-divider>
                                            <div>
                                                <v-overflow-btn
                                                    prefix="Ausgewählte Fragen:"
                                                    allow-overflow
                                                    dense
                                                    multiple
                                                    flat
                                                    :items="questionTitles"
                                                    v-model="selectedQuestions"
                                                >
                                                </v-overflow-btn>
                                            </div>
                                            <v-divider></v-divider>
                                            <v-card height="5000" color="#f8faff">
                                                <draggable
                                                    class="dragArea list-group"
                                                    :list="filterList"
                                                    group="filter"
                                                >
                                                    <div v-for="(filter, index) in filterList" :key="index">
                                                        <v-chip
                                                            :color="kategories[2].color"
                                                            v-model="consistencyChip"
                                                            v-if="filter.filterType === 'consistency'"
                                                            close
                                                            @click:close="deleteFromFilterList(filter)"
                                                        >
                                                            Es werden nur Teilnehmer angezeigt, die alle
                                                            Konsistenzfragen bestanden haben.
                                                        </v-chip>

                                                        <v-chip
                                                            :color="kategories[3].color"
                                                            close
                                                            v-if="filter.filterType === 'age'"
                                                            @click:close="deleteFromFilterList(filter)"
                                                        >
                                                            Nur Teilnehmer mit Alter
                                                            <filter-chip-menu
                                                                :color="kategories[3].color"
                                                                :items="['<', '>', '=']"
                                                                @choice-updated="
                                                                    (update) => {
                                                                        selectedAgeOperation = update
                                                                    }
                                                                "
                                                            >
                                                            </filter-chip-menu>
                                                            <v-text-field
                                                                type="number"
                                                                min="0"
                                                                oninput="validity.valid||(value='')"
                                                            >
                                                            </v-text-field>
                                                        </v-chip>

                                                        <v-chip
                                                            :color="kategories[3].color"
                                                            close
                                                            v-if="filter.filterType === 'gender'"
                                                            @click:close="deleteFromFilterList(filter)"
                                                        >
                                                            Nur Teilnehmer mit dem Geschlecht
                                                            <filter-chip-menu
                                                                :color="kategories[3].color"
                                                                :items="['Männlich', 'Weiblich', 'Divers']"
                                                                @choice-updated="
                                                                    (update) => {
                                                                        selectedGender = update
                                                                    }
                                                                "
                                                            >
                                                            </filter-chip-menu>
                                                        </v-chip>

                                                        <q-a-filter
                                                            v-if="filter.filterType === 'questionAnswer'"
                                                            :poll-index="pollIndex"
                                                            :filter-id="filter.filterId"
                                                            :selected-answer="filter.selectedAnswer"
                                                            :selected-question="filter.selectedQuestion"
                                                            :selected-category="filter.selectedCategory"
                                                            @updateData="updateQAFilter"
                                                        ></q-a-filter>

                                                        <!--                                        <p v-else>-->
                                                        <!--                                            filter.filterType-->
                                                        <!--                                        </p>-->
                                                    </div>
                                                </draggable>
                                            </v-card>
                                        </v-responsive>
                                    </v-card>
                                    <v-card-actions>
                                        <v-btn color="primary">
                                            Anwenden
                                        </v-btn>
                                        <v-btn color="secondary">
                                            Vergleichen
                                        </v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-flex>
                        </v-layout>
                    </v-container>
                    <v-container v-else>
                        <v-card>
                            <v-card-title>Der Server antwortet nicht</v-card-title>
                        </v-card>
                    </v-container>
                </v-container>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
import draggable from 'vuedraggable'
import { mapActions, mapGetters } from 'vuex'
import QAFilter from './Filter/QAFilter'
import FilterChipMenu from './Filter/FilterChipMenu'

export default {
    name: 'customEvaluation',
    components: {
        FilterChipMenu,
        QAFilter,
        draggable,
    },
    data() {
        return {
            dialog: false,
            selectedQuestion: '',
            selectedAnswer: '',
            selectedAgeOperation: '=',
            selectedAge: 50,
            selectedGender: '',
            consistencyChip: true,
            filterList: [],
            globalFilterId: 0,
            selectedQuestions: [],
            kategories: [
                {
                    name: 'Daten',
                    explanation: 'Wähle die Basis-Daten für deine Umfrage',
                    color: '#f5f3ca',
                    darkColor: '#C0BB48',
                    options: [
                        {
                            title: 'Umfrage',
                            filterType: 'surveySelection',
                        },
                    ],
                },
                {
                    name: 'Fragen',
                    explanation: 'Welche Fragen der Umfrage sollen betrachtet werden?',
                    color: '#e5f5ca',
                    darkColor: '#93C244',
                    options: [
                        {
                            title: 'Fragen',
                            filterType: 'questionSelection',
                        },
                    ],
                },
                {
                    name: 'Konsistenzfragen',
                    explanation:
                        'Hier kann man einstellen, ob inkonsistente Beantwortungen herausgefiltert werden sollen',
                    color: '#caf5da',
                    darkColor: '#4CB988',
                    options: [
                        {
                            title: 'Konsistenz',
                            filterType: 'consistency',
                        },
                    ],
                },
                {
                    name: 'Teilnehmer',
                    explanation:
                        'Hier können die Teilnehmer, deren Antworten analysiert werden sollen, gefiltert werden',
                    color: '#caf5ec',
                    darkColor: '#2FA399',
                    options: [
                        { title: 'Alter', filterType: 'age' },
                        {
                            title: 'Geschlecht',
                            filterType: 'gender',
                        },
                    ],
                },
                {
                    name: 'Antworten',
                    explanation: '',
                    color: '#caeaf5',
                    darkColor: '#2E8CAC',
                    options: [
                        {
                            title: 'Antwort',
                            filterType: 'questionAnswer',
                        },
                    ],
                },

                {
                    name: 'Logik',
                    explanation: '',
                    color: '#cadaf5',
                    darkColor: '#3D6CBB',
                    options: [
                        {
                            title: 'Oder',
                            filterType: 'orOperation',
                        },
                    ],
                },
            ],
        }
    },
    props: {
        chosenPoll: {
            default: '',
            type: String,
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
        pollIndex() {
            return this.pollTitles.indexOf(this.chosenPoll)
        },
        questionTitles() {
            const l = []
            for (let i = 0; i < this.polls.length; i++) {
                for (let j = 0; j < this.polls[i].categoryList.length; j++) {
                    for (let k = 0; k < this.polls[i].categoryList[j].questionList.length; k++) {
                        l.push(this.polls[i].categoryList[j].questionList[k].questionMessage)
                    }
                }
            }
            return l
        },
    },
    mounted() {
        console.log(this.polls)
        this.initialize()
    },
    methods: {
        ...mapActions({ initialize: 'evaluation/initialize', sendFilter: 'evaluation/sendFilter' }),
        saveToStore() {
            const filterData = []
            filterData.push({
                filterType: 'DataFilter',
                basePollId: 1,
                baseQuestionIds: [3, 4, 5, 6, 7, 8],
            })
            for (let f = 0; f < this.filterList.length; f++) {
                const filter = this.filterList[f]
                const poll = this.polls[this.pollIndex]
                const tPollId = poll.pollId
                let tCategoryId = 0
                let tQuestionId = 0
                const tAnswerPossibilities = []
                console.log(poll)
                for (let c = 0; c < poll.categoryList.length; c++) {
                    const category = poll.categoryList[c]
                    if (category.categoryName === filter.selectedCategory) {
                        tCategoryId = category.categoryId
                        for (let q = 0; q < category.questionList.length; q++) {
                            const question = category.questionList[q]
                            if (question.questionMessage === filter.selectedQuestion) {
                                tQuestionId = question.questionId
                                // for (let a = 0; a < filter.selectedAnswer.length; a++) {
                                const answer = filter.selectedAnswer // [a]
                                if (question.answerPossibilities.indexOf(answer)) {
                                    tAnswerPossibilities.push(question.answerPossibilities.indexOf(answer))
                                }
                                // }
                            }
                        }
                    }
                }
                filterData.push({
                    filterType: filter.filterType,
                    invertFilter: false,
                    targetPollId: tPollId,
                    targetCategoryId: tCategoryId,
                    targetQuestionId: tQuestionId,
                    targetAnswerPossibilities: tAnswerPossibilities,
                })
            }
            this.sendFilter(filterData)
            this.dialog = false
        },
        clone(item) {
            return {
                title: item.title,
                filterType: item.filterType,
                filterId: this.globalFilterId++,
            }
        },
        deleteFromFilterList(item) {
            console.log('deleteFromFilterList')
            const index = this.filterList.indexOf(item)
            if (index > -1) {
                this.filterList.splice(index, 1)
            }
        },
        addFilter(filter) {
            this.filterList.push(filter)
        },
        updateQAFilter([filterId, newCategory, newQuestion, newAnswer]) {
            console.log('updateQAFilter')
            for (let i = 0; i < this.filterList.length; i++) {
                if (this.filterList[i].filterId === filterId) {
                    this.filterList[i].selectedCategory = newCategory
                    this.filterList[i].selectedQuestion = newQuestion
                    this.filterList[i].selectedAnswer = newAnswer
                }
            }
            console.log(this.filterList)
        },
    },
}
</script>

<style scoped></style>
