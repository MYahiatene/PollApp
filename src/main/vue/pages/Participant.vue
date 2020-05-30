<template>
    <div v-if="getPoll[1] !== undefined" @change="fontColor">
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container>
            <div v-if="getPoll[1].data.logo !== undefined">
                <img :src="getPoll[1].data.logo" alt="failedToLoadLogo" />
            </div>
            <v-subheader class="display-1 font-weight-bold" :style="fontColorText"> Umfrage 1</v-subheader>
            <v-content>
                <v-row>
                    <v-col cols="8">
                        <!--v-list v-for="poll in getPoll.data" :key="poll.id"-->
                        <v-list
                            v-for="question in getPoll[1].data.categoryList[0].questionList"
                            :key="question.questionId"
                            two-line
                        >
                            <!--v-list v-for="question in category.questionList" :key="question.questionId" two-line-->
                            <v-card class="mx-auto">
                                <v-card-title class="col" :style="fontColorText">
                                    <!--the visibility of the number of questions depends on the poll configuration-->
                                    <div v-if="getVisibility">{{ getQuestionIndex() }}/{{ getNumberOfQuestions }}</div>
                                    <div class="ps-4">{{ question.questionMessage }}</div>
                                </v-card-title>
                                <div v-if="question.questionType === 'textfield'">
                                    <v-text-field label="Antwort" :color="fontColor" @input="saveAnswerField">
                                    </v-text-field>
                                </div>
                                <div v-else-if="question.questionType === 'choicebox'">
                                    <v-list v-for="answer in question.answerPossibilities" :key="answer.text">
                                        <v-checkbox
                                            class="ma-4 red--text"
                                            :label="answer"
                                            :color="fontColor"
                                            @change="saveAnswerCheckbox"
                                        ></v-checkbox>
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
                            </v-card> </v-list
                        ><!--/v-list-->
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
const axios = require('axios')
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    headers: {
        Authorization:
            'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRicmV0dG1hbm4iLCJleHAiOjE1OTExMTAzMzQsInJvbCI6WyJST0xFX1BPTExfQ1JFQVRPUiJdfQ.Q6A2ST5I5Ix8_8jfsgxc3ZQq9GG7i88w_bJPlfEYA-QiAavpUhPbjFoUQWd9vZ93Xqzvm4oCw23bJ1NGtp2ucw',
        'X-Custom-Header': 'foobar',
    },
})
export default {
    name: 'Participant',
    layout: 'participant',
    components: { AuthGate },
    data: () => ({
        questionIndex: 1,
        enabled: false,
        answerList: [],
        // for checkbox this should be initialised with questionAnswers false/null at least for multiple choice options
        // but probably for all of them
    }),
    created() {
        this.showPoll()
    },
    computed: {
        ...mapGetters({
            getPoll: 'participant/getPoll',
            isAuthenticated: 'login/isAuthenticated',
            getVisibility: 'participant/getVisibility',
            getNumberOfQuestions: 'participant/getNumberOfQuestions',
        }),
        fontColor() {
            return this.getPoll[1].data.fontColor
        },
        fontColorText() {
            return 'color:' + this.getPoll[1].data.fontColor
        },
    },
    methods: {
        showPoll() {
            this.$store.dispatch('participant/showPoll')
        },
        saveAnswers() {
            const answers = {
                answerList: this.answerList,
            }
            instance.post('/poll/addAnswer', answers)
        },
        getQuestionIndex() {
            this.questionIndex += 1
            return this.questionIndex - 1
        },
        saveAnswerCheckbox(e) {
            // this.answerList.append(e.payload[0])
        },
        saveAnswerField(e) {
            // this.answerList.append(e.payload[0])
        },
        // can I use giveAnswer, from AnswerService here? Does it get updated after every answer(every click or
        // every symbol input for field texts)? Need PollID(not a problem) and questionId it's not sent with
        // change event, but maybe give it as another parameter?
    },
}
</script>

<style scoped></style>
