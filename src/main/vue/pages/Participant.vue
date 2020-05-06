<template>
    <v-container>
        <v-subheader class="display-1"> Umfrage 1</v-subheader>
        <v-content>
            <v-row>
                <v-col cols="8">
                    <v-list v-for="question in questions" :key="question.id" two-line>
                        <v-card class="mx-auto">
                            <v-card-title>
                                <!--the visibility of the number of questions depends on the poll configuration-->
                                <div v-if="getVisibility">{{ question.id }}/{{ questions.length }}</div>
                                {{ question.text }}
                            </v-card-title>
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
                    <v-btn class="pl-4">Vorherige Seite</v-btn>
                    <v-btn class="pl-4">Nächste Seite</v-btn>
                    <v-btn color="primary" nuxt to="/AfterParticipated">
                        Absenden
                    </v-btn>
                </v-col>
            </v-row>
        </v-content>
    </v-container>
</template>

<script>
import { mapGetters } from 'vuex'
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
    computed: {
        ...mapGetters({
            getVisibility: 'isVisible',
        }),
        getVisibility() {
            return this.$store.state.participant.visibility
        },
    },
}
</script>

<style scoped></style>
