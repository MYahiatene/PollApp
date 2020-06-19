<template>
    <v-card class="ma-2">
        <v-app-bar dense flat>
            <!--            the title includes the Question and its id-->
            <v-toolbar-title> {{ questionTitle }} </v-toolbar-title>
        </v-app-bar>

        <p></p>

        <v-col cols="12" lg="12">
            <v-data-table :headers="header" :items="items" hide-default-footer dense> </v-data-table>
        </v-col>
    </v-card>
</template>

<script>
export default {
    name: 'SortQuestionEvaluationWidget',
    data: () => ({
        sortData: {
            answerList: [
                [2, 1, 0, 3],
                [2, 3, 1, 0],
                [2, 3, 1, 0],
            ],
        },

        meanOrder: [2, 3, 0, 1],
        mostCommonOrder: [0, 2, 3, 1],

        header: [
            { text: 'Element', value: 'antwort', sortable: false },
            { text: 'Position 1', value: 'antwort', sortable: false },
            { text: 'Position 2', value: 'abs', sortable: false },
            { text: 'Position 3', value: 'rel', sortable: false },
        ],
    }),

    computed: {},

    mounted() {
        this.computeData()
    },

    methods: {
        computeData() {
            // initializing a array that has size nxn with n = number of items
            const arrayOfValues = []
            for (let i = 0; i < this.sortData.answerList[0].length; i++) {
                arrayOfValues.push({ itemID: i, wasAtPositionNumbers: [], meanPositionValue: 0, meanPosition: -1 })
                for (let j = 0; j < this.sortData.answerList[0].length; j++) {
                    arrayOfValues[i].wasAtPositionNumbers.push(0)
                }
            }

            console.log(arrayOfValues)

            // in this array we store for each item (i) and each position (j) how many times the item was placed in that position

            for (let i = 0; i < this.sortData.answerList.length; i++) {
                for (let j = 0; j < this.sortData.answerList[i].length; j++) {
                    arrayOfValues[this.sortData.answerList[i][j]].wasAtPositionNumbers[j] += 1
                }
            }

            // Now we compute the mean order.

            // every item gets a mean position value computed from the number it was placed on a position times the index of thas position

            for (let i = 0; i < arrayOfValues.length; i++) {
                for (let j = 0; j < arrayOfValues[i].wasAtPositionNumbers.length; j++) {
                    arrayOfValues[i].meanPositionValue += arrayOfValues[i].wasAtPositionNumbers[j] * (j + 1)
                }

                arrayOfValues[i].meanPositionValue =
                    arrayOfValues[i].meanPositionValue / this.sortData.answerList.length
            }

            // we sort the items according to their mean position values

            let lowestMeanPositionValue = arrayOfValues.length * arrayOfValues.length
            let firstItem = arrayOfValues.length + 1

            for (let j = 0; j < arrayOfValues.length; j++) {
                for (let i = 0; i < arrayOfValues.length; i++) {
                    if (arrayOfValues[i].meanPosition === -1) {
                        if (lowestMeanPositionValue > arrayOfValues[i].meanPositionValue) {
                            lowestMeanPositionValue = arrayOfValues[i].meanPositionValue
                            firstItem = i
                            console.log('i: ' + i)
                            console.log('first: ' + firstItem)
                        }

                        if (lowestMeanPositionValue === arrayOfValues[i].meanPositionValue) {
                            if (firstItem !== i) {
                                console.log('item ' + i + ' is duplicate to ' + firstItem)
                            }
                        }
                    }
                }

                console.log('first ' + firstItem + 'position ' + j)

                arrayOfValues[firstItem].meanPosition = j
                lowestMeanPositionValue = arrayOfValues.length * arrayOfValues.length
            }

            console.log(arrayOfValues)
        },
    },
}
</script>

<style scoped></style>
