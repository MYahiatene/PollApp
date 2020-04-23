<template>
    <v-card>
        <v-container>
            <v-row no-gutters>
                <v-col>
                    <h3>Frage erstellen</h3>
                </v-col>
            </v-row>
            <v-row no-gutters>
                <v-textarea label="Fragentext" hint="Was soll den Umfrageteilnehmer gefragt werden?" rows="1">
                </v-textarea>
            </v-row>
            <v-row no-gutters>
                <v-overflow-btn :items="questionTypes" label="Antwortart"></v-overflow-btn>
            </v-row>
            <v-row no-gutters> <v-text-field label="Antwortmöglichkeit 1"></v-text-field> </v-row>
            <v-row no-gutters> <v-text-field label="Antwortmöglichkeit 2"></v-text-field> </v-row>
            <v-row no-gutters>
                <v-text-field
                    label="Antwortmöglichkeiten"
                    hint="Maximale Anzahl gleichzeitig auswählbarer Antworten"
                    type="number"
                    min="1"
                    max="3"
                    step="1"
                    value="1"
                ></v-text-field>
            </v-row>

            <v-row no-gutters>
                <v-switch label="Teilnehmer darf eigene Antworten hinzufügen"></v-switch>
            </v-row>
            <v-row no-gutters>
                <v-switch label="Teilnehmer darf Dateien hochladen"></v-switch>
            </v-row>
        </v-container>
    </v-card>
</template>

<script>
export default {
    name: 'QuestionBuildWidget',
    data() {
        return {
            questionTypes: ['Auswahlfrage', 'Freitextfrage', 'Bewertungsfrage', 'Sortierfrage'],
            antworten: ['', ''],
        }
    },
}
</script>

<style scoped></style>
