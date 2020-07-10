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

            <div v-else>
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

                <v-card class="ma-1" dense v-for="(list, index) in meanOrder" :key="index">
                    <v-card-title> Position: {{ index + 1 }} </v-card-title>

                    <v-card-subtitle> relative Position: {{ list[0].meanPositionValue }} </v-card-subtitle>

                    <v-card-text>
                        <v-row>
                            <div v-for="(item, id) in list" :key="id">
                                <v-chip v-if="!item.show" class="ma-1" @click="item.show = !item.show">
                                    {{ item.itemName }}<v-icon>mdi-magnify</v-icon>
                                </v-chip>
                            </div>
                        </v-row>

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
        // sortData: {
        //     answerPossibilities: ['Sympathie', 'Kompetenz', 'Höflichkeit', 'Pünktlichkeit', 'Zuverlässigkeit'],
        //     answerList: [
        //         [1, 0, 2, 3, 4],
        //         [0, 1, 2, 3, 4],
        //         [1, 0, 3, 2, 4],
        //         [0, 1, 2, 3, 4],
        //     ],
        // },

        answerPossibilites: [],

        arrayOfValues: [],

        meanOrder: [],

        span: 0,

        spanExplanation: false,
        shortView: true,

        noAnswers: false,

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
                            stepSize: 1,
                        },
                    },
                ],
            },
        },

        DiagramKey: 0,

        chartdataSets: [],

        chartdataSets1: [
            {
                labels: ['g', 'y', 'h'],
                datasets: [
                    {
                        label: 'bla',
                        backgroundColor: '#344444',
                        data: [1, 2, 3],
                    },
                ],
            },
            {
                labels: ['g', 'y', 'h'],
                datasets: [
                    {
                        label: 'blo',
                        backgroundColor: '#344444',
                        data: [1, 2, 3],
                    },
                ],
            },
            {
                labels: ['g', 'y', 'h'],
                datasets: [
                    {
                        label: 'blu',
                        backgroundColor: '#344444',
                        data: [1, 2, 3],
                    },
                ],
            },
        ],
        index: 0,
        currentIndex: 0,
        seriesPoll: false,
        numberOfWidgets: 1,
        diagramDataTest: [
            {
                meanOrder: [
                    [
                        [
                            {
                                itemID: 0,
                                meanPositionValue: 0,
                                meanPosition: 0,
                                itemName: 'a',
                                variance: 2,
                                standardDeviation: 2,
                                show: false,
                                wasAtPositionNumbers: [2, 0, 0],
                            },
                        ],
                        [
                            {
                                itemID: 1,
                                meanPositionValue: 2,
                                meanPosition: 2,
                                itemName: 'b',
                                variance: 2,
                                standardDeviation: 2,
                                show: false,
                                wasAtPositionNumbers: [0, 0, 2],
                            },
                        ],
                    ],
                    [
                        [
                            {
                                itemID: 0,
                                meanPositionValue: 0,
                                meanPosition: 0,
                                itemName: 'b',
                                variance: 2,
                                standardDeviation: 2,
                                show: false,
                                wasAtPositionNumbers: [2, 0, 0],
                            },
                        ],
                        [
                            {
                                itemID: 1,
                                meanPositionValue: 2,
                                meanPosition: 2,
                                itemName: 'a',
                                variance: 2,
                                standardDeviation: 2,
                                show: false,
                                wasAtPositionNumbers: [0, 0, 2],
                            },
                        ],
                    ],
                ],
                answerPossibilities: ['a', 'b'],
            },
        ],
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
        createChartData() {
            const items = this.answerPossibilites
            console.log(this.answerPossibilites)
            console.log(items)
            for (let i = 0; i < items.length; i++) {
                this.chartdataSets.push({ labels: [], datasets: [] })
                for (let j = 0; j < items.length; j++) {
                    this.chartdataSets[i].labels.push('Position ' + (j + 1) + '')
                }
            }
            console.log('chartData')
            console.log(this.chartdataSets)

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

        computeData(index) {
            this.meanOrder = []
            this.answerPossibilites = []
            if (this.diagramData[this.questionID].meanOrder.length > 1) {
                this.seriesPoll = true
                this.numberOfWidgets = this.diagramData[this.questionID].meanOrder.length
            }
            console.log('computeData')
            console.log(index)
            console.log(this.diagramData[this.questionID].meanOrder)

            if (this.diagramData[this.questionID].meanOrder[index].length === 0) {
                this.noAnswers = true
            } else {
                console.log(this.diagramData[this.questionID].meanOrder[index].length)
                for (let i = 0; i < this.diagramData[this.questionID].meanOrder[index].length; i++) {
                    console.log(this.diagramData[this.questionID].meanOrder[index][i])
                    this.meanOrder.push([])
                    for (let j = 0; j < this.diagramData[this.questionID].meanOrder[index][i].length; j++) {
                        console.log(i)
                        console.log(j)
                        console.log(this.diagramData[this.questionID].meanOrder[index][i][j])
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
                            wasAtPositionNumbers: this.diagramData[this.questionID].meanOrder[index][i][j]
                                .wasAtPositionNumbers,
                        })
                    }
                }

                console.log(this.meanOrder)
                this.answerPossibilites = this.diagramData[this.questionID].answerPossibilities

                this.span =
                    -this.meanOrder[0][0].meanPositionValue +
                    this.meanOrder[this.meanOrder.length - 1][0].meanPositionValue
                this.index = index

                this.createChartData()
            }
        },

        loadNextWidget() {
            console.log('loadNext')
            if (!(this.currentIndex === this.numberOfWidgets - 1)) {
                console.log('true')
                this.currentIndex++
                this.computeData(this.currentIndex)
            }
        },

        loadLastWidget() {
            console.log('loadLast')
            if (!(this.currentIndex === 0)) {
                this.currentIndex--
                this.computeData(this.currentIndex)
            }
        },
    },
}
</script>

<style scoped></style>
