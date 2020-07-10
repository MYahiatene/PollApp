<template>
    <!--     This widget allows a user to adjust the visual settings of a choiceQuestionEvaluationWidget
Once "Anwenden" is clicked a method named updateVisuals is called that can be implemented by the parent component
it passes the attributes:
 showDiagram (indicates if a visual diagram should be displayed)
 diagramType
 diagramcolor
 showTable (indicates if a data table should be displayed)
-->
    <v-container class="ma-3">
        <v-row>
            <p v-if="!changeDefault">
                Hier können die Design-Einstellungen für eine Auswahlfrage verändert werden.
            </p>
            <h3 v-else>
                Hier können die Design-Einstellungen für Auswahlfragen generell gesetzt werden.
            </h3>
        </v-row>
        <!--switch for "ShowDiagram"-->
        <v-row no-gutters>
            <v-switch v-model="showDiagram" label="Zeige ein Diagram an"> </v-switch>
        </v-row>
        <!--        This part is only displayed when a diagram is wanted-->
        <v-row v-show="showDiagram">
            <v-col cols="12" lg="5">
                <!--                overflowbutton for diagramType-->
                <v-overflow-btn
                    dense
                    :disabled="!showDiagram"
                    v-model="chosenDiagramAsWord"
                    :items="diagramTypesInWords"
                    label="Art des Diagrams"
                    @change="changeDiagramType()"
                ></v-overflow-btn>
            </v-col>
        </v-row>
        <!--        this part is only shown and enabled when a diagram is wanted -->

        <!--        Here we can decide if we want multiple colors -->

        <v-row v-show="showDiagram" no-gutters>
            <v-switch v-model="setMultipleColors" label="Verschiedene Farben pro Antwort" class="mr-8"> </v-switch>
            <v-btn class="ma-2" v-if="setMultipleColors" @click="loadColors(0)">
                {{ colorSchemes[0].name }}
            </v-btn>
            <v-btn class="ma-2" v-if="setMultipleColors" @click="loadColors(1)">
                {{ colorSchemes[1].name }}
            </v-btn>

            <v-btn class="ma-2" v-if="setMultipleColors" @click="loadRandomColors">
                Zufall
            </v-btn>

            <v-checkbox v-model="setRandomLength" v-if="setMultipleColors && changeDefault"> </v-checkbox>
            <v-text-field
                v-if="setMultipleColors && changeDefault"
                type="number"
                min="1"
                :max="upperLengthLimit"
                label="Anzahl der generierten Farben"
                v-model="randomLength"
                :disabled="!setRandomLength"
                @change="validateLengthInput"
            >
            </v-text-field>
            <v-row justify="center" v-if="setMultipleColors">
                <v-dialog v-model="colorDialog" width="600px">
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn color="accent" v-bind="attrs" v-on="on">
                            Mehr
                        </v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            Vorgefertigte Farbkombinationen:
                        </v-card-title>
                        <v-card-text>
                            <v-list v-for="(colorScheme, index) in colorSchemes" :key="index" dense>
                                <v-list-item>
                                    <v-chip
                                        v-for="(color, i) in colorScheme.colorArray"
                                        :key="i"
                                        :color="color"
                                    ></v-chip>
                                    <v-spacer></v-spacer>
                                    {{ colorScheme.name }}
                                    <v-spacer></v-spacer>

                                    <v-icon @click="loadColors(index), (colorDialog = false)">
                                        mdi-chevron-double-right
                                    </v-icon>
                                </v-list-item>
                            </v-list>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="info" text @click="colorDialog = false">Fertig</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-row>
        </v-row>

        <!--        This allows us to assemble an array of colors using draggable chips and a color picker -->

        <v-row v-show="showDiagram && setMultipleColors">
            <v-col v-if="changeDefault">
                <!--                this is rendered if we are in a settings window for one specific question-->
                <draggable :list="colorList">
                    <div v-for="(color, index) in colorList" :key="index">
                        <!--                        the chip that is currently editable in the color picker is displayed larger-->
                        <v-chip
                            v-if="index === currentChipIndex"
                            class="ma-1"
                            :color="color"
                            close
                            close-icon="mdi-minus-circle-outline"
                            @click:close="deleteColor(index)"
                            @click="updateCurrentChip(index)"
                            large
                        >
                            Farbe {{ index + 1 }}</v-chip
                        >
                        <v-chip
                            v-else
                            class="ma-1"
                            :color="color"
                            close
                            close-icon="mdi-minus-circle-outline"
                            @click:close="deleteColor(index)"
                            @click="updateCurrentChip(index)"
                        >
                            Farbe {{ index + 1 }}</v-chip
                        >
                    </div>
                </draggable>
                <!--                a click on this button creates a new chip -->
                <v-btn icon @click="newColor">
                    <v-icon> mdi-plus</v-icon>
                </v-btn>
            </v-col>

            <v-col v-else>
                <!--                if we are in a settings window for one component, the number of chips is set. So we can't delete one, nor can we add one-->
                <draggable :list="colorList">
                    <div v-for="(color, index) in colorList" :key="index">
                        <v-chip
                            v-if="index === currentChipIndex"
                            class="ma-1"
                            :color="color"
                            style="min-width: 2cm; text-align-all: center;"
                            @click="updateCurrentChip(index)"
                            large
                        >
                            {{ series ? 'Daten ' + index : answerTitles[index] }}</v-chip
                        >
                        <v-chip
                            v-else
                            class="ma-1"
                            :color="color"
                            style="min-width: 2cm; text-align: center;"
                            @click="updateCurrentChip(index)"
                        >
                            {{ series ? 'Daten ' + index : answerTitles[index] }}
                        </v-chip>
                    </div>
                </draggable>
            </v-col>

            <v-col>
                <v-color-picker v-model="currentChipColor" @input="colorChip"> </v-color-picker>
            </v-col>
        </v-row>

        <!--        this is shown if we only want one color -->

        <v-row v-show="showDiagram && !setMultipleColors">
            <v-col cols="12" lg="5">
                <!--                overflowbutton for diagramColor-->
                <v-overflow-btn
                    dense
                    v-model="chosenDiagramColorAsWord"
                    :items="diagramColorsInWords"
                    label="Farbe des Diagrams"
                    @change="changeDiagramColor()"
                ></v-overflow-btn>
            </v-col>
            <v-col>
                <!--                color picker -->
                <v-color-picker v-model="setChosenDiagramColor" @input="resetChosenDiagramColor"> </v-color-picker>
            </v-col>
        </v-row>

        <!--switch for "ShowTable"-->
        <v-row no-gutters>
            <v-switch v-model="showTable" label="Zeige eine Tabelle an"> </v-switch>
        </v-row>

        <!-- if we edit the settings for all questions we are asked to confirm, if we want already eddited questions to be changed-->
        <v-row v-if="changeDefault" no-gutters>
            <v-switch v-model="useOnAll" label="Auf alle Diagramme anwenden"> </v-switch>
        </v-row>
        <p v-if="changeDefault && useOnAll">
            Momentan werden nur die Diagramme verändert, die bisher noch nicht bearbeitet wurden.
        </p>
        <!-- Button that will send the picked data to the parent component -->
        <v-row no-gutters>
            <v-btn color="success" @click="saveDiagramFormat">
                Anwenden
            </v-btn>
        </v-row>
    </v-container>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import draggable from 'vuedraggable'

export default {
    name: 'visualEvaluationSettings',
    components: {
        draggable,
    },
    // these props can be passed by the parent component
    props: {
        questionId: {
            type: Number,
            default: -1,
        },
        // is this a settings window for one particular question or all questions?
        changeDefault: {
            type: Boolean,
            default: false,
        },
        series: {
            type: Boolean,
            default: false,
        },
    },
    mounted() {
        const format = this.getFormat(this.questionId)
        console.log('los gehts ' + this.questionId)
        console.log(format)
        this.diagramType = String(this.initialDiagramType)
        this.showTable = Boolean(this.initialShowTable)
        this.showDiagram = Boolean(this.initialShowDiagram)
        this.chosenDiagramAsWord = this.diagramTypesInWords[this.diagramTypes.indexOf(this.initialDiagramType)]
        this.chosenDiagramColorAsWord = this.diagramColorsInWords[
            this.diagramColors.indexOf(this.initialBackgroundColor)
        ]
        this.setChosenDiagramColor = String(this.getFormat(this.questionId).backgroundColor)
        // for (let i = 0; i < this.colorList.length; i++) {
        //     this.colorList[i] = this.initialBackgroundColors[i % this.initialBackgroundColors.length]
        // }
        this.colorList = [...this.initialBackgroundColors]
        this.setMultipleColors = Boolean(this.initialMultipleColors)
    },
    data() {
        return {
            chosenDiagramAsWord: '',

            diagramTypesInWords: ['Tortendiagramm', 'Balkendiagramm'],
            diagramTypes: ['pie', 'bar'],

            chosenDiagramColorAsWord: '',

            diagramColorsInWords: ['Grau', 'Petrol', 'Grün', 'Violet', 'Türkis', 'Blau'],
            diagramColors: ['#aaaaaa', '#114955', '#8ec136', '#551044', '#26A599', '#092140'],

            setChosenDiagramColor: '',

            setMultipleColors: false,

            colorList: ['#123456', '#444444', '#789005'],
            // index of the chip that can be altered by colorPicker
            currentChipIndex: 0,

            currentChipColor: '',

            useOnAll: false,

            diagramType: '',
            showTable: false,
            showDiagram: false,
            defaultColors: ['#114955', '#8ec136', '#551044', '#092140', '#26A599'],
            grayColors: ['#111111', '#222222', '#333333', '#444444', '#555555'],
            randomLength: 5,
            colorChars: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'],
            colorSchemes: [
                {
                    name: 'Umfrato',
                    colorArray: ['#114955', '#8ec136', '#551044', '#092140', '#26A599'],
                },
                {
                    name: 'Grau-Stufen',
                    colorArray: ['#111111', '#222222', '#333333', '#444444', '#555555'],
                },
                {
                    name: 'Durchscheinend',
                    colorArray: ['#11495588', '#8ec13666', '#55104444', '#09214022'],
                },
                {
                    name: 'Blau-Stufen',
                    colorArray: ['#0A1647', '#1A76A1', '#260B87', '#3162CB', '#131D5E'],
                },
                {
                    name: 'Sommer',
                    colorArray: ['#1D516A', '#A46262', '#32BE89', '#9F7A5A', '#5DA9BA', '#7E9345'],
                },
                {
                    name: 'Design 1',
                    colorArray: ['#114955', '#8ec136', '#551044', '#092140', '#26A599'],
                },
                {
                    name: 'Design 2',
                    colorArray: ['#111111', '#222222', '#333333', '#444444', '#555555'],
                },
                {
                    name: 'Design 3',
                    colorArray: ['#11495588', '#8ec13666', '#55104444', '#09214022'],
                },
                {
                    name: 'Design 4',
                    colorArray: ['#0A1647', '#1A76A1', '#260B87', '#3162CB', '#131D5E'],
                },
                {
                    name: 'Design 5',
                    colorArray: ['#1D516A', '#A46262', '#32BE89', '#9F7A5A', '#5DA9BA', '#7E9345'],
                },
                {
                    name: 'Design 6',
                    colorArray: ['#114955', '#8ec136', '#551044', '#092140', '#26A599'],
                },
                {
                    name: 'Design 7',
                    colorArray: ['#111111', '#222222', '#333333', '#444444', '#555555'],
                },
                {
                    name: 'Design 8',
                    colorArray: ['#11495588', '#8ec13666', '#55104444', '#09214022'],
                },
                {
                    name: 'Design 9',
                    colorArray: ['#0A1647', '#1A76A1', '#260B87', '#3162CB', '#131D5E'],
                },
                {
                    name: 'Design 10',
                    colorArray: ['#114955', '#8ec136', '#551044', '#092140', '#26A599'],
                },
                {
                    name: 'Design 11',
                    colorArray: ['#114955', '#8ec136', '#551044', '#092140', '#26A599'],
                },
                {
                    name: 'Design 12',
                    colorArray: ['#111111', '#222222', '#333333', '#444444', '#555555'],
                },
                {
                    name: 'Design 13',
                    colorArray: ['#11495588', '#8ec13666', '#55104444', '#09214022'],
                },
                {
                    name: 'Design 14',
                    colorArray: ['#0A1647', '#1A76A1', '#260B87', '#3162CB', '#131D5E'],
                },
                {
                    name: 'Design 15',
                    colorArray: ['#1D516A', '#A46262', '#32BE89', '#9F7A5A', '#5DA9BA', '#7E9345'],
                },
                {
                    name: 'Design 16',
                    colorArray: ['#114955', '#8ec136', '#551044', '#092140', '#26A599'],
                },
                {
                    name: 'Design 17',
                    colorArray: ['#111122', '#222222', '#333322', '#444422', '#555522'],
                },
                {
                    name: 'Design 18',
                    colorArray: ['#111100', '#222200', '#333300', '#444400', '#555500'],
                },
                {
                    name: 'Design 19',
                    colorArray: ['#0A1647', '#1A76A1', '#260B87', '#3162CB', '#131D5E'],
                },
                {
                    name: 'Design 20',
                    colorArray: ['#0A1647', '#1A76A1', '#260B87', '#3162CB', '#131D5E'],
                },
            ],
            setRandomLength: false,
            upperLengthLimit: 20,
            colorDialog: false,
            defaultColorListLength: 5, // load color will set a shorter list to length of 5
        }
    },
    methods: {
        ...mapMutations({
            setDiagramFormat: 'evaluation/setDiagramFormat',
            setDiagramFormats: 'evaluation/setDiagramFormats',
        }),

        async saveDiagramFormat() {
            const c = []
            for (let i = 0; i < this.colorList.length; i++) {
                c.push(this.colorList[i])
            }
            const format = {
                questionId: this.questionId,
                showDiagram: this.showDiagram,
                diagramType: this.diagramType,
                backgroundColors: c,
                backgroundColor: this.setChosenDiagramColor,
                multipleColors: this.setMultipleColors,
                showTable: this.showTable,
            }
            if (this.changeDefault) {
                this.setDiagramFormats({ useOnAll: this.useOnAll, format })
            } else {
                console.log('pushFormat')
                await this.setDiagramFormat(format)
                console.log('pushedFormat')
            }
            console.log('done')
            this.$emit('done')
        },

        // updates the setChosenDiagramColor, when it was picked through the overflow button
        changeDiagramColor() {
            this.setChosenDiagramColor = this.diagramColors[
                this.diagramColorsInWords.indexOf(this.chosenDiagramColorAsWord)
            ]
        },

        // updates the chosenDiagram, when it was picked through the overflow button
        changeDiagramType() {
            this.diagramType = this.diagramTypes[this.diagramTypesInWords.indexOf(this.chosenDiagramAsWord)]
        },

        computeDiagram() {
            this.chosenDiagramAsWord = this.diagramTypesInWords[this.diagramTypes.indexOf(this.diagramType)]
        },

        // resets the value of chosenDiagramColorAsWord, so nothing is displayed in overflow button once you pick a color in the color picker
        resetChosenDiagramColor() {
            this.chosenDiagramColorAsWord = ''
        },

        // if we add a new chip, we declare it the current chip, that can be edited
        newColor() {
            this.colorList.push('#aaaaaa')
            this.currentChipIndex = this.colorList.length - 1
            this.loadChipColorInPicker()
        },

        // if we delete a chip, the currentChipIndex needs to be decreased, if the deleted chip was in front of it
        deleteColor(index) {
            if (index > -1) {
                this.colorList.splice(index, 1)
            }

            if (index <= this.currentChipIndex) {
                this.currentChipIndex--
                this.loadChipColorInPicker()
            }
        },

        // colors the current Chip in the color from the color picker

        colorChip() {
            this.colorList[this.currentChipIndex] = this.currentChipColor
        },

        // the color of the current chip is loaded into the color picker

        loadChipColorInPicker() {
            this.currentChipColor = this.colorList[this.currentChipIndex]
        },

        // when we click on a chip, that chip is the new currentChip the color of the chip is loaded into the color picker

        updateCurrentChip(index) {
            this.currentChipIndex = index
            this.loadChipColorInPicker()
        },

        loadColors(index) {
            console.log('load colors')
            if (this.changeDefault && this.colorList.length < this.defaultColorListLength) {
                this.colorList = ['', '', '', '', '']
            }
            if (this.setRandomLength) {
                this.colorList = []
                for (let i = 0; i < this.randomLength; i++) {
                    this.colorList.push('')
                }
            }
            for (let i = 0; i < this.colorList.length; i++) {
                console.log('hu')
                console.log(this.colorSchemes[index].colorArray[i % this.colorSchemes[index].colorArray.length])
                this.colorList[i] = this.colorSchemes[index].colorArray[i % this.colorSchemes[index].colorArray.length]
            }
            this.$forceUpdate()
        },

        loadRandomColors() {
            if (this.changeDefault && this.setRandomLength) {
                this.colorList = []
                for (let i = 0; i < this.randomLength; i++) {
                    this.colorList.push('')
                }
            }
            for (let i = 0; i < this.colorList.length; i++) {
                let colorString = '#'
                for (let i = 0; i < 6; i++) {
                    const n = Math.round(Math.random() * 15)
                    colorString += this.colorChars[n]
                }
                this.colorList[i] = colorString
            }
            console.log('random')
            console.log(this.colorList)
            this.$forceUpdate()
        },

        validateLengthInput() {
            if (this.randomLength > this.upperLengthLimit) {
                this.randomLength = this.upperLengthLimit
            }
            if (this.randomLength < 0 || !this.randomLength) {
                this.randomLength = 1
            }
        },
    },

    computed: {
        ...mapGetters({
            getFormat: 'evaluation/getDiagramFormat',
            polls: 'evaluation/getPolls',
            diagramData: 'evaluation/getDiagramData',
        }),
        initialDiagramType() {
            return this.getFormat(this.questionId).diagramType
        },
        initialBackgroundColors() {
            return this.getFormat(this.questionId).backgroundColors
        },
        initialBackgroundColor() {
            return this.getFormat(this.questionId).backgroundColor
        },
        initialMultipleColors() {
            return this.getFormat(this.questionId).multipleColors
        },
        initialShowTable() {
            return this.getFormat(this.questionId).showTable
        },
        initialShowDiagram() {
            return this.getFormat(this.questionId).showDiagram
        },
        // here we can give a chip an outline to set it apart
        styleForChosenChip() {
            return (
                'border-color:' + this.$vuetify.theme.currentTheme.info + '; border-style: solid; border-width: thick;'
            )
        },
        answerTitles() {
            console.log(this.polls)
            console.log(this.questionId)
            const a = []
            /* for (let i = 0; i < this.polls.length; i++) {
                console.log(i)
                for (let j = 0; j < this.polls[i].categoryList.length; j++) {
                    console.log(j)
                    for (let k = 0; k < this.polls[i].categoryList[j].questionList.length; k++) {
                        console.log(k)
                        if (this.polls[i].categoryList[j].questionList[k].questionId === this.questionId) {
                            console.log('if')
                            for (
                                let l = 0;
                                l < this.polls[i].categoryList[j].questionList[k].answerPossibilities.length;
                                l++
                            ) {
                                if (this.polls[i].categoryList[j].questionList[k].answerPossibilities[l].length < 5) {
                                    a.push(
                                        '  ' +
                                            this.polls[i].categoryList[j].questionList[k].answerPossibilities[l] +
                                            '  '
                                    )
                                } else if (
                                    this.polls[i].categoryList[j].questionList[k].answerPossibilities[l].length > 30
                                ) {
                                    a.push(
                                        this.polls[i].categoryList[j].questionList[k].answerPossibilities[l].substr(
                                            0,
                                            27
                                        ) + '...'
                                    )
                                } else {
                                    a.push(this.polls[i].categoryList[j].questionList[k].answerPossibilities[l])
                                }
                            }
                        }
                    }
                }
            } */
            console.log(this.diagramData)
            console.log(this.questionId)
            for (let i = 0; i < this.diagramData.length; i++) {
                console.log(i)
                if (this.diagramData[i].id === this.questionId) {
                    for (let j = 0; j < this.diagramData[i].answerPossibilities.length; j++) {
                        if (this.diagramData[i].answerPossibilities[j].length < 5) {
                            a.push('  ' + this.diagramData[i].answerPossibilities[j] + '  ')
                        } else if (this.diagramData[i].answerPossibilities[j].length > 30) {
                            a.push(this.diagramData[i].answerPossibilities[j].substr(0, 27) + '...')
                        } else {
                            a.push(this.diagramData[i].answerPossibilities[j])
                        }
                    }
                }
            }
            console.log(a)
            return a
        },
    },
}
</script>

<style scoped></style>
