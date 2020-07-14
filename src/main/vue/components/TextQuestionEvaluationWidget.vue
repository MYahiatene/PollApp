<template>
    <v-card v-if="answers !== undefined">
        <v-card-title>{{ questionTitle }}</v-card-title>
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
                                    label="Suchen"
                                ></v-text-field>
                                <!--<v-checkbox :v-model="caseSensitive" @change="rerender"></v-checkbox>-->
                            </v-toolbar>
                        </template>

                        <template>
                            <v-row>
                                <v-col>
                                    <v-card v-if="tableView" class="ma-1" flat>
                                        <v-data-table
                                            :headers="tableHeaders"
                                            :items="answers"
                                            :search="search"
                                            :custom-filter="filterOnlyCapsText"
                                            class="elevation-1"
                                            dense
                                            :footer-props="footerProps"
                                            multi-sort
                                        >
                                        </v-data-table>
                                    </v-card>
                                    <!--<v-card v-if="tableView" class="ma-1" flat>
                                        <v-data-table
                                            :headers="freqHeaders"
                                            :items="frequency"
                                            :search="search"
                                            :custom-filter="filterOnlyCapsText"
                                            class="elevation-1"
                                            dense
                                            :footer-props="footerProps"
                                            multi-sort
                                        >
                                        </v-data-table>
                                    </v-card>-->
                                    <v-card
                                        v-for="(thing, index) in seriesToList(frequency)"
                                        :key="index"
                                        class="ma-1"
                                        flat
                                    >
                                        <div v-if="thing[0]">
                                            <template>
                                                <v-toolbar flat>
                                                    <v-toolbar-title
                                                        >Umfrage {{ thing[0].seriesCounter }}</v-toolbar-title
                                                    >
                                                </v-toolbar>
                                            </template>
                                            <v-data-table
                                                :headers="freqHeaders"
                                                :items="thing"
                                                :search="search"
                                                :custom-filter="filterOnlyCapsText"
                                                class="elevation-1"
                                                dense
                                                :footer-props="footerProps"
                                                multi-sort
                                                no-data-text="Keine Daten"
                                                no-results-text="Keine Ergebnisse"
                                            />
                                        </div>
                                        <div v-else>
                                            <template>
                                                <v-toolbar flat>
                                                    <v-toolbar-title>Keine Daten in dieser Umfrage</v-toolbar-title>
                                                </v-toolbar>
                                            </template>
                                            <v-data-table
                                                :headers="freqHeaders"
                                                :items="thing"
                                                :search="search"
                                                :custom-filter="filterOnlyCapsText"
                                                class="elevation-1"
                                                dense
                                                :footer-props="footerProps"
                                                multi-sort
                                                no-data-text="Keine Daten"
                                                no-results-text="Keine Ergebnisse"
                                            />
                                        </div>
                                    </v-card>
                                </v-col>
                            </v-row>
                        </template>
                    </v-data-iterator>
                </v-container>
            </v-layout>
        </v-container>
    </v-card>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'TextQuestionEvaluationWidget',
    props: {
        questionID: {
            type: Number,
        },
        questionTitle: {
            type: String,
        },
    },
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
                { text: 'Antwort', value: 'text', sortable: true },
                { text: 'Länge', value: 'value', sortable: true },
                { text: 'Beantwortet', value: 'edited', sortable: true },
                { text: 'Benutzer', value: 'creator', sortable: true },
                { text: 'Tendenzindex', value: 'tendency', sortable: true },
                { text: 'Serie', value: 'seriesCounter', sortable: true },
            ],
            freqHeaders: [
                { text: 'Wort', value: 'text', sortable: true },
                { text: 'Frequenz', value: 'frequency', sortable: true },
            ],
            footerProps: {
                itemsPerPageText: 'Zeilen pro Seite',
                itemsPerPageOptions: [10, 20, 50, -1],
                itemsPerPageAllText: 'Alle',
            },
        }
    },
    computed: {
        ...mapGetters({
            diagramData: 'evaluation/getDiagramData', // diagramdata.questionlist(questionID)
            pollName: 'evaluation/getPollName',
            isAuthenticated: 'login/isAuthenticated',
        }),

        answers() {
            // console.log(this.diagramData)
            // console.log(this.diagramData[this.questionID].texts)
            return this.diagramData[this.questionID].texts
        },
        frequency() {
            // console.log(this.diagramData[this.questionID].frequency)
            return this.diagramData[this.questionID].frequency
        },
        seriesToList() {
            return (seriesList) => {
                const outputList = [[]]
                // console.log('SeriesList: ', seriesList)
                for (const key in seriesList) {
                    // console.log('Key: ', seriesList[key])
                    // if (seriesList[key].seriesCounter - 1 < 0) seriesList[key].seriesCounter = 1
                    while (outputList.length <= seriesList[key].seriesCounter - 1) {
                        outputList.push([])
                    }
                    // console.log('SERIES LIST SERIES COUTER -1 ', seriesList[key].seriesCounter - 1)
                    // console.log('SERIES LIST KEY ', seriesList[key])
                    if (seriesList[key].seriesCounter - 1 >= 0)
                        outputList[seriesList[key].seriesCounter - 1].push(seriesList[key])
                    else outputList[seriesList[key].seriesCounter].push(seriesList[key])
                }
                // console.log('OutputList: ', outputList)
                return outputList
            }
        },
    },
    mounted() {
        // // console.log('mounted')
        // // console.log(this.polls)
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
            // // console.log('polls:')
            // // console.log(this.polls)
            // // console.log('wordListWithFreq:')
            // // console.log(this.returnWordListWithFreq(freqMap))
            // // console.log(freqMap)
            return freqMap
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
