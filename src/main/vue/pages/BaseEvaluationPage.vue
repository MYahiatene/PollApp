<template>
    <!--     This page is called whenever you want to see the Evaluation of a survey.
It provides a first overview of the result by displaying a table as well as a diagram for each separate question.
Thusly the user can easily get a first impression on the results.
-->
    <v-container>
        <!--        this is the real content of the page, a list of ChoiceQuestionEvaluationWidgets,
that each display a basic evaluation of one specific question-->
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="diagramData !== undefined">
            <v-row>
                <!--        the app bar is used to navigate the page.
   All the buttons here apply to the entire poll, nit one individual question-->
                <v-toolbar>
                    <v-card-title>{{ pollName }}</v-card-title>

                    <v-dialog v-model="dialog">
                        <!--             Here we open a setting window
        where the user can change the visual settings of every question at the same time-->
                        <v-card
                            ><visual-evaluation-settings @update-Visuals="updateVisuals"> </visual-evaluation-settings
                        ></v-card>
                    </v-dialog>
                    <v-spacer></v-spacer>

                    <v-spacer></v-spacer>

                    <!--            This button will lead to the Page where we can filter and analyse the data-->

                    <v-btn color="primary">
                        Analyse
                    </v-btn>

                    <!--            here we have a sub menu, that can hold a list of different options or setings-->
                    <v-menu bottom left>
                        <template v-slot:activator="{ on }">
                            <v-btn icon color="primary" v-on="on">
                                <v-icon>mdi-dots-vertical</v-icon>
                            </v-btn>
                        </template>

                        <v-list>
                            <v-list-item v-for="(item, i) in menuItems" :key="i">
                                <v-list-item-title @click="dialog = true">{{ item.title }}</v-list-item-title>
                            </v-list-item>
                        </v-list>
                    </v-menu>
                </v-toolbar>
            </v-row>
            <v-row>
                <v-col cols="12" lg="12">
                    <v-data-iterator
                        :items="items"
                        :items-per-page.sync="itemsPerPage"
                        :page="page"
                        :search="search"
                        :sort-by="'id'"
                        :sort-desc="sortDesc"
                        hide-default-footer
                    >
                        <template v-slot:header>
                            <v-toolbar class="mb-1">
                                <v-text-field
                                    v-model="search"
                                    clearable
                                    flat
                                    hide-details
                                    prepend-inner-icon="mdi-magnify"
                                    label="Search"
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
                                        <span class="black--text">Items per page</span>
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

                                        <span class="mr-4 black--text"> Page {{ page }} of {{ numberOfPages }} </span>
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
                            <!--            <template>-->
                            <v-row>
                                <v-col v-for="question in props.items" :key="question.id" fluid>
                                    <!--                    <v-col v-for="question in props.PollResult.questionList" :key="question.id">-->
                                    <a :id="'Frage' + question.id"> <v-spacer></v-spacer></a>
                                    <ChoiceQuestionEvaluationWidget
                                        :key="widgetKey"
                                        :show-table="showTable"
                                        :show-diagram="showDiagram"
                                        :questionId="question.id"
                                        :question-title="question.title"
                                        :answer-possibilities="question.answerPossibilities"
                                        :data="question.data"
                                        :background-color="defaultColor"
                                        :diagram-type="defaultDiagramType"
                                    ></ChoiceQuestionEvaluationWidget>
                                    <v-spacer></v-spacer>
                                    <!--                    </v-col>-->
                                </v-col>
                            </v-row>
                        </template>
                    </v-data-iterator>
                </v-col>
            </v-row>
        </v-container>
        <v-container v-else>
            <v-card>
                <v-card-title>Der Server antwortet nicht</v-card-title>
            </v-card>
        </v-container>
    </v-container>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'
import ChoiceQuestionEvaluationWidget from '../components/ChoiceQuestionEvaluationWidget'
import visualEvaluationSettings from '../components/visualEvaluationSettings'

export default {
    name: 'BaseEvaluationPage',
    components: { AuthGate, ChoiceQuestionEvaluationWidget, visualEvaluationSettings },
    data() {
        return {
            // key that forces the diagram to update
            // (value is set on the color of the diagram and update whenever updateVisuals is called)
            widgetKey: '',
            // by default we will jump to question 1
            questionToJumpTo: '',
            linkToQuestion: '#Frage1',
            // the dialog (setting window) is closed by default
            dialog: false,
            // default settings for the visual settings, they are passed as props into the choiceQuestionEvaluationWidgets
            defaultColor: '#aaaaaa',
            defaultDiagramType: 'bar',
            showDiagram: true,
            showTable: true,
            // options that will be displayed in the sub menu
            menuItems: [{ title: 'Visuelle Einstellungen' }, { title: 'Exportieren' }],

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

            // itemsPerPageArray: [1, 2, 3],
            search: '',
            filter: {},
            sortDesc: false,
            page: 1,
            itemsPerPage: 2,
            sortBy: 'id',
        }
    },
    mounted() {
        this.initialize()
    },
    computed: {
        ...mapGetters({
            diagramData: 'evaluation/getDiagramData',
            pollName: 'evaluation/getPollName',
            isAuthenticated: 'login/isAuthenticated',
        }),

        /**
         * creates array with all the questionId in a list, not needed right now, but might come in handy
         * @returns an array with all the ids from questionList
         */
        idList() {
            const l = []
            if (this.diagramData !== undefined) {
                for (let i = 0; i < this.diagramData.length; i++) {
                    l.push(i + 1)
                }
            }
            return l
        },

        /**
         * Gives us the highest questionId
         * @returns {number} length of questionList
         */

        highestQuestionId() {
            if (this.diagramData !== undefined) {
                return this.diagramData.length
            } else {
                return 0
            }
        },

        numberOfPages() {
            return Math.ceil(this.items.length / this.itemsPerPage)
        },
        filteredKeys() {
            return this.keys.filter((key) => key !== `id`)
        },
        items() {
            return this.diagramData
        },

        itemsPerPageArray() {
            return [1, this.items.length / 2, this.items.length]
        },
    },
    methods: {
        /*

        this method is emitted by the settings window.
        Once its called we update the visual settings of all the choiceQuestionEvaluationWidgets
        the widget key is added to force the widget to actually update the color

         */
        ...mapActions({ initialize: 'evaluation/initialize' }),
        updateVisuals(showDiagram, DiagramType, DiagramColor, showTable) {
            this.showDiagram = showDiagram
            this.defaultDiagramType = DiagramType
            this.defaultColor = DiagramColor
            this.showTable = showTable
            this.dialog = false
            this.widgetKey = DiagramColor
        },

        /*
        this method creates a unique href link for a question by concatenating the String "#Frage" with its id
        and saves it in linkToQuestion. It also resets questionToJumpTo to 1.
         */

        changeLinkToQuestion() {
            this.linkToQuestion = '#Frage' + this.questionToJumpTo
            this.questionToJumpTo = 1
        },

        nextPage() {
            if (this.page + 1 <= this.numberOfPages) this.page += 1
        },
        formerPage() {
            if (this.page - 1 >= 1) this.page -= 1
        },
        updateItemsPerPage(number) {
            this.itemsPerPage = number
        },
    },
}
</script>

<style scoped></style>
