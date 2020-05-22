<template>
    <!--    This widget provides a quick overview of the results of one question in a poll -->
    <v-card class="ma-2">
        <v-app-bar dense flat>
            <!--            the title includes the Question and its id-->
            <v-toolbar-title> Frage {{ questionId }}: {{ questionTitle }} </v-toolbar-title>

            <v-spacer></v-spacer>
            <!-- Here we have the BlockDialog that can be used to filter ans analyse this question or the entire poll-->
            <BlockDialog></BlockDialog>

            <!--            this button leads to the settings page for this specific question-->

            <v-btn icon color="primary" @click="visualSettings = !visualSettings">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>
        </v-app-bar>
        <v-container>
            <v-row v-if="visualSettings">
                <v-card>
                    <!--                    the visual settings the widget has right noe are passed as props-->
                    <visual-evaluation-settings
                        @input="onShow()"
                        :chosen-diagram="diagramType"
                        :chosen-diagram-color="backgroundColor"
                        :show-diagram="showDiagram"
                        :show-table="showTable"
                        v-on:update-Visuals="updateVisuals"
                    ></visual-evaluation-settings>
                </v-card>
            </v-row>

            <!--            here we display a visual diagram-->
            <v-row v-if="showDiagram">
                <v-col cols="12" lg="12">
                    <!--                    we check in diagramType which one the user wants-->
                    <div v-if="diagramType === 'bar'" :key="diagramKey">
                        <BarChartView :chartdata="chartdataSet" :options="options"></BarChartView>
                    </div>
                    <div v-if="diagramType === 'pie'" :key="diagramKey">
                        <PieChartView :chartdata="chartdataSet" :options="options"></PieChartView>
                    </div>
                </v-col>
            </v-row>
            <v-row> </v-row>
            <!-- here we display a table with the data-->
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
import visualEvaluationSettings from './visualEvaluationSettings'
import BarChartView from './BarChartView'
import PieChartView from './PieChartView'
import BlockDialog from './BlockDialog'

export default {
    components: { BlockDialog, BarChartView, PieChartView, visualEvaluationSettings },
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

        backgroundColor: {
            type: String,
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
        // key that forces the diagram to update (value does not have a meaning)
        diagramKey: '',
        // these options are needed to display a visual diagram, they are passed as props into that component
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
        // these aren't needed right now, might come in handy to keep the options that the widgets overrides the general design settings
        privateDiagramColor: '',
        privateDiagramType: '',
        privateShowTable: '',
        privateShowDiagram: '',

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
                h.push({ antwort: this.answerPossibilities[i], abs: this.data[i], rel: '-' })
            }
            return h
        },
        // here we compute the data for the visual diagram, writing it into a format the components can process
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
        /*

      this method is emitted by the settings window.
      Once its called we update the visual settings.

       */
        updateVisuals(showDiagram, DiagramType, DiagramColor, showTable) {
            this.showDiagram = showDiagram
            this.diagramType = DiagramType
            this.backgroundColor = DiagramColor
            this.showTable = showTable
            this.visualSettings = false
            this.$forceUpdate()
            this.diagramKey += 1
        },
    },
}
</script>

<style scoped></style>
