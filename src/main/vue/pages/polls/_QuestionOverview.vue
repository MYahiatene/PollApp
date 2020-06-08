<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="storeValid">
            <v-card class="pa-2 ma-0"><v-text-field v-model="poll.pollName" clearable class="display-1" /></v-card>

            <v-container>
                <v-row>
                    <v-col cols="12" lg="4" md="4" sm="4">
                        <v-row>
                            <v-card v-for="(p, i) in poll.categoryList" :key="i" class="mt-1" width="110rem" outlined>
                                <draggable>
                                    <v-list-item three-line>
                                        <v-list-item-content>
                                            <v-list-item-title class="headline mb-1">{{
                                                p.categoryName
                                            }}</v-list-item-title>
                                        </v-list-item-content>
                                    </v-list-item>
                                    <v-list-item v-for="(l, j) in poll.categoryList" :key="j">{{
                                        l.questionList[j].questionMessage
                                    }}</v-list-item>

                                    <v-card-actions> </v-card-actions>
                                </draggable>
                            </v-card>
                        </v-row>
                    </v-col>
                    <v-col cols="12" lg="8" md="8" sm="8">
                        <v-card class="pa-1" :style="frameColor">
                            <QuestionBuildWidget></QuestionBuildWidget>
                            <v-card flat class="mt-1 pa-1">
                                <p class="ma-2">(Wähle eine Frage aus einer Kategorie, um sie zu bearbeiten.)</p>
                            </v-card>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
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
import { mapActions, mapGetters, mapMutations } from 'vuex'
import draggable from 'vuedraggable'
import QuestionBuildWidget from '../../components/QuestionBuildWidget'
// import CategoryListElement from '../../components/CategoryListElement'

export default {
    name: 'QuestionOverview',
    components: { /* CategoryListElement, */ QuestionBuildWidget, draggable },
    data() {
        return {
            disableDrag: false,
            poll: [],
        }
    },
    created() {
        this.loadCategories()
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getCategories: 'pollOverview/getCategory',
        }),
        pollId() {
            return parseInt(this.$route.params.QuestionOverview)
        },
        pollname: {
            get() {
                return this.poll.pollName
            },
            set(data) {
                this.setPollName(data)
            },
        },
        storeValid() {
            return this.pollId !== undefined
        },
        /** updateQuestionList() {
            return this.$store.state.questionOverview.questionText
        }, */

        // this needs to be computed, so we can get it from the nuxt Config

        frameColor() {
            return 'background-color:' + this.$vuetify.theme.currentTheme.softAccent + ';'
        },
    },
    methods: {
        pollName() {
            return this.poll.pollName
        },
        async loadCategories() {
            await this.$axios
                .post('/onepoll', { pollId: this.$route.params.QuestionOverview })
                .then((response) => {
                    this.poll = response.data
                })
                .catch((error) => {
                    console.log(error)
                })
            console.log(this.poll)
        },
        ...mapActions({
            sendData: 'pollOverview/sendData',
            createCategory: 'pollOverview/createCategory',
            setCategory: 'pollOverview/setCategory',
        }),
        ...mapMutations({
            saveData: 'pollOverview/saveData',
            setPollName: 'pollOverview/setPollName',
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
        disableDraggable(disable) {
            this.disableDrag = disable
        },
    },
}
</script>
