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
                                        <v-chip class="ma-1" :color="item.darkColor" draggable>{{ option }}</v-chip>
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
                            <div>
                                Basisdaten:
                            </div>
                            <v-divider></v-divider>
                            <div>
                                <v-overflow-btn
                                    prefix="Ausgewählte Fragen:"
                                    allow-overflow
                                    chips
                                    deletable-chips
                                    small-chips
                                    dense
                                    multiple
                                    flat
                                    :items="QuestionTitles"
                                    v-model="QuestionTitles"
                                >
                                </v-overflow-btn>
                            </div>
                            <v-divider></v-divider>
                            <draggable class="dragArea list-group" :list="filterList" group="people"> </draggable>
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

export default {
    name: 'customEvaluation',
    components: {
        draggable,
    },
    data() {
        return {
            filterList: '',
            kategories: [
                {
                    name: 'Daten',
                    explanation: 'Wähle die Basis-Daten für deine Umfrage',
                    color: '#f5f3ca',
                    darkColor: '#C0BB48',
                    options: ['Basisdaten: [Umfrage 1] v'],
                },
                {
                    name: 'Fragen',
                    explanation: 'Welche Fragen der Umfrage sollen betrachtet werden?',
                    color: '#e5f5ca',
                    darkColor: '#93C244',
                    options: ['Folgende Fragen werden betrachtet: [1,3,4,6        ]'],
                },
                {
                    name: 'Konsistenzfragen',
                    explanation:
                        'Hier kann man einstellen, ob inkonsistente Beantwortungen herausgefiltert werden sollen',
                    color: '#caf5da',
                    darkColor: '#4CB988',
                    options: ['Nur konsistent beantwortete Umfragen'],
                },
                {
                    name: 'Teilnehmer',
                    explanation:
                        'Hier können die Teilnehmer, deren Antworten analysiert werden sollen, gefiltert werden',
                    color: '#caf5ec',
                    darkColor: '#2FA399',
                    options: [],
                },
                { name: 'Antworten', explanation: '', color: '#caeaf5', darkColor: '#2E8CAC', options: [] },

                { name: 'Logik', explanation: '', color: '#cadaf5', darkColor: '#3D6CBB', options: ['Oder'] },
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
        QuestionTitles() {
            const l = []
            for (let i = 0; i < this.PollResult.questionList.length; i++) {
                l.push(this.PollResult.questionList[i].title)
            }
            return l
        },
    },
}
</script>

<style scoped></style>
