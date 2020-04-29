<!--this widget enables the user to configure a choice question including all of its unique features-->
<template>
    <v-container>
        <v-row no-gutters>
            <v-overflow-btn :items="choiceType" label="Antwortart"></v-overflow-btn>
        </v-row>
        <v-row v-for="(answer, index) in answers" :key="index" no-gutters>
            <v-text-field :value="answer" :label="'Antwortmöglichkeit ' + (index + 1)"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-btn :color="activeColor" @click="fuck()">Antwortmöglichkeit hinzufügen</v-btn>
        </v-row>
        <v-row no-gutters>
            <v-text-field
                label="Antwortmöglichkeiten"
                hint="Maximale Anzahl gleichzeitig auswählbarer Antworten"
                type="number"
                min="1"
                max="answers.length"
                step="1"
                value="1"
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="Teilnehmer darf eigene Antworten hinzufügen"></v-switch>
        </v-row>
        <v-row no-gutters>
            <!--maybe not necessary inside this widget? could be moved into an independent question type-->
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
            fuck() {},
        }
    },
}
</script>
