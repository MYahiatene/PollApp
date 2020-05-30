<template>
    <v-container grid-list-md text-xs-center>
        <v-layout row wrap>
            <v-container fluid>
                <v-data-iterator
                    :items="answers"
                    :search="search"
                    :sort-by="sortBy"
                    :sort-desc="sortDesc"
                    hide-default-footer
                >
                    <template v-slot:header>
                        <v-toolbar class="mb-1">
                            <v-text-field
                                v-model="search"
                                clearable
                                flat
                                solo-inverted
                                hide-details
                                prepend-inner-icon="mdi-pencil"
                                label="Search"
                            ></v-text-field>
                            <template v-if="$vuetify.breakpoint.mdAndUp">
                                <v-spacer></v-spacer>
                                <v-select
                                    v-model="sortBy"
                                    flat
                                    solo-inverted
                                    hide-details
                                    :items="keys"
                                    prepend-inner-icon="mdi-pencil"
                                    label="Sort by"
                                ></v-select>
                                <v-spacer></v-spacer>
                                <v-btn-toggle v-model="sortDesc" mandatory>
                                    <v-btn large depressed :value="false">
                                        <v-icon>mdi-arrow-up</v-icon>
                                    </v-btn>
                                    <v-btn large depressed :value="true">
                                        <v-icon>mdi-arrow-down</v-icon>
                                    </v-btn>
                                </v-btn-toggle>
                            </template>
                        </v-toolbar>
                    </template>

                    <template>
                        <v-row>
                            <v-col>
                                <v-card>
                                    <v-container>
                                        <template v-if="freqView">
                                            <v-data-table
                                                :headers="headers"
                                                :items="prepareAnswers"
                                                class="elevation-1"
                                            >
                                            </v-data-table>
                                        </template>
                                        <template v-if="tableView">
                                            <v-data-table
                                                :headers="tableHeaders"
                                                :items="prepareAnswers"
                                                class="elevation-1"
                                            >
                                            </v-data-table>
                                        </template>
                                    </v-container>
                                </v-card>
                            </v-col>
                        </v-row>
                    </template>
                </v-data-iterator>
            </v-container>
        </v-layout>
    </v-container>
</template>

<script>
export default {
    name: 'TextQuestionEvaluationWidget',
    data: () => ({
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                yAxes: [
                    {
                        ticks: {
                            beginAtZero: true,
                        },
                    },
                ],
            },
        },
        search: '',
        tableView: true,
        freqView: true,
        filter: {},
        sortDesc: false,
        sortBy: 'name',
        keys: ['text'],
        headers: [
            { text: 'Wort', value: 'text', sortable: false },
            { text: 'Frequenz', value: 'wordFrequency', sortable: true },
            { text: 'Beantwortet', value: 'answered', sortable: true },
            { text: 'Benutzer', value: 'creator', sortable: true },
        ],
        tableHeaders: [
            { text: 'Antwort', value: 'text', sortable: false },
            { text: 'Beantwortet', value: 'answered', sortable: true },
            { text: 'Benutzer', value: 'creator', sortable: true },
        ],
        answers: [
            {
                text: 'Uh, wickey wild wild Wicky wicky wild Wickey wild, wicky wicky wild wild wild west',
                answered: '2020',
                creator: 'Idi Amin',
                id: '0',
            },
            {
                text: 'Uh, wickey wild wild Wicky wicky wild Wickey wild, wicky wicky wild wild wild west',
                answered: '2012',
                creator: 'Kony',
                id: '1',
            },
        ],
        contextActions: ['Beantworten', 'Hallo', 'FUCK GSE'],
    }),
    computed: {
        prepareAnswers() {
            const data = []
            for (let i = 0; i < this.answers.length; i++) {
                data[i] = this.answers[i]
                // data[i].setAttribute('wordFreq')
                data[i].wordFrequency = this.wordFreq(this.answers[i].text)
                delete data[i].id
            }
            console.log(data)
            return data
        },
    },
    methods: {
        wordFreq(string) {
            const words = string.toString().replace(/[.]/g, '').split(/\s/)
            const freqMap = {}
            words.forEach(function (w) {
                if (!freqMap[w]) {
                    freqMap[w] = 0
                }
                freqMap[w] += 1
            })

            return freqMap
        },
    },
}
</script>

<style scoped></style>
