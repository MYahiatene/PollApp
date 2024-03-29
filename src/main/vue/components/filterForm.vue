<!--This page provides the UI to pick out filters that are used on a survey-->

<template>
    <v-dialog v-model="dialog" overlay-color="background" persistent width="500" overlay-opacity="0.95" fullscreen>
        <template v-slot:activator="{ on }">
            <v-btn color="primary" v-on="on" class="ma-2" @click="getFilters"> Analyse </v-btn>
        </template>

        <v-card class="ma-2 pa-2">
            <v-card-title>Erweiterte Analyse von {{ pollName }}</v-card-title>
            <template>
                <!--                if we have a seriesPoll here the user can pick how many polls of that series he wants to Analyse. He can only include polls that happend before the current poll.-->
                <div v-if="poll">
                    <v-slider
                        class="ml-4 mr-8"
                        v-if="isSeriesPoll"
                        v-model="seriesPollNumber"
                        min="0"
                        :max="poll.seriesCounter - 1"
                        label="Anzahl der betrachteten vorhergegangenen Serienumfragen:"
                        :thumb-label="true"
                    />
                </div>

                <!--                we have an expansion panel for each type of filter-->

                <v-expansion-panels accordion multiple hover>
                    <v-expansion-panel>
                        <v-expansion-panel-header> Fragenauswahl </v-expansion-panel-header>
                        <v-expansion-panel-content>
                            Hier können Sie die Fragen auswählen, die in der Analyse betrachtet werden sollen.

                            <v-container fluid>
                                <v-select
                                    v-model="selectedQuestions"
                                    :items="questionTitles"
                                    multiple
                                    clearable
                                    :no-data-text="'Keine Fragen ausgewählt'"
                                >
                                    <!--                                    if there are too many selectedQuestions we only display a few -->
                                    <template v-slot:selection="{ item, index }">
                                        <v-chip
                                            v-if="index < questionTitlesDisplayedInSelect"
                                            close
                                            @click:close="selectedQuestions.splice(index, 1)"
                                        >
                                            <span>{{ item }}</span>
                                        </v-chip>
                                        <span
                                            v-if="index === questionTitlesDisplayedInSelect"
                                            class="grey--text caption"
                                        >
                                            (und
                                            {{ selectedQuestions.length - questionTitlesDisplayedInSelect }} weitere)
                                        </span>
                                    </template>
                                </v-select>
                            </v-container>
                        </v-expansion-panel-content>
                    </v-expansion-panel>
                    <v-expansion-panel v-if="userNames.length !== 0">
                        <v-expansion-panel-header> Teilnehmerauswahl </v-expansion-panel-header>
                        <v-expansion-panel-content>
                            Hier können Sie die Teilnehmer auswählen, die {{ userInverted ? 'NICHT' : '' }} in der
                            Analyse betrachtet werden sollen.

                            <v-container fluid>
                                <v-switch v-model="userInverted" label="Invertieren"> </v-switch>
                                <v-select
                                    v-model="selectedUsers"
                                    :items="userNames"
                                    multiple
                                    clearable
                                    :no-data-text="'Keine Teilnehmer anzuzeigen'"
                                >
                                    <template v-slot:selection="{ item, index }">
                                        <v-chip
                                            v-if="index < userNamesDisplayedInSelect"
                                            close
                                            @click:close="selectedUsers.splice(index, 1)"
                                        >
                                            <span>{{ item }}</span>
                                        </v-chip>
                                        <span v-if="index === userNamesDisplayedInSelect" class="grey--text caption">
                                            (und
                                            {{ selectedUsers.length - userNamesDisplayedInSelect }} weitere)
                                        </span>
                                    </template>
                                </v-select>
                            </v-container>
                        </v-expansion-panel-content>
                    </v-expansion-panel>

                    <v-expansion-panel>
                        <v-expansion-panel-header> Konsistenzfragen </v-expansion-panel-header>
                        <v-expansion-panel-content>
                            <v-container fluid>
                                <v-row>
                                    <v-col colls="12" lg="8">
                                        <div v-if="maxConsistencyValue === 0">Erstellen Sie erst Konsistenzfragen!</div>
                                        <div v-else-if="maxConsistencyValue === 1">
                                            <v-switch
                                                v-model="consistencyOn"
                                                hint="Es werden nur noch Teilnehmer angezeigt, die mindestens diese Anzahl von Konsistenzfragen bestanden haben."
                                                label="Konsistenzfrage anwenden"
                                                @change="
                                                    consistencyOn
                                                        ? (minConsistencyValue = 1)
                                                        : (minConsistencyValue = 0)
                                                "
                                            >
                                            </v-switch>
                                        </div>
                                        <div v-else>
                                            <v-slider
                                                v-model="minConsistencyValue"
                                                min="0"
                                                step="1"
                                                :max="maxConsistencyValue"
                                                :thumbLabel="true"
                                                hint="Es werden nur noch Teilnehmer angezeigt, die mindestens diese Anzahl von Konsistenzfragen bestanden haben."
                                                label="Konsistenzfragen"
                                                @change="updateConsistencyOn()"
                                            >
                                            </v-slider>
                                        </div>
                                    </v-col>
                                    <v-col colls="12" lg="4">
                                        <control-questions
                                            :poll-index="pollIndex"
                                            @close-event="updateNumberOfConsistencyQuestions"
                                        ></control-questions>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-expansion-panel-content>
                    </v-expansion-panel>

                    <v-expansion-panel>
                        <v-expansion-panel-header> Antwortenfilter </v-expansion-panel-header>
                        <v-expansion-panel-content dense>
                            Hier können Sie das Antwortverhalten der Teilnehmer, die Sie betrachten wollen, festlegen.
                            Dieser Filter funktioniert allerdings nur auf Range-, Slider- und Auswahlfragen.

                            <v-row>
                                <v-col colls="12" lg="10">
                                    <v-btn @click="addQAFilter"> <v-icon> mdi-plus</v-icon> </v-btn>
                                </v-col>
                            </v-row>

                            <div>
                                <div v-for="filter in qafilterList" :key="filter.filterIndex">
                                    <div v-if="filter.active">
                                        <v-card class="pa-3 ma-2">
                                            <v-row>
                                                <v-spacer></v-spacer>
                                                <v-btn icon @click="addQAFilter()">
                                                    <v-icon> mdi-plus </v-icon>
                                                </v-btn>
                                                <v-btn icon @click="deleteQAFilter(filter.filterIndex)">
                                                    <v-icon> mdi-delete </v-icon>
                                                </v-btn>
                                            </v-row>
                                            <v-row>
                                                <v-col cols="12" lg="11">
                                                    <base-q-a-filter
                                                        :key="qaKey"
                                                        :poll-index="pollIndex"
                                                        :filter-id="filter.filterIndex"
                                                        :initial-category-index="
                                                            qafilterList[filter.filterIndex].categoryIndex
                                                        "
                                                        :initial-question-index="
                                                            qafilterList[filter.filterIndex].questionIndex
                                                        "
                                                        :initial-answer-indices="
                                                            qafilterList[filter.filterIndex].answerIndices
                                                        "
                                                        :initial-invert="qafilterList[filter.filterIndex].invertFilter"
                                                        :initial-is-slider="qafilterList[filter.filterIndex].isSlider"
                                                        @updateData="updateQaFilter"
                                                    ></base-q-a-filter>
                                                </v-col>
                                            </v-row>
                                        </v-card>
                                    </div>
                                </div>
                            </div>
                        </v-expansion-panel-content>
                    </v-expansion-panel>

                    <v-expansion-panel>
                        <v-expansion-panel-header> Zeitraumfilter </v-expansion-panel-header>
                        <v-expansion-panel-content>
                            Hier können Sie den Antwortzeitpunkt der Teilnehmer, die Sie betrachten wollen festlegen.

                            <v-switch label="Teilnahme über Zeit darstellen" v-model="showTimeDiagram"> </v-switch>
                            <v-switch
                                v-if="showTimeDiagram"
                                label="Relativ zur Umfragestartzeit"
                                v-model="relativeTimeDiagram"
                            >
                            </v-switch>

                            <v-row>
                                <v-col colls="12" lg="10">
                                    <v-btn @click="addDateFilter"> <v-icon> mdi-plus</v-icon> </v-btn>
                                </v-col>
                            </v-row>

                            <div v-for="filter in dateFilterList" :key="filter.filterIndex">
                                <div v-if="filter.active">
                                    <v-card class="pa-3 ma-2">
                                        <v-row>
                                            <v-spacer></v-spacer>
                                            <v-btn icon @click="addDateFilter()">
                                                <v-icon> mdi-plus </v-icon>
                                            </v-btn>
                                            <v-btn icon @click="deleteDateFilter(filter.filterIndex)">
                                                <v-icon> mdi-delete </v-icon>
                                            </v-btn>
                                        </v-row>
                                        <v-row>
                                            <v-col cols="12" lg="11">
                                                <base-date-filter
                                                    :start-date="filter.startDate"
                                                    :end-date="filter.endDate"
                                                    :invert-filter="filter.invertFilter"
                                                    :key="dateKey"
                                                    @newDateFilter="updateDateFilter"
                                                    :filter-index="filter.filterIndex"
                                                ></base-date-filter>
                                            </v-col>
                                        </v-row>
                                    </v-card>
                                </div>
                            </div>
                        </v-expansion-panel-content>
                    </v-expansion-panel>
                </v-expansion-panels>
            </template>
            <v-card-actions>
                <v-col>
                    <v-btn color="primary" @click="saveToStore(), (dialog = false)"> Anwenden </v-btn>
                </v-col>
                <v-col>
                    <v-btn color="secondary" @click="clearFilter()"> Keine Filter </v-btn>
                </v-col>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import ControlQuestions from './ControlQuestions'
import baseQAFilter from './Filter/baseQAFilter'
import BaseDateFilter from './Filter/baseDateFilter'

export default {
    name: 'FilterForm',
    components: {
        BaseDateFilter,
        baseQAFilter,
        ControlQuestions,
    },
    props: {
        pollIndex: {
            type: Number,
            default: 0,
        },
        pollId: {
            type: Number,
        },
        pollName: {
            type: String,
        },
    },
    data() {
        return {
            applyConsistency: false,
            minConsistencyValue: 0,
            maxConsistencyValue: 0,
            qaKey: 0,
            dateKey: 0,
            qafilter: true,
            datefilter: true,
            qafilterList: [],

            dateFilterList: [],

            selectedQuestions: [],
            questionTitlesDisplayedInSelect: 3,
            userNamesDisplayedInSelect: 5,

            dialog: false,

            filterData: [],

            showTimeDiagram: false,
            relativeTimeDiagram: false,
            consistencyOn: false,
            selectedUsers: [],
            userInverted: false,
            userNames: [],
            seriesPollNumber: -1,
        }
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
            getSessions: 'evaluation/getSessions',
            filters: 'evaluation/getFilterList',
        }),

        /**
         * this method return if the current poll is a series poll
         * @returns {boolean}
         */

        isSeriesPoll() {
            // console.log('poll')
            // console.log(this.poll)
            return (
                this.poll.prevInSeries !== undefined && this.poll.prevInSeries !== null && this.poll.prevInSeries >= 0
            )
        },

        /**
         * gives a list of all the categories in the current poll
         * @returns {[]|default.methods.poll.categoryList}
         */

        categories() {
            return this.poll.categoryList
        },

        /**
         * gives the poll that should be analysed
         * @returns {string}
         */

        poll() {
            return this.polls[this.pollIndex]
        },

        /**
         * computes the ids from the QuestionMessages that are stored in selectedQuestions
         * @returns {[]}
         */

        chosenQuestionIds() {
            // console.log('chosenQuestionIds()')
            const questionIds = []
            for (let i = 0; i < this.selectedQuestions.length; i++) {
                const poll = this.polls[this.pollIndex]
                for (let c = 0; c < poll.categoryList.length; c++) {
                    for (let q = 0; q < poll.categoryList[c].questionList.length; q++) {
                        if (poll.categoryList[c].questionList[q].questionMessage === this.selectedQuestions[i]) {
                            questionIds.push(poll.categoryList[c].questionList[q].questionId)
                        }
                    }
                }
            }
            return questionIds
        },

        /**
         * gives the titles of all the questions (in all categories) of the poll
         * @returns {[]|*[]}
         */

        questionTitles() {
            // console.log('questionTitle()')
            if (this.pollIndex === -1) {
                return []
            }
            const questionTitles = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                for (let j = 0; j < this.polls[this.pollIndex].categoryList[i].questionList.length; j++) {
                    questionTitles.push(this.polls[this.pollIndex].categoryList[i].questionList[j].questionMessage)
                }
            }
            // console.log('questionTitles: ')
            return questionTitles
        },
    },
    mounted() {
        this.selectedQuestions = []
        for (let i = 0; i < this.questionTitles.length; i++) {
            this.selectedQuestions.push(this.questionTitles[i])
        }
        this.getPollTakers()

        this.updateNumberOfConsistencyQuestions()
        this.saveToStore()
    },
    methods: {
        ...mapActions({
            sendFilter: 'evaluation/sendFilter',
            updateData: 'evaluation/updateData',
            getUsers: 'participation/loadPollTakers',
        }),

        /**
         * Gets all the filters that are currently selected by the user and sends them to the store
         * @returns {Promise<void>}
         */

        async saveToStore() {
            // console.log('saveToStore()')
            const filterData = []
            filterData.push({
                filterType: 'dataFilter',
                basePollId: this.pollId,
                baseQuestionIds: this.chosenQuestionIds, // these are questionIDs
                timeDiagram: this.showTimeDiagram,
                timeDiagramRelative: this.relativeTimeDiagram,
                numberOfEvaluatedSeriesPolls: this.seriesPollNumber,
            })
            filterData.push({
                filterType: 'consistency',
                minSuccesses: this.minConsistencyValue, // von 0 (aus) bis zur Länge der Konsistenzfragen
            })
            const users = []
            for (let i = 0; i < this.selectedUsers.length; i++) {
                users.push(this.selectedUsers[i])
            }
            filterData.push({
                filterType: 'user',
                invertFilter: this.userInverted,
                userNames: users,
            })
            for (let i = 0; i < this.qafilterList.length; i++) {
                if (this.qafilterList[i].active) {
                    const filter = this.qafilterList[i]
                    // console.log('saveQAFilter')
                    // console.log(this.polls)
                    // console.log(this.pollIndex)
                    // console.log(filter)
                    if (filter.answerIndices.length !== 0) {
                        filterData.push({
                            filterType: 'questionAnswer',
                            invertFilter: filter.invertFilter,
                            targetQuestionId: filter.questionId,
                            targetAnswerPossibilities: filter.answerIndices,
                            isSlider: filter.isSlider,
                        })
                    }
                }
            }
            if (this.datefilter) {
                let numberOfDateFilters = 0
                // console.log(this.dateFilterList)
                for (let i = 0; i < this.dateFilterList.length; i++) {
                    if (this.dateFilterList[i].active) {
                        numberOfDateFilters += 1
                        filterData.push({
                            filterType: 'date',
                            invertFilter: this.dateFilterList[i].invertFilter,
                            startDate: this.dateFilterList[i].startDate,
                            endDate: this.dateFilterList[i].endDate,
                        })
                    }
                }
                if (numberOfDateFilters > 1) {
                    filterData.push({
                        filterType: 'or',
                    })
                }
            }
            // console.log(filterData)
            await this.sendFilter(filterData)
            this.$emit('close-event')
        },

        /**
         * gets the current number of ConsistencyQuestions so a slider can be shown accordingly
         * @returns {Promise<void>}
         */

        async updateNumberOfConsistencyQuestions() {
            // console.log('updateNumberOfConsistencyQuestions()')
            await this.$axios
                .get('/poll/' + this.polls[this.pollIndex].pollId + '/consistencyquestionnumber')
                .then((response) => {
                    this.maxConsistencyValue = response.data
                })
                .catch((reason) => {
                    // console.log(reason)
                    this.maxConsistencyValue = 0
                })
        },

        /**
         * Gets a list with all the userNames of the participants of the survey
         *
         */

        getPollTakers() {
            if (this.polls[this.pollIndex].anonymityStatus === '3') {
                this.$axios.get('/getPollTakers/' + this.polls[this.pollIndex].pollId).then((response) => {
                    this.userNames = response.data
                    this.selectedUsers = []
                    for (let i = 0; i < this.userNames.length; i++) {
                        this.selectedUsers.push(this.userNames[i])
                    }
                })
            }
        },

        /**
         * adds a new QAFilter to qafilterList
         *
         */

        addQAFilter() {
            const id = this.qafilterList.length
            this.qafilterList.push({
                active: true,
                filterIndex: id,
                filterType: 'qaFilter',
                categoryIndex: -1,
                questionIndex: -1,
                isSlider: false,
                answerIndices: [],
            })
        },

        /**
         * sets a qaFilter to not active, causing it not to be displayed nor be send to the store
         * @param index of the filter
         */

        deleteQAFilter(index) {
            this.qafilterList[index].active = false
        },

        /**
         * updates the qa Filter with the specified filterId by setting all its attributes with the passed attributes
         * @param filterId
         * @param categoryIndex
         * @param questionIndex
         * @param questionId
         * @param answerIndices
         * @param isSlider
         * @param invertFilter
         */

        updateQaFilter([filterId, categoryIndex, questionIndex, questionId, answerIndices, isSlider, invertFilter]) {
            this.qafilterList[filterId].categoryIndex = categoryIndex
            this.qafilterList[filterId].questionIndex = questionIndex
            this.qafilterList[filterId].questionId = questionId
            this.qafilterList[filterId].answerIndices = answerIndices
            this.qafilterList[filterId].isSlider = isSlider
            this.qafilterList[filterId].invertFilter = invertFilter
            // console.log(this.qafilterList[filterId])
        },

        /**
         * adds a new dateFilter to dateFilterList
         */

        addDateFilter() {
            const id = this.dateFilterList.length
            this.dateFilterList.push({
                active: true,
                filterIndex: id,
                filterType: 'dateFilter',
                endDate: '',
                startDate: '',
                invertFilter: false,
            })
        },

        /**
         * sets a dateFilter to not active, causing it not to be displayed nor be send to the store
         * @param index of the filter
         */

        deleteDateFilter(index) {
            this.dateFilterList[index].active = false
            this.$forceUpdate()
        },

        /**
         * updates the date Filter with the specified filterId by setting all its attributes with the passed attributes
         * @param filterIndex
         * @param startDate
         * @param endDate
         * @param invertFilter
         */

        updateDateFilter([filterIndex, startDate, endDate, invertFilter]) {
            // console.log('updateDateFilter()')
            // console.log(invertFilter)
            this.dateFilterList[filterIndex].startDate = startDate
            this.dateFilterList[filterIndex].endDate = endDate
            this.dateFilterList[filterIndex].invertFilter = invertFilter
            // console.log(this.dateFilterList)
        },

        /**
         * consistencyOn must always be true when the minConsistencyValue is not 0
         */
        updateConsistencyOn() {
            this.consistencyOn = !(this.minConsistencyValue === 0)
        },

        /**
         * updates all the data structures to display the filters that have been send from the store
         *
         */

        getFilters() {
            // console.log('getFilters')
            if (this.filters.length !== 0) {
                // console.log('do stuff')
                // console.log(this.filters)
                this.qafilterList = []
                this.dateFilterList = []
                if (this.filters[0].filterType === 'dataFilter') {
                    if (this.filters[0].baseQuestionIds.length > 0) {
                        this.selectedQuestions = []
                        for (let k = 0; k < this.filters[0].baseQuestionIds.length; k++) {
                            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                                for (
                                    let j = 0;
                                    j < this.polls[this.pollIndex].categoryList[i].questionList.length;
                                    j++
                                ) {
                                    if (
                                        this.polls[this.pollIndex].categoryList[i].questionList[j].questionId ===
                                        this.filters[0].baseQuestionIds[k]
                                    )
                                        this.selectedQuestions.push(
                                            this.polls[this.pollIndex].categoryList[i].questionList[j].questionMessage
                                        )
                                }
                            }
                        }
                    }
                    this.seriesPollNumber = this.filters[0].numberOfEvaluatedSeriesPolls

                    // console.log(this.selectedQuestions)
                }

                for (let i = 0; i < this.filters.length; i++) {
                    if (this.filters[i].filterType === 'consistency') {
                        // console.log('consis filter')
                        this.minConsistencyValue = this.filters[i].minSuccesses
                        this.updateConsistencyOn()
                    }
                    if (this.filters[i].filterType === 'user') {
                        // console.log('user filter')
                        this.userInverted = this.filters[i].invertFilter
                        this.selectedUsers = []
                        for (let j = 0; j < this.filters[i].userNames.length; j++) {
                            this.selectedUsers.push(this.filters[i].userNames[j])
                        }
                    }

                    if (this.filters[i].filterType === 'date') {
                        // console.log('date filter')
                        const id = this.dateFilterList.length
                        this.dateFilterList.push({
                            active: true,
                            filterIndex: id,
                            filterType: 'date',
                            endDate: this.filters[i].endDate,
                            startDate: this.filters[i].startDate,
                            invertFilter: this.filters[i].invertFilter,
                        })
                    }

                    if (this.filters[i].filterType === 'questionAnswer') {
                        // console.log('qa filter')
                        const a = []
                        if (this.filters[i].isSlider) {
                            for (let j = 0; j < this.filters[i].targetAnswerPossibilities.length; j++) {
                                a.push(parseFloat(this.filters[i].targetAnswerPossibilities[j]))
                            }
                        } else {
                            for (let j = 0; j < this.filters[i].targetAnswerPossibilities.length; j++) {
                                a.push(parseInt(this.filters[i].targetAnswerPossibilities[j]))
                            }
                        }

                        const id = this.qafilterList.length
                        this.qafilterList.push({
                            active: true,
                            filterIndex: id,
                            filterType: 'qaFilter',
                            questionId: this.filters[i].targetQuestionId,
                            categoryIndex: this.getCategoryIndexAndQuestionIndexFromQuestionId(
                                this.filters[i].targetQuestionId
                            ).categoryIndex,
                            questionIndex: this.getCategoryIndexAndQuestionIndexFromQuestionId(
                                this.filters[i].targetQuestionId
                            ).questionIndex,
                            isSlider: this.filters[i].isSlider,
                            answerIndices: a,
                            invertFilter: this.filters[i].invertFilter,
                        })
                    }

                    // console.log(this.dateFilterList)

                    // console.log('qaFilter in FF')

                    // console.log(this.qafilterList)

                    this.updateFilter()
                }

                if (this.dateFilterList.length === 0) {
                    this.addDateFilter()
                }
                if (this.qafilterList.length === 0) {
                    this.addQAFilter()
                }
            }
        },

        /**
         * sets all filters t their default values
         */

        clearFilter() {
            this.selectedQuestions = []
            for (let i = 0; i < this.questionTitles.length; i++) {
                this.selectedQuestions.push(this.questionTitles[i])
            }
            this.selectedUsers = []
            for (let i = 0; i < this.userNames.length; i++) {
                this.selectedUsers.push(this.userNames[i])
            }
            this.userInverted = false
            this.minConsistencyValue = 0
            this.updateConsistencyOn()
            this.qafilterList = []
            this.dateFilterList = []
            this.addDateFilter()
            this.addQAFilter()
            this.seriesPollNumber = 0
            this.updateFilter()
            this.saveToStore()
        },

        /**
         * forces date and qa filter to be displayed again
         */

        updateFilter() {
            this.qaKey += 1
            this.dateKey += 1
        },

        /**
         * computed the index of the category a question is in, as well as the index that question has within that category
         * @param questionId of the question
         * @returns {{questionIndex: number, categoryIndex: number}|null}
         */

        getCategoryIndexAndQuestionIndexFromQuestionId(questionId) {
            // console.log('bin in getCat...')
            for (let c = 0; c < this.categories.length; c++) {
                // console.log(c)
                const questions = []
                for (let i = 0; i < this.categories[c].questionList.length; i++) {
                    if (
                        this.categories[c].questionList[i].questionType !== 'SortQuestion' &&
                        this.categories[c].questionList[i].questionType !== 'TextQuestion'
                    )
                        questions.push(this.categories[c].questionList[i])
                }
                for (let q = 0; q < questions.length; q++) {
                    // console.log(q)
                    if (questions[q].questionId === questionId) {
                        // console.log({ categoryIndex: c, questionIndex: q })
                        return { categoryIndex: c, questionIndex: q }
                    }
                }
            }
            return null
        },
    },
}
</script>

<style scoped></style>
