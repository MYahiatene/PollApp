<template>
    <v-container>
        <v-layout row justify-space-around>
            <v-flex md6 lg3>
                <v-card class="ma-3 pa-3">
                    <v-card-title>Filter</v-card-title>
                    <template>
                        <v-expansion-panels accordion multiple hover>
                            <v-expansion-panel v-for="item in kategories" :key="item">
                                <v-expansion-panel-header :color="item.color">{{ item.name }}</v-expansion-panel-header>
                                <v-expansion-panel-content :color="item.color">
                                    <div class="overline">{{ item.explanation }}</div>
                                    <div v-for="option in item.options" :key="option">
                                        <draggable
                                            :list="item.options"
                                            :clone="clone"
                                            :group="{ name: 'filter', pull: 'clone' }"
                                        >
                                            <v-chip class="ma-1 filter" :color="item.darkColor" draggable>{{
                                                option.title
                                            }}</v-chip>
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
                    <v-card-title>Analyse {{ Questions }} fjskl</v-card-title>
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
                            <v-card height="200" color="#f8faff">
                                <draggable class="dragArea list-group" :list="filterList" group="filter">
                                    <div v-for="filter in filterList" :key="filter">
                                        <v-chip
                                            :color="kategories[2].color"
                                            close
                                            v-model="consistencyChip"
                                            v-if="filter.type === 'consistency'"
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
                                            <v-text-field type="number" min="0" oninput="validity.valid||(value='')">
                                            </v-text-field>
                                        </v-chip>

                                        <v-chip
                                            :color="kategories[3].color"
                                            close
                                            v-if="filter.type === 'gender'"
                                            @click:close="deleteFromFilterList(filter)"
                                        >
                                            Nur Teilnehmer mit dem Geschlecht: {{ selectedGender }} {{ filter.type }}
                                            <v-overflow-btn
                                                :items="['Männlich', 'Weiblich', 'Divers']"
                                                v-model="selectedGender"
                                                flat
                                                elevation="0"
                                                style="box-shadow: none;"
                                            >
                                            </v-overflow-btn>
                                        </v-chip>

                                        <v-chip
                                            :color="kategories[4].color"
                                            close
                                            v-if="filter.type === 'questionAnswer'"
                                            @click:close="deleteFromFilterList(filter)"
                                        >
                                            Nur Teilnehmer, die bei Frage {{ selectedQuestion }}
                                            <v-overflow-btn
                                                :items="['1', '2', '3']"
                                                v-model="selectedQuestion"
                                                elevation="0"
                                                style="box-shadow: none;"
                                            >
                                            </v-overflow-btn>
                                            die Antwort {{ selectedAnswer }}
                                            <v-overflow-btn
                                                :items="['1', '2', '3']"
                                                v-model="selectedAnswer"
                                                flat
                                                elevation="0"
                                                style="box-shadow: none;"
                                            >
                                            </v-overflow-btn>
                                            ausgewählt haben.
                                        </v-chip>
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
</template>

<script>
import draggable from 'vuedraggable'
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'customEvaluation',
    components: {
        draggable,
    },
    data() {
        return {
            chosenPoll: '',
            selectedQuestion: '',
            selectedAnswer: '',
            selectedAgeOperation: '=',
            selectedAge: 50,
            selectedGender: '',
            consistencyChip: true,
            filterList: [],
            selectedQuestions: [],
            kategories: [
                {
                    name: 'Daten',
                    explanation: 'Wähle die Basis-Daten für deine Umfrage',
                    color: '#f5f3ca',
                    darkColor: '#C0BB48',
                    options: [{ title: 'Umfrage', type: 'surveySelection' }],
                },
                {
                    name: 'Fragen',
                    explanation: 'Welche Fragen der Umfrage sollen betrachtet werden?',
                    color: '#e5f5ca',
                    darkColor: '#93C244',
                    options: [{ title: 'Fragen', type: 'questionSelection' }],
                },
                {
                    name: 'Konsistenzfragen',
                    explanation:
                        'Hier kann man einstellen, ob inkonsistente Beantwortungen herausgefiltert werden sollen',
                    color: '#caf5da',
                    darkColor: '#4CB988',
                    options: [{ title: 'Konsistenz', type: 'consistency' }],
                },
                {
                    name: 'Teilnehmer',
                    explanation:
                        'Hier können die Teilnehmer, deren Antworten analysiert werden sollen, gefiltert werden',
                    color: '#caf5ec',
                    darkColor: '#2FA399',
                    options: [
                        { title: 'Alter', type: 'age' },
                        { title: 'Geschlecht', type: 'gender' },
                    ],
                },
                {
                    name: 'Antworten',
                    explanation: '',
                    color: '#caeaf5',
                    darkColor: '#2E8CAC',
                    options: [{ title: 'Antwort', type: 'questionAnswer' }],
                },

                {
                    name: 'Logik',
                    explanation: '',
                    color: '#cadaf5',
                    darkColor: '#3D6CBB',
                    options: [{ title: 'Oder', type: 'orOperation' }],
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
            items: 'navigation/getPolls',
        }),

        pollTitles() {
            const pollTitles = Object.assign([{}], this.items)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.items[i].pollName
            }

            return pollTitles
        },

        Questions() {
            const categories = this.items[0].categoryList
            return categories[0].questionList[0].title
            // const questions = this.items[this.pollTitles.indexOf(this.chosenPoll)].categoryList[0].questionList[0].title
        },

        QuestionTitles() {
            const l = []
            for (let i = 0; i < this.PollResult.questionList.length; i++) {
                l.push(this.PollResult.questionList[i].title)
            }
            return l
        },

        clone({ name }) {
            return name
        },
    },

    mounted() {
        console.log('mounted')
        console.log(this.items)
        this.initialize()
    },

    methods: {
        ...mapActions({ initialize: 'navigation/initialize' }),

        deleteFromFilterList(item) {
            const index = this.filterList.indexOf(item)
            if (index > -1) {
                this.filterList.splice(index, 1)
            }
        },
    },
}
</script>

<style scoped></style>
