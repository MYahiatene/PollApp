<template>
    <v-container>
        <v-app-bar fixed app elevate-on-scroll>
            <v-toolbar-title> {{ PollResult.name }}</v-toolbar-title>

            <v-dialog v-model="dialog">
                <!--                <template v-slot:activator="{ on }">-->
                <!--                    <v-btn color="red lighten-2" dark v-on="on">-->
                <!--                        Click Me-->
                <!--                    </v-btn>-->
                <!--                </template>-->
                <v-card
                    ><visual-evaluation-settings v-on:update-Visuals="updateVisuals"> </visual-evaluation-settings
                ></v-card>
            </v-dialog>
            <v-spacer></v-spacer>

            <v-app-bar flat>
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
                <a :href="linkToQuestion" id="jump">
                    <v-btn>
                        Los
                    </v-btn>
                </a>
            </v-app-bar>

            <v-spacer></v-spacer>

            <v-btn>
                Analyse
            </v-btn>

            <v-menu bottom left>
                <template v-slot:activator="{ on }">
                    <v-btn icon color="teal" v-on="on">
                        <v-icon>mdi-dots-vertical</v-icon>
                    </v-btn>
                </template>

                <v-list>
                    <v-list-item v-for="(item, i) in menuItems" :key="i">
                        <v-list-item-title @click="dialog = true">{{ item.title }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-app-bar>

        <v-container>
            <v-row>
                <v-col cols="12" lg="3"> </v-col>

                <!--                <v-overflow-btn-->
                <!--                    v-model="questionToJumpTo"-->
                <!--                    :items="idList"-->
                <!--                    label=" "-->
                <!--                    @change=""-->
                <!--                ></v-overflow-btn>-->
            </v-row>
            <v-row>
                <v-col cols="12" fluid>
                    <div v-for="question in PollResult.questionList" :key="question.id">
                        <a :id="'Frage' + question.id"> <v-spacer></v-spacer></a>
                        <ChoiceQuestionEvaluationWidget
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
        <!--        <p>{{ chartdataSets[0].labels[0] }}</p>-->
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
            questionToJumpTo: '',
            linkToQuestion: '#Frage1',
            dialog: false,
            defaultColor: '#888888',
            defaultDiagramType: 'bar',
            showDiagram: true,
            showTable: true,
            menuItems: [{ title: 'Visuelle Einstellungen' }, { title: 'Exportieren' }],
            PollResult: {
                name: 'Mittagessen Umfrage',
                questionList: [
                    { id: 1, title: 'How are you?', answerPossibilities: ['Good', 'Bad', 'Okay'], data: [22, 6, 7] },
                    {
                        id: 2,
                        title: 'What is your gender?',
                        answerPossibilities: ['female', 'male', 'other'],
                        data: [20, 19, 1],
                    },
                    {
                        id: 3,
                        title: 'Do you like cake?',
                        answerPossibilities: ['Yes', 'Why not?', 'I prefer something else', 'Disgusting'],
                        data: [17, 8, 4, 2],
                    },
                    {
                        id: 4,
                        title: 'What is your favorite frosting?',
                        answerPossibilities: ['Chocolate', 'Vanilla'],
                        data: [17, 22],
                    },
                    {
                        id: 5,
                        title: 'When was the last time you ate cake?',
                        answerPossibilities: [
                            'Today!',
                            'I can not even remember...',
                            'I have never eaten cake in my life!',
                            'Some time during the last month.',
                        ],
                        data: [17, 22, 5, 55],
                    },
                ],
            },
            // firstchartdataSet: {
            //     labels: ['Ja', 'Nein', 'Enthaltung'],
            //     datasets: [
            //         {
            //             label: 'Do you like cake?',
            //             backgroundColor: '#888888',
            //             data: [3, 3, 14],
            //         },
            //     ],
            // },

            // chartdataSets: [
            //     {
            //         labels: ['Good', 'Bad', 'Okay'],
            //         datasets: [
            //             {
            //                 label: 'How are you?',
            //                 backgroundColor: '#111111',
            //                 data: [18, 5, 7],
            //             },
            //         ],
            //     },
            //     {
            //         labels: ['female', 'male', 'other'],
            //         datasets: [
            //             {
            //                 label: 'What is your gender?',
            //                 backgroundColor: '#444444',
            //                 data: [6, 16, 8],
            //             },
            //         ],
            //     },
            //     {
            //         labels: ['Ja', 'Nein', 'Enthaltung'],
            //         datasets: [
            //             {
            //                 label: 'Do you like cake?',
            //                 backgroundColor: '#888888',
            //                 data: [3, 3, 14],
            //             },
            //         ],
            //     },
            // ],
        }
    },
    computed: {
        idList() {
            const l = []
            for (let i = 0; i < this.PollResult.questionList.length; i++) {
                l.push(i + 1)
            }
            return l
        },

        highestQuestionId() {
            return this.PollResult.questionList.length
        },
        // chartdataSets() {
        //     const c = []
        //     for (let i = 0; i < this.PollResult.questionList; i++) {
        //         c.push({
        //             labels: this.PollResult.questionList[i].answerPossibilities,
        //             datasets: [
        //                 {
        //                     label: this.PollResult.questionList[i].title,
        //                     backgroundColor: this.defaultColor,
        //                     data: this.questionList[i].data,
        //                 },
        //             ],
        //         })
        //     }
        //
        //     return c
        // },
        // chartdataSets() {
        //     const c = []
        //     for (let i = 0; i < this.PollResult.questionList; i++) {
        //         c.push(this.firstchartdataSet)
        //     }
        //
        //     return c
        // },
        // chartdataSet() {
        //     return this.chartdataSets[0]
        // },
    },
    methods: {
        updateVisuals(showDiagram, DiagramType, DiagramColor, showTable) {
            this.showDiagram = showDiagram
            this.defaultDiagramType = DiagramType
            this.defaultColor = DiagramColor
            this.showTable = showTable
            this.dialog = false
        },

        changeLinkToQuestion() {
            this.linkToQuestion = '#Frage' + this.questionToJumpTo
            this.questionToJumpTo = 1
        },
    },
}
</script>

<style scoped></style>
