<template>
    <AuthGate v-if="isAuthenticated !== true"></AuthGate>
    <v-container v-else-if="storeValid">
        <v-card class="pa-2 ma-0">
            <v-text-field v-model="pollData.pollName" clearable class="display-1" />
        </v-card>
        <v-row>
            <v-col cols="12" lg="4" md="4" sm="4">
                <v-row>
                    <v-btn depressed @click="createCategory()" class="ma-5">
                        <v-icon color="primary">
                            mdi-plus
                        </v-icon>
                        Kategorie hinzufügen
                    </v-btn>
                    <v-spacer></v-spacer>
                    <v-btn depressed @click="createQuestion()" class="ma-5">
                        <v-icon color="primary">
                            mdi-plus
                        </v-icon>
                        Frage hinzufügen
                    </v-btn>

                    <v-expansion-panels multiple>
                        <v-expansion-panel v-for="category in categoryData" :key="category.categoryId">
                            <draggable :options="{ group: 'test' }">
                                <v-expansion-panel-header>{{ category.categoryName }}</v-expansion-panel-header>
                                <draggable :options="{ group: 'test' }">
                                    <v-expansion-panel-content
                                        v-for="question in category.questionList"
                                        :key="question.questionId"
                                    >
                                        <v-list>
                                            <v-list-item @click="selectQuestion(question, question.questionType)"
                                                >{{ question.questionMessage }}
                                            </v-list-item>
                                        </v-list>
                                    </v-expansion-panel-content>
                                </draggable>
                            </draggable>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </v-row>
            </v-col>

            <v-col cols="12" lg="8" md="8" sm="8">
                <v-card class="pa-1" :style="frameColor">
                    <div v-if="questionIndex !== 0">
                        <v-card flat class="ma-0">
                            <v-container>
                                <v-row no-gutters>
                                    <v-col>
                                        <h3>Frage bearbeiten</h3>
                                    </v-col>
                                    <v-spacer></v-spacer>
                                    <v-spacer></v-spacer>
                                    <v-col>
                                        <v-btn @click="deleteQuestion">
                                            <v-icon color="primary" left>
                                                mdi-delete
                                            </v-icon>
                                            Löschen
                                        </v-btn>
                                    </v-col>
                                </v-row>
                                <v-row no-gutters>
                                    <v-textarea
                                        v-model="questionMessage"
                                        label="Fragentext"
                                        hint="Was soll den Umfrageteilnehmer gefragt werden?"
                                        rows="1"
                                    >
                                    </v-textarea>
                                </v-row>
                                <v-row no-gutters>
                                    <v-overflow-btn
                                        v-model="questionTypeChoice"
                                        :items="questionWidgets"
                                        label="Fragenart"
                                    ></v-overflow-btn>
                                </v-row>
                                <v-row></v-row>
                            </v-container>
                        </v-card>
                    </div>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
    <v-container v-else>
        <v-card>
            <v-card-title>
                <v-spacer />
                <v-icon>
                    mdi-sync-alert
                </v-icon>
                Der Umfrato-Server hat noch nicht geantwortet
                <v-spacer />
            </v-card-title>
        </v-card>
    </v-container>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import draggable from 'vuedraggable'
// import QuestionBuildWidget from '../../components/QuestionBuildWidget'
// import CategoryListElement from '../../components/CategoryListElement'

export default {
    name: 'QuestionOverview',
    components: {
        /* CategoryListElement, */
        /* QuestionBuildWidget */
        draggable,
    },
    data() {
        return {
            editedIndex: -1,
            disableDrag: false,
            pollData: [],
            categoryData: [],
            questionListData: [],
            test: [],
            questionWidgets: [
                {
                    text: 'Auswahlfrage',
                    value: 'ChoiceQuestion',
                },
                {
                    text: 'Freitextfrage',
                    value: 'TextQuestion',
                },
                {
                    text: 'Intervallfrage',
                    value: 'RangeQuestion',
                },
            ],
            questionMessage: '',
            questionIndex: 0,
            categoryIndex: 0,
            questionTitle: '',
            categoryTitle: '',
            questionType: '',
            defaultCategory: {
                categoryId: null,
                categoryName: '',
                pollId: null,
                questionList: [],
            },
            editCategory: {
                categoryId: null,
                categoryName: 'Neue Kategorie',
                pollId: this.$route.params.QuestionOverview,
                questionList: [],
            },
            categoryDialog: false,
        }
    },
    created() {
        this.loadPoll()
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
                return this.pollData.pollName
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
            return this.pollData.pollName
        },
        async loadPoll() {
            await this.$axios
                .get('/getonepoll', {
                    params: {
                        pollId: this.$route.params.QuestionOverview,
                    },
                })
                .then((response) => {
                    this.pollData = response.data
                    console.log(this.pollData)
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        async loadCategories() {
            await this.$axios
                .get('/getallcategories', {
                    params: {
                        pollId: this.$route.params.QuestionOverview,
                    },
                })
                .then((response) => {
                    response.data.forEach((ele) => {
                        const obj = {}
                        this.test = response.data
                        obj.categoryId = ele.categoryId
                        obj.categoryName = ele.categoryName
                        obj.questionList = ele.questionList
                        this.categoryData.push(obj)
                    })
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        createCategory() {
            this.$axios
                .post('/addcategory', {
                    pollId: this.$route.params.QuestionOverview,
                    name: this.editCategory.categoryName,
                })
                .then((response) => {
                    this.editCategory = response.data
                    console.log(this.editCategory)
                    this.defaultCategory = Object.assign({}, this.editCategory)
                    this.categoryData.push(this.defaultCategory)
                    console.log(this.categoryData)
                })
        },
        editCat(category) {
            this.editedIndex = this.categoryData.indexOf(category)
            this.editCategory = Object.assign({}, category)
            this.categoryDialog = true
        },
        editCategoryData(categoryID, categoryName) {
            this.$axios.put('/editcategory', {
                categoryId: categoryID,
                name: categoryName,
            })
        },
        save() {
            Object.assign(this.categoryData[this.editedIndex], this.editCategory)
            this.editCategoryData(this.editCategory.categoryId, this.editCategory.categoryName)
            this.close()
        },
        close() {
            this.categoryDialog = false
            this.$nextTick(() => {
                this.editCategory = Object.assign({}, this.defaultCategory)
                this.editedIndex = -1
            })
        },
        deleteCategory(category) {
            const index = this.categoryData.indexOf(category)
            confirm('Sind sie sich sicher, dass sie diese Kategorie löschen möchten?') &&
                this.categoryData.splice(index, 1) &&
                this.$axios
                    .put('/deletecategory', {
                        categoryId: category.categoryId,
                    })
                    .catch((error) => {
                        console.log(error)
                    })
        },
        ...mapActions({
            sendData: 'pollOverview/sendData',
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
        selectQuestion(question, questionType) {
            this.questionIndex = 1
            this.questionMessage = question.questionMessage

            switch (questionType) {
                case 'ChoiceQuestion':
                    return 'ChoiceQuestion'
                case 'TextQuestion':
                    return 'TextQuestion'
                case 'RangeQuestion':
                    return 'RangeQuestion'
                default:
                    return ''
            }
        },
    },
}
</script>
