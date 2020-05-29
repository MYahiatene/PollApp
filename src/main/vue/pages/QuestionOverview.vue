<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="polls[0].pollId !== undefined">
            <v-subheader class="display-2">{{ poll.pollName }}</v-subheader>
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
                                    <v-subheader class="headline">{{ categorie.categoryName }}</v-subheader>
                                    <draggable v-model="categorie.questionList">
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
                    Der Umfrato-Server antwortet nicht
                    <v-spacer
                /></v-card-title>
            </v-card>
        </v-container>
    </div>
</template>

<script>
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
        questions: {
            get() {
                return null
            },
            set(data) {},
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
        ...mapMutations({ saveData: 'pollOverview/sendData' }),
        createCategory() {},
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
