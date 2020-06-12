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

        <!--        Here we can decide if we want multiple colors -->

        <v-row v-show="showDiagram" no-gutters>
            <v-switch v-model="setMultipleColors" label="Verschiedene Farben pro Antwort"> </v-switch>
        </v-row>

        <!--        This allows us to assemble an array of colors using draggable chips and a color picker -->

        <v-row v-show="showDiagram && setMultipleColors">
            <v-col v-if="!oneQuestion">
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
                            @click="updateCurrentChip(index)"
                            large
                        >
                            Farbe {{ index + 1 }}</v-chip
                        >
                        <v-chip v-else class="ma-1" :color="color" @click="updateCurrentChip(index)">
                            Farbe {{ index + 1 }}</v-chip
                        >
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
        <v-row v-if="!oneQuestion" no-gutters>
            <v-switch v-model="useOnAll" label="Auf alle Diagramme anwenden?"> </v-switch>
        </v-row>
        <!-- Button that will send the picked data to the parent component -->
        <v-row no-gutters>
            <v-btn
                color="success"
                @click="
                    $emit(
                        'update-Visuals',
                        showDiagram,
                        chosenDiagram,
                        colorList,
                        setChosenDiagramColor,
                        setMultipleColors,
                        showTable
                    ),
                        $emit(
                            'update-all-Visuals',
                            showDiagram,
                            chosenDiagram,
                            colorList,
                            setChosenDiagramColor,
                            setMultipleColors,
                            showTable,
                            useOnAll
                        )
                "
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
        // is this a settings window for one particular question or all questions?
        oneQuestion: {
            type: Boolean,
            default: false,
        },
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
        chosenDiagramColor: {
            type: String,
            default: '#aaaaaa',
        },

        multipleColors: {
            type: Boolean,
            default: false,
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
        this.setMultipleColors = this.multipleColors
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

            setMultipleColors: false,

            colorList: ['#123456', '#444444', '#789005'],
            // index of the chip that can be altered by colorPicker
            currentChipIndex: 0,

            currentChipColor: '',

            useOnAll: false,
        }
    },

    methods: {
        // updates the setChosenDiagramColor, when it was picked through the overflow button
        changeDiagramColor() {
            this.setChosenDiagramColor = this.diagramColors[
                this.diagramColorsInWords.indexOf(this.chosenDiagramColorAsWord)
            ]
        },
        // updates the chosenDiagram, when it was picked through the overflow button
        changeDiagramType() {
            this.chosenDiagram = this.diagramTypes[this.diagramTypesInWords.indexOf(this.chosenDiagramAsWord)]
        },

        computeDiagram() {
            this.chosenDiagramAsWord = this.diagramTypesInWords[this.diagramTypes.indexOf(this.chosenDiagram)]
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
    },

    computed: {
        // here we can give a chip an outline to set it apart
        styleForChosenChip() {
            return (
                'border-color:' + this.$vuetify.theme.currentTheme.info + '; border-style: solid; border-width: thick;'
            )
        },
    },
}
</script>

<style scoped></style>
