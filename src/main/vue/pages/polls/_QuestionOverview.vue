<template>
    <div v-if="this.pollData.pollStatus === 0">
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="storeValid">
            <v-card class="pa-2 ma-0">
                <v-text-field class="display-1" v-model="pollData.pollName" @focusout="editPollName(pollData)" />
            </v-card>

            <v-container>
                <v-row>
                    <v-col cols="12" lg="4" md="4" sm="4">
                        <v-row>
                            <v-card class="ma-0" min-width="400" max-width="500">
                                <v-row>
                                    <v-spacer></v-spacer>
                                    <v-col cols="6"
                                        ><v-card-title>
                                            <h2 style="font-weight: normal; text-align: center;" class="ma-0">
                                                Kategorien
                                            </h2>
                                        </v-card-title></v-col
                                    >
                                    <v-spacer></v-spacer
                                ></v-row>
                                <v-row>
                                    <v-col cols="6">
                                        <v-btn depressed @click="createCategory()" class="ml-11">
                                            <v-icon color="primary">
                                                mdi-plus
                                            </v-icon>
                                            Kategorie
                                        </v-btn>
                                    </v-col>
                                    <v-col cols="6">
                                        <v-btn @click="addQuestion()">
                                            <v-icon color="primary" left>mdi-plus</v-icon>
                                            Frage
                                        </v-btn>
                                    </v-col>
                                </v-row>
                                <v-divider></v-divider>
                                <v-container>
                                    <v-row>
                                        <v-col cols="12">
                                            <v-card-text>
                                                <v-row>
                                                    <!--                                        negative margin in order to cancel out the prior waste of space-->
                                                    <v-col cols="12" lg="12" md="12" sm="12">
                                                        <v-expansion-panels
                                                            tile
                                                            multiple
                                                            :disabled="disableDrag"
                                                            class="mt-n6"
                                                        >
                                                            <v-list
                                                                v-for="category in categoryData"
                                                                :key="category.categoryId"
                                                                two-line
                                                            >
                                                                <v-col cols="13">
                                                                    <CategoryListElement
                                                                        :buildIndex="buildIndex"
                                                                        :categoryID="category.categoryId"
                                                                        :pollID="pollData.pollId"
                                                                        :questions="category.questionList"
                                                                        :pollData="pollData"
                                                                        :category="category"
                                                                        :categoryData="categoryData"
                                                                        @text-input="disableDraggable"
                                                                    />
                                                                </v-col>
                                                            </v-list>
                                                        </v-expansion-panels>
                                                    </v-col>
                                                </v-row>
                                            </v-card-text>
                                        </v-col>
                                    </v-row>
                                </v-container>
                            </v-card>
                        </v-row>
                    </v-col>
                    <v-col cols="12" lg="8" md="8" sm="8">
                        <v-card v-show="buildIndex > 0" class="pa-1" :style="frameColor">
                            <QuestionBuildWidget :categoryData="categoryData"></QuestionBuildWidget>
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
                    <v-spacer />
                </v-card-title>
            </v-card>
        </v-container>
    </div>
    <div v-else>
        <v-card>
            <v-card-title>Die Umfrage ist aktiv und daher nicht bearbeitbar</v-card-title>
        </v-card>
    </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import CategoryListElement from '../../components/CategoryListElement'
import QuestionBuildWidget from '../../components/QuestionBuildWidget'

export default {
    name: 'QuestionOverview',
    components: { CategoryListElement, QuestionBuildWidget },
    data() {
        return {
            editIndex: false,
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
                    text: 'Sortierfrage',
                    value: 'SortQuestion',
                },
                {
                    text: 'Sliderfrage',
                    value: 'SliderQuestion',
                },
                {
                    text: 'Reichweitenfrage',
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
        this.setBuildIndex(0)
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getCategories: 'pollOverview/getCategory',
            getBuildIndex: 'questionOverview/getBuildIndex',
        }),
        pollId() {
            return parseInt(this.$route.params.QuestionOverview)
        },
        buildIndex: {
            get() {
                return this.getBuildIndex
            },
            set(value) {
                this.setBuildIndex(value)
            },
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

        frameColor() {
            return 'background-color:' + this.$vuetify.theme.currentTheme.softAccent + ';'
        },
    },
    methods: {
        editPollName(pollData) {
            this.$axios.put('/editpollname', { pollId: pollData.pollId, pollName: pollData.pollName })
        },
        addQuestion() {
            this.setQuestion({
                categoryId: null,
                questionMessage: '',
                answerPossibilities: [],
                questionType: '',
                userAnswers: false,
                numberOfPossibleAnswers: 1,
                endValue: 10,
                startValue: 0,
                stepSize: 1,
                belowMessage: '',
                aboveMessage: '',
                hideValues: false,
                textMultiline: false,
                textMinimum: 0,
                textMinBool: false,
                textMaximum: 1000,
                textMaxBool: false,
                choiceType: '',
                questionId: null,
            })
            this.buildIndex = 1
        },
        editQuestion(question) {
            this.$axios.post('/editquestion', {
                questionId: this.selectedQuestion.questionId,
                answerPossibilities: this.selectedQuestion.answerPossibilities,
                numberOfPossibleAnswers: this.selectedQuestion.numberOfPossibleAnswers,
                questionMessage: this.selectedQuestion.questionMessage,
                questionType: this.selectedQuestion.questionType,
            })
            this.questionIndex = 0
        },
        onEnd(evt) {
            /*
                console.log('old drag index:\n' + evt.oldDraggableIndex + '\n')
                console.log('new drag index:\n' + evt.newDraggableIndex + '\n')
                console.log('old index:\n' + evt.oldIndex + '\n')
                console.log('new index:\n' + evt.newIndex + '\n') */
            console.log(evt)
            if (evt.pullMode) {
                //  this.$axios.post('/changequestioncategory')
            }
            // console.log(this.categoryData)
        },
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
            this.$axios.put('/removeQuestion', {
                pollId: this.pollData.pollId,
                questionId: this.selectedQuestion.questionId,
                categoryId: this.selectedQuestion.categoryId,
            })
            this.categoryData.forEach((element) => {
                if (this.selectedQuestion.categoryId === element.categoryId) {
                    element.questionList.forEach((el) => {
                        if (el.questionId === this.selectedQuestion.questionId) {
                            element.questionList.splice(element.questionList.indexOf(el), 1)
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
                    categoryName: this.editCategory.categoryName,
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
        activateCreateQuestion() {
            this.questionIndex = 0
            this.categoryIndex = false
            this.questionCreationIndex = true
        },
        createQuestion(NewQuestionMessage, NewQuestionType) {
            this.buildIndex = 1
            /* if (NewQuestionMessage === '' || NewQuestionType === '') {
                    alert('Feld ausfüllen!')
                } else {
                    const question = {
                        pollId: this.pollData.pollId,
                        questionMessage: NewQuestionMessage,
                        questionType: NewQuestionType,
                        categoryId: this.pollData.categoryList[0].categoryId,
                        answerPossibilities: [],
                        userAnswers: false,
                        numberOfPossibleAnswers: 1,
                    }
                    this.$axios.post('/addquestion', question).then((response) => {
                        question.questionId = response.data.questionId
                    })
                    this.categoryData[0].questionList.push(question)
                } */
        },
        ...mapActions({
            sendData: 'pollOverview/sendData',
            setCategory: 'pollOverview/setCategory',
        }),
        ...mapMutations({
            saveData: 'pollOverview/saveData',
            setPollName: 'pollOverview/setPollName',
            setPollID: 'pollOverview/setPollID',
            updateCategorys: 'pollOverview/updateCategoryOrder',
            setBuildIndex: 'questionOverview/setBuildIndex',
            setQuestion: 'questionOverview/setQuestion',
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
