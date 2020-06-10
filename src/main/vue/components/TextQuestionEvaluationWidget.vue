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
                                solo
                                hide-details
                                prepend-inner-icon="mdi-pencil"
                                label="Search"
                            ></v-text-field>
                            <!--<v-checkbox :v-model="caseSensitive" @change="rerender"></v-checkbox>-->
                        </v-toolbar>
                    </template>

                    <template>
                        <v-row>
                            <v-col>
                                <v-card>
                                    <v-container>
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
                                        <template v-if="tableView">
                                            <v-data-table
                                                :headers="freqHeaders"
                                                :items="prepareWordFrequencies"
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
            caseSensitive: false,
            enableFillWords: true,
            filter: {},
            sortDesc: false,
            sortBy: 'name',
            keys: ['text'],
            wordFrequenz: {},
            tableHeaders: [
                { text: 'Antwort', value: 'text', sortable: false },
                { text: 'Länge', value: 'wordLength', sortable: true },
                { text: 'Beantwortet', value: 'answered', sortable: true },
                { text: 'Benutzer', value: 'creator', sortable: true },
                { text: 'Tendenzindex', value: 'tendency', sortable: true },
            ],
            freqHeaders: [
                { text: 'Wort', value: 'text', sortable: false },
                { text: 'Frequenz', value: 'value', sortable: true },
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
                {
                    text: 'telefone sind schon sehr fesch',
                    answered: '2020',
                    creator: 'Da Vinci',
                    id: '2',
                },
                {
                    text: 'Fck GSE der die das im die die die die die das nacho fuck afd',
                    answered: '2010',
                    creator: 'TBrettmann',
                    id: '3',
                },
            ],
            fillers: [
                'die',
                'der',
                'und',
                'in',
                'zu',
                'den',
                'das',
                'nicht',
                'von',
                'sie',
                'ist',
                'des',
                'sich',
                'mit',
                'dem',
                'dass',
                'er',
                'es',
                'ein',
                'ich',
                'auf',
                'so',
                'eine',
                'auch',
                'als',
                'an',
                'nach',
                'wie',
                'im',
                'für',
            ],
            positiveWords: ['gut', 'fesch', 'schnafte', 'töfte', 'flott'],
            negativeWords: ['doof', 'schlecht', 'gse', 'fuck', 'lahm', 'afd'],
        }
    },
    computed: {
        ...mapGetters({
            polls: 'navigation/getPolls',
            isAuthenticated: 'login/isAuthenticated',
        }),
        prepareAnswers() {
            const data = []
            const freqMap = {}
            for (let i = 0; i < this.answers.length; i++) {
                data[i] = this.answers[i]
                // data[i].setAttribute('wordFreq')
                data[i].wordFrequency = this.wordFreq(this.answers[i].text, freqMap)
                data[i].tendency = this.findTendency(this.wordFreq(this.answers[i].text, {}))
                // data[i].setAttribute('wordLength')
                data[i].wordLength = this.countWords(this.answers[i].text)
                delete data[i].id
            }
            // this.wordFrequenz = this.data[0].wordFrequency
            // console.log(data)
            return data
        },
        prepareWordFrequencies() {
            const data = []
            const freqMap = {}
            for (let i = 0; i < this.answers.length; i++) {
                data[i] = this.answers[i]
                // data[i].setAttribute('wordFreq')
                if (this.caseSensitive) {
                    data[i].wordFrequency = this.wordFreq(this.answers[i].text.toLowerCase(), freqMap)
                } else {
                    data[i].wordFrequency = this.wordFreq(this.answers[i].text.toLowerCase(), freqMap)
                }
            }
            delete freqMap.id
            delete freqMap.creator
            delete freqMap.answer
            console.log(this.returnWordListWithFreq(freqMap))
            return this.returnWordListWithFreq(freqMap)
            // return data
        },
    },
    mounted() {
        // console.log('mounted')
        // console.log(this.polls)
        this.initialize()
    },
    methods: {
        ...mapActions({ initialize: 'navigation/initialize' }),
        wordFreq(string, freqMap) {
            const words = string.toString().replace(/[.]/g, '').split(/\s/)
            words.forEach(function (w) {
                if (!freqMap[w]) {
                    freqMap[w] = 0
                }
                freqMap[w] += 1
            })
            // console.log('polls:')
            // console.log(this.polls)
            // console.log('wordListWithFreq:')
            // console.log(this.returnWordListWithFreq(freqMap))
            // console.log(freqMap)
            return freqMap
        },

        countWords(string) {
            return string.split(' ').length
        },

        returnWordListWithFreq(stringArray) {
            let listIndex = 0
            const outputArray = []
            for (const key in stringArray) {
                if (!this.fillers.includes(key) || this.enableFillWords) {
                    const item = { value: stringArray[key], text: key, tendency: key.tendency }
                    outputArray[listIndex] = item
                    // console.log(listIndex, outputArray[listIndex])
                    listIndex++
                }
                // outputArray += item
            }
            // console.log('outputArray: ')
            // console.log(outputArray)
            return outputArray
        },

        findTendency(stringArray) {
            let tendency = 0
            for (const key in stringArray) {
                if (this.positiveWords.includes(key.toLowerCase())) {
                    tendency += stringArray[key]
                }
                if (this.negativeWords.includes(key.toLowerCase())) {
                    tendency -= stringArray[key]
                }
            }
            console.log(tendency)
            return tendency
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
