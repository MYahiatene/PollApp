<template>
    <v-container>
        <v-row no-gutters>
            <v-overflow-btn :items="choiceType" label="Antwortart"></v-overflow-btn>
        </v-row>
        <v-row no-gutters>
            <v-col>
                <v-btn :color="activeColor" @click="addQuestion()">Antwortmöglichkeit hinzufügen</v-btn>
            </v-col>
            <v-col>
                <v-btn :color="activeColor" @click="removeQuestion()">Antwortmöglichkeit entfernen</v-btn>
            </v-col>
        </v-row>
        <v-row v-for="(answer, index) in answers" :key="index" no-gutters>
            <v-text-field :value="answer" :label="'Antwortmöglichkeit ' + (index + 1)"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-text-field
                label="Antwortmöglichkeiten"
                hint="Maximale Anzahl gleichzeitig auswählbarer Antworten"
                type="number"
                min="1"
                :max="answers.length"
                step="1"
                value="1"
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="Teilnehmer darf eigene Antworten hinzufügen"></v-switch>
        </v-row>
        <v-row no-gutters>
            <v-switch label="Teilnehmer darf eigene Dateien hochladen"></v-switch>
        </v-row>
    </v-container>
</template>

<script>
export default {
    name: 'ChoiceQuestionConfigWidget',
    data() {
        return {
            choiceType: ['Standartauswahl', 'Drop-Down', 'Sortieren'],
            answers: ['', ''],
            addQuestion() {
                this.answers.push('')
            },
            removeQuestion() {
                this.answers.pop()
            },
        }
    },
}
</script>
