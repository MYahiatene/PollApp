<template>
    <v-card>
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
                                            :items="prepareAnswers"
                                            :search="search"
                                            :custom-filter="filterOnlyCapsText"
                                            class="elevation-1"
                                            dense
                                            :footer-props="footerProps"
                                        >
                                        </v-data-table>
                                    </v-card>
                                    <v-card v-if="tableView" class="ma-1" flat>
                                        <v-data-table
                                            :headers="freqHeaders"
                                            :items="prepareWordFrequencies"
                                            :search="search"
                                            :custom-filter="filterOnlyCapsText"
                                            class="elevation-1"
                                            dense
                                            :footer-props="footerProps"
                                        >
                                        </v-data-table>
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
            fillers: [
                'nicht',
                'die',
                'der',
                'und',
                'in',
                'zu',
                'den',
                'das',
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
            positiveWords: [
                'gut',
                'fesch',
                'schnafte',
                'töfte',
                'flott',
                'positiv',
                'überzeugend',
                'erfreut',
                'softwerkende',
                'beeindruckend',
                'durchdacht',
                'eifrig',
                'eindrucksvoll',
                'schön',
                'reizend',
                'idyllisch',
                'groovy',
                'nice',
                'urcool',
                'megaaffentittengeil',
                'bäumig',
                'pfundig',
                'endgeil',
                'benutzerfreundlich',
                'angenehm',
                'anregend',
                'ansprechend',
                'atemberaubend',
                'attraktiv',
                'auffallend',
                'aufmerksam',
                'aufregend',
                'ausgewogen',
                'ausgezeichnet',
                'außergewöhnlich',
                'beeindruckend',
                'befriedigend',
                'begeisternd',
                'bejahend',
                'bekräftigend',
                'belebend',
                'beliebt',
                'bemerkenswert',
                'beneidenswert',
                'benutzerfreundlich',
                'bequem',
                'berauschend',
                'beruhigend',
                'berühmt',
                'beschwingt',
                'bestätigend',
                'bewährt',
                'bewundernswert',
                'brüderlich',
                'chancengleich',
                'charismatisch',
                'charmant',
                'couragiert',
                'dankbar',
                'durchdacht',
                'edel',
                'ehrgeizig',
                'ehrlich',
                'eifrig',
                'eindeutig',
                'eindrucksvoll',
                'einfallsreich',
                'einfühlend',
                'einwandfrei',
                'ekstatisch',
                'elegant',
                'elektrisierend',
                'empfehlenswert',
                'energisch',
                'engagiert',
                'entgegenkommend',
                'entspannt',
                'entzückend',
                'erfolgreich',
                'erfreulich',
                'erfüllend',
                'erhellend',
                'erleuchtend',
                'erreichbar',
                'erstaunlich',
                'erstklassig',
                'euphorisch',
                'exquisit',
                'exzellent',
                'fähig',
                'fantastisch',
                'faszinierend',
                'fehlerfrei',
                'feierlich',
                'fesselnd',
                'festlich',
                'fleißig',
                'freundlich',
                'friedlich',
                'frisch',
                'froh',
                'fröhlich',
                'frohlockend',
                'furchtlos',
                'gedeihlich',
                'geduldig',
                'geerdet',
                'gefeiert',
                'genial',
                'genießerisch',
                'genussvoll',
                'geschätzt',
                'geschickt',
                'geschmackvoll',
                'gestärkt',
                'gesund',
                'gewinnend',
                'glänzend',
                'glaubwürdig',
                'glücklich',
                'göttlich',
                'grandios',
                'großzügig',
                'handlich',
                'harmonisch',
                'heilig',
                'heilsam',
                'heiter',
                'herausragend',
                'herrlich',
                'hervorragend',
                'herzlich',
                'hilfreich',
                'hinreißend',
                'hochgeschätzt',
                'höflich',
                'humorvoll',
                'ideal',
                'idyllisch',
                'inspirierend',
                'interessant',
                'intuitiv',
                'jubelnd',
                'jugendlich',
                'klug',
                'kompetent',
                'königlich',
                'köstlich',
                'kraftvoll',
                'lächelnd',
                'langlebig',
                'lebendig',
                'leidenschaftlich',
                'leuchtend',
                'liebenswert',
                'liebenswürdig',
                'liebevoll',
                'lobenswert',
                'luxuriös',
                'makellos',
                'malerisch',
                'meisterhaft',
                'motivierend',
                'mutig',
                'niedlich',
                'nutzbringend',
                'offen',
                'ordentlich',
                'organisiert',
                'perfekt',
                'phänomenal',
                'positiv',
                'prächtig',
                'prachtvoll',
                'prickelnd',
                'problemlos',
                'produktiv',
                'pünktlich',
                'reibungslos',
                'reichhaltig',
                'renommiert',
                'respektvoll',
                'romantisch',
                'rücksichtsvoll',
                'sauber',
                'schick',
                'schmeichelnd',
                'schön',
                'schwungvoll',
                'seriös',
                'sicher',
                'solidarisch',
                'spektakulär',
                'spielerisch',
                'spontan',
                'stilvoll',
                'sympathisch',
                'tadellos',
                'tapfer',
                'tolerant',
                'treu',
                'triumphierend',
                'tüchtig',
                'überraschend',
                'überschwänglich',
                'überzeugend',
                'umsichtig',
                'unberührt',
                'unbeschwert',
                'uneigennützig',
                'unglaublich',
                'unkompliziert',
                'unterstützend',
                'unwiderstehlich',
                'verantwortungsvoll',
                'verführerisch',
                'vergnüglich',
                'verjüngend',
                'verliebt',
                'verlockend',
                'vertrauensvoll',
                'verwöhnend',
                'verzaubert',
                'verzückt',
                'vollendet',
                'vorteilhaft',
                'warm',
                'warmherzig',
                'wegweisend',
                'weise',
                'wendig',
                'wertvoll',
                'wichtig',
                'wirksam',
                'wohlerzogen',
                'wohlmeinend',
                'wohltätig',
                'wohltuend',
                'wunderbar',
                'wünschenswert',
                'würdevoll',
                'zauberhaft',
                'zugänglich',
                'zuverlässig',
            ],
            negativeWords: [
                'doof',
                'schlecht',
                'negativ',
                'scheiße',
                'enttäuschend',
                'enttäuschung',
                'enttäuscht',
                'uff',
                'yikes',
                'fuck',
                'lahm',
                'afd',
                'NN',
                'abgefeimt',
                'affektiert',
                'aggressiv',
                'ambivalent',
                'angeberisch',
                'anmaßend',
                'arglistig',
                'argwöhnisch',
                'arrogant',
                'aufdringlich',
                'aufgeblasen',
                'beratungsresistent',
                'blasiert',
                'borniert',
                'boshaft',
                'cholerisch',
                'dekadent',
                'demagogisch',
                'deprimiert',
                'despotisch',
                'distanziert',
                'dogmatisch',
                'dominant',
                'dreist',
                'egoistisch',
                'egozentrisch',
                'eifersüchtig',
                'eigenmächtig',
                'einfältig',
                'eingebildet',
                'einseitig',
                'eitel',
                'ekelerregend',
                'elitär',
                'fies',
                'jähzornig',
                'garstig',
                'gefallsüchtig',
                'gefrustet',
                'gnädig',
                'gönnerhaft',
                'großkotzig',
                'großspurig',
                'großtuerisch',
                'heimtückig',
                'herablassen',
                'hinterhältig',
                'hintertrieben',
                'hochfahrend',
                'hochmütig',
                'hoffärtig',
                'hoffnungslos',
                'hysterisch',
                'ignorant',
                'infam',
                'intrigant',
                'kleinkariert',
                'kompliziert',
                'kopfhängerisch',
                'langweilig',
                'lethargisch',
                'lügnerisch',
                'maliziös',
                'manipulativ',
                'mutlos',
                'naiv',
                'narzisstisch',
                'neurotisch',
                'oberflächlich',
                'pedantisch',
                'phlegmatisch',
                'protzig',
                'reserviert',
                'reserviert',
                'resigniert',
                'rücksichtslos',
                'scheinheilig',
                'schlampig',
                'schuftig',
                'selbstgefällig',
                'selbstgerecht',
                'selbstsüchtig',
                'selbstverliebt',
                'skrupellos',
                'spießig',
                'stur',
                'überheblich',
                'unbeweglich',
                'ungeduldig',
                'unnahbar',
                'unsozial',
                'unzugänglich',
                'verbohrt',
                'verlogen',
                'vernagelt',
                'verschlagen',
                'versnobt',
                'snobistisch',
                'verstiegen',
                'willkürlich',
                'zynisch',
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
            return this.diagramData[this.questionID].answers
        },

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
