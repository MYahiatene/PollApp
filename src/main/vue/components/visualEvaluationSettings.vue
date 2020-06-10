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
            <p>
                Hier können die Design-Einstellungen für eine Auswahlfrage verändert werden.
            </p>
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

        <v-row no-gutters>
            <v-switch v-model="multipleColors" label="Verschiedene Farben pro Antwort"> </v-switch>
        </v-row>

        <v-row v-show="showDiagram && multipleColors">
            <v-col>
                <draggable :list="colorList">
                    <div v-for="(color, index) in colorList" :key="index">
                        <v-chip
                            class="ma-1"
                            :color="color"
                            close
                            close-icon="mdi-minus-circle-outline"
                            @click:close="deleteColor(index)"
                            @click="currentChipIndex = index"
                        >
                            Farbe {{ index + 1 }}</v-chip
                        >
                    </div>
                </draggable>
                <v-btn icon @click="newColor">
                    <v-icon> mdi-plus</v-icon>
                </v-btn>
            </v-col>

            <v-col>
                <v-color-picker v-model="currentChipColor" @input="colorChip"> </v-color-picker>
            </v-col>
        </v-row>

        <v-row v-show="showDiagram && !multipleColors">
            <!--            <v-row v-show="!forbidColorSwitch && showDiagram">-->
            <v-col cols="12" lg="5">
                <!--                overflowbutton for diagramColor-->

                <!--                    :disabled="forbidColorSwitch"-->
                <v-overflow-btn
                    dense
                    v-model="chosenDiagramColorAsWord"
                    :items="diagramColorsInWords"
                    label="Farbe des Diagrams"
                    @change="changeDiagramColor()"
                ></v-overflow-btn>
            </v-col>
            <v-col>
                <v-color-picker v-model="setChosenDiagramColor" @input="resetChosenDiagramColor"> </v-color-picker>
            </v-col>
        </v-row>

        <!--switch for "ShowTable"-->
        <v-row no-gutters>
            <v-switch v-model="showTable" label="Zeige eine Tabelle an"> </v-switch>
        </v-row>
        <!-- Button that will send the picked data to the parent component -->
        <v-row no-gutters>
            <v-btn
                color="success"
                @click="$emit('update-Visuals', showDiagram, chosenDiagram, chosenColorArray, showTable)"
            >
                Anwenden
            </v-btn>
        </v-row>
    </v-container>
</template>

<script>
import draggable from 'vuedraggable'

export default {
    name: 'visualEvaluationSettings',
    components: {
        draggable,
    },
    // these props can be passed by the parent component
    props: {
        showDiagram: {
            type: Boolean,
        },
        chosenDiagram: {
            type: String,
            default: 'bar',
        },
        chosenDiagramColors: {
            type: Array,
            default: () => ['#aaaaaa'],
        },
        showTable: {
            type: Boolean,
        },
    },
    mounted() {
        this.chosenDiagramAsWord = this.diagramTypesInWords[this.diagramTypes.indexOf(this.chosenDiagram)]
        this.chosenDiagramColorAsWord = this.diagramColorsInWords[this.diagramColors.indexOf(this.chosenDiagramColor)]
        this.setChosenDiagramColor = this.chosenDiagramColor
        this.colorList = this.chosenDiagramColors
        console.log('ben here')
    },

    data() {
        return {
            chosenDiagramAsWord: '',

            diagramTypesInWords: ['Tortendiagramm', 'Balkendiagramm'],
            diagramTypes: ['pie', 'bar'],

            chosenDiagramColorAsWord: '',

            diagramColorsInWords: ['Grau', 'Petrol', 'Grün', 'Violet', 'Türkis'],
            diagramColors: ['#aaaaaa', '#114955', '#8EC136', '#551044', '#26A599'],

            setChosenDiagramColor: '',

            multipleColors: true,

            colorList: ['#123456', '#444444', '#789005'],
            // index of the chip that can be altered by colorPicker
            currentChipIndex: 0,

            currentChipColor: '',
        }
    },

    methods: {
        changeDiagramColor() {
            // this.chosenDiagramColor = this.diagramColors[
            //     this.diagramColorsInWords.indexOf(this.chosenDiagramColorAsWord)
            // ]
            this.setChosenDiagramColor = this.diagramColors[
                this.diagramColorsInWords.indexOf(this.chosenDiagramColorAsWord)
            ]
            // this.disableColorSwitch()
        },
        changeDiagramType() {
            this.chosenDiagram = this.diagramTypes[this.diagramTypesInWords.indexOf(this.chosenDiagramAsWord)]
            // this.forbidColorSwitch = false
        },

        // disableColorSwitch() {
        //     this.forbidColorSwitch = true
        // },

        computeDiagram() {
            this.chosenDiagramAsWord = this.diagramTypesInWords[this.diagramTypes.indexOf(this.chosenDiagram)]
        },

        resetChosenDiagramColor() {
            this.chosenDiagramColorAsWord = ''
        },

        newColor() {
            this.colorList.push('#aaaaaa')
            this.currentChipIndex = this.colorList.length - 1
        },

        deleteColor(index) {
            if (index > -1) {
                this.colorList.splice(index, 1)
            }
        },

        colorChip() {
            this.colorList[this.currentChipIndex] = this.currentChipColor
        },
    },

    computed: {
        chosenColorArray() {
            if (this.multipleColors) {
                return this.colorList
            } else {
                const c = []
                c.push(this.setChosenDiagramColor)
                return c
            }
        },

        chosenDiagramColor() {
            return this.chosenDiagramColors[0]
        },
    },
}
</script>

<style scoped></style>
