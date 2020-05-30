<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="polls.length > 0">
            <v-text-field class="display-1" v-model="pollname" />
            <v-content>
                <v-row>
                    <v-col cols="4">
                        <v-container>
                            <v-row class="ma-n4">
                                <v-list
                                    v-for="categorie in poll.categoryList"
                                    :key="categorie.categoryId"
                                    two-line
                                    class="pa-3"
                                >
                                    <v-text-field class="headline" v-model="categorie.categoryName"></v-text-field>
                                    <draggable :list="categorie.questionList">
                                        <v-list v-for="question in categorie.questionList" :key="question.questionId">
                                            <question-list-element
                                                :poll-id="poll.pollId"
                                                :category-id="categorie.categoryId"
                                                :question-id="question.questionId"
                                                :question-message="question.questionMessage"
                                                :question-type="question.questionType"
                                            ></question-list-element>
                                        </v-list>
                                    </draggable>
                                </v-list>
                            </v-row>
                        </v-container>
                        <div class="ma-8" @click="createCategory()">
                            <v-btn depressed small>Neue Kategorie</v-btn>
                        </div>
                    </v-col>
                    <v-col>
                        <QuestionBuildWidget></QuestionBuildWidget>
                    </v-col>
                </v-row>
            </v-content>
        </v-container>
        <v-container v-else>
            <v-card>
                <v-card-title>
                    <v-spacer />
                    <v-icon>
                        mdi-sync-alert
                    </v-icon>
                    Der Umfrato-Server hat noch nicht geantwortet
                    <v-spacer
                /></v-card-title>
            </v-card>
        </v-container>
    </div>
</template>

<script>
/* TODO: Draggable fixen (auch Design), Question & Category hinzufügen, Daten updaten, Restliche Fragetypen anbinden
    (Zum Server und zurück? oder zum Server und synchron rekonstruieren?)
 *   */
import { mapActions, mapGetters, mapMutations } from 'vuex'
import draggable from 'vuedraggable'
import QuestionBuildWidget from '../components/QuestionBuildWidget'
import QuestionListElement from '../components/QuestionListElement'

export default {
    name: 'QuestionOverview',
    components: { QuestionListElement, QuestionBuildWidget, draggable },
    data() {
        return {
            pollId: 1,
        }
    },
    mounted() {
        this.initialize()
    },
    computed: {
        ...mapGetters({
            polls: 'pollOverview/getPolls',
            isAuthenticated: 'login/isAuthenticated',
        }),
        pollname: {
            get() {
                return this.poll.pollName
            },
            set(data) {
                this.setPollName(data)
            },
        },
        poll() {
            for (let i = 0; i < this.polls.length; i++) {
                if (this.polls[i].pollId === this.pollId) {
                    return this.polls[i]
                }
            }
            return null
        },
        /** updateQuestionList() {
            return this.$store.state.questionOverview.questionText
        }, */
    },
    methods: {
        ...mapActions({ initialize: 'pollOverview/initialize', sendData: 'pollOverview/sendData' }),
        ...mapMutations({
            saveData: 'pollOverview/sendData',
            setPollName: 'pollOverview/setPollName',
            createCategory: 'pollOverview/createCategory',
        }),
        saveState() {
            const payload = {
                pollId: '',
                questionMessage: this.test,
                answerPossibilities: [],
                questionType: '',
            }
            this.saveData(payload)
        },
        sendState() {
            this.sendData()
        },
    },
}
</script>
