<template>
    <AuthGate v-if="isAuthenticated !== true"></AuthGate>
    <v-container v-else-if="storeValid">
        <QuestionBuildWidget></QuestionBuildWidget>
        <v-card class="pa-2 ma-0">
            <v-text-field v-model="pollData.pollName" clearable class="display-1" />
        </v-card>
        <v-row>
            <v-col cols="2">
                <v-btn depressed @click="createCategory()" class="ma-5">
                    <v-icon color="primary">
                        mdi-plus
                    </v-icon>
                    Kategorie hinzufügen
                </v-btn>
            </v-col>
            <v-col cols="2">
                <v-btn depressed @click="activateCreateQuestion()" class="ma-5">
                    <v-icon color="primary">
                        mdi-plus
                    </v-icon>
                    Frage hinzufügen
                </v-btn>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="4">
                <v-expansion-panels multiple>
                    <v-expansion-panel v-for="category in categoryData" :key="category.categoryId">
                        <draggable :options="{ group: 'test' }">
                            <v-icon @click="editCat(category)">mdi-pencil</v-icon>
                            <v-icon @click="deleteCategory(category)">mdi-delete</v-icon>
                            <v-expansion-panel-header class="justify-self-start" disable-icon-rotate>
                                <h2>{{ category.categoryName }}</h2></v-expansion-panel-header
                            >
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
            </v-col>
            <v-col align="center" v-show="categoryIndex">
                <v-card max-width="500">
                    <v-card-title> Kategorie umbenennen</v-card-title>
                    <v-col>
                        <v-text-field v-model="tmpcategory.categoryName"></v-text-field>
                    </v-col>
                    <v-card-actions>
                        <v-btn color="primary" dark class="mb-2" @click="this.categoryIndex = false">Abbrechen</v-btn>
                        <v-spacer></v-spacer>
                        <v-btn color="primary" dark class="mb-2" @click="save(tmpcategory)">Sichern </v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
            <v-col v-if="questionCreationIndex">
                <v-card class="pa-1" :style="frameColor">
                    <div>
                        <v-card flat class="ma-0">
                            <v-container>
                                <v-row no-gutters>
                                    <v-col>
                                        <h3>Frage hinzufügen</h3>
                                    </v-col>
                                    <v-spacer></v-spacer>
                                    <v-spacer></v-spacer>
                                </v-row>
                                <v-row no-gutters>
                                    <v-textarea
                                        v-model="questionObj.questionMessage"
                                        label="Fragentext"
                                        hint="Was soll den Umfrageteilnehmer gefragt werden?"
                                        rows="1"
                                    >
                                    </v-textarea>
                                </v-row>
                                <v-row no-gutters>
                                    <v-overflow-btn
                                        v-model="questionObj.questionType"
                                        :items="questionWidgets"
                                        label="Fragenart"
                                    ></v-overflow-btn>
                                </v-row>
                                <v-card-actions>
                                    <v-col>
                                        <v-btn
                                            @click="
                                                createQuestion(questionObj.questionMessage, questionObj.questionType)
                                            "
                                        >
                                            <v-icon color="primary" left>
                                                mdi-plus
                                            </v-icon>
                                            Speichern
                                        </v-btn>
                                    </v-col>
                                    <v-col cols="2">
                                        <v-btn @click="questionCreationIndex = false">
                                            <v-icon color="primary" left>
                                                mdi-minus
                                            </v-icon>
                                            Abbrechen
                                        </v-btn>
                                    </v-col>
                                </v-card-actions>
                            </v-container>
                        </v-card>
                    </div>
                </v-card>
            </v-col>
            <v-col v-if="questionIndex !== 0" cols="8">
                <v-container>
                    <v-row>
                        <v-col>
                            <v-card class="pa-1" :style="frameColor" cols="6">
                                <div>
                                    <v-card flat class="ma-0">
                                        <v-container>
                                            <v-row no-gutters>
                                                <v-col>
                                                    <h3>Frage bearbeiten</h3>
                                                </v-col>
                                                <v-spacer></v-spacer>
                                                <v-spacer></v-spacer>
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
                                            <v-card-actions>
                                                <v-row>
                                                    <v-col cols="10">
                                                        <v-btn @click="editQuestion">
                                                            <v-icon color="primary" left>
                                                                mdi-pencil
                                                            </v-icon>
                                                            Speichern
                                                        </v-btn>
                                                    </v-col>
                                                    <v-col cols="6" md="6" sm="6" lg="2">
                                                        <v-btn @click="deleteQuestion">
                                                            <v-icon color="primary" left>
                                                                mdi-delete
                                                            </v-icon>
                                                            Löschen
                                                        </v-btn>
                                                    </v-col>
                                                </v-row>
                                            </v-card-actions>
                                        </v-container>
                                    </v-card>
                                </div>
                            </v-card>
                        </v-col>
                    </v-row>
                </v-container>
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
import QuestionBuildWidget from '../../components/QuestionBuildWidget'
// import CategoryListElement from '../../components/CategoryListElement'

export default {
    name: 'QuestionOverview',
    components: {
        QuestionBuildWidget,
        /* CategoryListElement, */
        draggable,
    },
    data() {
        return {
            disableDrag: false,
            questionCreationIndex: false,
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
            selectedQuestion: {},
            questionMessage: '',
            questionObj: { questionMessage: '', questionType: '' },
            questionIndex: 0,
            tmpcategory: {},
            categoryIndex: false,
            questionTitle: '',
            categoryTitle: '',
            questionType: '',
            defaultCategory: {
                categoryId: null,
                categoryName: '',
                pollId: null,
            },
            editCategory: {
                categoryId: null,
                categoryName: 'Neue Kategorie',
                pollId: this.$route.params.QuestionOverview,
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
                    console.log('CategoryData')
                    console.log(this.categoryData)
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        deleteQuestion() {
            this.$axios.post('/removeQuestion', {
                pollId: this.pollData.pollId,
                questionId: this.selectedQuestion.questionId,
            })
            console.log(this.pollData.pollId)
            console.log(this.selectedQuestion.questionId)
            this.categoryData.forEach((element) => {
                if (this.selectedQuestion.categoryId === element.categoryId) {
                    element.questionList.forEach((el) => {
                        if (el.questionId === this.selectedQuestion.questionId) {
                            element.questionList.slice(element.questionList.indexOf(el), 1)
                        }
                    })
                }
            })
            this.categoryData[0].questionList.forEach((question) => {
                console.log(question.questionId)
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
            this.questionIndex = 0
            this.questionCreationIndex = false
            this.categoryIndex = true
            this.tmpcategory = category
        },
        save(category) {
            this.$axios.put('/editcategory', {
                categoryId: category.categoryId,
                name: category.categoryName,
            })
            this.categoryIndex = false
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
        activateCreateQuestion() {
            this.questionIndex = 0
            this.categoryIndex = false
            this.questionCreationIndex = true
        },
        createQuestion(NewQuestionMessage, NewQuestionType) {
            if (NewQuestionMessage === '' || NewQuestionType === '') {
                alert('Feld ausfüllen!')
            } else {
                const question = {
                    pollId: this.pollData.pollId,
                    questionMessage: NewQuestionMessage,
                    questionType: NewQuestionType,
                    categoryId: this.categoryData.categoryId,
                    answerPossibilities: [],
                    userAnswers: false,
                    numberOfPossibleAnswers: 1,
                }
                this.$axios.post('/addquestion', question).then((response) => {
                    question.questionId = response.data.questionId
                })
                this.categoryData[0].questionList.push(question)
            }
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
            this.selectedQuestion = question
            this.categoryIndex = false
            this.questionCreationIndex = false
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
