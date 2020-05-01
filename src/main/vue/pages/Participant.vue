<template>
    <v-container>
        <v-subheader class="display-1"> Umfrage 1</v-subheader>
        <v-content>
            <v-row>
                <v-col cols="8">
                    <v-list v-for="question in questions" :key="question.id" two-line>
                        <v-card class="mx-auto">
                            <v-card-title> {{ question.text }}</v-card-title>
                            <div v-if="question.type === 'textQuestion'">
                                <v-text-field label="Antwort"> </v-text-field>
                            </div>
                            <div v-else-if="question.type === 'choiceQuestion'">
                                <v-list v-for="answer in question.answers" :key="answer.text">
                                    <v-checkbox class="ma-4" :label="answer.text"></v-checkbox>
                                </v-list>
                            </div>
                        </v-card>
                    </v-list>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="8">
                    <v-btn class="ma-4">Vorherige Seite</v-btn>
                    <v-btn class="ma-4">Nächste Seite</v-btn>
                </v-col>
            </v-row>
        </v-content>
    </v-container>
</template>

<script>
export default {
    name: 'Participant',
    data: () => ({
        questions: [
            {
                id: '1',
                text: 'Was für eine Frage?',
                type: 'textQuestion',
            },
            {
                id: '2',
                text: 'Was für zwei Fragen?',
                type: 'choiceQuestion',
                answers: [
                    {
                        text: 'a',
                    },
                    {
                        text: 'b',
                    },
                    {
                        text: 'c',
                    },
                ],
            },
        ],
    }),
}
</script>

<style scoped></style>
