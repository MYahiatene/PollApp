<template>
    <v-container>
        <v-subheader class="display-1"> Umfrage 1</v-subheader>
        <v-content>
            <v-row>
                <v-col cols="8">
                    <v-list v-for="poll in getPoll" :key="poll.id">
                        <v-list v-for="question in poll.questionList" :key="question.id" two-line>
                            <v-card class="mx-auto">
                                <v-card-title>
                                    <!--the visibility of the number of questions depends on the poll configuration-->
                                    <!--div v-if="getVisibility">{{ question.id }}/{{ questions.length }}</div-->
                                    {{ question.questionMessage }}
                                </v-card-title>
                                <!--div v-if="question.type === 'textQuestion'">
                                    <v-text-field label="Antwort"> </v-text-field>
                                <div-->
                                <v-text-field label="Antwort"></v-text-field>
                                <!--div v-else-if="question.type === 'choiceQuestion'">
                                    <v-list v-for="answer in question.answers" :key="answer.text">
                                        <v-checkbox class="ma-4" :label="answer.text"></v-checkbox>
                                    </v-list>
                                    <div v-if="question.ownAnswersAllowed">
                                        <v-checkbox
                                            v-model="enabled"
                                            hide-details
                                            class="ma-4"
                                            label="Eigene Antwort:"
                                        ></v-checkbox>
                                        <v-col>
                                            <v-text-field></v-text-field>
                                        </v-col>
                                    </div>
                                </div-->
                            </v-card>
                        </v-list>
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
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'Participant',
    layout: 'participant',
    data: () => ({
        enabled: false,
        questions: [],
    }),
    created() {
        this.showPoll()
    },
    computed: {
        ...mapGetters({ getPoll: 'participant/getPoll' }),
    },
    methods: {
        showPoll() {
            this.$store.dispatch('participant/showPoll')
        },
        ...mapActions({ showPoll: 'participant/showPoll' }),
    },
}
</script>

<style scoped></style>
