<!--this widget enables the user to configure a general question, including all of its common features, to specify-->
<!--the final question type and to preview (wysiwyg) the final question as it will be displayed to the participants-->
<template>
    <div v-if="buildIndex > 0">
        <div>
            <v-card flat class="ma-0">
                <v-container>
                    <v-row no-gutters>
                        <v-col>
                            <h3>{{ header }}</h3>
                        </v-col>
                    </v-row>
                    <v-row no-gutters>
                        <v-textarea
                            v-model="questionMessageModel"
                            label="Fragentext"
                            hint="Was soll den Umfrageteilnehmer gefragt werden?"
                            rows="1"
                        />
                    </v-row>
                    <v-row no-gutters>
                        <v-select
                            v-model="categoryType"
                            :items="categoryNames"
                            label="Zu welcher Kategorie soll die Frage hinzugefügt werden?"
                        />
                    </v-row>
                    <v-row no-gutters>
                        <v-model v-model="fileNameModel">
                            {{ 'Angehängte Datei: ' + fileNameModel }}
                            <v-btn text x-small @click="removeFile">{{ this.removeButtonText }}</v-btn>
                        </v-model>
                    </v-row>
                    <v-row no-gutters>
                        <input type="file" name="file" id="file" @change="uploadFile" />
                    </v-row>
                    <v-row no-gutters>
                        <v-select v-model="questionType" :items="questionWidgets" label="Fragenart" />
                    </v-row>
                    <v-row>
                        <component
                            :questionData="questionData"
                            :is="questionType"
                            :categoryData="categoryData"
                            :buildIndex:="buildIndex"
                            :categoryType="categoryType"
                        />
                    </v-row>
                    <v-row>
                        <v-col cols="8" />
                        <v-col>
                            <v-btn :disabled="questionMessageBool" @click="createQuestion">
                                <v-icon color="primary" left> mdi-plus </v-icon>
                                Speichern
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-container>
            </v-card>
        </div>
    </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import ChoiceQuestion from './ChoiceQuestion'
import TextQuestion from './TextQuestion'
import RangeQuestion from './RangeQuestion'
import SortQuestion from './SortQuestion'
import SliderQuestion from './SliderQuestion'

export default {
    name: 'QuestionBuildWidget',
    components: { ChoiceQuestion, TextQuestion, SortQuestion, RangeQuestion, SliderQuestion },
    props: {
        categoryData: { type: Array },
        categoryNames: {
            type: Array,
        },
    },
    data() {
        return {
            categories: [],
            questionData: { questionTypeChoice: this.questionTypeChoice },
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
            file: null,
            removeButtonText: 'X',
        }
    },
    computed: {
        ...mapGetters({
            getQuestion: 'questionOverview/getQuestion',
            questionToLoad: 'pollOverview/questionToLoad',
            getQuestionMessage: 'questionOverview/getQuestionMessage',
            getBuildIndex: 'questionOverview/getBuildIndex',
            getSaveButtonStatus: 'questionOverview/getSaveButtonStatus',
            getCategoryType: 'questionOverview/getCategoryType',
            getUsername: 'login/getUsername',
            getFileName: 'questionOverview/getFileName',
        }),
        questionMessageBool: {
            get() {
                if (
                    this.questionType === 'TextQuestion' ||
                    this.questionType === 'RangeQuestion' ||
                    this.questionType === 'SliderQuestion'
                ) {
                    return !(this.questionType.length && this.questionMessageModel.length && this.getSaveButtonStatus)
                } else {
                    return !(
                        this.questionType.length &&
                        this.questionMessageModel.length &&
                        this.getQuestion.answerPossibilities.length &&
                        this.getSaveButtonStatus
                    )
                }
            },
        },
        buildIndex: {
            get() {
                return this.getBuildIndex
            },
            set(value) {
                this.setBuildIndex(value)
            },
        },
        header() {
            return this.buildIndex === 1 ? 'Frage erstellen' : 'Frage editieren'
        },
        questionMessageModel: {
            get() {
                return this.getQuestionMessage
            },
            set(value) {
                this.setQM(value)
            },
        },
        questionType: {
            get() {
                return this.getQuestion.questionType
            },
            set(value) {
                this.setQuestionType(value)
            },
        },
        categoryType: {
            get() {
                let returnValue = null
                this.categoryNames.forEach((category) => {
                    if (category.value === this.getQuestion.categoryId) {
                        returnValue = category
                    }
                })
                return returnValue
            },
            set(value) {
                this.setCategoryType(value)
            },
        },
        fileNameModel: {
            get() {
                return this.getQuestion.fileName
            },
            set() {
                this.setFileName(this.fileName)
            },
        },
    },
    methods: {
        async uploadFile(event) {
            const tmpfile = event.target.files[0]
            if (tmpfile.name.includes('.jpg') || tmpfile.name.includes('.mp3')) {
                const file = event.target.files[0]
                this.fileName = file.name
                this.fileNameModel = this.fileName
                const formdata = new FormData()
                formdata.append('file', file)
                await this.$axios
                    .post('/upload', formdata, { headers: { 'Content-Type': 'multipart/form-data' } })
                    .then(function (res) {
                        console.log(res)
                    })
            } else {
                console.log('not allowed file extension')
            }
        },
        removeFile() {
            this.$axios
                .post('/removeFile/' + this.fileNameModel, {
                    fileName: this.fileNameModel,
                })
                .then((response) => {
                    console.log(response)
                })
            this.fileName = ''
            this.removeButtonText = 'entfernt'
            this.fileNameModel = ''
        },
        async createQuestion() {
            if (this.buildIndex === 1) {
                await this.$axios
                    .post('/addquestion', {
                        pollId: this.$route.params.QuestionOverview,
                        questionType: this.getQuestion.questionType,
                        questionMessage: this.getQuestion.questionMessage,
                        answerPossibilities: this.getQuestion.answerPossibilities,
                        numberOfPossibleAnswers: this.getQuestion.numberOfPossibleAnswers,
                        userAnswers: this.getQuestion.userAnswers,
                        textMultiline: this.getQuestion.textMultiline,
                        textMinimum: this.getQuestion.textMinimum,
                        textMinBool: this.getQuestion.textMinBool,
                        textMaximum: this.getQuestion.textMaximum,
                        textMaxBool: this.getQuestion.textMaxBool,
                        endValue: this.getQuestion.endValue,
                        startValue: this.getQuestion.startValue,
                        stepSize: this.getQuestion.stepSize,
                        belowMessage: this.getQuestion.belowMessage,
                        aboveMessage: this.getQuestion.aboveMessage,
                        hideValues: this.getQuestion.hideValues,
                        dropDown: this.getQuestion.dropDown,
                        categoryType: this.getQuestion.categoryType,
                        fileName: this.fileName,
                    })
                    .then((response) => {
                        this.categoryData.forEach((category) => {
                            if (category.categoryId === response.data.categoryId) {
                                category.questionList.push(response.data)
                            }
                        })
                        this.file = null
                        this.fileName = null
                    })
                const obj = {
                    lastEditFrom: this.getUsername,
                    pollId: this.$route.params.QuestionOverview,
                }
                await this.$axios.post('/newLastEdit', obj)
            } else {
                await this.$axios
                    .put('/editquestion', {
                        pollId: this.$route.params.QuestionOverview,
                        questionType: this.getQuestion.questionType,
                        questionMessage: this.getQuestion.questionMessage,
                        answerPossibilities: this.getQuestion.answerPossibilities,
                        numberOfPossibleAnswers: this.getQuestion.numberOfPossibleAnswers,
                        userAnswers: this.getQuestion.userAnswers,
                        questionId: this.getQuestion.questionId,
                        textMultiline: this.getQuestion.textMultiline,
                        textMinimum: this.getQuestion.textMinimum,
                        textMinBool: this.getQuestion.textMinBool,
                        textMaximum: this.getQuestion.textMaximum,
                        textMaxBool: this.getQuestion.textMaxBool,
                        endValue: this.getQuestion.endValue,
                        startValue: this.getQuestion.startValue,
                        stepSize: this.getQuestion.stepSize,
                        belowMessage: this.getQuestion.belowMessage,
                        aboveMessage: this.getQuestion.aboveMessage,
                        hideValues: this.getQuestion.hideValues,
                        dropDown: this.getQuestion.dropDown,
                        categoryType: this.getQuestion.categoryType,
                        fileName: this.fileName,
                    })
                    .then((response) => {
                        let questionExists = false
                        this.categoryData.forEach((category) => {
                            if (category.categoryId === response.data.categoryId) {
                                category.questionList.forEach((question) => {
                                    if (question.questionId === response.data.questionId) {
                                        // console.log('id gefunden')
                                        questionExists = true
                                    }
                                })
                            }
                        })
                        if (!questionExists) {
                            this.categoryData.forEach((category) =>
                                category.questionList.forEach((question) => {
                                    if (question.questionId === this.getQuestion.questionId) {
                                        category.questionList.splice(category.questionList.indexOf(question), 1)
                                    }
                                })
                            )
                            this.categoryData.forEach((category) => {
                                if (category.categoryId === response.data.categoryId) {
                                    category.questionList.push(response.data)
                                }
                            })
                        }
                        this.file = null
                        this.fileName = null
                    })
                const obj = {
                    lastEditFrom: this.getUsername,
                    pollId: this.$route.params.QuestionOverview,
                }
                await this.$axios.post('/newLastEdit', obj)
            }
            this.setBuildIndex(0)
            this.removeButtonText = 'X'
        },
        ...mapMutations({
            setCategoryType: 'questionOverview/setCategoryType',
            setSaveButtonStatus: 'questionOverview/setSaveButtonStatus',
            setQuestionMessage: 'pollOverview/setQuestionMessage',
            setQuestionType: 'questionOverview/setQuestionType',
            setBuildIndex: 'questionOverview/setBuildIndex',
            setQuestion: 'questionOverview/setQuestion',
            setFileName: 'questionOverview/setFileName',
        }),
        ...mapActions({
            setQM: 'questionOverview/setQuestionMessage',
            deleteQuestion: 'pollOverview/deleteQuestion',
            setFileName: 'questionOverview/setFileName',
        }),
    },
}
</script>

<style scoped></style>
