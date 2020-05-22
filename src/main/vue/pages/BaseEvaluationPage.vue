<template>
    <!--     This page is called whenever you want to see the Evaluation of a survey.
It provides a first overview of the result by displaying a table as well as a diagram for each separate question.
Thusly the user can easily get a first impression on the results.
-->
    <v-container>
        <!--        this is the real content of the page, a list of ChoiceQuestionEvaluationWidgets,
that each display a basic evaluation of one specific question-->
        <v-container>
            <v-row>
                <!--        the app bar is used to navigate the page.
   All the buttons here apply to the entire poll, nit one individual question-->
                <v-toolbar>
                    <v-card-title>{{ PollResult.name }}</v-card-title>

                    <v-dialog v-model="dialog">
                        <!--             Here we open a setting window
        where the user can change the visual settings of every question at the same time-->
                        <v-card
                            ><visual-evaluation-settings v-on:update-Visuals="updateVisuals">
                            </visual-evaluation-settings
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
                            :value="highestQuestionId"
                            v-model="questionToJumpTo"
                            prefix="Springe zu Frage: "
                            type="number"
                            @input="changeLinkToQuestion()"
                            oninput="validity.valid||(value='')"
                            min="1"
                            :max="PollResult.questionList.length"
                            class="shrink"
                        ></v-text-field>
                        <v-spacer></v-spacer>
                        <!--                After pressing this button we will jump to he href that is associated with the questionId we have entered -->
                        <a :href="linkToQuestion" style="text-decoration: none;" id="jump">
                            <v-btn color="info">
                                Los
                            </v-btn>
                        </a>
                    </v-card>
                </v-col>
                <v-col cols="12" lg="8" fluid>
                    <div v-for="question in PollResult.questionList" :key="question.id">
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
    </v-container>
</template>

<script>
import ChoiceQuestionEvaluationWidget from '../components/ChoiceQuestionEvaluationWidget'
import visualEvaluationSettings from '../components/visualEvaluationSettings'

export default {
    name: 'BaseEvaluationPage',
    components: { ChoiceQuestionEvaluationWidget, visualEvaluationSettings },
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
        /**
         * creates array with all the questionId in a list, not needed right now, but might come in handy
         * @returns an array with all the ids from questionList
         */

        idList() {
            const l = []
            for (let i = 0; i < this.PollResult.questionList.length; i++) {
                l.push(i + 1)
            }
            return l
        },

        /**
         * Gives us the highest questionId
         * @returns {number} length of questionList
         */

        highestQuestionId() {
            return this.PollResult.questionList.length
        },
    },
    methods: {
        /*

        this method is emitted by the settings window.
        Once its called we update the visual settings of all the choiceQuestionEvaluationWidgets

         */
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
