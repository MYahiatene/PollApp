<template>
    <v-container class="ma-3">
        <v-row>
            <p>
                Hier können die Design-Einstellungen für eine Auswahlfrage verändert werden.
            </p>
        </v-row>

        <v-row no-gutters>
            <v-switch v-model="showDiagram" label="Zeige ein Diagram an"> </v-switch>
        </v-row>
        <v-row>
            <v-col cols="12" lg="5">
                <v-overflow-btn
                    :disabled="!showDiagram"
                    v-model="chosenDiagramAsWord"
                    :items="diagramTypesInWords"
                    label="Art des Diagrams"
                    @change="changeDiagramType()"
                ></v-overflow-btn>
            </v-col>
        </v-row>
        <v-row v-show="!forbidColorSwitch && showDiagram">
            <v-col cols="12" lg="5">
                <v-overflow-btn
                    :disabled="forbidColorSwitch"
                    v-model="chosenDiagramColorAsWord"
                    :items="diagramColorsInWords"
                    label="Farbe des Diagrams"
                    @change="changeDiagramColor()"
                ></v-overflow-btn>
            </v-col>
        </v-row>

        <v-row no-gutters>
            <v-switch v-model="showTable" label="Zeige eine Tabelle an"> </v-switch>
        </v-row>

        <v-row no-gutters>
            <v-btn @click="$emit('update-Visuals', showDiagram, chosenDiagram, chosenDiagramColor, showTable)">
                Anwenden
            </v-btn>
        </v-row>
    </v-container>
</template>

<script>
export default {
    props: {
        //     showDiagram: {
        //         type: Boolean,
        //         default: true,
        //     },
        chosenDiagram: {
            type: String,
            default: 'bar',
        },
        chosenDiagramColor: {
            type: String,
            default: '#555555',
        },
        //     showTable: {
        //         type: Boolean,
        //         default: true,
        //     },
    },
    name: 'visualEvaluationSettings',
    data() {
        return {
            // chosenDiagram: 'bar',
            chosenDiagramAsWord: '',

            diagramTypesInWords: ['TortenDiagram', 'Balkendiagram'],
            diagramTypes: ['pie', 'bar'],

            // chosenDiagramColor: '#555555',
            chosenDiagramColorAsWord: '',

            diagramColorsInWords: ['Grau', 'Blau', 'Grün', 'Rot'],
            diagramColors: ['#555555', '#000088', '#008800', '#880000'],

            showDiagram: true,
            showTable: true,
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
