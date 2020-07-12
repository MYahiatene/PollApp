<template>
    <v-card dense class="ma-2 pa-2">
        <v-app-bar dense flat>
            <!--            the title includes the Question and its id-->
            <v-toolbar-title>
                {{ questionTitle }} {{ seriesPoll ? '(Umfrage : ' + currentIndex + ')' : '' }}</v-toolbar-title
            >
            <v-spacer></v-spacer>
            <div v-if="seriesPoll">
                <v-btn @click="loadLastWidget()">
                    <v-icon>mdi-arrow-left</v-icon>
                </v-btn>
                <v-btn @click="loadNextWidget()">
                    <v-icon>mdi-arrow-right</v-icon>
                </v-btn>
            </div>
            <v-spacer></v-spacer>

            <div v-if="!noAnswers">
                <v-switch class="mt-4" v-model="shortView" label="Kurzübersicht"> </v-switch>
            </div>

            <div v-else>
                Keine Antworten
            </div>
        </v-app-bar>

        <div v-if="!noAnswers">
            <!--            if shortView is active we only display a few chips that give an initial idea on how the question was ansered-->
            <v-container fluid v-if="shortView">
                <v-row justify="center">
                    <v-col cols="12">
                        <v-row justify="center" v-for="(list, index) in meanOrder" :key="index">
                            <v-chip v-for="(item, id) in list" :key="id" class="ma-1">
                                {{ item.itemName }}
                            </v-chip>
                        </v-row>
                    </v-col>
                </v-row>
            </v-container>

            <!--            else we have a more elaborate analysis-->

            <div v-else>
                <!--                first we display the span with an information box for it-->
                <v-card-subtitle>
                    <v-btn icon @click="spanExplanation = !spanExplanation"><v-icon>mdi-magnify</v-icon></v-btn>
                    Spannweite:
                    {{ span }}
                </v-card-subtitle>

                <v-card dense v-if="spanExplanation" class="ma-1 pa-3">
                    <v-btn icon @click="spanExplanation = !spanExplanation" class="mt-n2 ml-n2"
                        ><v-icon>mdi-close</v-icon></v-btn
                    >
                    <p>Hier reicht die potentielle Spannweite von 0 bis {{ answerPossibilites.length - 1 }}.</p>
                    <p>
                        Eine geringe Spannweite bedeutet, dass alle Konzepte im Mittel ähnlich bewertet wurden, sich die
                        verschiedenen Bewertungen der Teilnehmer also gegenseitig aufheben. Eine große Spannweite heißt,
                        dass sich sehr eindeutige Unterschiede in der Gewichtung gab. Eine große Spannweite bedeutet
                        also, dass sich die Teilnehmer einiger waren und der durchschnittlichen Ordnung eine größe
                        Bedeutung zugemessen werden kann.
                    </p>
                </v-card>

                <!--                for every position there is a separate v-card-->

                <v-card class="ma-1" dense v-for="(list, index) in meanOrder" :key="index">
                    <v-card-title> Position: {{ index + 1 }} </v-card-title>

                    <v-card-subtitle> relative Position: {{ list[0].meanPositionValue }} </v-card-subtitle>

                    <!--                    within we have chips for every answer possibility-->

                    <v-card-text>
                        <v-row>
                            <div v-for="(item, id) in list" :key="id">
                                <v-chip v-if="!item.show" class="ma-1" @click="item.show = !item.show">
                                    {{ item.itemName }}<v-icon>mdi-magnify</v-icon>
                                </v-chip>
                            </div>
                        </v-row>

                        <!--                        if the show attribute is true we provide an entire v-card for the item displaying additional information-->

                        <div v-for="(item, id) in list" :key="id">
                            <v-card v-if="item.show" class="pa-2 ma-2">
                                <v-card-title>
                                    {{ item.itemName }}
                                    <v-spacer></v-spacer>
                                    <v-btn icon @click="item.show = false">
                                        <v-icon>mdi-close</v-icon>
                                    </v-btn>
                                </v-card-title>

                                <v-card-text>
                                    <v-row>
                                        <v-col cols="12" lg="4">
                                            <v-card flat>
                                                <BarChartView
                                                    :key="DiagramKey"
                                                    style="height: 30vh;"
                                                    :chartdata="chartdataSets[item.itemID]"
                                                    :options="barChartOptions"
                                                ></BarChartView>
                                            </v-card>
                                        </v-col>
                                        <v-col cols="12" lg="4">
                                            <div>
                                                Wurde

                                                <div v-for="(value, key) in item.wasAtPositionNumbers" :key="key">
                                                    {{ value }} mal an Position {{ key + 1 }},
                                                </div>

                                                positioniert.
                                            </div>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        Varianz: {{ item.variance }} Standartabweichung: {{ item.standardDeviation }}
                                    </v-row>
                                </v-card-text>
                            </v-card>
                        </div>
                    </v-card-text>
                </v-card>
            </div>
        </div>
    </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
import BarChartView from './BarChartView'

export default {
    name: 'SortQuestionEvaluationWidget',
    props: {
        questionID: {
            type: Number,
            default: 0,
        },
        questionTitle: {
            type: String,
            default: 'hi',
        },
    },
    components: { BarChartView },
    data: () => ({
        answerPossibilites: [],
        chartdataSets: [],
        meanOrder: [],

        span: 0,

        // indicates if an additional window with an explanation for the meaning of span is displayed
        spanExplanation: false,
        // indicates if a short overview or a detailed analysis is displayed
        shortView: true,
        // indicates if there were no participants left who answered the question (might be caused by a very restrictive filter)
        noAnswers: false,

        // these options are needed to display a visual diagram, they are passed as props into that component
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
                            stepSize: 1,
                        },
                    },
                ],
            },
        },

        // this number is used to trigger an update in the barcharts

        DiagramKey: 0,

        index: 0,

        // data for seriesPolls:
        // stores the index of the poll that is currently displayed
        currentIndex: 0,
        // stores if we have a seriesPoll or a regular poll
        seriesPoll: false,
        // stores the number of polls (would be one for a regular poll)
        numberOfWidgets: 1,
    }),

    computed: {
        ...mapGetters({
            diagramData: 'evaluation/getDiagramData', // diagramdata.questionlist(questionID)
        }),
    },

    mounted() {
        this.computeData(0)
    },

    methods: {
        /**
         * creates data for the bar charts using the values from wasAtPositionOrder for each answerPossibility
         */
        createChartData() {
            const items = this.answerPossibilites
            for (let i = 0; i < items.length; i++) {
                this.chartdataSets.push({ labels: [], datasets: [] })
                for (let j = 0; j < items.length; j++) {
                    this.chartdataSets[i].labels.push('Position ' + (j + 1) + '')
                }
            }

            for (let j = 0; j < this.meanOrder.length; j++) {
                for (let k = 0; k < this.meanOrder[j].length; k++) {
                    console.log(this.meanOrder[j][k].itemID)
                    this.chartdataSets[this.meanOrder[j][k].itemID].datasets.push({
                        label: this.meanOrder[j][k].itemName,
                        backgroundColor: this.$vuetify.theme.currentTheme.primary,
                        data: this.meanOrder[j][k].wasAtPositionNumbers,
                    })
                }
            }
            this.DiagramKey += 1
        },

        /**
         *
         * This methods uses an index of a seriesPoll to compute all the needed data that was send in diagramData
         * @param index of the seriesPoll, if we have a regular poll this will be 0
         */

        computeData(index) {
            this.meanOrder = []
            this.answerPossibilites = []
            // check if we have a seriesPoll
            if (this.diagramData[this.questionID].meanOrder.length > 1) {
                this.seriesPoll = true
                this.numberOfWidgets = this.diagramData[this.questionID].meanOrder.length
            }
            // check if there were no answers
            if (this.diagramData[this.questionID].meanOrder[index].length === 0) {
                this.noAnswers = true
            } else {
                // copy values from store into a front end array
                for (let i = 0; i < this.diagramData[this.questionID].meanOrder[index].length; i++) {
                    this.meanOrder.push([])
                    for (let j = 0; j < this.diagramData[this.questionID].meanOrder[index][i].length; j++) {
                        const p = []
                        for (
                            let k = 0;
                            k < this.diagramData[this.questionID].meanOrder[index][i][j].wasAtPositionNumbers.length;
                            k++
                        ) {
                            p.push(this.diagramData[this.questionID].meanOrder[index][i][j].wasAtPositionNumbers[k])
                        }
                        this.meanOrder[i].push({
                            itemID: this.diagramData[this.questionID].meanOrder[index][i][j].itemID,
                            meanPositionValue: this.diagramData[this.questionID].meanOrder[index][i][j]
                                .meanPositionValue,
                            meanPosition: this.diagramData[this.questionID].meanOrder[index][i][j].meanPosition,
                            itemName: this.diagramData[this.questionID].meanOrder[index][i][j].itemName,
                            variance: this.diagramData[this.questionID].meanOrder[index][i][j].variance,
                            standardDeviation: this.diagramData[this.questionID].meanOrder[index][i][j]
                                .standardDeviation,
                            show: false,
                            wasAtPositionNumbers: p,
                        })
                    }
                }

                this.answerPossibilites = this.diagramData[this.questionID].answerPossibilities

                this.span =
                    -this.meanOrder[0][0].meanPositionValue +
                    this.meanOrder[this.meanOrder.length - 1][0].meanPositionValue
                this.index = index

                this.createChartData()
            }
        },

        /**
         *
         * This method checks if there is a next Widget to load,
         * so it checks if there is another poll that comes before the current one.
         * If we have a regular poll, that will never be the case
         *
         * if we do have a next Widget though the current index is increased and computeData is called to get the new values
         */

        loadNextWidget() {
            if (!(this.currentIndex === this.numberOfWidgets - 1)) {
                this.currentIndex++
                this.computeData(this.currentIndex)
            }
        },

        /**
         * This method checks if there is a former Widget to load,
         * so it checks if there is another poll that comes after the current one.
         * If we have a regular poll, that will never be the case
         *
         * if we do have a former Widget though the current index is decreased and computeData is called to get the new values
         */

        loadLastWidget() {
            if (!(this.currentIndex === 0)) {
                this.currentIndex--
                this.computeData(this.currentIndex)
            }
        },
    },
}
</script>

<style scoped></style>
