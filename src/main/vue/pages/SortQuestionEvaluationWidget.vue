<template>
    <v-card dense class="ma-2 pa-2">
        <v-app-bar dense flat>
            <!--            the title includes the Question and its id-->
            <v-toolbar-title> {{ questionTitle }} </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-switch class="mt-4" v-model="shortView" label="Kurzübersicht"> </v-switch>
        </v-app-bar>

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
                <v-btn icon @click="spanExplanation = !spanExplanation"><v-icon>mdi-magnify</v-icon></v-btn> Spannweite:
                {{ span }}
            </v-card-subtitle>

            <v-card dense v-if="spanExplanation" class="ma-1 pa-1">
                <v-btn icon @click="spanExplanation = !spanExplanation"><v-icon>mdi-close</v-icon></v-btn>
                <p>Hier reicht die potentielle Spannweite von 0 bis {{ sortData.answerPossibilities.length - 1 }}.</p>
                <p>
                    Eine geringe Spannweite bedeutet, dass alle Konzepte im Mittel ähnlich bewertet wurden, sich die
                    verschiedenen Bewertungen der Teilnehmer also gegenseitig aufheben. Eine große Spannweite heißt,
                    dass sich sehr eindeutige Unterschiede in der Gewichtung gab. Eine große Spannweite bedeutet also,
                    dass sich die Teilnehmer einiger waren und der durchschnittlichen Ordnung eine größe Bedeutung
                    zugemessen werden kann.
                </p>
            </v-card>

            <v-card class="ma-1" dense v-for="(list, index) in meanOrder" :key="index">
                <v-card-title> Position: {{ index + 1 }} </v-card-title>

                <v-card-subtitle> relative Position: {{ list[0].meanPositionValue }} </v-card-subtitle>

                <v-card-text>
                    <v-row>
                        <div v-for="(item, id) in list" :key="id">
                            <v-chip v-if="!item.show" class="ma-1" @click="item.show = !item.show">
                                {{ item.itemName }} <v-icon>mdi-magnify</v-icon>
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
    </v-card>
</template>

<script>
import BarChartView from '../components/BarChartView'

export default {
    name: 'SortQuestionEvaluationWidget',
    components: { BarChartView },
    data: () => ({
        sortData: {
            answerPossibilities: ['Sympathie', 'Kompetenz', 'Höflichkeit', 'Pünktlichkeit', 'Zuverlässigkeit'],
            answerList: [
                [1, 0, 2, 3, 4],
                [0, 1, 2, 3, 4],
                [1, 0, 3, 2, 4],
                [0, 1, 2, 3, 4],
            ],
        },

        arrayOfValues: [],

        meanOrder: [],

        span: 0,

        spanExplanation: false,
        shortView: true,

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
    }),

    computed: {},

    mounted() {
        this.computeData()
        this.createChartData()
    },

    methods: {
        createChartData() {
            for (let i = 0; i < this.arrayOfValues.length; i++) {
                this.chartdataSets.push({ labels: [], datasets: [] })
                for (let j = 0; j < this.sortData.answerPossibilities.length; j++) {
                    this.chartdataSets[i].labels.push('Position ' + (j + 1) + '')
                }
                this.chartdataSets[i].datasets.push({
                    label: this.arrayOfValues[i].itemName,
                    backgroundColor: this.$vuetify.theme.currentTheme.primary,
                    data: this.arrayOfValues[i].wasAtPositionNumbers,
                })
            }
        },

        computeData() {
            if (this.sortData.answerPossibilities.length !== this.sortData.answerList[0].length) {
                console.log('Fehlerhafte Daten in sortData')
            }
            // initializing a array that has size nxn with n = number of items

            for (let i = 0; i < this.sortData.answerList[0].length; i++) {
                this.arrayOfValues.push({
                    itemID: i,
                    itemName: this.sortData.answerPossibilities[i],
                    wasAtPositionNumbers: [],
                    meanPositionValue: 0,
                    meanPosition: -1,
                    twin: -1,
                    show: false,
                    variance: 0,
                    standardDeviation: 0,
                })
                for (let j = 0; j < this.sortData.answerList[0].length; j++) {
                    this.arrayOfValues[i].wasAtPositionNumbers.push(0)
                }
            }

            console.log(this.arrayOfValues)

            // in this array we store for each item (i) and each position (j) how many times the item was placed in that position

            for (let i = 0; i < this.sortData.answerList.length; i++) {
                for (let j = 0; j < this.sortData.answerList[i].length; j++) {
                    this.arrayOfValues[this.sortData.answerList[i][j]].wasAtPositionNumbers[j] += 1
                }
            }

            // Now we compute the mean order.

            // every item gets a mean position value computed from the number it was placed on a position times the index of that position

            for (let i = 0; i < this.arrayOfValues.length; i++) {
                for (let j = 0; j < this.arrayOfValues[i].wasAtPositionNumbers.length; j++) {
                    this.arrayOfValues[i].meanPositionValue += this.arrayOfValues[i].wasAtPositionNumbers[j] * (j + 1)
                }

                this.arrayOfValues[i].meanPositionValue =
                    this.arrayOfValues[i].meanPositionValue / this.sortData.answerList.length
            }

            // we sort the items according to their mean position values and save the order in orderedItems

            const orderedItems = []

            let lowestMeanPositionValue = this.arrayOfValues.length * this.arrayOfValues.length
            let firstItem = this.arrayOfValues.length + 1

            for (let j = 0; j < this.arrayOfValues.length; j++) {
                for (let i = 0; i < this.arrayOfValues.length; i++) {
                    if (this.arrayOfValues[i].meanPosition === -1) {
                        if (lowestMeanPositionValue >= this.arrayOfValues[i].meanPositionValue) {
                            if (lowestMeanPositionValue === this.arrayOfValues[i].meanPositionValue) {
                                if (firstItem !== i) {
                                    console.log('item ' + i + ' is duplicate to ' + firstItem)
                                    this.arrayOfValues[i].twin = firstItem
                                }
                            }
                            lowestMeanPositionValue = this.arrayOfValues[i].meanPositionValue
                            firstItem = i
                            console.log('i: ' + i)
                            console.log('first: ' + firstItem)
                        }
                    }
                }

                console.log('first ' + firstItem + 'position ' + j)

                this.arrayOfValues[firstItem].meanPosition = j
                orderedItems[j] = firstItem
                lowestMeanPositionValue = this.arrayOfValues.length + 1
            }

            // Now we compute the variance and standard deviation of the wasAtPositionNumbers

            for (let i = 0; i < this.arrayOfValues.length; i++) {
                for (let j = 0; j < this.arrayOfValues[i].wasAtPositionNumbers.length; j++) {
                    this.arrayOfValues[i].variance +=
                        this.arrayOfValues[i].wasAtPositionNumbers[j] *
                        (j + 1 - this.arrayOfValues[i].meanPositionValue) ** 2
                }

                this.arrayOfValues[i].variance = this.arrayOfValues[i].variance / this.sortData.answerList.length

                this.arrayOfValues[i].standardDeviation = Math.sqrt(this.arrayOfValues[i].variance)
            }

            // Now we build the meanOrder Array by pushing an Array of items for each position

            let position = 0
            for (let i = 0; i < orderedItems.length; i++) {
                this.meanOrder.push([])
                let twinsLeft = true
                let currentItem = this.arrayOfValues[orderedItems[i]]
                console.log('arrayOfValues[orderedItems[i] with i as ' + i + 'is')
                console.log(currentItem)

                while (twinsLeft) {
                    this.meanOrder[position].push(currentItem)

                    console.log('added item ' + currentItem.itemID + 'at position :' + position)
                    if (currentItem.twin === -1) {
                        console.log('no twins for ' + i)
                        twinsLeft = false
                    } else {
                        currentItem = this.arrayOfValues[currentItem.twin]
                        i++
                    }
                }
                position++
            }

            console.log(this.arrayOfValues)
            console.log('ordered')
            console.log(orderedItems)
            console.log('meanOrder: ')
            console.log(this.meanOrder)

            this.span =
                -this.meanOrder[0][0].meanPositionValue + this.meanOrder[this.meanOrder.length - 1][0].meanPositionValue
        },
    },
}
</script>

<style scoped></style>
