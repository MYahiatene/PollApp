<template>
    <v-dialog v-model="dialog" overlay-color="background" persistent width="500" overlay-opacity="0.95" fullscreen>
        <template v-slot:activator="{ on }">
            <v-btn color="primary" v-on="on">
                Analyse
            </v-btn>
        </template>

        <v-card class="ma-2 pa-2">
            <v-card-title>Erweiterte Analyse {{ polls.length }}</v-card-title>
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
                                <v-select v-model="selectedQuestions" :items="questionTitles" multiple>
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
                                        <v-slider
                                            v-model="minConsistencyValue"
                                            min="0"
                                            step="1"
                                            :max="maxConsistencyValue"
                                            :thumbLabel="true"
                                            hint="Es werden nur noch Teilnehmer angezeigt, die mindestens diese Anzahl von Konsistenzfragen bestanden haben."
                                            label="Konsistenzfragen"
                                        >
                                        </v-slider>
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

                            <v-row>
                                <v-col colls="12" lg="10">
                                    <v-btn @click="addQAFilter"> <v-icon> mdi-plus</v-icon> </v-btn>
                                </v-col>
                            </v-row>

                            <div>
                                <div v-for="filter in qafilterList" :key="filter.filterId">
                                    <div v-if="filter.active">
                                        <v-card class="pa-3 ma-2">
                                            <v-row>
                                                <v-spacer></v-spacer>
                                                <v-btn icon @click="addQAFilter()">
                                                    <v-icon> mdi-plus </v-icon>
                                                </v-btn>
                                                <v-btn icon @click="deleteQAFilter(filter.filterId)">
                                                    <v-icon> mdi-delete </v-icon>
                                                </v-btn>
                                            </v-row>
                                            <v-row>
                                                <v-col cols="12" lg="11">
                                                    <base-q-a-filter
                                                        :poll-index="pollIndex"
                                                        :filter-id="filter.filterId"
                                                        :initial-category-index="
                                                            qafilterList[filter.filterId].categoryId
                                                        "
                                                        :initial-question-index="
                                                            qafilterList[filter.filterId].questionId
                                                        "
                                                        :initial-answer-indices="
                                                            qafilterList[filter.filterId].answerIds
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

                            <div v-for="filter in dateFilterList" :key="filter.filterId">
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
                    <v-spacer />
                    <v-expansion-panel>
                        <v-expansion-panel-header>
                            Sessionmanagement
                        </v-expansion-panel-header>
                        <v-expansion-panel-content>
                            Hier können Sie Auswertungs-Sessions speichern und laden.
                            <v-row>
                                <v-col cols="6">
                                    <v-overflow-btn
                                        v-model="choosenSessionTitle"
                                        label="Session zum laden wählen"
                                        editable
                                        :items="sessions"
                                    >
                                    </v-overflow-btn>
                                    <v-btn color="primary" @click="loadOneSession()">
                                        Laden
                                    </v-btn>
                                </v-col>
                                <v-col cols="6">
                                    <v-text-field
                                        v-model="sessionTitle"
                                        label="Sessiontitel"
                                        hint="Sie sollten jeder Session einen Namen geben, um sie später unterscheiden zu können."
                                    >
                                    </v-text-field>
                                    <v-btn color="primary" @click="saveToStore(), saveThisSession()">
                                        Speichern
                                    </v-btn>
                                </v-col>
                            </v-row>
                        </v-expansion-panel-content>
                    </v-expansion-panel>
                </v-expansion-panels>
            </template>
            <v-card-actions>
                <v-col>
                    <v-btn color="primary" @click="saveToStore(), (dialog = false)"> Anwenden </v-btn>
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
            globalFilterId: 1,
            applyConsistency: false,
            minConsistencyValue: 0,
            maxConsistencyValue: 0,
            sessionTitle: '',
            choosenSessionTitle: '',
            qafilter: false,
            datefilter: true,
            qafilterList: [
                { active: true, filterId: 0, filterType: 'qaFilter', categoryId: -1, questionId: -1, answerIds: [] },
            ],

            dateFilterList: [
                {
                    active: true,
                    filterIndex: 0,
                    filterType: 'date',
                    endDate: '',
                    endTime: '',
                    startDate: '',
                    startTime: '',

                    invertFilter: false,
                },
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
        }
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
            getSessions: 'evaluation/getSessions',
        }),

        pollTitles() {
            console.log('pollTitles()')
            const pollTitles = []
            console.log(this.polls)
            for (let i = 0; i < this.polls.length; i++) {
                pollTitles.push(' (#' + this.polls[i].pollId + ') ' + this.polls[i].pollName)
            }
            console.log(pollTitles)
            return pollTitles
        },

        pollIndex() {
            console.log('pollIndex()')
            console.log(this.chosenPoll)
            console.log(this.pollTitles)
            console.log(this.pollTitles.indexOf(this.chosenPoll))
            return this.pollTitles.indexOf(this.chosenPoll)
        },

        chosenQuestionIds() {
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
            console.log(this.polls)
            console.log(this.pollIndex)
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                for (let j = 0; j < this.polls[this.pollIndex].categoryList[i].questionList.length; j++) {
                    questionTitles.push(this.polls[this.pollIndex].categoryList[i].questionList[j].questionMessage)
                }
            }
            console.log('questionTitles: ')
            console.log(questionTitles)
            return questionTitles
        },

        sessions() {
            const sessions = this.getSessions
            const titles = []
            for (let i = 0; i < sessions.length; i++) {
                titles.push(sessions[i].sessionTitle)
            }
            return titles
        },
    },
    mounted() {
        console.log('mounted()')
        console.log(this.initialPollIndex)
        console.log(this.pollTitles)
        this.chosenPoll = this.pollTitles[this.initialPollIndex]
        console.log(this.chosenPoll)
        console.log(this.initialPollIndex)
        this.selectedQuestions = this.questionTitles
        this.loadSessions()
        this.updateNumberOfConsistencyQuestions()
    },
    methods: {
        ...mapActions({
            sendFilter: 'evaluation/sendFilter',
            saveSession: 'evaluation/saveSession',
            loadSessions: 'evaluation/loadSessions',
            loadSession: 'evaluation/loadSession',
        }),

        async saveToStore() {
            console.log('saveToStore()')
            const filterData = []
            filterData.push({
                filterType: 'dataFilter',
                basePollId: this.polls[this.pollIndex].pollId,
                baseQuestionIds: this.chosenQuestionIds, // these are questionIDs
            })
            filterData.push({
                filterType: 'consistency',
                minSuccesses: this.minConsistencyValue, // von 0 (aus) bis zur Länge der Konsistenzfragen
            })
            console.log('Jo')
            console.log(this.qafilterList)
            console.log(filterData)
            for (let i = 0; i < this.qafilterList.length; i++) {
                console.log(i)
                const filter = this.qafilterList[i]
                console.log(this.qafilterList)
                console.log(this.polls)
                console.log(this.pollIndex)
                filterData.push({
                    filterType: 'questionAnswer',
                    invertFilter: false,
                    targetQuestionId: this.polls[this.pollIndex].categoryList[filter.categoryIndex].questionList[
                        filter.questionIndex
                    ].questionId,
                    targetAnswerPossibilities: filter.answerIndices,
                })
            }
            if (this.datefilter) {
                for (let i = 0; i < this.dateFilterList.length; i++) {
                    if (this.dateFilterList[i].active) {
                        filterData.push({
                            filterType: 'date',
                            invertFilter: this.dateFilterList[i].invertFilter,
                            startDate: this.dateFilterList[i].startDate,
                            endDate: this.dateFilterList[i].endDate,
                            timeDiagram: this.showTimeDiagram,
                        })
                    }
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
            console.log(filterData)
            await this.sendFilter(filterData)
            console.log('save filter was called')
            this.$emit('close-event')
        },

        async updateNumberOfConsistencyQuestions() {
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
            this.qafilterList.push({
                active: true,
                filterId: this.globalFilterId++,
                filterType: 'qaFilter',
                categoryId: -1,
                questionId: -1,
                answerIds: [],
            })
        },

        deleteQAFilter(index) {
            console.log('deleteQAFilter()')
            this.qafilterList[index].active = false
        },

        updateQaFilter([filterId, categoryIndex, questionIndex, answerIndices]) {
            console.log('updateQAFilter()')
            this.qafilterList[filterId].categoryIndex = categoryIndex
            this.qafilterList[filterId].questionIndex = questionIndex
            this.qafilterList[filterId].answerIndices = answerIndices
            console.log(this.qafilterList)
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
            console.log(index)
            this.dateFilterList[index].active = false
            this.$forceUpdate()
        },

        updateDateFilter([filterIndex, startDate, endDate, invertFilter]) {
            console.log('updateDateFilter()')
            console.log(startDate)
            console.log(endDate)
            console.log(this.dateFilterList)
            console.log(filterIndex)

            this.dateFilterList[filterIndex].startDate = startDate
            this.dateFilterList[filterIndex].endDate = endDate

            this.dateFilterList[filterIndex].invertFilter = invertFilter
            console.log(this.dateFilterList)
        },

        saveThisSession() {
            console.log('saveThisSession()')
            this.saveToStore()
            const payload = {
                sessionTitle: this.sessionTitle,
                lastUsername: 'Jan',
            }
            this.saveSession(payload)
        },

        loadOneSession() {
            console.log('loadOneSession()')
            let id = -1
            const sessions = this.getSessions
            for (let i = 0; i < sessions.length; i++) {
                if (sessions[i].sessionTitle === this.choosenSessionTitle) {
                    id = sessions[i].sessionId
                }
            }
            this.loadSession(id)
        },
    },
}
</script>

<style scoped></style>
