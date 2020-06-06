<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="storeValid">
            <v-card class="pa-2 ma-0"
                ><v-text-field clearable placeholder="Titel der Umfrage" class="display-1" v-model="pollname"
            /></v-card>

            <v-container>
                <v-row>
                    <v-col cols="12" lg="4" md="4" sm="4">
                        <v-row>
                            <v-card class="ma-0">
                                <v-card-title>
                                    <h2 style="font-weight: normal;" class="ma-0">Kategorien</h2>

                                    <v-spacer></v-spacer>

                                    <v-btn depressed @click="createCategory(this.pollId)" class="ml-11">
                                        <v-icon color="primary">
                                            mdi-plus
                                        </v-icon>
                                        <v-spacer></v-spacer>
                                        Hinzufügen
                                    </v-btn>
                                </v-card-title>
                                <v-divider></v-divider>
                                <v-card-text>
                                    <v-row>
                                        <!--                                        negative margin in order to cancel out the prior waste of space-->
                                        <v-col cols="12" lg="12" md="12" sm="12">
                                            <v-expansion-panels tile multiple :disabled="disableDrag" class="mt-n6">
                                                <draggable v-model="categories" :disabled="disableDrag">
                                                    <v-list
                                                        v-for="category in categories"
                                                        :key="category.categoryId"
                                                        two-line
                                                    >
                                                        <CategoryListElement
                                                            :categoryID="category.categoryId"
                                                            :pollID="pollId"
                                                            @text-input="disableDraggable"
                                                        />
                                                    </v-list>
                                                </draggable>
                                            </v-expansion-panels>
                                        </v-col>
                                    </v-row>
                                </v-card-text>
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
import CategoryListElement from '../../components/CategoryListElement'

export default {
    name: 'QuestionOverview',
    components: { CategoryListElement, QuestionBuildWidget, draggable },
    data() {
        return {
            disableDrag: false,
            categories: [],
        }
    },
    created() {
        this.loadCategories()
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
        async loadCategories() {
            await this.$axios
                .get('/categories', null, { pollId: '1' })
                .then((response) => {
                    console.log(response)
                    console.log(response.data)
                    this.categories = response.data
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        ...mapActions({
            sendData: 'pollOverview/sendData',
            createCategory: 'pollOverview/createCategory',
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
