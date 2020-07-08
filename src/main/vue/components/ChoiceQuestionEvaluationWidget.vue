<template>
    <!--    This widget provides a quick overview of the results of one question in a poll -->
    <v-card class="ma-2">
        <v-app-bar dense flat>
            <!--            the title includes the Question and its id-->
            <v-toolbar-title> {{ questionTitle }} </v-toolbar-title>

            <v-spacer></v-spacer>
            <!--            this button leads to the settings page for this specific question-->

            <div v-if="(calculated.relative[0] === 'NaN')">
                Keine Antworten
            </div>

            <div v-if="!(calculated.relative[0] === 'NaN')">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn
                            icon
                            :color="relativ ? 'accent' : 'primary'"
                            v-bind="attrs"
                            v-on="on"
                            @click=";(relativ = !relativ), (diagramKey += 1)"
                        >
                            <v-icon>
                                %
                            </v-icon>
                        </v-btn>
                    </template>
                    <span>Relative bzw. Absolute Häufigkeit umschalten</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn
                            icon
                            :color="cumulated ? 'accent' : 'primary'"
                            v-bind="attrs"
                            v-on="on"
                            @click=";(cumulated = !cumulated), (diagramKey += 1)"
                        >
                            <v-icon> mdi-sigma</v-icon>
                        </v-btn>
                    </template>
                    <span>Kumulierte Häufigkeit an- und ausschalten</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn icon color="primary" v-bind="attrs" v-on="on" @click="visualSettings = !visualSettings">
                            <v-icon> mdi-brush </v-icon>
                        </v-btn>
                    </template>
                    <span>Digrammfarben anpassen</span>
                </v-tooltip>
            </div>
        </v-app-bar>

        <div v-if="!(calculated.relative[0] === 'NaN')">
            <v-container>
                <v-row v-if="visualSettings">
                    <v-col cols="12" lg="12">
                        <v-card>
                            <!--                    the visual settings the widget gets the current settings passed as props-->
                            <visual-evaluation-settings
                                :question-id="questionId"
                                :change-default="false"
                                @done=";(diagramKey += 1), (visualSettings = false)"
                            ></visual-evaluation-settings>
                        </v-card>
                    </v-col>
                </v-row>

                <!--            here we display a visual diagram-->
                <v-row v-if="showDiagram">
                    <v-col cols="12" lg="12">
                        <!--                    we check in diagramType which one the user wants-->
                        <div v-if="diagramType === 'bar'">
                            <BarChartView
                                :chartdata="chartdataSet"
                                :options="barChartOptions"
                                :key="diagramKey"
                            ></BarChartView>
                        </div>
                        <!--                     the height is set to 60% of the screen-->
                        <div v-else-if="diagramType === 'pie'" style="height: 60vh;">
                            <PieChartView
                                :chartdata="chartdataSet"
                                :options="pieChartOptions"
                                :key="diagramKey"
                            ></PieChartView>
                        </div>
                    </v-col>
                </v-row>
                <v-row> </v-row>
                <!-- here we display a table with the data-->

                <v-row>
                    <v-col cols="3">
                        <v-chip :color="this.$vuetify.theme.currentTheme.info">Median: {{ calculated.median }}</v-chip>
                    </v-col>
                    <v-col cols="3">
                        <v-chip :color="this.$vuetify.theme.currentTheme.info">Modus: {{ calculated.mode }}</v-chip>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" lg="12">
                        <div v-if="showTable">
                            <v-data-table :headers="header" :items="items" hide-default-footer dense> </v-data-table>
                        </div>
                    </v-col>
                </v-row>
            </v-container>
        </div>
    </v-card>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import BarChartView from './BarChartView'
import PieChartView from './PieChartView'
import visualEvaluationSettings from './visualEvaluationSettings'
export default {
    components: { BarChartView, PieChartView, visualEvaluationSettings },
    // these props are past in by the parent component
    props: {
        questionId: {
            type: Number,
        },
        questionTitle: {
            type: String,
        },
        answerPossibilities: {
            type: Array,
        },
        dataInput: {
            type: Array,
        },
        calculated: {
            type: Object,
        },
    },
    name: 'ChoiceQuestionEvaluationWidget',
    data: () => ({
        // key that forces the diagram to update
        // (value is set on the color of the diagram and update whenever updateVisuals is called)
        diagramKey: 0,
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
                            callback: (value, index, values) => {
                                if (value % 1 === 0) {
                                    return value
                                }
                                return null
                            },
                        },
                    },
                ],
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
        ...mapGetters({
            getFormat: 'evaluation/getDiagramFormat',
            getDiagramOption: 'evaluation/getDiagramOption',
        }),
        cumulated: {
            get() {
                return this.getDiagramOption(this.questionId).cumulated
            },
            set(value) {
                this.setDiagramOption({
                    questionId: this.questionId,
                    cumulated: value,
                    relativ: this.relativ,
                })
            },
        },
        relativ: {
            get() {
                return this.getDiagramOption(this.questionId).relativ
            },
            set(value) {
                this.setDiagramOption({
                    questionId: this.questionId,
                    cumulated: this.cumulated,
                    relativ: value,
                })
            },
        },
        chartData() {
            console.log('chartData()')
            let baseData = []
            if (this.relativ) {
                for (let i = 0; i < this.calculated.relative.length; i++) {
                    baseData.push(this.calculated.relative[i] * 100)
                }
            } else {
                baseData = this.dataInput
            }
            const data = []
            if (baseData.length !== 0) {
                data.push(baseData[0])
                for (let i = 1; i < baseData.length; i++) {
                    data.push((this.cumulated ? data[i - 1] : 0) + baseData[i])
                }
            }
            return data
        },
        diagramType() {
            const dt = this.getFormat(this.questionId).diagramType
            console.log(dt)
            return dt
        },
        backgroundColors() {
            console.log('backgroundColor()')
            const bcs = this.getFormat(this.questionId).backgroundColors
            console.log(bcs)
            return bcs
        },
        backgroundColor() {
            console.log('backgroundColor()')
            const bc = this.getFormat(this.questionId).backgroundColor
            console.log(bc)
            return bc
        },
        multipleColors() {
            return this.getFormat(this.questionId).multipleColors
        },
        showTable() {
            return this.getFormat(this.questionId).showTable
        },
        showDiagram() {
            return this.getFormat(this.questionId).showDiagram
        },
        items() {
            const h = []
            for (let i = 0; i < this.answerPossibilities.length; i++) {
                const percentage = Math.round(this.calculated.relative[i] * 100)
                h.push({ antwort: this.answerPossibilities[i], abs: this.dataInput[i], rel: percentage + '%' })
            }
            return h
        },
        // here we compute the data for the visual diagram, writing it into a format the components can process.
        // We either give one color or an array of colors
        chartdataSet() {
            console.log('chartdataSet()')
            const data = {
                labels: this.answerPossibilities,
                datasets: [
                    {
                        label: this.questionTitle,
                        backgroundColor: this.multipleColors ? this.choppedBackgroundColors : this.backgroundColor,
                        data: this.chartData,
                    },
                ],
            }
            console.log(data)
            return data
        },
        /*

        This method creates an array of color, which length is the one of answers for this question
       */

        choppedBackgroundColors() {
            console.log('choppedBackgroundColors()')
            const c = []
            for (let i = 0; i < this.answerPossibilities.length; i++) {
                c[i] = this.backgroundColors[i % this.backgroundColors.length]
            }
            return c
        },

        // pie charts dont have a grid in the background

        pieChartOptions() {
            return {
                responsive: true,
                maintainAspectRatio: false,
                segmentShowStroke: false,
                elements: {
                    arc: {
                        borderColor: this.$vuetify.theme.currentTheme.vCardColor,
                    },
                },
                scales: {
                    yAxes: [{ display: false }],
                },
            }
        },
    },
    methods: {
        /*

      this method is emitted by the settings window.
      Once its called we update the visual settings.
      the diagram key is added to force the widget to actually update the color

       */
        ...mapMutations({
            setDiagramOption: 'evaluation/setDiagramOption',
        }),

        /* updateVisuals(showDiagram, diagramType, diagramColors, diagramColor, multipleColors, showTable) {
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
        }, */
    },
}
</script>

<style scoped></style>
