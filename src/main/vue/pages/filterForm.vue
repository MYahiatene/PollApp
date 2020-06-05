<template>
    <v-card class="ma-2 pa-2">
        <v-card-title>Erweiterte Analyse {{ polls.length }}</v-card-title>
        <template>
            <v-overflow-btn prefix="Basisdaten:" :items="pollTitles" dense v-model="chosenPoll"> </v-overflow-btn>
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
                                        close
                                        @click:close="selectedQuestions.splice(index, 1)"
                                        v-if="index < questionTitlesDisplayedInSelect"
                                    >
                                        <span>{{ item }}</span>
                                    </v-chip>
                                    <span v-if="index === questionTitlesDisplayedInSelect" class="grey--text caption"
                                        >(und
                                        {{ selectedQuestions.length - questionTitlesDisplayedInSelect }}
                                        weitere)</span
                                    >
                                </template>
                            </v-select>
                        </v-container>
                    </v-expansion-panel-content>
                </v-expansion-panel>

                <v-expansion-panel>
                    <v-expansion-panel-header>
                        Teilnehmerfilter
                    </v-expansion-panel-header>
                    <v-expansion-panel-content>
                        Hier können Sie die Teilnehmer, deren Ergebnisse betrachtet werden filtern.
                    </v-expansion-panel-content>
                </v-expansion-panel>

                <v-expansion-panel>
                    <v-expansion-panel-header>
                        Antwortenfilter
                    </v-expansion-panel-header>
                    <v-expansion-panel-content dense>
                        Hier können Sie das Antwortverhalten der Teilnehmer, die Sie betrachten wollen festlegen.
                        <v-divider></v-divider>
                        <v-row>
                            <v-col colls="12" lg="10">
                                <v-checkbox
                                    v-model="applyConsistency"
                                    label="Betrachte nur Teilnehmer, die alle Konsistenzfragen konsistent beantwortet haben."
                                >
                                </v-checkbox>
                            </v-col>
                            <v-col>
                                <v-btn>
                                    Konsistenzfragen bearbeiten
                                </v-btn>
                            </v-col>
                        </v-row>

                        <v-divider></v-divider>

                        <v-row>
                            <v-col colls="12" lg="10">
                                <v-checkbox v-model="qafilter" label="Weitere Filter..."> </v-checkbox>
                            </v-col>
                            <v-col v-if="qafilter">
                                <v-btn @click="addQAFilter"> <v-icon> mdi-plus</v-icon> </v-btn>
                            </v-col>
                        </v-row>

                        <div v-if="qafilter">
                            <div v-for="filter in qafilterList" :key="filter.filterId">
                                <div v-if="filter.active">
                                    <v-card class="pa-3 ma-2">
                                        <v-row align="right">
                                            <v-spacer></v-spacer>
                                            <v-btn icon @click="addQAFilter()">
                                                <v-icon> mdi-plus </v-icon>
                                            </v-btn>
                                            <v-btn icon @click="deleteQAFilter(filter.filterId)">
                                                <v-icon> mdi-delete </v-icon>
                                            </v-btn>
                                        </v-row>
                                        <v-row>
                                            <v-col align="right" colls="12" lg="11">
                                                <base-q-a-filter
                                                    :poll-index="pollIndex"
                                                    :filter-id="filter.filterId"
                                                    :initial-category-index="qafilterList[filter.filterId].categoryId"
                                                    :initial-question-index="qafilterList[filter.filterId].questionId"
                                                    :initial-answer-indices="qafilterList[filter.filterId].answerIds"
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
            </v-expansion-panels>
        </template>
        <v-card-actions>
            <v-col align="right">
                <v-btn color="primary"> Anwenden </v-btn>
            </v-col>
        </v-card-actions>
    </v-card>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import baseQAFilter from '../components/Filter/baseQAFilter'

export default {
    name: 'filterForm',
    components: {
        baseQAFilter,
    },
    data() {
        return {
            globalFilterId: 1,
            applyConsistency: false,
            qafilter: false,
            qafilterList: [
                { active: true, filterId: 0, filterType: 'qaFilter', categoryId: -1, questionId: -1, answerIds: [] },
            ],
            initialPollIndex: 0,
            chosenPoll: '',
            selectedQuestions: [],
            questionTitlesDisplayedInSelect: 3,
        }
    },

    methods: {
        ...mapActions({ initialize: 'evaluation/initialize' }),
        addQAFilter() {
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
            this.qafilterList[index].active = false
        },

        updateQaFilter([filterId, categoryIndex, questionIndex, answerIndices]) {
            console.log('updateQAFilter')
            this.qafilterList[filterId].categoryId = categoryIndex
            this.qafilterList[filterId].questionId = questionIndex
            for (let i = 0; i < answerIndices.length; i++) {
                this.qafilterList[filterId].answerIds[i] = answerIndices[i]
            }
            console.log(this.qafilterList)
        },
    },

    mounted() {
        this.initialize()
        this.chosenPoll = this.pollTitles[this.initialPollIndex]
        this.selectedQuestions = this.questionTitles
    },

    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),

        pollTitles() {
            const pollTitles = Object.assign([{}], this.polls)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.polls[i].pollName
            }
            return pollTitles
        },

        pollIndex() {
            return this.pollTitles.indexOf(this.pollTitles[0])
        },

        questionTitles() {
            const questionTitles = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                for (let j = 0; j < this.polls[this.pollIndex].categoryList[i].questionList.length; j++) {
                    questionTitles.push(this.polls[this.pollIndex].categoryList[i].questionList[j].questionMessage)
                }
            }
            console.log('questionTitles: ')
            console.log(questionTitles)
            return questionTitles
        },
    },
}
</script>

<style scoped></style>
