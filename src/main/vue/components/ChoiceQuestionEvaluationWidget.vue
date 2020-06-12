<template>
    <!--    This widget provides a quick overview of the results of one question in a poll -->
    <v-card class="ma-2">
        <v-app-bar dense flat>
            <!--            the title includes the Question and its id-->
            <v-toolbar-title> {{ questionTitle }} </v-toolbar-title>

            <v-spacer></v-spacer>
            <!-- Here we have the BlockDialog that can be used to filter ans analyse this question or the entire poll-->
            <custom-evaluation />

            <!--            this button leads to the settings page for this specific question-->

            <v-btn icon color="primary" @click="visualSettings = !visualSettings">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>
        </v-app-bar>
        <v-container>
            <v-row v-if="visualSettings">
                <v-col cols="12" lg="12">
                    <v-card>
                        <!--                    the visual settings the widget gets the current settings passed as props-->
                        <visual-evaluation-settings
                            :one-question="true"
                            :chosen-diagram="diagramType"
                            :chosen-diagram-colors="choppedBackgroundColors"
                            :chosen-diagram-color="backgroundColor"
                            :multiple-colors="multipleColors"
                            :show-diagram="showDiagram"
                            :show-table="showTable"
                            v-on:update-Visuals="updateVisuals"
                        ></visual-evaluation-settings>
                    </v-card>
                </v-col>
            </v-row>

            <!--            here we display a visual diagram-->
            <v-row v-if="showDiagram">
                <v-col cols="12" lg="12">
                    <!--                    we check in diagramType which one the user wants-->
                    <div v-if="diagramType === 'bar'" :key="diagramKey">
                        <BarChartView :chartdata="chartdataSet" :options="barChartOptions"></BarChartView>
                    </div>
                    <!--                     the height is set to 60% of the screen-->
                    <div v-if="diagramType === 'pie'" :key="diagramKey" style="height: 60vh;">
                        <PieChartView :chartdata="chartdataSet" :options="pieChartOptions"></PieChartView>
                    </div>
                </v-col>
            </v-row>
            <v-row> </v-row>
            <!-- here we display a table with the data-->
            <v-row>
                <v-col cols="12" lg="12">
                    <div v-if="showTable">
                        <p>Median: {{ calculated.median }}</p>
                        <v-data-table :headers="header" :items="items" hide-default-footer dense> </v-data-table></div
                ></v-col>
            </v-row>
        </v-container>
    </v-card>
</template>

<script>
import CustomEvaluation from './customEvaluation'
import BarChartView from './BarChartView'
import PieChartView from './PieChartView'
import visualEvaluationSettings from './visualEvaluationSettings'

export default {
    components: { CustomEvaluation, BarChartView, PieChartView, visualEvaluationSettings },
    // these props are past in by the parent component
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

        calculated: {
            type: Object,
        },

        backgroundColors: {
            type: Array,
        },

        backgroundColor: {
            type: String,
            default: '#aaaaaa',
        },

        multipleColors: {
            type: Boolean,
            default: false,
        },

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
        // key that forces the diagram to update
        // (value is set on the color of the diagram and update whenever updateVisuals is called)
        diagramKey: '',
        // these options are needed to display a visual diagram, they are passed as props into that component

        // bar charts dont have a legend
        barChartOptions: {
            legend: {
                display: false,
            },
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

        // pie charts dont have a grid in the background

        pieChartOptions: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                yAxes: [{ display: false }],
            },
        },

        // indicates that the setting window is closed by default
        visualSettings: false,
        // headers for the table
        header: [
            { text: 'Antwort', value: 'antwort', sortable: false },
            { text: 'Absolute Häufigkeit', value: 'abs', sortable: false },
            { text: 'Relative Häufigkeit', value: 'rel', sortable: false },
        ],
    }),

    computed: {
        // here we compute the data for the table
        items() {
            const h = []
            for (let i = 0; i < this.answerPossibilities.length; i++) {
                const percentage = Math.round(this.calculated.relative[i] * 100)
                h.push({ antwort: this.answerPossibilities[i], abs: this.data[i], rel: percentage + '%' })
            }
            return h
        },
        // here we compute the data for the visual diagram, writing it into a format the components can process.
        // We either give one color or an array of colors
        chartdataSet() {
            if (this.multipleColors) {
                return {
                    labels: this.answerPossibilities,
                    datasets: [
                        {
                            label: this.questionTitle,
                            backgroundColor: this.choppedBackgroundColors,
                            data: this.data,
                        },
                    ],
                }
            } else {
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
            }
        },
        /*

        This method creates an array of color, which length is the one of answers for this question
       */

        choppedBackgroundColors() {
            const c = []
            for (let i = 0; i < this.answerPossibilities.length; i++) {
                c[i] = this.backgroundColors[i % this.backgroundColors.length]
            }
            return c
        },
    },
    methods: {
        /*

      this method is emitted by the settings window.
      Once its called we update the visual settings.
      the diagram key is added to force the widget to actually update the color

       */
        updateVisuals(showDiagram, diagramType, diagramColors, diagramColor, multipleColors, showTable) {
            this.showDiagram = showDiagram
            this.diagramType = diagramType

            this.multipleColors = multipleColors

            this.choppedBackgroundColors = diagramColors

            this.backgroundColor = diagramColor
            this.privateBackgroundColor = diagramColor

            this.showTable = showTable

            this.visualSettings = false
            if (multipleColors) {
                for (let i = 0; i < diagramColors.length; i++) {
                    this.diagramKey = this.diagramKey + diagramColors[i]
                }
            } else {
                this.diagramKey = diagramColor
            }

            this.visualsUpdated = true
        },
    },
}
</script>

<style scoped></style>
