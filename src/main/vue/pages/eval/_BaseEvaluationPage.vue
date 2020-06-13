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
                    <v-card-title>{{ pollName }}</v-card-title>

                    <v-dialog v-model="dialog">
                        <!--             Here we open a setting window
        where the user can change the visual settings of every question at the same time-->
                        <v-card
                            ><visual-evaluation-settings :one-question="false" @update-all-Visuals="updateVisuals">
                            </visual-evaluation-settings
                        ></v-card>
                    </v-dialog>
                    <v-spacer></v-spacer>

                    <v-spacer></v-spacer>

                    <!--            This button will lead to the Page where we can filter and analyse the data-->

                    <v-btn :to="'/filterForm'" color="primary">
                        Analyse
                    </v-btn>

                    <!--                   title of the poll-->

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
                                            :show-table="showTable"
                                            :show-diagram="showDiagram"
                                            :question-id="question.id"
                                            :question-title="question.title"
                                            :answer-possibilities="question.answerPossibilities"
                                            :data="question.data"
                                            :calculated="question.calculated"
                                            :background-colors="defaultColors"
                                            :background-color="defaultColor"
                                            :multiple-colors="multipleColors"
                                            :diagram-type="defaultDiagramType"
                                        ></ChoiceQuestionEvaluationWidget>
                                    </div>

                                    <div v-else-if="question.type === 'text'">
                                        <textQuestionEvaluationWidget
                                            :question-i-d="question.questionID"
                                            :question-title="question.title"
                                        >
                                        </textQuestionEvaluationWidget>
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
            <!--             this is displayed, if -->
            <v-card>
                <v-card-title>Der Server antwortet nicht</v-card-title>
            </v-card>
        </v-container>
    </v-container>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import AuthGate from '../../components/AuthGate'
import ChoiceQuestionEvaluationWidget from '../../components/ChoiceQuestionEvaluationWidget'
import visualEvaluationSettings from '../../components/visualEvaluationSettings'
import TextQuestionEvaluationWidget from '../../components/TextQuestionEvaluationWidget'

export default {
    name: 'BaseEvaluationPage',
    components: {
        AuthGate,
        ChoiceQuestionEvaluationWidget,
        visualEvaluationSettings,
        TextQuestionEvaluationWidget,
    },
    data() {
        return {
            // key that forces the diagram to update
            // (value is set on the color of the diagram and update whenever updateVisuals is called)
            widgetKey: '',
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
        }
    },
    mounted() {
        this.initialize(this.$route.params.BaseEvaluationPage)
    },
    computed: {
        ...mapGetters({
            diagramData: 'evaluation/getDiagramData',
            pollName: 'evaluation/getPollName',
            isAuthenticated: 'login/isAuthenticated',
        }),

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
            for (let i = 0; i < this.items.length; i++) {
                i2[i] = {
                    answerPossibilities: this.items[i].answerPossibilities,
                    data: this.items[i].data,
                    id: this.items[i].id,
                    title: 'Frage ' + (i + 1) + ': ' + this.items[i].title,
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
    },
    methods: {
        ...mapActions({ initialize: 'evaluation/initialize' }),

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

        async forceUpdate() {
            await this.initialize(this.$route.params.BaseEvaluationPage)
            this.widgetKey += 1
        },
    },
}
</script>

<style scoped></style>
