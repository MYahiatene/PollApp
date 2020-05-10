<template>
    <v-card class="ma-2">
        <v-app-bar dense flat>
            <v-toolbar-title> Frage {{ questionId }}: {{ questionTitle }} </v-toolbar-title>

            <v-spacer></v-spacer>

            <v-btn> Analyse </v-btn>

            <v-btn icon color="teal" @click="visualSettings = !visualSettings">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>
        </v-app-bar>

        <v-container>
            <v-row v-if="visualSettings">
                <v-card>
                    <visual-evaluation-settings v-on:update-Visuals="updateVisuals"></visual-evaluation-settings>
                </v-card>
            </v-row>
            <v-row v-if="showDiagram">
                <v-col cols="12" lg="12">
                    <div v-if="diagramType === 'bar'">
                        <BarChartView :chartdata="chartdataSet" :options="options"></BarChartView>
                    </div>
                    <div v-if="diagramType === 'pie'">
                        <PieChartView :chartdata="chartdataSet" :options="options"></PieChartView>
                    </div>
                </v-col>
            </v-row>
            <v-row> </v-row>

            <v-row>
                <v-col cols="12" lg="12">
                    <div v-if="showTable">
                        <v-data-table :headers="header" :items="items" hide-default-footer dense> </v-data-table></div
                ></v-col>
            </v-row>
        </v-container>
    </v-card>
</template>

<script>
import visualEvaluationSettings from '../pages/visualEvaluationSettings'
import BarChartView from './BarChartView'
import PieChartView from './PieChartView'

export default {
    components: { BarChartView, PieChartView, visualEvaluationSettings },
    props: {
        diagramType: {
            type: String,
        },
        questionId: {
            type: Number,
        },
        questionTitle: {
            type: String,
        },
        answerPossibilities: {
            type: Array,
        },
        data: {
            type: Array,
        },

        backgroundColor: {
            type: String,
        },

        // chartdataSet: {
        //     labels: {
        //         type: Array,
        //     },
        //     datasets: [
        //         {
        //             label: { type: String },
        //             backgroundColor: {
        //                 type: String,
        //             },
        //             data: {
        //                 type: Array,
        //             },
        //         },
        //     ],
        // },

        showTable: {
            type: Boolean,
            default: true,
        },
        showDiagram: {
            type: Boolean,
            default: true,
        },
    },
    name: 'ChoiceQuestionEvaluationWidget',
    data: () => ({
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

        privateDiagramColor: '',
        privateDiagramType: '',
        privateShowTable: '',
        privateShowDiagram: '',
        visualSettings: false,

        header: [
            { text: 'Antwort', value: 'antwort', sortable: false },
            { text: 'Absolute Häufigkeit', value: 'abs', sortable: false },
            { text: 'Relative Häufigkeit', value: 'rel', sortable: false },
        ],
    }),

    computed: {
        items() {
            const h = []
            for (let i = 0; i < this.answerPossibilities.length; i++) {
                h.push({ antwort: this.answerPossibilities[i], abs: this.data[i], rel: '-' })
            }
            return h
        },

        chartdataSet() {
            return {
                labels: this.answerPossibilities,
                datasets: [
                    {
                        label: this.questionTitle,
                        backgroundColor: this.backgroundColor,
                        data: this.data,
                    },
                ],
            }
        },
    },
    methods: {
        updateVisuals(showDiagram, DiagramType, DiagramColor, showTable) {
            this.showDiagram = showDiagram
            this.diagramType = DiagramType
            this.backgroundColor = DiagramColor
            this.showTable = showTable
            this.visualSettings = false
        },
    },
}
</script>

<style scoped></style>
