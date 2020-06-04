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
                                                :search="search"
                                                :custom-filter="filterOnlyCapsText"
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
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'TextQuestionEvaluationWidget',
    data: () => {
        return {
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
            freqView: false,
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
            freqHeaders: [
                { text: 'Wort', value: 'value', sortable: false },
                { text: 'Frequenz', value: 'text', sortable: true },
            ],
            answers: [
                {
                    text: 'Uh wickey wild wild Wicky wicky wild Wickey wild wicky wicky wild wild wild west f gse',
                    answered: '2020',
                    creator: 'Idi Amin',
                    id: '0',
                },
                {
                    text: 'Uh wickey wild wild Wicky wicky wild Wickey wild wicky wicky wild wild wild west',
                    answered: '2012',
                    creator: 'Kony',
                    id: '1',
                },
            ],
            contextActions: ['Beantworten', 'Hallo', 'FGSE'],
        }
    },
    computed: {
        ...mapGetters({
            polls: 'navigation/getPolls',
            isAuthenticated: 'login/isAuthenticated',
        }),
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
        prepareWordFrequencies() {
            const data = []
            for (let i = 0; i < this.answers.length; i++) {
                // data[i].setAttribute('wordFreq')
                data[i] = this.returnWordListWithFreq(this.wordFreq(this.answers[i].text))
            }
            console.log(data)
            return data
        },
    },
    mounted() {
        console.log('mounted')
        console.log(this.polls)
        this.initialize()
    },
    methods: {
        ...mapActions({ initialize: 'navigation/initialize' }),
        wordFreq(string) {
            const words = string.toString().replace(/[.]/g, '').split(/\s/)
            const freqMap = {}
            words.forEach(function (w) {
                if (!freqMap[w]) {
                    freqMap[w] = 0
                }
                freqMap[w] += 1
            })
            console.log('polls:')
            console.log(this.polls)
            // console.log('wordListWithFreq:')
            // console.log(this.returnWordListWithFreq(freqMap))
            console.log(freqMap)
            return freqMap
        },

        returnWordListWithFreq(stringArray) {
            let listIndex = 0
            const outputArray = []
            for (const key in stringArray) {
                const item = { value: stringArray[key], text: key }
                // outputArray += item
                outputArray[listIndex] = item
                // console.log(listIndex, outputArray[listIndex])
                listIndex++
            }
            console.log('outputArray: ')
            console.log(outputArray)
            return outputArray
        },

        filterOnlyCapsText(value, search, item) {
            return (
                value != null &&
                search != null &&
                typeof value === 'string' &&
                value.toString().toLowerCase().includes(search.toLowerCase())
            )
        },
    },
}
</script>

<style scoped></style>
