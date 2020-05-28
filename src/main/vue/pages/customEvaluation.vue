<template>
    <v-container>
        <v-layout row justify-space-around>
            <v-flex md6 lg3>
                <v-card class="ma-3 pa-3">
                    <v-card-title>Filter</v-card-title>
                    <template>
                        <v-expansion-panels accordion multiple hover>
                            <v-expansion-panel v-for="item in kategories" :key="item">
                                <v-expansion-panel-header>{{ item.name }}</v-expansion-panel-header>
                                <v-expansion-panel-content>
                                    <div class="overline">{{ item.explanation }}</div>
                                    <div v-for="option in item.options" :key="option">
                                        <v-chip draggable>{{ option }}</v-chip>
                                    </div>
                                </v-expansion-panel-content>
                            </v-expansion-panel>
                        </v-expansion-panels>
                    </template>
                </v-card>
            </v-flex>
            <v-flex md6 lg9>
                <v-card class="ma-3 pa-3">
                    <v-card-title>Analyse</v-card-title>
                    <v-card>
                        <v-responsive :aspect-ratio="16 / 5"> </v-responsive>
                    </v-card>
                    <v-card-actions>
                        <v-btn>
                            Anwenden
                        </v-btn>
                        <v-btn>
                            Vergleichen
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
export default {
    name: 'customEvaluation',
    data() {
        return {
            kategories: [
                {
                    name: 'Daten',
                    explanation: 'Wähle die Basis-Daten für deine Umfrage',
                    options: ['Basisdaten: [Umfrage 1] v'],
                },
                {
                    name: 'Fragen',
                    explanation: 'Welche Fragen der Umfrage sollen betrachtet werden?',
                    options: ['Folgende Fragen werden betrachtet: [1,3,4,6        ]'],
                },
                {
                    name: 'Konsistenzfragen',
                    explanation:
                        'Hier kann man einstellen, ob inkonsistente Beantwortungen herausgefiltert werden sollen',
                    options: ['Nur konsistent beantwortete Umfragen'],
                },
                {
                    name: 'Teilnehmer',
                    explanation:
                        'Hier können die Teilnehmer, deren Antworten analysiert werden sollen, gefiltert werden',
                    options: [],
                },
                { name: 'Antworten', explanation: '', options: [] },
                { name: 'Logik', explanation: '', options: [] },
            ],
        }
    },
}
</script>

<style scoped></style>
