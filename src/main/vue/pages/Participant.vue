<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container>
            <v-subheader class="display-1"> Umfrage 1</v-subheader>
            <v-content>
                <v-row>
                    <v-col cols="8">
                        <!--v-list v-for="poll in getPoll.data" :key="poll.id"-->
                        <v-list v-for="category in getPoll[1].data.categoryList" :key="category.categoryId" two-line>
                            <v-list v-for="question in category.questionList" :key="question.questionId" two-line>
                                <v-card class="mx-auto">
                                    <v-card-title>
                                        <!--the visibility of the number of questions depends on the poll configuration-->
                                        <!--div v-if="getVisibility">{{ question.id }}/{{ questions.length }}</div-->
                                        {{ question.questionMessage }}
                                    </v-card-title>
                                    <div v-if="question.questionType === 'textfield'">
                                        <v-text-field label="Antwort"> </v-text-field>
                                    </div>
                                    <div v-else-if="question.questionType === 'choicebox'">
                                        <v-list v-for="answer in question.answerPossibilities" :key="answer.text">
                                            <v-checkbox class="ma-4" :label="answer"></v-checkbox>
                                        </v-list>
                                        <!--div v-if="question.ownAnswersAllowed">
                                                <v-checkbox
                                                    v-model="enabled"
                                                    hide-details
                                                    class="ma-4"
                                                    label="Eigene Antwort:"
                                                ></v-checkbox>
                                                <v-col>
                                                    <v-text-field></v-text-field>
                                                </v-col>
                                            </div-->
                                    </div>
                                </v-card>
                            </v-list></v-list
                        >
                        <!--/v-list-->
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
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'

export default {
    name: 'Participant',
    layout: 'participant',
    components: { AuthGate },
    data: () => ({
        enabled: false,
    }),
    created() {
        this.showPoll()
    },
    computed: {
        ...mapGetters({
            getPoll: 'participant/getPoll',
            isAuthenticated: 'login/isAuthenticated',
        }),
    },
    methods: {
        showPoll() {
            this.$store.dispatch('participant/showPoll')
        },
        // ...mapActions({ showPoll: 'participant/showPoll' }),
    },
}
</script>

<style scoped></style>
