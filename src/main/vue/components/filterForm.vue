<template>
    <v-dialog v-model="dialog" overlay-color="background" persistent width="500" overlay-opacity="0.95" fullscreen>
        <template v-slot:activator="{ on }">
            <v-btn color="primary" v-on="on" class="ma-2" @click="getFilters">
                Analyse
            </v-btn>
        </template>

        <v-card class="ma-2 pa-2">
            <v-card-title>Erweiterte Analyse {{ filters.length }}</v-card-title>
            <template>
                <v-overflow-btn v-model="chosenPoll" editable prefix="Basisdaten:" :items="pollTitles" dense>
                </v-overflow-btn>
                <v-expansion-panels accordion multiple hover>
                    <v-expansion-panel>
                        <v-expansion-panel-header>
                            Fragenauswahl
                        </v-expansion-panel-header>
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

                    <v-expansion-panel>
                        <v-expansion-panel-header>
                            Konsistenzfragen
                        </v-expansion-panel-header>
                        <v-expansion-panel-content>
                            <v-container fluid>
                                <v-row>
                                    <v-col colls="12" lg="10">
                                        <div v-if="maxConsistencyValue === 0">
                                            Erstelle erst Konsistenzfragen!
                                        </div>
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
                                    <v-col>
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
                        <v-expansion-panel-header>
                            Antwortenfilter
                        </v-expansion-panel-header>
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
                        <v-expansion-panel-header>
                            Zeitraumfilter
                        </v-expansion-panel-header>
                        <v-expansion-panel-content>
                            Hier können Sie den Antwortzeitpunkt der Teilnehmer, die Sie betrachten wollen festlegen.

                            <v-switch label="Teilnahme über Zeit darstellen" v-model="showTimeDiagram"> </v-switch>

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
        initialPollIndex: {
            type: Number,
            default: 0,
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
            qafilterList: [
                // {
                //     active: true,
                //     filterId: 0,
                //     filterType: 'qaFilter',
                //     questionId: -1,
                //     categoryIndex: -1,
                //     questionIndex: -1,
                //     answerIndices: [],
                //     invertFilter: false,
                // },
            ],

            dateFilterList: [
                // {
                //     active: true,
                //     filterIndex: 0,
                //     filterType: 'date',
                //     endDate: '',
                //     startDate: '',
                //     invertFilter: false,
                // },
            ],

            chosenPoll: '',
            selectedQuestions: [],
            questionTitlesDisplayedInSelect: 3,

            dialog: false,

            filterData: [
                /* {
                    pollId: 1,
                    chosenQuestions: [11, 12, 32, 4, 50, 8], // these are questionIDs
                    filterType: 'dataFilter',
                },
                {
                    filterType: 'questionAnswer', // 'consistency', 'dataFilter'
                    invertFilter: false,
                    targetQuestionId: 3, // within a category
                    targetAnswerPossibilities: ['2', '3', '5'], // or within an answerlist of a question
                },
                {
                    filterType: 'consistency',
                    minSuccesses: 0, // von 0 (aus) bis zur Länge der Konsistenzfragen
                },
                {
                    filterType: 'date',
                    startDate: ' ',
                    endDate: ' ',
                    invertFilter: false,
                }, */
            ],

            filterData1: {
                pollId: 1,
                chosenQuestions: [11, 12, 32, 4, 50, 8], // these are questionIDs
                chosenQuestionTypes: ['singleChoice', 'multiChoice', 'range', 'slide', 'sort', 'text'],
                applyConsistency: false,
                qafilters: [
                    {
                        categoryIndex: 1, // these are the indices, they indicate the position within a poll
                        questionIndex: 3, // within a category
                        answers: [2, 3, 5], // or within an answerlist of a question
                    },
                ],
            },
            showTimeDiagram: false,
            consistencyOn: false,
        }
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
            getSessions: 'evaluation/getSessions',
            filters: 'evaluation/getFilterList',
        }),

        categories() {
            return this.polls[this.pollIndex].categoryList
        },

        pollTitles() {
            console.log('pollTitles()')
            const pollTitles = []
            for (let i = 0; i < this.polls.length; i++) {
                pollTitles.push('#' + this.polls[i].pollId + ' ' + this.polls[i].pollName)
            }
            return pollTitles
        },

        pollIndex() {
            console.log('pollIndex()')
            return this.pollTitles.indexOf(this.chosenPoll)
        },

        chosenQuestionIds() {
            console.log('chosenQuestionIds()')
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

        questionTitles() {
            console.log('questionTitle()')
            if (this.pollIndex === -1) {
                return []
            }
            const questionTitles = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                for (let j = 0; j < this.polls[this.pollIndex].categoryList[i].questionList.length; j++) {
                    questionTitles.push(this.polls[this.pollIndex].categoryList[i].questionList[j].questionMessage)
                }
            }
            console.log('questionTitles: ')
            return questionTitles
        },
    },
    mounted() {
        console.log('mounted()')
        console.log(this.filters)
        this.chosenPoll = this.pollTitles[this.initialPollIndex]
        for (let i = 0; i < this.questionTitles.length; i++) {
            this.selectedQuestions.push(this.questionTitles[i])
        }
        this.updateNumberOfConsistencyQuestions()
    },
    methods: {
        ...mapActions({
            sendFilter: 'evaluation/sendFilter',
            updateData: 'evaluation/updateData',
        }),

        async saveToStore() {
            console.log('saveToStore()')
            const filterData = []
            filterData.push({
                filterType: 'dataFilter',
                basePollId: this.polls[this.pollIndex].pollId,
                baseQuestionIds: this.chosenQuestionIds, // these are questionIDs
                timeDiagram: this.showTimeDiagram,
            })
            filterData.push({
                filterType: 'consistency',
                minSuccesses: this.minConsistencyValue, // von 0 (aus) bis zur Länge der Konsistenzfragen
            })
            for (let i = 0; i < this.qafilterList.length; i++) {
                if (this.qafilterList[i].active) {
                    const filter = this.qafilterList[i]
                    console.log('saveQAFilter')
                    console.log(this.polls)
                    console.log(this.pollIndex)
                    console.log(filter)
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
                console.log(this.dateFilterList)
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
            console.log(filterData)
            // for (let f = 0; f < this.qafilterList.length; f++) {
            //     if (this.qafilterList[f].active) {
            //         filterData.push({
            //             filterType: 'qaFilter',
            //             invertFilter: false,
            //             targetPollId: this.pollIndex,
            //             targetCategoryId: this.qafilterList[f].categoryId,
            //             targetQuestionId: this.qafilterList[f].questionId,
            //             targetAnswerPossibilities: this.qafilterList[f].answerIds,
            //         })
            //     }
            // }
            await this.sendFilter(filterData)
            this.$emit('close-event')
        },

        async updateNumberOfConsistencyQuestions() {
            console.log('updateNumberOfConsistencyQuestions()')
            await this.$axios
                .get('/poll/' + this.polls[this.pollIndex].pollId + '/consistencyquestionnumber')
                .then((response) => {
                    this.maxConsistencyValue = response.data
                })
                .catch((reason) => {
                    console.log(reason)
                    this.maxConsistencyValue = 0
                })
        },

        addQAFilter() {
            console.log('addQAFilter()')
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

        deleteQAFilter(index) {
            console.log('deleteQAFilter()')
            this.qafilterList[index].active = false
        },

        updateQaFilter([filterId, categoryIndex, questionIndex, questionId, answerIndices, isSlider, invertFilter]) {
            console.log('updateQAFilter()')
            this.qafilterList[filterId].categoryIndex = categoryIndex
            this.qafilterList[filterId].questionIndex = questionIndex
            this.qafilterList[filterId].questionId = questionId
            this.qafilterList[filterId].answerIndices = answerIndices
            this.qafilterList[filterId].isSlider = isSlider
            this.qafilterList[filterId].invertFilter = invertFilter
            console.log(this.qafilterList[filterId])
        },

        addDateFilter() {
            console.log('addDateFilter()')
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

        deleteDateFilter(index) {
            console.log('deleteDateFilter()')
            this.dateFilterList[index].active = false
            this.$forceUpdate()
        },

        updateDateFilter([filterIndex, startDate, endDate, invertFilter]) {
            console.log('updateDateFilter()')
            console.log(invertFilter)
            this.dateFilterList[filterIndex].startDate = startDate
            this.dateFilterList[filterIndex].endDate = endDate
            this.dateFilterList[filterIndex].invertFilter = invertFilter
            console.log(this.dateFilterList)
        },
        updateConsistencyOn() {
            this.consistencyOn = !(this.minConsistencyValue === 0)
        },

        getFilters() {
            console.log('getFilters')
            if (this.filters.length !== 0) {
                console.log('do stuff')
                console.log(this.filters)
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

                    console.log(this.selectedQuestions)
                }

                for (let i = 0; i < this.filters.length; i++) {
                    console.log('bu')
                    if (this.filters[i].filterType === 'consistency') {
                        console.log('consis filter')
                        this.minConsistencyValue = this.filters[i].minSuccesses
                        this.updateConsistencyOn()
                    }
                    if (this.filters[i].filterType === 'date') {
                        console.log('date filter')
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

                    // if (this.filters[i].filterType === 'questionAnswer') {
                    //     console.log('qa filter')
                    //     const a = []
                    //     for (let j = 0; j < this.filters[i].targetAnswerPossibilities.length; j++) {
                    //         a.push(parseInt(this.filters[i].targetAnswerPossibilities[j]))
                    //     }
                    //     const id = this.qafilterList.length
                    //     this.qafilterList.push({
                    //         active: true,
                    //         filterIndex: id,
                    //         filterType: 'qaFilter',
                    //         questionId: this.filters[i].targetQuestionId,
                    //         categoryIndex: this.getCategoryIndexAndQuestionIndexFromQuestionId(
                    //             this.filters[i].targetQuestionId
                    //         ).categoryIndex,
                    //         questionIndex: this.getCategoryIndexAndQuestionIndexFromQuestionId(
                    //             this.filters[i].targetQuestionId
                    //         ).questionIndex,
                    //         isSlider: this.filters[i].isSlider,
                    //         answerIndices: a,
                    //     })
                    // }

                    console.log(this.dateFilterList)

                    console.log(this.qafilterList)

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

        clearFilter() {
            this.selectedQuestions = []
            for (let i = 0; i < this.questionTitles.length; i++) {
                this.selectedQuestions.push(this.questionTitles[i])
            }
            this.minConsistencyValue = 0
            this.updateConsistencyOn()
            this.qafilterList = [
                // {
                //     active: true,
                //     filterId: 0,
                //     filterType: 'qaFilter',
                //     categoryIndex: -1,
                //     questionId: -1,
                //     questionIndex: -1,
                //     answerIndices: [],
                //     invertFilter: false,
                // },
            ]
            this.dateFilterList = [
                // {
                //     active: true,
                //     filterIndex: 0,
                //     filterType: 'date',
                //     endDate: '',
                //     startDate: '',
                //     invertFilter: false,
                // },
            ]
            this.addDateFilter()
            this.addQAFilter()
            this.updateFilter()
            this.saveToStore()
        },

        updateFilter() {
            this.qaKey += 1
            this.dateKey += 1
        },

        getCategoryIndexAndQuestionIndexFromQuestionId(questionId) {
            console.log('bin in getCat...')
            for (let c = 0; c < this.categories.length; c++) {
                console.log(c)
                const questions = []
                for (let i = 0; i < this.categories[c].questionList.length; i++) {
                    if (
                        this.categories[c].questionList[i].questionType !== 'SortQuestion' &&
                        this.categories[c].questionList[i].questionType !== 'TextQuestion'
                    )
                        questions.push(this.categories[c].questionList[i])
                }
                for (let q = 0; q < questions.length; q++) {
                    console.log(q)
                    if (questions[q].questionId === questionId) {
                        console.log({ categoryIndex: c, questionIndex: q })
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
