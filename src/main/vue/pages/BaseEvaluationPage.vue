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
                <v-col cols="12" lg="2">
                    <!-- here we have a search component, that allows the user to jump to a specific question in the poll-->
                    <v-card class="pa-1">
                        <!--                 oninput: When the user types a number that is higher that the highest question Id or below zero,
        the input will be deleted as it is invalid-->
                        <v-text-field
                            id="startValue"
                            v-model="questionToJumpTo"
                            :value="highestQuestionId"
                            prefix="Springe zu Frage: "
                            type="number"
                            oninput="validity.valid||(value='')"
                            min="1"
                            :max="diagramData.length"
                            class="shrink"
                            @input="changeLinkToQuestion()"
                        ></v-text-field>
                        <v-spacer></v-spacer>
                        <!--                After pressing this button we will jump to he href that is associated with the questionId we have entered -->
                        <a id="jump" :href="linkToQuestion" style="text-decoration: none;">
                            <v-btn color="info">
                                Los
                            </v-btn>
                        </a>
                    </v-card>
                </v-col>
                <v-col cols="12" lg="8" fluid>
                    <div v-for="question in diagramData" :key="question.id">
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
                    </div>
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
            // this key does not carry a meaning it simply exists so we can force the widget to rerender
            widgetKey: '0',
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
        }
    },
    mounted() {
        this.initialize()
    },
    computed: {
        /**
         * creates array with all the questionId in a list, not needed right now, but might come in handy
         * @returns an array with all the ids from questionList
         */
        ...mapGetters({
            diagramData: 'evaluation/getDiagramData',
            pollName: 'evaluation/getPollName',
            isAuthenticated: 'login/isAuthenticated',
        }),

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
    },
    methods: {
        /*

        this method is emitted by the settings window.
        Once its called we update the visual settings of all the choiceQuestionEvaluationWidgets

         */
        ...mapActions({ initialize: 'evaluation/initialize' }),
        updateVisuals(showDiagram, DiagramType, DiagramColor, showTable) {
            this.showDiagram = showDiagram
            this.defaultDiagramType = DiagramType
            this.defaultColor = DiagramColor
            this.showTable = showTable
            this.dialog = false
            this.$forceUpdate()
            this.widgetKey += 1
            console.log(this.widgetKey)
        },

        /*
        this method creates a unique href link for a question by concatenating the String "#Frage" with its id
        and saves it in linkToQuestion. It also resets questionToJumpTo to 1.
         */

        changeLinkToQuestion() {
            this.linkToQuestion = '#Frage' + this.questionToJumpTo
            this.questionToJumpTo = 1
        },
    },
}
</script>

<style scoped></style>
