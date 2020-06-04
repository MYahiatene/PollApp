<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="storeValid">
            <v-text-field class="display-1" v-model="pollname" />
            <v-container>
                <v-row>
                    <v-col cols="4">
                        <v-row>
                            <v-spacer />
                            <v-btn @click="createCategory()">
                                <v-icon color="primary">
                                    mdi-plus
                                </v-icon>
                                Kategorie
                            </v-btn>
                            <v-spacer />
                            <v-expansion-panels class="mt-4" :disabled="disableDrag" accordion multiple>
                                <draggable v-model="categorys" :disabled="disableDrag">
                                    <v-list v-for="category in categorys" :key="category.categoryId" two-line>
                                        <CategoryListElement
                                            :categoryID="category.categoryId"
                                            :pollID="pollId"
                                            @text-input="disableDraggable"
                                        />
                                    </v-list>
                                </draggable>
                            </v-expansion-panels>
                        </v-row>
                    </v-col>
                    <v-col>
                        <QuestionBuildWidget></QuestionBuildWidget>
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
import CategoryListElement from '../../components/CategoryListElement'

export default {
    name: 'QuestionOverview',
    components: { CategoryListElement, QuestionBuildWidget, draggable },
    data() {
        return {
            disableDrag: false,
        }
    },
    mounted() {
        this.setPollID(this.pollId)
        this.initialize()
    },
    computed: {
        ...mapGetters({
            poll: 'pollOverview/getPoll',
            isAuthenticated: 'login/isAuthenticated',
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
        categorys: {
            get() {
                return this.poll.categoryList
            },
            set(list) {
                this.updateCategorys(list)
            },
        },
        storeValid() {
            return this.poll.pollId !== undefined
        },
        /** updateQuestionList() {
            return this.$store.state.questionOverview.questionText
        }, */
    },
    methods: {
        ...mapActions({
            initialize: 'pollOverview/initialize',
            sendData: 'pollOverview/sendData',
            createCategory: 'pollOverview/createCategory',
        }),
        ...mapMutations({
            saveData: 'pollOverview/saveData',
            setPollName: 'pollOverview/setPollName',
            setPollID: 'pollOverview/setPollID',
            updateCategorys: 'pollOverview/updateCategoryOrder',
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
