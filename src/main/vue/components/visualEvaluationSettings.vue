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
                    :disabled="!showDiagram"
                    v-model="chosenDiagramAsWord"
                    :items="diagramTypesInWords"
                    label="Art des Diagrams"
                    @change="changeDiagramType()"
                ></v-overflow-btn>
            </v-col>
        </v-row>
        <!--        this part is only shown and enabled when a diagram is wanted and the diagram type has been updated -->
        <v-row v-show="!forbidColorSwitch && showDiagram">
            <v-col cols="12" lg="5">
                <!--                overflowbutton for diagramColor-->
                <v-overflow-btn
                    :disabled="forbidColorSwitch"
                    v-model="chosenDiagramColorAsWord"
                    :items="diagramColorsInWords"
                    label="Farbe des Diagrams"
                    @change="changeDiagramColor()"
                ></v-overflow-btn>
            </v-col>
        </v-row>
        <!--switch for "ShowTable"-->
        <v-row no-gutters>
            <v-switch v-model="showTable" label="Zeige eine Tabelle an"> </v-switch>
        </v-row>
        <!-- Button that will send the picked data to the parennt component -->
        <v-row no-gutters>
            <v-btn @click="$emit('update-Visuals', showDiagram, chosenDiagram, chosenDiagramColor, showTable)">
                Anwenden
            </v-btn>
        </v-row>
    </v-container>
</template>

<script>
export default {
    // these props can be passed by the parent component
    props: {
        showDiagram: {
            type: Boolean,
        },
        chosenDiagram: {
            type: String,
            default: 'bar',
        },
        chosenDiagramColor: {
            type: String,
            default: '#aaaaaa',
        },
        showTable: {
            type: Boolean,
        },
    },
    mounted() {
        this.forbidColorSwitch = this.chosenDiagram === ''
        this.chosenDiagramAsWord = this.diagramTypesInWords[this.diagramTypes.indexOf(this.chosenDiagram)]
        this.chosenDiagramColorAsWord = this.diagramColorsInWords[this.diagramColors.indexOf(this.chosenDiagramColor)]
    },
    name: 'visualEvaluationSettings',
    data() {
        return {
            chosenDiagramAsWord: '',

            diagramTypesInWords: ['Tortendiagramm', 'Balkendiagramm'],
            diagramTypes: ['pie', 'bar'],

            chosenDiagramColorAsWord: '',

            diagramColorsInWords: ['Grau', 'Petrol', 'Grün', 'Violet', 'Türkis'],
            diagramColors: ['#aaaaaa', '#114955', '#8EC136', '#551044', '#009999'],

            forbidColorSwitch: true,
        }
    },

    methods: {
        changeDiagramColor() {
            this.chosenDiagramColor = this.diagramColors[
                this.diagramColorsInWords.indexOf(this.chosenDiagramColorAsWord)
            ]
            this.disableColorSwitch()
        },
        changeDiagramType() {
            this.chosenDiagram = this.diagramTypes[this.diagramTypesInWords.indexOf(this.chosenDiagramAsWord)]
            this.forbidColorSwitch = false
        },

        disableColorSwitch() {
            this.forbidColorSwitch = true
        },

        computeDiagram() {
            this.chosenDiagramAsWord = this.diagramTypesInWords[this.diagramTypes.indexOf(this.chosenDiagram)]
        },
    },
}
</script>

<style scoped></style>
