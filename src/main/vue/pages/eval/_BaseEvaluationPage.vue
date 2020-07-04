<template>
    <!--     This page is called whenever you want to see the Evaluation of a survey.
It provides a first overview of the result by displaying a table as well as a diagram for each separate question.
Thusly the user can easily get a first impression on the results.
-->
    <v-container>
        <!--        this is the real content of the page, a list of ChoiceQuestionEvaluationWidgets,
that each display a basic evaluation of one specific question-->

        <!--        Here we check if we are authenticated and if we could load the data -->
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="diagramData !== undefined">
            <v-row>
                <!--        the app bar is used to navigate the page.


   All the buttons here apply to the entire poll, not one individual question-->

                <v-toolbar>
                    <!--                    button for refresh-->

                    <!--                    <v-btn icon onClick="window.location.reload()" color="primary"><v-icon> mdi-refresh</v-icon></v-btn>-->
                    <v-btn icon color="primary" @click="forceUpdate"><v-icon> mdi-refresh</v-icon></v-btn>

                    <!--                    title of the poll-->
                    <v-col cols="5">
                        <v-card-title> Angezeigte Teilnehmer: {{ participants }} </v-card-title>
                    </v-col>

                    <v-dialog v-model="dialog">
                        <!--             Here we open a setting window
        where the user can change the visual settings of every question at the same time-->
                        <v-card
                            ><visual-evaluation-settings
                                :change-default="true"
                                @done=";(dialog = false), (widgetKey += 1)"
                            >
                            </visual-evaluation-settings>
                        </v-card>
                    </v-dialog>
                    <v-col cols="3">
                        <v-card-title> {{ pollName }} </v-card-title>
                    </v-col>

                    <!--            This button will lead to the Page where we can filter and analyse the data-->
                    <v-spacer />
                    <v-tooltip bottom>
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn
                                icon
                                :color="relativ ? 'accent' : 'primary'"
                                v-bind="attrs"
                                v-on="on"
                                @click=";(relativ = !relativ), (widgetKey += 1)"
                            >
                                <v-icon>
                                    %
                                </v-icon>
                            </v-btn>
                        </template>
                        <span>Global Relative bzw. Absolute Häufigkeit umschalten</span>
                    </v-tooltip>
                    <v-tooltip bottom>
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn
                                icon
                                :color="cumulated ? 'accent' : 'primary'"
                                v-bind="attrs"
                                v-on="on"
                                @click=";(cumulated = !cumulated), (widgetKey += 1)"
                            >
                                <v-icon> mdi-sigma</v-icon>
                            </v-btn>
                        </template>
                        <span>Global Kumulierte Häufigkeit an- und ausschalten</span>
                    </v-tooltip>
                    <v-tooltip bottom>
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn
                                icon
                                color="primary"
                                v-bind="attrs"
                                v-on="on"
                                @click="visualSettings = !visualSettings"
                            >
                                <v-icon @click="dialog = true">mdi-brush</v-icon>
                            </v-btn>
                        </template>
                        <span>Globale Digrammfarben anpassen</span>
                    </v-tooltip>
                    <v-tooltip bottom>
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn icon color="primary" v-bind="attrs" v-on="on">
                                <v-icon>mdi-cloud-download</v-icon>
                            </v-btn>
                        </template>
                        <span>Exportieren</span>
                    </v-tooltip>
                    <session-manager @close-event="widgetKey += 1"></session-manager>
                    <filter-form :initial-poll-index="getPollIndex" @close-event="widgetKey += 1"></filter-form>

                    <!--                   title of the poll-->

                    <!--            here we have a sub menu, that can hold a list of different options or setings-->
                    <!--                    <v-menu bottom left>-->
                    <!--                        <template v-slot:activator="{ on }">-->
                    <!--                            <v-btn icon color="primary">-->
                    <!--                                <v-icon @click="dialog = true">mdi-dots-vertical</v-icon>-->
                    <!--                            </v-btn>-->
                    <!--                        </template>-->

                    <!--                        <v-list>-->
                    <!--                            <v-list-item v-for="(item, i) in menuItems" :key="i">-->
                    <!--                                <v-list-item-title @click="dialog = true">{{ item.title }}</v-list-item-title>-->
                    <!--                            </v-list-item>-->
                    <!--                        </v-list>-->
                    <!--                    </v-menu>-->
                </v-toolbar>
            </v-row>

            <!--             Here the real content of the page starts -->
            <v-row>
                <v-col cols="12" lg="12">
                    <v-data-iterator
                        :items="items2"
                        :items-per-page.sync="itemsPerPage"
                        :page="page"
                        :search="search"
                        :sort-by="'id'"
                        :sort-desc="sortDesc"
                        no-data-text="Keine Teilnehmer gefunden"
                        no-results-text="Keine passenden Fragen gefunden"
                        hide-default-footer
                    >
                        <template v-slot:header>
                            <!--                            in he toolbar we can search for items and set the number of items per page -->
                            <v-toolbar class="mb-1">
                                <v-text-field
                                    v-model="search"
                                    clearable
                                    flat
                                    hide-details
                                    prepend-inner-icon="mdi-magnify"
                                    label="Suche"
                                ></v-text-field>
                                <template v-if="$vuetify.breakpoint.mdAndUp">
                                    <v-spacer></v-spacer>
                                    <v-btn-toggle v-model="sortDesc" mandatory>
                                        <v-btn depressed :value="false">
                                            <v-icon>mdi-arrow-up</v-icon>
                                        </v-btn>
                                        <v-btn depressed :value="true">
                                            <v-icon>mdi-arrow-down</v-icon>
                                        </v-btn>
                                    </v-btn-toggle>
                                </template>
                                <v-spacer></v-spacer>
                                <template>
                                    <v-row class="mt-2" align="center" justify="center">
                                        <span class="black--text">Fragen pro Seite</span>
                                        <v-menu offset-y>
                                            <template v-slot:activator="{ on }">
                                                <v-btn text class="ml-2" v-on="on">
                                                    {{ itemsPerPage }}
                                                    <v-icon>mdi-chevron-down</v-icon>
                                                </v-btn>
                                            </template>
                                            <v-list>
                                                <v-list-item
                                                    v-for="(number, index) in itemsPerPageArray"
                                                    :key="index"
                                                    @click="updateItemsPerPage(number)"
                                                >
                                                    <v-list-item-title>{{ number }}</v-list-item-title>
                                                </v-list-item>
                                            </v-list>
                                        </v-menu>

                                        <v-spacer></v-spacer>

                                        <span class="mr-4 black--text"> Seite {{ page }} von {{ numberOfPages }} </span>
                                        <v-btn dark color="secondary" class="mr-1" @click="formerPage">
                                            <v-icon>mdi-chevron-left</v-icon>
                                        </v-btn>
                                        <v-btn dark color="secondary" class="ml-1" @click="nextPage">
                                            <v-icon>mdi-chevron-right</v-icon>
                                        </v-btn>
                                    </v-row>
                                </template>
                            </v-toolbar>
                        </template>

                        <template v-slot:default="props">
                            <v-row>
                                <v-col
                                    v-for="question in props.items"
                                    :key="question.id"
                                    cols="12"
                                    lg="12"
                                    md="12"
                                    sm="12"
                                >
                                    <div v-if="question.type === 'choice'">
                                        <!--                                 Here we have the ChoiceQuestionEvaluationWidgets    -->
                                        <ChoiceQuestionEvaluationWidget
                                            :key="widgetKey"
                                            :question-id="question.id"
                                            :question-title="question.title"
                                            :answer-possibilities="question.answerPossibilities"
                                            :data-input="question.data"
                                            :calculated="question.calculated"
                                        ></ChoiceQuestionEvaluationWidget>
                                    </div>

                                    <div v-else-if="question.type === 'text'">
                                        <textQuestionEvaluationWidget
                                            :questionID="question.questionID"
                                            :questionTitle="question.title"
                                        >
                                        </textQuestionEvaluationWidget>
                                    </div>

                                    <div v-else-if="question.type === 'sort'">
                                        <SortQuestionEvaluationWidget
                                            :key="sortWidgetKey"
                                            :questionID="question.questionID"
                                            :questionTitle="question.title"
                                        >
                                        </SortQuestionEvaluationWidget>
                                    </div>
                                    <v-spacer></v-spacer>
                                </v-col>
                            </v-row>
                        </template>
                    </v-data-iterator>
                </v-col>
            </v-row>
        </v-container>
        <v-container v-else>
            <v-card>
                <v-card-title>Der Server hat noch nicht geantwortet</v-card-title>
            </v-card>
        </v-container>
        <v-container>
            <v-btn class="pl-4" @click="exportAnswers()">Antworten exportieren </v-btn>
            <v-btn class="pl-4" @click="exportResults()">Antworten exportieren </v-btn>
            <v-btn class="pl-4" @click="exportCSV()">Antworten exportierenCSV </v-btn>
            <v-col cols="12" md="4">
                Datei hochladen
                <input type="file" @change="uploadFile" />
            </v-col>
        </v-container>
    </v-container>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import AuthGate from '../../components/AuthGate'
import ChoiceQuestionEvaluationWidget from '../../components/ChoiceQuestionEvaluationWidget'
import visualEvaluationSettings from '../../components/visualEvaluationSettings'
import TextQuestionEvaluationWidget from '../../components/TextQuestionEvaluationWidget'
import FilterForm from '../../components/filterForm'
import SessionManager from '../../components/SessionManager'
import SortQuestionEvaluationWidget from '../SortQuestionEvaluationWidget'

export default {
    name: 'BaseEvaluationPage',
    components: {
        SessionManager,
        FilterForm,
        AuthGate,
        ChoiceQuestionEvaluationWidget,
        visualEvaluationSettings,
        TextQuestionEvaluationWidget,
        SortQuestionEvaluationWidget,
    },
    data() {
        return {
            // key that forces the diagram to update
            // (value is set on the color of the diagram and update whenever updateVisuals is called)
            widgetKey: '',
            sortWidgetKey: '',
            // the dialog (setting window) is closed by default
            dialog: false,
            // default settings for the visual settings, they are passed as props into the choiceQuestionEvaluationWidgets
            defaultColors: ['#aaaaaa'],
            defaultColor: '#aaaaaa',
            multipleColors: false,
            defaultDiagramType: 'bar',
            showDiagram: true,
            showTable: true,
            useOnAll: false,
            // options that will be displayed in the sub menu
            menuItems: [{ title: 'Visuelle Einstellungen' }, { title: 'Exportieren' }],
            // itemsPerPageArray: [1, 2, 3],
            search: '',
            filter: {},
            sortDesc: false,
            page: 1,
            itemsPerPage: 2,
            sortBy: 'id',
            file: '',
        }
    },
    created() {
        this.reset()
        this.initialize(this.$route.params.BaseEvaluationPage)
        this.loadSessions()
        console.log('DD:')
        console.log(this.diagramData)
    },
    watch: {
        currentTheme() {
            this.widgetKey += 1
        },
    },
    computed: {
        ...mapGetters({
            diagramData: 'evaluation/getDiagramData',
            pollName: 'evaluation/getPollName',
            isAuthenticated: 'login/isAuthenticated',
            participants: 'evaluation/getParticipants',
            getPolls: 'evaluation/getPolls',
            getDiagramOption: 'evaluation/getDiagramOption',
        }),
        cumulated: {
            get() {
                return this.getDiagramOption(-1).cumulated
            },
            set(value) {
                this.setDiagramOptions({
                    useOnAll: true,
                    option: {
                        questionId: -1,
                        cumulated: value,
                        relativ: this.relativ,
                    },
                })
            },
        },
        relativ: {
            get() {
                return this.getDiagramOption(-1).relativ
            },
            set(value) {
                this.setDiagramOptions({
                    useOnAll: true,
                    option: {
                        questionId: -1,
                        cumulated: this.cumulated,
                        relativ: value,
                    },
                })
            },
        },
        currentTheme() {
            return this.$vuetify.theme.dark
        },

        // computes the number of pages
        numberOfPages() {
            return Math.ceil(this.items.length / this.itemsPerPage)
        },

        // filteredKeys() {
        //     return this.keys.filter((key) => key !== `id`)
        // },

        // gets diagramData
        items() {
            return this.diagramData
        },

        // these are used to have the Frage + id ad on in the title, so you can filter and search vor it

        items2() {
            const i2 = []
            let c = -1
            console.log(this.items)
            for (let i = 0; i < this.items.length; i++) {
                if (this.items[i].title === 'Teilnahmen über Zeit') {
                    c = 0
                }
            }
            for (let i = 0; i < this.items.length; i++) {
                i2[i] = {
                    answerPossibilities: this.items[i].answerPossibilities,
                    data: this.items[i].data,
                    id: this.items[i].id,
                    title: (c === i ? '' : 'Frage ' + (i - c) + ': ') + this.items[i].title,
                    calculated: this.items[i].calculated,
                    type: this.items[i].type,
                    questionID: i, // we need the index in the list
                }
            }
            return i2
        },

        // we compute an array of option on how many questions we want per page

        itemsPerPageArray() {
            return [1, Math.round(this.items.length / 2), this.items.length]
        },

        getPollIndex() {
            console.log('getPollIndex()')
            console.log(this.getPolls)
            console.log(this.$route.params)
            for (let i = 0; i < this.getPolls.length; i++) {
                console.log(i)
                if (this.getPolls[i].pollId.toString() === this.$route.params.BaseEvaluationPage) {
                    console.log('treffer')
                    console.log(i)
                    return i
                }
            }
            console.log('shit')
            return -1
        },
    },
    methods: {
        ...mapMutations({
            reset: 'evaluation/resetState',
            setDiagramOptions: 'evaluation/setDiagramOptions',
        }),
        ...mapActions({
            initialize: 'evaluation/initialize',
            updateData: 'evaluation/updateData',
            loadSessions: 'evaluation/loadSessions',
        }),

        exportAnswers() {
            this.$store.dispatch('evaluation/exportAnswers', 1) // This should be PollId
            this.downloadClick(1)
        },

        exportResults() {
            this.$store.dispatch('evaluation/exportResults', 1) // This should be PollId
            this.downloadClickResult(1)
        },

        exportCSV() {
            this.$store.dispatch('evaluation/exportCSV', 1) // This should be PollId
            this.downloadClickCSV(1)
        },

        downloadClick(pollId) {
            console.log(this.$store.dispatch('evaluation/awaitPollText', pollId))
        },

        downloadClickResult(pollId) {
            console.log(this.$store.dispatch('evaluation/awaitPollResultText', pollId))
        },

        downloadClickCSV(pollId) {
            console.log(this.$store.dispatch('evaluation/awaitPollResultCsv', pollId))
        },

        uploadFile(e) {
            const file = e.target.files[0]
            const reader = new FileReader()
            reader.readAsText(file)
            reader.onload = (e) => {
                console.log('File: ', file)
                console.log('FileE: ', e.target.result)
                console.log(this.$store.dispatch('evaluation/importPoll', e.target.result))
            }
        },

        /*

      this method is emitted by the settings window.
      Once its called we update the visual settings of all the choiceQuestionEvaluationWidgets
      the widget key is added to force the widget to actually update the color

       */
        updateVisuals(showDiagram, DiagramType, DiagramColors, DiagramColor, multipleColors, showTable, useOnAll) {
            this.showDiagram = showDiagram
            this.defaultDiagramType = DiagramType

            this.defaultColor = DiagramColor
            this.defaultColors = DiagramColors

            this.multipleColors = multipleColors
            this.showTable = showTable

            this.dialog = false

            if (multipleColors) {
                for (let i = 0; i < DiagramColors.length; i++) {
                    this.widgetKey += +DiagramColors[i]
                }
            } else {
                this.widgetKey = DiagramColor
            }

            this.useOnAll = useOnAll
        },

        // goes to next page

        nextPage() {
            if (this.page + 1 <= this.numberOfPages) this.page += 1
        },

        // goes to last page
        formerPage() {
            if (this.page - 1 >= 1) this.page -= 1
        },

        // updates how many widgets are displayed per page
        updateItemsPerPage(number) {
            this.itemsPerPage = number
        },

        // updates the page
        async forceUpdate() {
            console.log('force-update')
            // gets the stuff from the database
            await this.updateData()
            // forces the diagrams to update
            this.widgetKey += 1
            this.sortWidgetKey += 1
        },
    },
}
</script>

<style scoped></style>
