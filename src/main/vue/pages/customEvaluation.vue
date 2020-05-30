<template>
    <v-container>
        <v-container v-if="items.length > 0">
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
                                                <v-chip class="ma-1 filter" :color="item.darkColor" draggable>
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
                        <v-card-title>Analyse</v-card-title>
                        <v-card class="pa-2">
                            <v-responsive :aspect-ratio="16 / 5">
                                <v-overflow-btn prefix="Basisdaten:" :items="pollTitles" dense v-model="chosenPoll">
                                </v-overflow-btn>
                                <v-divider></v-divider>
                                <div>
                                    <v-overflow-btn
                                        prefix="Ausgewählte Fragen:"
                                        allow-overflow
                                        dense
                                        multiple
                                        flat
                                        :items="QuestionTitles"
                                        v-model="selectedQuestions"
                                    >
                                    </v-overflow-btn>
                                </div>
                                <v-divider></v-divider>
                                <v-card height="5000" color="#f8faff">
                                    <draggable class="dragArea list-group" :list="filterList" group="filter">
                                        <div v-for="(filter, index) in filterList" :key="index">
                                            <v-chip
                                                :color="kategories[2].color"
                                                v-model="consistencyChip"
                                                v-if="filter.type === 'consistency'"
                                                close
                                                @click:close="deleteFromFilterList(filter)"
                                            >
                                                Es werden nur Teilnehmer angezeigt, die alle Konsistenzfragen konsistent
                                                beantwortet haben.
                                            </v-chip>

                                            <v-chip
                                                :color="kategories[3].color"
                                                close
                                                v-if="filter.type === 'age'"
                                                @click:close="deleteFromFilterList(filter)"
                                            >
                                                Nur Teilnehmer mit Alter {{ selectedAgeOperation }} {{ filter.type }}
                                                <v-overflow-btn
                                                    :items="['<', '>', '=']"
                                                    v-model="selectedAgeOperation"
                                                    flat
                                                    elevation="0"
                                                    style="box-shadow: none;"
                                                >
                                                </v-overflow-btn>
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
                                                v-if="filter.type === 'gender'"
                                                @click:close="deleteFromFilterList(filter)"
                                            >
                                                Nur Teilnehmer mit dem Geschlecht: {{ selectedGender }}
                                                {{ filter.type }}
                                                <v-overflow-btn
                                                    :items="['Männlich', 'Weiblich', 'Divers']"
                                                    v-model="selectedGender"
                                                    flat
                                                    elevation="0"
                                                    style="box-shadow: none;"
                                                >
                                                </v-overflow-btn>
                                            </v-chip>

                                            <q-a-filter
                                                v-if="filter.type === 'questionAnswer'"
                                                :poll-index="pollIndex"
                                                :filter-id="filter.filterId"
                                                :selected-answer="filter.selectedAnswer"
                                                :selected-question="filter.selectedQuestion"
                                                :selected-category="filter.selectedCategory"
                                                @updateData="updateQAFilter"
                                            ></q-a-filter>
                                            <!--                                        <p v-else>-->
                                            <!--                                            filter.type-->
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
</template>

<script>
import draggable from 'vuedraggable'
import { mapActions, mapGetters } from 'vuex'
import QAFilter from '../components/Filter/QAFilter'

export default {
    name: 'customEvaluation',
    components: {
        QAFilter,
        draggable,
    },
    data() {
        return {
            chosenPoll: 'Umfrage zur IT-Messe 2020',
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
                            type: 'surveySelection',
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
                            type: 'questionSelection',
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
                            type: 'consistency',
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
                        { title: 'Alter', type: 'age' },
                        {
                            title: 'Geschlecht',
                            type: 'gender',
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
                            type: 'questionAnswer',
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
                            type: 'orOperation',
                        },
                    ],
                },
            ],

            // mock data set
            PollResult: {
                name: 'Umfrage zur IT-Messe 2021',
                questionList: [
                    {
                        id: 1,
                        title: 'Wie hat Ihnen die Veranstaltung insgesamt gefallen?',
                        answerPossibilities: ['Sehr gut', 'Gut', 'Überwiegend gut', 'Schlecht', 'Ich weiß nicht'],
                        data: [70, 65, 30, 5, 25],
                    },

                    {
                        id: 2,
                        title: 'Welches Geschlecht haben Sie?',
                        answerPossibilities: ['Weiblich', 'Männlich', 'Divers'],
                        data: [20, 19, 1],
                    },

                    {
                        id: 3,
                        title: 'Wie geht es Ihnen heute?',
                        answerPossibilities: ['Gut', 'In Ordnung', 'Schlecht'],
                        data: [22, 8, 7],
                    },
                    {
                        id: 4,
                        title: 'Was hat Sie am Meisten überzeugt?',
                        answerPossibilities: [
                            'Die Vorträge',
                            'Die Informationsstände',
                            'Das Catering',
                            'Ich kann mich nicht entscheiden',
                        ],
                        data: [17, 8, 4, 2],
                    },
                    {
                        id: 5,
                        title: 'Werden Sie uns nächstes Jahr wieder besuchen?',
                        answerPossibilities: ['Ja', 'Nein'],
                        data: [50, 21],
                    },
                    {
                        id: 6,
                        title: 'Wie viel Zeit haben sie auf der Messe verbracht?',
                        answerPossibilities: ['unter einer Stunde', '1-2 Stunden', '2-5 Stunden', 'über 5 Stunden'],
                        data: [12, 45, 40, 20],
                    },
                ],
            },
        }
    },
    computed: {
        ...mapGetters({
            items: 'evaluation/getPolls',
        }),
        pollTitles() {
            const pollTitles = Object.assign([{}], this.items)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.items[i].pollName
            }

            return pollTitles
        },
        pollIndex() {
            return this.pollTitles.indexOf(this.chosenPoll)
        },
        QuestionTitles() {
            const l = []
            for (let i = 0; i < this.PollResult.questionList.length; i++) {
                l.push(this.PollResult.questionList[i].title)
            }
            return l
        },
    },
    mounted() {
        console.log(this.items)
        this.initialize()
    },
    methods: {
        ...mapActions({ initialize: 'evaluation/initialize' }),
        clone(item) {
            return {
                title: item.title,
                type: item.type,
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
